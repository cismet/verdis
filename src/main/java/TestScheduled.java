/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/

import Sirius.navigator.connection.*;
import Sirius.navigator.connection.proxy.ConnectionProxy;

import org.apache.log4j.Level;

import java.util.Date;

import de.cismet.cids.server.actions.DefaultScheduledServerAction;
import de.cismet.cids.server.actions.DefaultScheduledServerActionTestImpl;
import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import de.cismet.verdis.server.action.VeranlagungsdateiScheduledServerAction;

/*
 * Copyright (C) 2012 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TestScheduled {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TestScheduled.class);

    public static final String CONNECTION_CLASS = "Sirius.navigator.connection.RESTfulConnection";
    public static final String CONNECTION_PROXY_CLASS =
        "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";

    public static final String CALLSERVER_URL = "http://localhost:9917/callserver/binary";
    public static final String CALLSERVER_DOMAIN = "VERDIS_GRUNDIS";
    public static final String CALLSERVER_USER = "SteinbacherD102";
    public static final String CALLSERVER_PASSWORD = "buggalo";
    public static final String CALLSERVER_GROUP = "VORN_schreiben_KA";

    //~ Constructors -----------------------------------------------------------

    // private final Set<Key> allFlurstueckKeys = new HashSet<Key>();

    /**
     * Creates a new BrokerTester object.
     */
    public TestScheduled() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            Log4JQuickConfig.configure4LumbermillOnLocalhost();
            LOG.setLevel(Level.WARN);
            SessionManager.init(initProxy());

            final TestScheduled test = new TestScheduled();
            test.doTest();
            System.exit(0);
        } catch (Exception ex) {
            LOG.fatal(ex, ex);
            System.exit(1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void doTest() throws Exception {
        final VeranlagungsdateiScheduledServerAction scheduledServerActionImpl =
            new VeranlagungsdateiScheduledServerAction();
        final Object executeTask = SessionManager.getProxy()
                    .executeTask(scheduledServerActionImpl.getTaskName(),
                        CALLSERVER_DOMAIN,
                        null,
                        DefaultScheduledServerAction.createExecutionRuleSAP("0 * * * * ?")
//                        DefaultScheduledServerAction.createExecutionRuleSAP("0 * * * * ?"),
//                        new ServerActionParameter<Date>(
//                            VeranlagungsdateiScheduledServerAction.PARAM_FROM,
//                            new Date(2012, 06, 1, 00, 00)),
//                        new ServerActionParameter<Date>(
//                            VeranlagungsdateiScheduledServerAction.PARAM_TO,
//                            new Date(2012, 06, 8, 00, 00))
                        );
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static ConnectionProxy initProxy() throws Exception {
        final Connection connection = ConnectionFactory.getFactory().createConnection(CONNECTION_CLASS, CALLSERVER_URL);

        final ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setCallserverURL(CALLSERVER_URL);
        connectionInfo.setPassword(CALLSERVER_PASSWORD);
        connectionInfo.setUserDomain(CALLSERVER_DOMAIN);
        connectionInfo.setUsergroup(CALLSERVER_GROUP);
        connectionInfo.setUsergroupDomain(CALLSERVER_DOMAIN);
        connectionInfo.setUsername(CALLSERVER_USER);

        final ConnectionSession session = ConnectionFactory.getFactory()
                    .createSession(connection, connectionInfo, true);
        return ConnectionFactory.getFactory().createProxy(CONNECTION_PROXY_CLASS, session);
    }
}
