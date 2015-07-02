/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2011 jruiz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.util;

import com.vividsolutions.jts.geom.Geometry;

import java.sql.Date;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CidsBeanSupport {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CidsBeanSupport.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean deepcloneCidsBean(final CidsBean cidsBean) throws Exception {
        if (cidsBean == null) {
            return null;
        }
        final CidsBean cloneBean = cidsBean.getMetaObject().getMetaClass().getEmptyInstance().getBean();
        deepcopyAllProperties(cidsBean, cloneBean);
        return cloneBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sourceBean  DOCUMENT ME!
     * @param   targetBean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void deepcopyAllProperties(final CidsBean sourceBean, final CidsBean targetBean) throws Exception {
        deepcopyAllProperties(sourceBean, targetBean, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sourceBean        DOCUMENT ME!
     * @param   targetBean        DOCUMENT ME!
     * @param   cleanCollections  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void deepcopyAllProperties(final CidsBean sourceBean,
            final CidsBean targetBean,
            final boolean cleanCollections) throws Exception {
        if ((sourceBean == null) || (targetBean == null)) {
            return;
        }

        for (final String propName : sourceBean.getPropertyNames()) {
            final Object o = sourceBean.getProperty(propName);

            if (propName.toLowerCase().equals("id")) {
                final int id = (Integer)sourceBean.getProperty("id");
                targetBean.setProperty("id", id);
                targetBean.getMetaObject().setID(id);
            } else if (o instanceof CidsBean) {
                targetBean.setProperty(propName, deepcloneCidsBean((CidsBean)o));
            } else if (o instanceof Collection) {
                final List<CidsBean> list = (List<CidsBean>)o;
                final List<CidsBean> targetList = targetBean.getBeanCollectionProperty(propName);

                final List<CidsBean> toAdd = new ArrayList<CidsBean>(list);
                toAdd.removeAll(targetList);

                final List<CidsBean> toRemove = new ArrayList<CidsBean>(targetList);
                toRemove.removeAll(list);

//                targetList.retainAll(list);
//                list.removeAll(targetList);

                final HashMap<Integer, CidsBean> origTargetMap = new HashMap<Integer, CidsBean>();
                final List<CidsBean> toUpdateTarget = new ArrayList<CidsBean>(targetList);
                toUpdateTarget.retainAll(list);
                for (final CidsBean cidsBean : toUpdateTarget) {
                    origTargetMap.put(cidsBean.getMetaObject().getId(), cidsBean);
                }

                final HashMap<Integer, CidsBean> origArrayMap = new HashMap<Integer, CidsBean>();
                final List<CidsBean> toUpdateOrig = new ArrayList<CidsBean>(list);
                toUpdateOrig.retainAll(targetList);
                for (final CidsBean cidsBean : toUpdateOrig) {
                    origArrayMap.put(cidsBean.getMetaObject().getId(), cidsBean);
                }

                for (final Integer id : origArrayMap.keySet()) {
                    final CidsBean origArrayBean = origArrayMap.get(id);
                    final CidsBean targetArrayBean = origTargetMap.get(id);
                    deepcopyAllProperties(origArrayBean, targetArrayBean);
                }

                targetList.removeAll(toRemove);
                targetList.addAll(toAdd);
            } else if (o instanceof Geometry) {
                targetBean.setProperty(propName, ((Geometry)o).clone());
            } else if (o instanceof Float) {
                targetBean.setProperty(propName, new Float(o.toString()));
            } else if (o instanceof Boolean) {
                targetBean.setProperty(propName, new Boolean(o.toString()));
            } else if (o instanceof Long) {
                targetBean.setProperty(propName, new Long(o.toString()));
            } else if (o instanceof Double) {
                targetBean.setProperty(propName, new Double(o.toString()));
            } else if (o instanceof Integer) {
                targetBean.setProperty(propName, new Integer(o.toString()));
            } else if (o instanceof Date) {
                targetBean.setProperty(propName, ((Date)o).clone());
            } else if (o instanceof String) {
                targetBean.setProperty(propName, o);
            } else if (o instanceof Timestamp) {
                targetBean.setProperty(propName, ((Timestamp)o).clone());
            } else {
                if (o != null) {
                    LOG.error("unknown property type: " + o.getClass().getName());
                }
                targetBean.setProperty(propName, o);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sourceBean  DOCUMENT ME!
     * @param   targetBean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void copyAllProperties(final CidsBean sourceBean, final CidsBean targetBean) throws Exception {
        if ((sourceBean == null) || (targetBean == null)) {
            return;
        }

        for (final String propName : sourceBean.getPropertyNames()) {
            final Object o = sourceBean.getProperty(propName);
            targetBean.setProperty(propName, o);
        }
    }
}
