/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

import Sirius.navigator.connection.Connection;
import Sirius.navigator.connection.ConnectionFactory;
import Sirius.navigator.connection.ConnectionInfo;
import Sirius.navigator.connection.ConnectionSession;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;

import Sirius.server.middleware.types.MetaObject;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.multibean.MultiBeanHelper;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import de.cismet.verdis.commons.constants.VerdisConstants;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class Test1 {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Test1.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of Test1.
     */
    public Test1() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            final String callServerURL = "http://localhost:9917/callserver/binary";
            final String connectionClass = "Sirius.navigator.connection.RESTfulConnection";
            Log4JQuickConfig.configure4LumbermillOnLocalhost();

//            String callServerURL="rmi://localhost/callServer";
//            String connectionClass="Sirius.navigator.connection.RMIConnection";
            final Connection connection = ConnectionFactory.getFactory()
                        .createConnection(connectionClass, callServerURL);
            ConnectionSession session = null;
            ConnectionProxy proxy = null;
            final ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setCallserverURL(callServerURL);
            connectionInfo.setPassword("xxx");
            connectionInfo.setUserDomain(VerdisConstants.DOMAIN);
            connectionInfo.setUsergroup("VORN_schreiben_KA");
            connectionInfo.setUsergroupDomain(VerdisConstants.DOMAIN);
            connectionInfo.setUsername("SteinbacherD102");

            session = ConnectionFactory.getFactory().createSession(connection, connectionInfo, true);

            LOG.fatal("session created");
            proxy = ConnectionFactory.getFactory()
                        .createProxy("Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler", session);

            SessionManager.init(proxy);

            final MetaObject mo = proxy.getMetaObject(
                    3736,
                    CidsBean.getMetaClassFromTableName(VerdisConstants.DOMAIN,
                        VerdisConstants.MC.KASSENZEICHEN).getID(),
                    VerdisConstants.DOMAIN);
            final CidsBean cidsBean = mo.getBean();
            LOG.fatal(cidsBean.getMOString());
            final MultiBeanHelper mbh = new MultiBeanHelper();
            mbh.setDummyBean(createDummyBean());
            final Collection<CidsBean> col = new ArrayList<>();
            col.add(((CidsBean)cidsBean.getProperty(VerdisConstants.MC.KANALANSCHLUSS)).getBeanCollectionProperty(
                    VerdisConstants.PROP.KANALANSCHLUSS.BEFREIUNGENUNDERLAUBNISSE).iterator().next()
                        .getBeanCollectionProperty(VerdisConstants.PROP.BEFREIUNGERLAUBNIS.GEOMETRIEN).iterator()
                        .next());
            mbh.setBeans(col);
            final CidsBean childBean = mbh.getDummyBean();
            childBean.setProperty(
                VerdisConstants.PROP.BEFREIUNGERLAUBNIS_GEOMETRIE.BEMERKUNG,
                "hat sich was verändert ?");
            LOG.fatal(cidsBean.getMOString());
        } catch (final Exception e) {
            LOG.fatal(e, e);
            System.exit(1);
        }
        System.exit(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createDummyBean() throws Exception {
        final CidsBean dummyBean = CidsBean.createNewCidsBeanFromTableName(
                VerdisConstants.DOMAIN,
                VerdisConstants.MC.BEFREIUNGERLAUBNIS_GEOMETRIE);
        final CidsBean geomBean = CidsBean.createNewCidsBeanFromTableName(
                VerdisConstants.DOMAIN,
                VerdisConstants.MC.GEOM);
        try {
            dummyBean.setProperty(VerdisConstants.PROP.BEFREIUNGERLAUBNIS_GEOMETRIE.GEOMETRIE, geomBean);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        return dummyBean;
    }
}
