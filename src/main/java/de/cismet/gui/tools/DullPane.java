/*
 * DullPane.java
 *
 * Created on 8. Januar 2005, 14:40
 */

package de.cismet.gui.tools;

/**
 * Halbdurchsichtiges JPanel
 * @author hell
 */
public class DullPane extends javax.swing.JPanel {
    java.awt.Color color;
    /** Creates a new instance of DullPane */
    public DullPane() {
        color = new java.awt.Color(220, 220, 220, 200);
        setOpaque(false);
    }
    protected void paintComponent(java.awt.Graphics g)
    {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        g2.setPaint(color);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
