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

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;

import java.awt.Frame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.jasperreports.ReportHelper;
import de.cismet.cids.utils.jasperreports.ReportSwingWorkerDialog;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.ByteArrayDownload;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class EBGeneratorDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(EBGeneratorDialog.class);
    private static final String MAP_REPORT =
        "/de/cismet/cids/custom/reports/verdis/<mode>_map<format><orientation>.jasper";
    private static final String A4_FORMAT = "A4";
    private static final String A3_FORMAT = "A3";
    private static final String LANDSCAPE_ORIENTATION = "LS";
    private static final String PORTRAIT_ORIENTATION = "P";

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

    private final Mode mode;
    private CidsBean kassenzeichen;
    private Frame parent;
    private String title = "";

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.ButtonGroup btnGroupFormat;
    private javax.swing.ButtonGroup btnGroupOrientation;
    private javax.swing.JButton btnPrint;
    private javax.swing.JComboBox cbScale;
    private javax.swing.JCheckBox chkFillAbflusswirksamkeit;
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
        pack();
    }

    //~ Methods ----------------------------------------------------------------

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
        pnlOrientation = new javax.swing.JPanel();
        rbPortraitMode = new javax.swing.JRadioButton();
        rbLandscapeMode = new javax.swing.JRadioButton();
        lblFiller3 = new javax.swing.JLabel();
        lblScale = new javax.swing.JLabel();
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
        rbA4.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            rbA4,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.rbA4.text")); // NOI18N
        rbA4.setActionCommand(org.openide.util.NbBundle.getMessage(
                EBGeneratorDialog.class,
                "EBGeneratorDialog.rbA4.actionCommand"));                                                  // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlFormat.add(rbA3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFiller2,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.lblFiller2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlFormat.add(lblFiller2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(pnlFormat, gridBagConstraints);

        pnlOrientation.setLayout(new java.awt.GridBagLayout());

        btnGroupOrientation.add(rbPortraitMode);
        rbPortraitMode.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            rbPortraitMode,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.rbPortraitMode.text")); // NOI18N
        rbPortraitMode.setActionCommand(org.openide.util.NbBundle.getMessage(
                EBGeneratorDialog.class,
                "EBGeneratorDialog.rbPortraitMode.actionCommand"));                                                  // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlOrientation.add(rbPortraitMode, gridBagConstraints);

        btnGroupOrientation.add(rbLandscapeMode);
        org.openide.awt.Mnemonics.setLocalizedText(
            rbLandscapeMode,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.rbLandscapeMode.text")); // NOI18N
        rbLandscapeMode.setActionCommand(org.openide.util.NbBundle.getMessage(
                EBGeneratorDialog.class,
                "EBGeneratorDialog.rbLandscapeMode.actionCommand"));                                                  // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlOrientation.add(rbLandscapeMode, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFiller3,
            org.openide.util.NbBundle.getMessage(EBGeneratorDialog.class, "EBGeneratorDialog.lblFiller3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlOrientation.add(lblFiller3, gridBagConstraints);

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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        getContentPane().add(lblScale, gridBagConstraints);

        cbScale.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "optimal angepasst", "1:200", "1:300", "1:400", "1:500", "1:750", "1:1000" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(cbScale, gridBagConstraints);

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
        generateReport(taHinweise.getText());
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
     * @param  hints  DOCUMENT ME!
     */
    private void generateReport(final String hints) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("starting report generation for feb report");
        }

        final SwingWorker<Boolean, Object> worker = new SwingWorker<Boolean, Object>() {

                private final ReportSwingWorkerDialog dialog = new ReportSwingWorkerDialog((Frame)parent, true);
                private boolean forceQuit = false;

                @Override
                protected Boolean doInBackground() throws Exception {
                    SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                StaticSwingTools.showDialog(dialog);
                            }
                        });
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("generating report beans");
                    }
                    String repMap = "";
                    String mapHeightPropkey = "FEPGeneratorDialog.mapHeight";
                    String mapWidthPropkey = "FEPGeneratorDialog.mapWidth";
                    if (btnGroupFormat.getSelection().getActionCommand().equals(rbA4.getActionCommand())) {
                        repMap = MAP_REPORT.replace("<format>", A4_FORMAT);
                        mapHeightPropkey += A4_FORMAT;
                        mapWidthPropkey += A4_FORMAT;
                    } else {
                        repMap = MAP_REPORT.replace("<format>", A3_FORMAT);
                        mapHeightPropkey += A3_FORMAT;
                        mapWidthPropkey += A3_FORMAT;
                    }

                    if (btnGroupOrientation.getSelection().getActionCommand().equals(
                                    rbLandscapeMode.getActionCommand())) {
                        repMap = repMap.replace("<orientation>", LANDSCAPE_ORIENTATION);
                        mapHeightPropkey += LANDSCAPE_ORIENTATION;
                        mapWidthPropkey += LANDSCAPE_ORIENTATION;
                    } else {
                        repMap = repMap.replace("<orientation>", PORTRAIT_ORIENTATION);
                        mapHeightPropkey += PORTRAIT_ORIENTATION;
                        mapWidthPropkey += PORTRAIT_ORIENTATION;
                    }
                    if (Mode.FRONTEN.equals(mode)) {
                        repMap = repMap.replace("<mode>", "fronten");
                    } else {
                        repMap = repMap.replace("<mode>", "feb");
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Report File for Map: " + repMap);
                    }
                    final int mapWidth = Integer.parseInt(NbBundle.getMessage(
                                EBGeneratorDialog.class,
                                mapWidthPropkey));
                    final int mapHeight = Integer.parseInt(NbBundle.getMessage(
                                EBGeneratorDialog.class,
                                mapHeightPropkey));

                    final EBReportBean reportBean;
                    if (Mode.FRONTEN.equals(mode)) {
                        reportBean = new FrontenReportBean(
                                kassenzeichen,
                                mapHeight,
                                mapWidth,
                                getSelectedScaleDenominator());
                    } else {
                        reportBean = new FlaechenReportBean(
                                kassenzeichen,
                                hints,
                                mapHeight,
                                mapWidth,
                                getSelectedScaleDenominator(),
                                chkFillAbflusswirksamkeit.isSelected());
                    }
                    final Collection<EBReportBean> reportBeans = new LinkedList<EBReportBean>();
                    reportBeans.add(reportBean);
                    boolean ready;

                    do {
                        ready = true;
                        for (final EBReportBean rb : reportBeans) {
                            if (!rb.isReadyToProceed() || forceQuit) {
                                ready = false;
                                break;
                            }
                        }
                    } while (!ready);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("ready to procced");
                    }
                    final HashMap parameters = new HashMap();
                    parameters.put("fillKanal", reportBean.isFillAbflusswirksamkeit());

                    final ArrayList<String> reports = new ArrayList<String>();

                    reports.add(repMap);
                    if (Mode.FLAECHEN.equals(mode)) {
                        reports.add("/de/cismet/cids/custom/reports/verdis/feb_flaechen.jasper");
                    }

                    final List<InputStream> ins = new ArrayList<InputStream>();
                    for (final String report : reports) {
                        final JasperReport jasperReport = (JasperReport)JRLoader.loadObject(EBGeneratorDialog.class
                                        .getResourceAsStream(report));

                        final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportBeans);
                        // print aus report und daten erzeugen
                        final JasperPrint jasperPrint = JasperFillManager.fillReport(
                                jasperReport,
                                parameters,
                                dataSource);
                        jasperPrint.setOrientation(jasperReport.getOrientation());

                        final ByteArrayOutputStream outTmp = new ByteArrayOutputStream();
                        JasperExportManager.exportReportToPdfStream(jasperPrint, outTmp);
                        ins.add(new ByteArrayInputStream(outTmp.toByteArray()));
                        outTmp.close();
                    }
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ReportHelper.concatPDFs(ins, out, false);
                    // zusammengefügten pdfStream in Datei schreiben

                    if (DownloadManagerDialog.showAskingForUserTitle(parent)) {
                        final String jobname = DownloadManagerDialog.getJobname();

                        DownloadManager.instance()
                                .add(new ByteArrayDownload(out.toByteArray(), "", jobname, "feb_report", ".pdf"));
                    }

                    return true;
                }

                @Override
                protected void done() {
                    boolean error = false;
                    try {
                        error = !get();
                    } catch (InterruptedException ex) {
                        // unterbrochen, nichts tun
                    } catch (ExecutionException ex) {
                        error = true;
                        LOG.error("error while generating report", ex);
                    }
                    dialog.setVisible(false);
                    if (error) {
                        final ErrorInfo ei = new ErrorInfo(
                                "Error",                           // NOI18N
                                "Error during report generation.", // NOI18N
                                null,
                                null,
                                null,
                                Level.ALL,
                                null);
                        JXErrorPane.showDialog(parent, ei);
                    }
                }
            };
        worker.execute();
    }
}
