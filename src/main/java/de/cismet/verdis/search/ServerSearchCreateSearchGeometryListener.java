/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.search;

import Sirius.navigator.connection.SessionManager;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.PNode;

import org.apache.log4j.Logger;

import java.beans.PropertyChangeSupport;

import java.util.Collection;

import javax.swing.SwingWorker;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AbstractCreateSearchGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateGeometryListenerInterface;

import de.cismet.verdis.server.search.GeomServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ServerSearchCreateSearchGeometryListener extends AbstractCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(ServerSearchCreateSearchGeometryListener.class);

    public static final String INPUT_LISTENER_NAME = "CREATE_CUSTOMSEARCH_GEOMETRY";

    public static final String ACTION_SEARCH_STARTED = "ACTION_SEARCH_STARTED";
    public static final String ACTION_SEARCH_DONE = "ACTION_SEARCH_DONE";
    public static final String ACTION_SEARCH_FAILED = "ACTION_SEARCH_FAILED";

    //~ Instance fields --------------------------------------------------------

    private GeomServerSearch geomServerSearch;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CreateSearchGeometryListener object.
     *
     * @param  mc            DOCUMENT ME!
     * @param  serverSearch  DOCUMENT ME!
     */
    public ServerSearchCreateSearchGeometryListener(final MappingComponent mc, final GeomServerSearch serverSearch) {
        super(mc);

        setMode(CreateGeometryListenerInterface.POLYGON);
        setGeomServerSearch(serverSearch);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  geomServerSearch  DOCUMENT ME!
     */
    protected final void setGeomServerSearch(final GeomServerSearch geomServerSearch) {
        this.geomServerSearch = geomServerSearch;
    }

    @Override
    protected boolean performSearch(final PureNewFeature searchFeature) {
        final String crs = geomServerSearch.getCrs();
        final Geometry geometry;
        if (crs != null) {
            final int srid = CrsTransformer.extractSridFromCrs(crs);
            geometry = CrsTransformer.transformToGivenCrs(searchFeature.getGeometry(), crs);
            geometry.setSRID(srid);
        } else {
            geometry = searchFeature.getGeometry();
        }
        geomServerSearch.setGeometry(geometry);
        final PropertyChangeSupport propChangeSupport = getPropertyChangeSupport();
        propChangeSupport.firePropertyChange(ACTION_SEARCH_STARTED, null, geometry);
        new SwingWorker<Collection, Void>() {

                @Override
                protected Collection doInBackground() throws Exception {
                    final Collection collection = SessionManager.getProxy()
                                .customServerSearch(SessionManager.getSession().getUser(), geomServerSearch);
                    return collection;
                }

                @Override
                protected void done() {
                    try {
                        final Collection collection = get();
                        if (collection == null) {
                            propChangeSupport.firePropertyChange(
                                ACTION_SEARCH_FAILED,
                                null,
                                new Exception("Fehler während der Suche."));
                        } else {
                            propChangeSupport.firePropertyChange(ACTION_SEARCH_DONE, null, collection);
                        }
                    } catch (final Exception ex) {
                        propChangeSupport.firePropertyChange(ACTION_SEARCH_FAILED, null, ex);
                    }
                }
            }.execute();

        return true;
    }

    @Override
    protected PNode getPointerAnnotation() {
        return null;
    }
}
