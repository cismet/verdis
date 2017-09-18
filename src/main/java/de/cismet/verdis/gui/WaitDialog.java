/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import java.awt.Dialog;
import java.awt.Event;
import java.awt.EventQueue;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.verdis.CidsAppBackend;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class WaitDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static WaitDialog INSTANCE;
    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WaitDialog.class);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form LockingDialog.
     */
    private WaitDialog() {
        super(Main.getInstance());
        initComponents();
        setModal(true);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(WaitDialog.class, "WaitDialog.title")); // NOI18N
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jProgressBar1.setMaximum(0);
        jProgressBar1.setBorder(null);
        jProgressBar1.setBorderPainted(false);
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setMaximumSize(new java.awt.Dimension(300, 50));
        jProgressBar1.setMinimumSize(new java.awt.Dimension(300, 50));
        jProgressBar1.setOpaque(false);
        jProgressBar1.setPreferredSize(new java.awt.Dimension(300, 50));
        jProgressBar1.setString(org.openide.util.NbBundle.getMessage(
                WaitDialog.class,
                "WaitDialog.jProgressBar1.string")); // NOI18N
        jProgressBar1.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jProgressBar1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static WaitDialog getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  max  DOCUMENT ME!
     */
    public void startSavingKassenzeichen(final int max) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        startSavingKassenzeichen(max);
                    }
                });
        } else {
            if (max == 1) {
                jProgressBar1.setString("Kassenzeichen wird gespeichert...");
                jProgressBar1.setMaximum(0);
                jProgressBar1.setIndeterminate(true);
            } else {
                jProgressBar1.setString("Kassenzeichen werden gespeichert...");
                jProgressBar1.setMaximum(max);
                jProgressBar1.setIndeterminate(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progress  DOCUMENT ME!
     */
    public void progressSavingKassenzeichen(final int progress) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        progressSavingKassenzeichen(progress);
                    }
                });
        } else {
            jProgressBar1.setValue(progress);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  max  DOCUMENT ME!
     */
    public void startDeletingKassenzeichen(final int max) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        startSavingKassenzeichen(max);
                    }
                });
        } else {
            jProgressBar1.setString("Kassenzeichen wird gelöscht...");
            if (max == 1) {
                jProgressBar1.setMaximum(0);
                jProgressBar1.setIndeterminate(true);
            } else {
                jProgressBar1.setMaximum(max);
                jProgressBar1.setIndeterminate(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progress  DOCUMENT ME!
     */
    public void progressKassenzeichen(final int progress) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        progressSavingKassenzeichen(progress);
                    }
                });
        } else {
            jProgressBar1.setValue(progress);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  max  DOCUMENT ME!
     */
    public void startCreateCrossLinks(final int max) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        startSavingKassenzeichen(max);
                    }
                });
        } else {
            jProgressBar1.setString("Querverweise werden gespeichert...");
            jProgressBar1.setMaximum(max);
            jProgressBar1.setIndeterminate(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progress  DOCUMENT ME!
     */
    public void progressCreateCrossLinks(final int progress) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        progressCreateCrossLinks(progress);
                    }
                });
        } else {
            jProgressBar1.setValue(progress);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  max  DOCUMENT ME!
     */
    public void startLoadingKassenzeichen(final int max) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        startLoadingKassenzeichen(max);
                    }
                });
        } else {
            if (max == 1) {
                jProgressBar1.setString("Kassenzeichen wird geladen...");
                jProgressBar1.setMaximum(0);
                jProgressBar1.setIndeterminate(true);
            } else {
                jProgressBar1.setString("Kassenzeichen werden geladen...");
                jProgressBar1.setMaximum(max);
                jProgressBar1.setIndeterminate(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progress  DOCUMENT ME!
     */
    public void progressLoadingKassenzeichen(final int progress) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        progressLoadingKassenzeichen(progress);
                    }
                });
        } else {
            jProgressBar1.setValue(progress);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void startCheckCrosslinks() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        startCheckCrosslinks();
                    }
                });
        } else {
            jProgressBar1.setString("Querverweise werden abgerufen...");
            jProgressBar1.setMaximum(0);
            jProgressBar1.setIndeterminate(true);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void startSearchDeletedKassenzeichenFromHistory() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        startSearchDeletedKassenzeichenFromHistory();
                    }
                });
        } else {
            jProgressBar1.setString("Gelöschtes Kassenzeichen wird gesucht...");
            jProgressBar1.setMaximum(0);
            jProgressBar1.setIndeterminate(true);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void startCheckLocks() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        startCheckLocks();
                    }
                });
        } else {
            jProgressBar1.setString("vorhandene Sperre werden geprüft...");
            jProgressBar1.setMaximum(0);
            jProgressBar1.setIndeterminate(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lockOrRelease  DOCUMENT ME!
     * @param  max            DOCUMENT ME!
     */
    public void startLockOrRelease(final boolean lockOrRelease, final int max) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        startLockOrRelease(lockOrRelease, max);
                    }
                });
        } else {
            if (lockOrRelease) {
                jProgressBar1.setString("Kassenzeichen werden gesperrt...");
            } else {
                jProgressBar1.setString("Kassenzeichen werden freigegeben...");
            }
            jProgressBar1.setMaximum(max);
            jProgressBar1.setIndeterminate(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progress  DOCUMENT ME!
     */
    public void progressLockOrRelease(final int progress) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        progressLockOrRelease(progress);
                    }
                });
        } else {
            jProgressBar1.setValue(progress);
        }
    }

    @Override
    public void dispose() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        dispose();
                    }
                });
        } else {
            setVisible(false);
        }
    }
    /**
     * DOCUMENT ME!
     */
    public void showDialog() {
        showDialog("bitte warten");
    }
    /**
     * DOCUMENT ME!
     *
     * @param  message  DOCUMENT ME!
     */
    public void showDialog(final String message) {
        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    setLocationRelativeTo(Main.getInstance());
                    jProgressBar1.setString(message);
                    jProgressBar1.setMaximum(0);
                    jProgressBar1.setIndeterminate(true);
                    jProgressBar1.setValue(0);
                    new Thread(new Runnable() {

                            @Override
                            public void run() {
                                WaitDialog.this.setVisible(true);
                            }
                        }).start();
                }
            };
        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final WaitDialog INSTANCE = new WaitDialog();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
