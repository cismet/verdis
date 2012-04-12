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
/*
 * ValidationDemo.java
 *
 * Created on 03.07.2011, 17:27:39
 */
package de.cismet.validation.test;

import Sirius.navigator.connection.Connection;
import Sirius.navigator.connection.ConnectionFactory;
import Sirius.navigator.connection.ConnectionInfo;
import Sirius.navigator.connection.ConnectionSession;
import Sirius.navigator.connection.RESTfulConnection;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.commons.lang.StringUtils;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;

import de.cismet.validation.display.EmbeddedValidatorDisplay;

import de.cismet.validation.validator.BindingValidator;

import de.cismet.verdis.constants.KassenzeichenPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ValidationDemo extends javax.swing.JPanel implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final String USERNAME = "SteinbacherD102";
    private static final String PASSWORD = "sb";
    private static final String USERGROUP = "VORN";
    private static final String USER_DOMAIN = "VERDIS_GRUNDIS";
    private static final String USERGROUP_DOMAIN = "VERDIS_GRUNDIS";
    private static final String CALLSERVER_URL = "http://localhost:9986/callserver/binary";
    private static final String CALLSERVER_CLASSNAME = RESTfulConnection.class.getCanonicalName();

    //~ Instance fields --------------------------------------------------------

    // End of variables declaration//GEN-END:variables
    private CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ValidationDemo object.
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public ValidationDemo() throws Exception {
        initCidsBean();
        initComponents();

        final BindingValidator validator1 = new BindingValidator(this.bindingGroup.getBinding(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER)) {

                @Override
                public ValidatorState performValidation() {
                    final CidsBean cidsBean = ValidationDemo.this.getCidsBean();
                    if (cidsBean == null) {
                        return null;
                    }
                    final Integer kassenzeichennummer = (Integer)cidsBean.getProperty(
                            KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
                    if (kassenzeichennummer.intValue() < 1000) {
                        return new ValidatorStateImpl(
                                ValidatorStateImpl.Type.ERROR,
                                "zu kurz! klick zum korrigieren",
                                new AbstractAction("ToolTipText") {

                                    @Override
                                    public void actionPerformed(final ActionEvent e) {
                                        ValidationDemo.this.jTextField1.setText(
                                            StringUtils.rightPad(
                                                ValidationDemo.this.jTextField1.getText(),
                                                10
                                                        - ValidationDemo.this.jTextField1.getText().length(),
                                                '0'));
                                        JOptionPane.showMessageDialog(
                                            ValidationDemo.this,
                                            "<html>Hint geklickt.<br/>Zahl wurde mit Nullen aufgefüllt.");
                                    }
                                });
                    }
                    if (kassenzeichennummer.intValue() < 1000000) {
                        return new ValidatorStateImpl(ValidatorStateImpl.Type.WARNING, "ein bisschen zu kurz!");
                    }
                    if (kassenzeichennummer.intValue() > 1000000000) {
                        return new ValidatorStateImpl(ValidatorStateImpl.Type.ERROR, "zu lang!");
                    }
                    return new ValidatorStateImpl(ValidatorStateImpl.Type.VALID, "genau richtig!");
                }
            };
        validator1.attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(this.jTextField1));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jTextField1 = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kassenzeichennummer}"),
                jTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 169;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(50, 50, 50, 50);
        add(jTextField1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    protected final Connection getConnection() throws ConnectionException {
        return ConnectionFactory.getFactory()
                    .createConnection(CALLSERVER_CLASSNAME, "http://localhost:9986/callserver/binary");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected final void initCidsBean() throws Exception {
        final Connection connection = getConnection();
        final ConnectionInfo connectionInfo = getConnectionInfo();

        final ConnectionSession session = ConnectionFactory.getFactory()
                    .createSession(connection, connectionInfo, true);

        final ConnectionProxy proxy = ConnectionFactory.getFactory()
                    .createProxy("Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler", session);

        SessionManager.init(proxy);

        final int classId = ClassCacheMultiple.getMetaClass("VERDIS_GRUNDIS", "kassenzeichen").getId();
        final MetaObject metaObject = proxy.getMetaObject(64151, classId, "VERDIS_GRUNDIS");

        if (metaObject != null) {
            setCidsBean(metaObject.getBean());
        } else {
            setCidsBean(null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected final ConnectionInfo getConnectionInfo() {
        final ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setCallserverURL("http://localhost:9986/callserver/binary");
        connectionInfo.setUsername("SteinbacherD102");
        connectionInfo.setPassword("sb");
        connectionInfo.setUserDomain("VERDIS_GRUNDIS");
        connectionInfo.setUsergroup("VORN");
        connectionInfo.setUsergroupDomain("VERDIS_GRUNDIS");
        return connectionInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();

        final JFrame f = new JFrame("TestSzenario");
        f.add(new ValidationDemo());
        f.setVisible(true);
        f.setDefaultCloseOperation(2);
        f.setSize(400, 400);
    }

    @Override
    public final CidsBean getCidsBean() {
        return this.cidsBean;
    }

    @Override
    public final void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
    }
}
