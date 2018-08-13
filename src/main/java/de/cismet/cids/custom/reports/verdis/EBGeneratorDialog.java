/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.reports.verdis;

import org.apache.log4j.Logger;

import java.awt.Frame;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.KeyStroke;

import de.cismet.cids.custom.utils.ByteArrayActionDownload;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cismap.commons.gui.printing.BackgroundTaskDownload;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

import de.cismet.verdis.commons.constants.KassenzeichenPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.server.action.EBReportServerAction;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class EBGeneratorDialog extends javax.swing.JDialog implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(EBGeneratorDialog.class);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static enum Mode {

        //~ Enum constants -----------------------------------------------------

        FLAECHEN, FRONTEN
    }

    //~ Instance fields --------------------------------------------------------

    private final ConnectionContext connectionContext = ConnectionContext.create(
            AbstractConnectionContext.Category.OTHER,
            getClass().getCanonicalName());
    private final Mode mode;
    private final CidsBean kassenzeichen;
    private final Frame parent;
    private String title = "";
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.ButtonGroup btnGroupFormat;
    private javax.swing.ButtonGroup btnGroupOrientation;
    private javax.swing.JButton btnPrint;
    private javax.swing.JComboBox cbScale;
    private javax.swing.JCheckBox chkFillAbflusswirksamkeit;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFiller2;
    private javax.swing.JLabel lblFiller3;
    private javax.swing.JLabel lblFormat;
    private javax.swing.JLabel lblHinweise;
    private javax.swing.JLabel lblOrientation;
    private javax.swing.JLabel lblScale;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlFormat;
    private javax.swing.JPanel pnlOrientation;
    private javax.swing.JRadioButton rbA3;
    private javax.swing.JRadioButton rbA4;
    private javax.swing.JRadioButton rbLandscapeMode;
    private javax.swing.JRadioButton rbPortraitMode;
    private javax.swing.JTextArea taHinweise;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FEPGeneratorDialog object.
     *
     * @param  kassenzeichen  DOCUMENT ME!
     * @param  parent         DOCUMENT ME!
     * @param  mode           DOCUMENT ME!
     */
    public EBGeneratorDialog(final CidsBean kassenzeichen, final Frame parent, final Mode mode) {
        super(parent, false);
        this.kassenzeichen = kassenzeichen;
        this.parent = parent;
        this.mode = mode;

        if (Mode.FRONTEN.equals(mode)) {
            title = "Frontenbogen - Report Parameter";
        } else {
            title = "Flächenerfassungsbogen - Report Parameter";
        }

        initComponents();

        if (Mode.FRONTEN.equals(mode)) {
            jScrollPane1.setVisible(false);
            lblHinweise.setVisible(false);
        }

        getRootPane().setDefaultButton(btnPrint);
        StaticSwingTools.doClickButtonOnKeyStroke(
            btnPrint,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            getRootPane());
        StaticSwingTools.doClickButtonOnKeyStroke(
            btnCancel,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            getRootPane());

        pack();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * Creates new form FEPGeneratorDialog.
     */
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btnGroupFormat = new javax.swing.ButtonGroup();
        btnGroupOrientation = new javax.swing.ButtonGroup();
        lblHinweise = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taHinweise = new javax.swing.JTextArea();
        lblFormat = new javax.swing.JLabel();
        lblOrientation = new javax.swing.JLabel();
        pnlFormat = new javax.swing.JPanel();
        rbA4 = new javax.swing.JRadioButton();
        rbA3 = new javax.swing.JRadioButton();
        lblFiller2 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        pnlOrientation = new javax.swing.JPanel();
        rbPortraitMode = new javax.swing.JRadioButton();
        rbLandscapeMode = new javax.swing.JRadioButton();
        lblFiller3 = new javax.swing.JLabel();
        jRadioButton2 = new javax.swing.JRadioButton();
        lblScale = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cbScale = new javax.swing.JComboBox();
        pnlButtons = new javax.swing.JPanel();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        chkFillAbflusswirksamkeit = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(title);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblHinweise,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.lblHinweise.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(lblHinweise, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(26, 50));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(262, 80));

        taHinweise.setColumns(20);
        taHinweise.setLineWrap(true);
        taHinweise.setRows(5);
        jScrollPane1.setViewportView(taHinweise);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFormat,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.lblFormat.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        getContentPane().add(lblFormat, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblOrientation,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.lblOrientation.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        getContentPane().add(lblOrientation, gridBagConstraints);

        pnlFormat.setLayout(new java.awt.GridBagLayout());

        btnGroupFormat.add(rbA4);
        org.openide.awt.Mnemonics.setLocalizedText(
            rbA4,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.rbA4.text")); // NOI18N
        rbA4.setActionCommand(org.openide.util.NbBundle.getMessage(
                EBGeneratorDialog.class,
                "EBGeneratorDialog.rbA4.actionCommand"));                                                  // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlFormat.add(rbA4, gridBagConstraints);

        btnGroupFormat.add(rbA3);
        org.openide.awt.Mnemonics.setLocalizedText(
            rbA3,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.rbA3.text")); // NOI18N
        rbA3.setActionCommand(org.openide.util.NbBundle.getMessage(
                EBGeneratorDialog.class,
                "EBGeneratorDialog.rbA3.actionCommand"));                                                  // NOI18N
        rbA3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rbA3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlFormat.add(rbA3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFiller2,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.lblFiller2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlFormat.add(lblFiller2, gridBagConstraints);

        btnGroupFormat.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            jRadioButton1,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.jRadioButton1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlFormat.add(jRadioButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(pnlFormat, gridBagConstraints);

        pnlOrientation.setLayout(new java.awt.GridBagLayout());

        btnGroupOrientation.add(rbPortraitMode);
        org.openide.awt.Mnemonics.setLocalizedText(
            rbPortraitMode,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.rbPortraitMode.text")); // NOI18N
        rbPortraitMode.setActionCommand(org.openide.util.NbBundle.getMessage(
                EBGeneratorDialog.class,
                "EBGeneratorDialog.rbPortraitMode.actionCommand"));                                                  // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlOrientation.add(rbPortraitMode, gridBagConstraints);

        btnGroupOrientation.add(rbLandscapeMode);
        org.openide.awt.Mnemonics.setLocalizedText(
            rbLandscapeMode,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.rbLandscapeMode.text")); // NOI18N
        rbLandscapeMode.setActionCommand(org.openide.util.NbBundle.getMessage(
                EBGeneratorDialog.class,
                "EBGeneratorDialog.rbLandscapeMode.actionCommand"));                                                  // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlOrientation.add(rbLandscapeMode, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFiller3,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.lblFiller3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlOrientation.add(lblFiller3, gridBagConstraints);

        btnGroupOrientation.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            jRadioButton2,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.jRadioButton2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlOrientation.add(jRadioButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(pnlOrientation, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblScale,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.lblScale.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        getContentPane().add(lblScale, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        cbScale.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "optimal", "1:200", "1:300", "1:400", "1:500", "1:750", "1:1000" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(cbScale, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel1, gridBagConstraints);

        pnlButtons.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnPrint,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.btnPrint.text")); // NOI18N
        btnPrint.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPrintActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        pnlButtons.add(btnPrint, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnCancel,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCancelActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        pnlButtons.add(btnCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(pnlButtons, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkFillAbflusswirksamkeit,
            org.openide.util.NbBundle.getMessage(
                EBGeneratorDialog.class,
                "EBGeneratorDialog.chkFillAbflusswirksamkeit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(chkFillAbflusswirksamkeit, gridBagConstraints);
    }                                                                 // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rbA3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rbA3ActionPerformed
    }                                                                        //GEN-LAST:event_rbA3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPrintActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPrintActionPerformed
        generateReport(
            taHinweise.getText(),
            jRadioButton1.isSelected()
                ? null : btnGroupFormat.getSelection().getActionCommand().equals(rbA4.getActionCommand()),
            jRadioButton2.isSelected()
                ? null
                : btnGroupOrientation.getSelection().getActionCommand().equals(rbLandscapeMode.getActionCommand()));
        this.setVisible(false);
    }                                                                            //GEN-LAST:event_btnPrintActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
    }                                                                             //GEN-LAST:event_btnCancelActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Double getSelectedScaleDenominator() {
        final String scale = (String)cbScale.getSelectedItem();
        if (scale.matches("1:[0-9]+")) {
            // format of string eg 1:500
            final String[] splittedScale = scale.split(":");
            return Double.parseDouble(splittedScale[1]);
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hints      DOCUMENT ME!
     * @param  dinA4      DOCUMENT ME!
     * @param  landscape  DOCUMENT ME!
     */
    private void generateReport(final String hints, final Boolean dinA4, final Boolean landscape) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("starting report generation for feb report");
        }

        final EBReportServerAction.Type type;
        final EBReportServerAction.MapFormat mapFormat;

        if (Mode.FRONTEN.equals(mode)) {
            type = EBReportServerAction.Type.FRONTEN;
        } else {
            type = EBReportServerAction.Type.FLAECHEN;
        }

        if ((dinA4 == null) && (landscape == null)) {
            mapFormat = null;
        } else if (dinA4 && (landscape == null)) {
            mapFormat = EBReportServerAction.MapFormat.A4;
        } else if (!dinA4 && (landscape == null)) {
            mapFormat = EBReportServerAction.MapFormat.A3;
        } else if ((dinA4 == null) && landscape) {
            mapFormat = EBReportServerAction.MapFormat.LS;
        } else if ((dinA4 == null) && !landscape) {
            mapFormat = EBReportServerAction.MapFormat.P;
        } else if (dinA4 && landscape) {
            mapFormat = EBReportServerAction.MapFormat.A4LS;
        } else if (dinA4) {
            mapFormat = EBReportServerAction.MapFormat.A4P;
        } else if (landscape) {
            mapFormat = EBReportServerAction.MapFormat.A3LS;
        } else {
            mapFormat = EBReportServerAction.MapFormat.A3P;
        }

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();

            final int nummer = (Integer)kassenzeichen.getProperty(
                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER);
            final String fileName = (Mode.FLAECHEN.equals(mode)) ? ("FEB-" + nummer) : ("STR-" + nummer);
            downloadFromGenerator(type, mapFormat, hints, jobname, fileName);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  type       DOCUMENT ME!
     * @param  mapFormat  DOCUMENT ME!
     * @param  hints      DOCUMENT ME!
     * @param  title      DOCUMENT ME!
     * @param  fileName   DOCUMENT ME!
     */
    private void downloadFromAction(final EBReportServerAction.Type type,
            final EBReportServerAction.MapFormat mapFormat,
            final String hints,
            final String title,
            final String fileName) {
        final ByteArrayActionDownload download = new ByteArrayActionDownload(
                VerdisConstants.DOMAIN,
                EBReportServerAction.TASK_NAME,
                kassenzeichen.getMetaObject().getId(),
                new ServerActionParameter[] {
                    new ServerActionParameter(EBReportServerAction.Parameter.TYPE.toString(), type.toString()),
                    new ServerActionParameter(
                        EBReportServerAction.Parameter.MAP_FORMAT.toString(),
                        mapFormat.toString()),
                    new ServerActionParameter(
                        EBReportServerAction.Parameter.MAP_SCALE.toString(),
                        getSelectedScaleDenominator()),
                    new ServerActionParameter(EBReportServerAction.Parameter.HINTS.toString(), hints),
                    new ServerActionParameter(
                        EBReportServerAction.Parameter.ABLUSSWIRKSAMKEIT.toString(),
                        chkFillAbflusswirksamkeit.isSelected()),
                },
                "",
                title,
                fileName,
                ".pdf",
                getConnectionContext());

        DownloadManager.instance().add(download);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  type       DOCUMENT ME!
     * @param  mapFormat  DOCUMENT ME!
     * @param  hints      DOCUMENT ME!
     * @param  title      DOCUMENT ME!
     * @param  fileName   DOCUMENT ME!
     */
    private void downloadFromGenerator(final EBReportServerAction.Type type,
            final EBReportServerAction.MapFormat mapFormat,
            final String hints,
            final String title,
            final String fileName) {
        final BackgroundTaskDownload.DownloadTask swingWorkerBackgroundTask =
            new BackgroundTaskDownload.DownloadTask() {

                @Override
                public void download(final File fileToSaveTo) throws Exception {
                    try(final FileOutputStream out = new FileOutputStream(fileToSaveTo)) {
                        out.write(
                            EBGenerator.gen(
                                EBGenerator.getProperties(connectionContext),
                                (Integer)kassenzeichen.getProperty(
                                    KassenzeichenPropertyConstants.PROP__KASSENZEICHENNUMMER),
                                type,
                                mapFormat,
                                getSelectedScaleDenominator(),
                                hints,
                                chkFillAbflusswirksamkeit.isSelected(),
                                connectionContext));
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
            };

        DownloadManager.instance()
                .add(new BackgroundTaskDownload(swingWorkerBackgroundTask, "", title, fileName, ".pdf"));
    }
}
