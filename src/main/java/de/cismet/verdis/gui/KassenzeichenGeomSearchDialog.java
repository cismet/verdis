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
package de.cismet.verdis.gui;

import de.cismet.verdis.CidsAppBackend;
import de.cismet.verdis.search.ServerSearchCreateSearchGeometryListener;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.DefaultListModel;
import org.apache.log4j.Logger;

/**
 *
 * @author jruiz
 */
public class KassenzeichenGeomSearchDialog extends javax.swing.JDialog implements PropertyChangeListener {

    private static final Logger LOG = Logger.getLogger(KassenzeichenGeomSearchDialog.class);
    
    private static KassenzeichenGeomSearchDialog INSTANCE;
    
    /**
     * Creates new form KassenzeichenGeomSearchDialog
     */
    private KassenzeichenGeomSearchDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();        
    }
                
    public static KassenzeichenGeomSearchDialog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KassenzeichenGeomSearchDialog(Main.getCurrentInstance(), false);
            INSTANCE.setLocationRelativeTo(Main.getCurrentInstance());            
        }
        return INSTANCE;
    }

    
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final DefaultListModel model = (DefaultListModel) lstKassenzeichen.getModel();

        if (evt.getPropertyName().equals(ServerSearchCreateSearchGeometryListener.ACTION_SEARCH_STARTED)) {
            model.removeAllElements();
            model.addElement("Suche wird durchgeführt");        

            lstKassenzeichen.setEnabled(false);            
            
            jProgressBar1.setVisible(true);                    
            jButton1.setVisible(false);
            setVisible(true);
        } else if (evt.getPropertyName().equals(ServerSearchCreateSearchGeometryListener.ACTION_SEARCH_DONE)) {
            model.removeAllElements();
            
            final Collection<Integer> ids = (Collection<Integer>)evt.getNewValue();
            
            jProgressBar1.setVisible(false);                                        
            jButton1.setVisible(true);
                        
            if (ids != null && !ids.isEmpty()) {
                for (final int id : ids) {
                    model.addElement(id);
                }
                lstKassenzeichen.setEnabled(true);
                jButton1.setEnabled(true);
            } else {
                model.addElement("keine Kassenzeichen gefunden");                        
                lstKassenzeichen.setEnabled(false);
                jButton1.setEnabled(false);                   
            }                    
            setVisible(true);
        } else if (evt.getPropertyName().equals(ServerSearchCreateSearchGeometryListener.ACTION_SEARCH_FAILED)) {
            model.removeAllElements();
            final Exception ex = (Exception) evt.getNewValue();
            model.addElement(ex.getMessage());
            LOG.error("error while searching kassenzeichen", ex);
            jButton1.setEnabled(false);
            lstKassenzeichen.setEnabled(false);
            jButton1.setVisible(true);
            jProgressBar1.setVisible(false);                    
            setVisible(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane2 = new javax.swing.JScrollPane();
        lstKassenzeichen = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(KassenzeichenGeomSearchDialog.class, "KassenzeichenGeomSearchDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        lstKassenzeichen.setModel(new DefaultListModel());
        lstKassenzeichen.setEnabled(false);
        lstKassenzeichen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstKassenzeichenMouseClicked(evt);
            }
        });
        lstKassenzeichen.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstKassenzeichenValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lstKassenzeichen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        getContentPane().add(jScrollPane2, gridBagConstraints);

        jButton1.setText(org.openide.util.NbBundle.getMessage(KassenzeichenGeomSearchDialog.class, "KassenzeichenGeomSearchDialog.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(jButton1, gridBagConstraints);

        jProgressBar1.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        getContentPane().add(jProgressBar1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lstKassenzeichenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstKassenzeichenMouseClicked
        if (evt.getClickCount() == 2) {
            if (lstKassenzeichen.getSelectedValue() != null) {
                gotoSelectedKassenzeichen();
            }
        }
    }//GEN-LAST:event_lstKassenzeichenMouseClicked

    private void lstKassenzeichenValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstKassenzeichenValueChanged
        jButton1.setEnabled(!lstKassenzeichen.getSelectionModel().isSelectionEmpty());
    }//GEN-LAST:event_lstKassenzeichenValueChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        gotoSelectedKassenzeichen();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void gotoSelectedKassenzeichen() {
        final int kassenzeichennummer = (Integer) lstKassenzeichen.getSelectedValue();
        Main.getCurrentInstance().getKzPanel().gotoKassenzeichen(Integer.toString(kassenzeichennummer));
    }    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(KassenzeichenGeomSearchDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KassenzeichenGeomSearchDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KassenzeichenGeomSearchDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KassenzeichenGeomSearchDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                KassenzeichenGeomSearchDialog dialog = new KassenzeichenGeomSearchDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList lstKassenzeichen;
    // End of variables declaration//GEN-END:variables
}
