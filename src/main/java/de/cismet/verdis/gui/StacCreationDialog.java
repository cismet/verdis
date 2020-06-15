/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.verdis.gui;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import java.io.StringReader;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import de.cismet.cids.server.actions.GetServerResourceServerAction;
import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.action.CreateAStacForKassenzeichenServerAction;
import de.cismet.verdis.server.action.PreExistingStacException;
import de.cismet.verdis.server.json.StacOptionsDurationJson;
import de.cismet.verdis.server.utils.VerdisServerResources;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class StacCreationDialog extends JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(StacCreationDialog.class);

    private static StacCreationDialog INSTANCE = null;

//    private static final String PROPERTY__INTRO_TEXT = "intro_text";
//    private static final String PROPERTY__DIALOG_WIDTH="dialog_width";
//    private static final String PROPERTY__DIALOG_HEIGHT="dialog_height";
//    private static final String PROPERTY__MS_BEFORE_AUTOCLOSE="ms_before_autoclose";
    private static final String PROPERTY__STAC_URL_TEMPLATE = "stac_url_template";
    private static final String PROPERTY__STAC_MESSAGE_TEMPLATE = "stac_message_template";
    private static final String PROPERTY__STAC_MESSAGE_TEMPLATE_DATE = "stac_message_template_date";
    private static final String PROPERTY__STAC_MESSAGE_TEMPLATE_SHORT = "stac_message_template_short";
    private static final String PROPERTY__STAC_MESSAGE_TEMPLATE_LONG = "stac_message_template_long";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    //~ Instance fields --------------------------------------------------------

    private final Properties properties;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form StacCreationDialog.
     */
    public StacCreationDialog() {
        super(Main.getInstance(), true);

        Properties properties = null;
        try {
            properties = getProperties(ConnectionContext.createDummy());
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        this.properties = properties;

        initComponents();

        getRootPane().setDefaultButton(jButton1);
        StaticSwingTools.doClickButtonOnKeyStroke(
            jButton1,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            getRootPane());
        StaticSwingTools.doClickButtonOnKeyStroke(
            jButton2,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            getRootPane());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static StacCreationDialog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StacCreationDialog();
            INSTANCE.pack();
            INSTANCE.jLabel5.setVisible(false);
            INSTANCE.jProgressBar1.setVisible(false);
        }
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   stac  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String toDashes(final String stac) {
        if (stac == null) {
            return null;
        } else if (stac.length() == 12) {
            return stac.substring(0, 4) + "-" + stac.substring(4, 8) + "-" + stac.substring(8, 12);
        } else {
            return stac;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Properties getProperties(final ConnectionContext connectionContext) throws Exception {
        final User user = SessionManager.getSession().getUser();
        final Properties properties = new Properties();
        properties.load(new StringReader(
                (String)SessionManager.getProxy().executeTask(
                    user,
                    GetServerResourceServerAction.TASK_NAME,
                    VerdisConstants.DOMAIN,
                    VerdisServerResources.STAC_CREATION_DIALOG_PROPERTIES.getValue(),
                    connectionContext)));
        return properties;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stac          DOCUMENT ME!
     * @param  shortNotLong  DOCUMENT ME!
     * @param  date          DOCUMENT ME!
     */
    private void toClipboard(final String stac, final Boolean shortNotLong, final Date date) {
        final String stacWithDashes = toDashes(stac);
        final String stacUrl = properties.getProperty(PROPERTY__STAC_URL_TEMPLATE).contains("%s")
            ? String.format(properties.getProperty(PROPERTY__STAC_URL_TEMPLATE), stac)
            : properties.getProperty(PROPERTY__STAC_URL_TEMPLATE);

        final String message;
        if (properties.containsKey(PROPERTY__STAC_MESSAGE_TEMPLATE_SHORT)
                    && !properties.getProperty(PROPERTY__STAC_MESSAGE_TEMPLATE_SHORT).isEmpty()
                    && properties.containsKey(PROPERTY__STAC_MESSAGE_TEMPLATE_LONG)
                    && !properties.getProperty(PROPERTY__STAC_MESSAGE_TEMPLATE_LONG).isEmpty()) {
            if (shortNotLong != null) {
                message = String.format(
                        properties.getProperty(
                            shortNotLong ? PROPERTY__STAC_MESSAGE_TEMPLATE_SHORT
                                         : PROPERTY__STAC_MESSAGE_TEMPLATE_LONG),
                        stacUrl,
                        stacWithDashes);
            } else {
                final String dateString = DATE_FORMAT.format(date);
                message = String.format(
                        properties.getProperty(PROPERTY__STAC_MESSAGE_TEMPLATE_DATE),
                        dateString,
                        stacUrl,
                        stacWithDashes);
            }
        } else {
            message = String.format(properties.getProperty(PROPERTY__STAC_MESSAGE_TEMPLATE),
                    stacUrl,
                    stacWithDashes);
        }
        final StringSelection stringSelection = new StringSelection(message);
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        jRadioButton3 = new javax.swing.JRadioButton();
        defaultBindableDateChooser1 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel6 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jCheckBox2 = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.title")); // NOI18N
        setMaximumSize(new java.awt.Dimension(625, 350));
        setMinimumSize(new java.awt.Dimension(625, 350));
        setPreferredSize(new java.awt.Dimension(625, 350));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/verdis/res/images/stac.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jLabel3.text"));      // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 20);
        jPanel8.add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jLabel4.text")); // NOI18N
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel8.add(jLabel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel8.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(jPanel8, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jTextField1.setEditable(false);
        jTextField1.setText(org.openide.util.NbBundle.getMessage(
                StacCreationDialog.class,
                "StacCreationDialog.jTextField1.text")); // NOI18N
        jTextField1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jTextField1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jLabel1.text")); // NOI18N
        jLabel1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel2.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel5.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            jRadioButton2,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jRadioButton2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jRadioButton2, gridBagConstraints);

        buttonGroup1.add(jRadioButton1);
        org.openide.awt.Mnemonics.setLocalizedText(
            jRadioButton1,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jRadioButton1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jRadioButton1, gridBagConstraints);

        jPanel9.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jRadioButton3);
        org.openide.awt.Mnemonics.setLocalizedText(
            jRadioButton3,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jRadioButton3.text")); // NOI18N
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jRadioButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel9.add(jRadioButton3, gridBagConstraints);

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jRadioButton3,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                defaultBindableDateChooser1,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel9.add(defaultBindableDateChooser1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel3.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jPanel3, gridBagConstraints);

        jProgressBar1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jProgressBar1, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel6.add(jButton2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel6.add(jButton1, gridBagConstraints);

        jPanel7.setLayout(new java.awt.GridLayout(0, 1));

        jCheckBox2.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox2,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jCheckBox2.text")); // NOI18N
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jCheckBox2ActionPerformed(evt);
                }
            });
        jPanel7.add(jCheckBox2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jPanel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(filler1, gridBagConstraints);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(StacCreationDialog.class, "StacCreationDialog.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jLabel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        getContentPane().add(jPanel1, gridBagConstraints);

        bindingGroup.bind();

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        final ServerActionParameter<String> paramUser = new ServerActionParameter<>(
                CreateAStacForKassenzeichenServerAction.Parameter.USER.toString(),
                SessionManager.getSession().getUser().getName());
        final ServerActionParameter<String> paramKassenzeichen = new ServerActionParameter<>(
                CreateAStacForKassenzeichenServerAction.Parameter.KASSENZEICHEN.toString(),
                String.valueOf(
                    Main.getInstance().getCidsBean().getProperty(
                        VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHENNUMMER)));

        final ServerActionParameter<Timestamp> paramDate;
        final ServerActionParameter<StacOptionsDurationJson.Unit> paramDurationUnit;
        final ServerActionParameter<Integer> paramDurationValue;
        if (jRadioButton1.isSelected()) {
            paramDurationUnit = new ServerActionParameter<>(
                    CreateAStacForKassenzeichenServerAction.Parameter.DURATION_UNIT.toString(),
                    StacOptionsDurationJson.Unit.DAYS);
            paramDurationValue = new ServerActionParameter<>(
                    CreateAStacForKassenzeichenServerAction.Parameter.DURATION_VALUE.toString(),
                    2);
            paramDate = null;
        } else if (jRadioButton2.isSelected()) {
            paramDurationUnit = new ServerActionParameter<>(
                    CreateAStacForKassenzeichenServerAction.Parameter.DURATION_UNIT.toString(),
                    StacOptionsDurationJson.Unit.MONTHS);
            paramDurationValue = new ServerActionParameter<>(
                    CreateAStacForKassenzeichenServerAction.Parameter.DURATION_VALUE.toString(),
                    2);
            paramDate = null;
        } else if (jRadioButton3.isSelected()) {
            paramDurationUnit = null;
            paramDurationValue = null;
            paramDate = new ServerActionParameter<>(CreateAStacForKassenzeichenServerAction.Parameter.EXPIRATION
                            .toString(),
                    new Timestamp(defaultBindableDateChooser1.getDate().getTime() + (24 * 60 * 60 * 1000) - 1));
        } else {
            paramDurationUnit = null;
            paramDurationValue = null;
            paramDate = null;
        }

        final Collection<ServerActionParameter> params = new ArrayList<>();
        params.add(paramKassenzeichen);
        params.add(paramUser);
        if (paramDate != null) {
            params.add(paramDate);
        }
        if (paramDurationUnit != null) {
            params.add(paramDurationUnit);
        }
        if (paramDurationValue != null) {
            params.add(paramDurationValue);
        }

        jProgressBar1.setVisible(true);
        jProgressBar1.setIndeterminate(true);
        jTextField1.setText(null);
        jTextField1.setEnabled(false);
        jCheckBox2.setEnabled(false);
        jLabel1.setEnabled(false);
        jButton1.setEnabled(false);
        jRadioButton1.setEnabled(false);
        jRadioButton2.setEnabled(false);
        jRadioButton3.setEnabled(false);
        defaultBindableDateChooser1.setEnabled(false);

        doStac(params);
    } //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  params  DOCUMENT ME!
     */
    private void doStac(final Collection<ServerActionParameter> params) {
        new SwingWorker<Object, Void>() {

                @Override
                protected Object doInBackground() throws Exception {
                    final Object ret = SessionManager.getProxy()
                                .executeTask(
                                    CreateAStacForKassenzeichenServerAction.TASKNAME,
                                    VerdisConstants.DOMAIN,
                                    null,
                                    ConnectionContext.createDummy(),
                                    params.toArray(new ServerActionParameter[0]));
                    return ret;
                }

                @Override
                protected void done() {
                    try {
                        final Object ret = get();
                        if (ret instanceof String) {
                            final String stac = (String)ret;
                            final Boolean shortNotLong;
                            if (jRadioButton1.isSelected()) {
                                shortNotLong = true;
                            } else if (jRadioButton2.isSelected()) {
                                shortNotLong = false;
                            } else {
                                shortNotLong = null;
                            }
                            final Date date = jRadioButton2.isSelected() ? defaultBindableDateChooser1.getDate() : null;

                            toClipboard(stac, shortNotLong, date);
                            jTextField1.setText(toDashes(stac));
                        } else {
                            if (ret instanceof PreExistingStacException) {
                                final int answer = JOptionPane.showConfirmDialog(
                                        StacCreationDialog.this,
                                        "<html>"
                                                + "Es existiert bereits ein gültiger Zugriffscode für dieses Kassenzeichen.<br>"
                                                + "Wenn Sie fortfahren, wird der bestehende Zugriffscode ungültig,<br>"
                                                + "und es wird ein neuer Zugriffscode erzeugt.<br><br>"
                                                + "Möchten Sie den <b>bestehenden Zugriffscode ungültig machen</b>,<br>und einen neuen Zugriffscode erzeugen ?",
                                        "Zugriffscode exisitert bereits.",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE);

                                if (JOptionPane.YES_OPTION == answer) {
                                    params.add(new ServerActionParameter(
                                            CreateAStacForKassenzeichenServerAction.Parameter.EXPIRE_PRE_EXISTING
                                                        .toString(),
                                            Boolean.TRUE));
                                    doStac(params);
                                }
                            }
                        }
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    } finally {
                        jProgressBar1.setVisible(false);
                        jProgressBar1.setIndeterminate(false);
                        if (jCheckBox2.isSelected()) {
                            jLabel5.setVisible(true);
                            new Timer().schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        jButton2.doClick();
                                    }
                                }, 2000);
                        } else {
                            finishCreation();
                        }
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void finishCreation() {
        jLabel5.setVisible(false);
        jTextField1.setEnabled(true);
        jLabel1.setEnabled(true);
        jCheckBox2.setEnabled(true);
        jButton1.setEnabled(true);
        jRadioButton1.setEnabled(true);
        jRadioButton2.setEnabled(true);
        jRadioButton3.setEnabled(true);
        defaultBindableDateChooser1.setEnabled(jRadioButton3.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        setVisible(false);
        jCheckBox2.setEnabled(true);
        jTextField1.setText(null);
        finishCreation();
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jRadioButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jRadioButton3ActionPerformed
        if (defaultBindableDateChooser1.getDate() == null) {
            defaultBindableDateChooser1.setDate(new Date());
        }
    }                                                                                 //GEN-LAST:event_jRadioButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jCheckBox2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jCheckBox2ActionPerformed
    }                                                                              //GEN-LAST:event_jCheckBox2ActionPerformed
}
