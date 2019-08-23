/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 srichter
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
package de.cismet.cids.custom.featurerenderer.verdis_grundis;

import Sirius.server.middleware.types.MetaObject;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.gui.piccolo.CustomFixedWidthStroke;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.verdis.commons.constants.VerdisConstants;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class FrontFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static Color erColor = new Color(5164484);

    //~ Instance fields --------------------------------------------------------

    private final CustomFixedWidthStroke stroke = new CustomFixedWidthStroke(
            10f,
            CismapBroker.getInstance().getMappingComponent());
    private String key = null;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assign() {
        final CidsBean satzung_strassenreinigung = (CidsBean)cidsBean.getProperty(
                VerdisConstants.PROP.FRONT.FRONTINFO
                        + "."
                        + VerdisConstants.PROP.FRONTINFO.LAGE_SR);
        if (satzung_strassenreinigung == null) {
            key = (String)cidsBean.getProperty(VerdisConstants.PROP.FRONT.FRONTINFO + "."
                            + VerdisConstants.PROP.FRONTINFO.SR_KLASSE_OR + "."
                            + VerdisConstants.PROP.STRASSENREINIGUNG.KEY);
        } else {
            key = (String)satzung_strassenreinigung.getProperty("sr_klasse.key");
        }
    }

    @Override
    public Paint getLinePaint() {
        if (cidsBean != null) {
            assign();
        }
        if (key == null) {
            return Color.GREEN.darker();
        }
        if (key.equals("C1") || key.equals("C2") || key.equals("C3")) {
            return erColor;
        } else {
            return Color.ORANGE;
        }
    }

    @Override
    public Stroke getLineStyle() {
        return stroke;
    }

    @Override
    public String getAlternativeName() {
        if (MetaObject.NEW == cidsBean.getMetaObject().getStatus()) {
            return "neue Straßenfront";
        } else {
            return "Straßenfront " + cidsBean.getProperty(VerdisConstants.PROP.FRONT.NUMMER);
        }
    }
}
