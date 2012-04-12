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

import Sirius.navigator.connection.Connection;
import Sirius.navigator.connection.ConnectionFactory;
import Sirius.navigator.connection.ConnectionInfo;
import Sirius.navigator.connection.RESTfulConnection;
import Sirius.navigator.exception.ConnectionException;

import de.cismet.cids.custom.objecteditors.EditorTester;

import de.cismet.verdis.CidsAppBackend;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VerdisEditorTester extends EditorTester {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VerdisEditorTester.class);

    private static final String USERNAME = "SteinbacherD102";
    private static final String PASSWORD = "sb";
    private static final String USERGROUP = "VORN";
    private static final String USER_DOMAIN = "VERDIS_GRUNDIS";
    private static final String USERGROUP_DOMAIN = "VERDIS_GRUNDIS";

    private static final String CALLSERVER_URL = "http://localhost:9986/callserver/binary";
    private static final String CALLSERVER_CLASSNAME = RESTfulConnection.class.getCanonicalName();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WrrlEditorTester object.
     *
     * @param   className    DOCUMENT ME!
     * @param   editorClass  DOCUMENT ME!
     * @param   domain       DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public VerdisEditorTester(final String className, final Class editorClass, final String domain) throws Exception {
        super(className, editorClass, domain);
        if (LOG.isDebugEnabled()) {
            LOG.debug("init VerdisEditorTester");
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void initProxy() throws Exception {
        super.initProxy();
        CidsAppBackend.init(getProxy());
    }

    @Override
    protected Connection getConnection() throws ConnectionException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getConnection");
        }
        return ConnectionFactory.getFactory().createConnection(CALLSERVER_CLASSNAME, CALLSERVER_URL);
    }

    @Override
    protected ConnectionInfo getConnectionInfo() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getConnectionInfo");
        }
        final ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setCallserverURL(CALLSERVER_URL);
        connectionInfo.setUsername(USERNAME);
        connectionInfo.setPassword(PASSWORD);
        connectionInfo.setUserDomain(USER_DOMAIN);
        connectionInfo.setUsergroup(USERGROUP);
        connectionInfo.setUsergroupDomain(USERGROUP_DOMAIN);
        return connectionInfo;
    }

    @Override
    public void run() {
        EditorTester.run(this);
    }
}
