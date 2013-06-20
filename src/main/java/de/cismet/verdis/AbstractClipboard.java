/*
 * Copyright (C) 2013 cismet GmbH
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
package de.cismet.verdis;

import de.cismet.cids.custom.util.CidsBeanSupport;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.verdis.gui.AbstractCidsBeanTable;
import de.cismet.verdis.gui.Main;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author gbaatz
 */
public abstract class AbstractClipboard {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractClipboard.class);
    private AbstractCidsBeanTable table;
    private Collection<CidsBean> clipboardBeans = new ArrayList<CidsBean>();
    private List<ClipboardListener> listeners = new ArrayList<ClipboardListener>();
    private boolean isCutted = false;

    public AbstractClipboard(AbstractCidsBeanTable table) {
        this.table = table;
    }

    /**
     * DOCUMENT ME!
     *
     * @param listener DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean addListener(final ClipboardListener listener) {
        return listeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param listener DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean removeListener(final ClipboardListener listener) {
        return listeners.remove(listener);
    }

    /**
     * DOCUMENT ME!
     */
    protected void fireClipboardChanged() {
        for (final ClipboardListener listener : listeners) {
            listener.clipboardChanged();
        }
    }

    public Collection<CidsBean> getClipboardBeans() {
        return clipboardBeans;
    }

    public AbstractCidsBeanTable getTable() {
        return table;
    }

    public void paste() {
        if (isPastable()) {
            try {
                int notPastableCounter = 0;
                final int numOfClipBoardItems = clipboardBeans.size();
                for (final CidsBean clipboardBean : clipboardBeans) {
                    if (isPastable(clipboardBean)) {
                        final CidsBean pasteBean = createPastedBean(clipboardBean);
                        getTable().addBean(pasteBean);
                    } else {
                        notPastableCounter++;
                    }
                }
                clipboardBeans.clear();
                if (notPastableCounter < numOfClipBoardItems) {
                    fireClipboardChanged();
                }
                if (notPastableCounter > 0) {
                    LOG.info(notPastableCounter
                                + " cidsBean(s) not pasted because the cidsinfoBean of this bean(s) was still assigned to a cidsBean of the current kassenzeichen");
                }
            } catch (Exception ex) {
                LOG.error("error while pasting bean", ex);
            }
        }
    }

    public abstract CidsBean createPastedBean(final CidsBean clipboardBean) throws Exception;

    public abstract boolean isPastable(final CidsBean clipboardBean);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isPastable() {
        return !clipboardBeans.isEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isCopyable() {
        return !isSelectionEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isCutable() {
        return !isSelectionEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean isSelectionEmpty() {
        return table.getSelectedBeans().isEmpty();
    }
    
    

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean cutOrCopy(final Collection<CidsBean> cidsBeans) {
        if ((cidsBeans != null) && !cidsBeans.isEmpty()) {
            if (!checkNotPasted()) {
                return false;
            }
            try {
                clipboardBeans.clear();
                for (final CidsBean cidsBean : cidsBeans) {
                    this.clipboardBeans.add(CidsBeanSupport.deepcloneCidsBean(cidsBean));
                }
                fireClipboardChanged();
                return true;
            } catch (Exception ex) {
                LOG.error("error while copying or cutting cidsbean", ex);
                clipboardBeans.clear();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void copy() {
        if (isCopyable()) {
            final Collection<CidsBean> selectedBeans = getSelectedBeans();
            cutOrCopy(selectedBeans);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void cut() {
        if (isCutable()) {
            final Collection<CidsBean> selectedBeans = getSelectedBeans();
            isCutted = cutOrCopy(selectedBeans);
            if (isCutted) {
                for (final CidsBean selectedBean : selectedBeans) {
                    table.removeBean(selectedBean);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> getSelectedBeans() {
        return table.getSelectedBeans();
    }
    
    
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkNotPasted() {
        int answer = JOptionPane.YES_OPTION;
        if (isCutted && (clipboardBeans != null)) {
            answer = JOptionPane.showConfirmDialog(
                    Main.getCurrentInstance(),
                    "In der Verdis-Zwischenablage befinden sich noch Daten die\nausgeschnitten und noch nicht wieder eingef\u00FCgt wurden.\nMöchten Sie diese Daten jetzt verwerfen ?",
                    "Ausschneiden",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
        }
        return answer == JOptionPane.YES_OPTION;
    }
    
    
    /**
     * DOCUMENT ME!
     */
    public void storeToFile() {
//        CismetThreadPool.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    final XStream x = new XStream(new Dom4JDriver());
//                    final FileWriter f = new FileWriter(Main.verdisDirectory + Main.fs + "flaechenClipboardBackup.xml");
//                    x.toXML(clipboardBeans, f);
//                    f.close();
//                } catch (Exception ex) {
//                    LOG.error("Beim Sichern des Clipboards ist etwas schiefgegangen", ex);
//                }
//            }
//        });
    }

    /**
     * DOCUMENT ME!
     */
    public void loadFromFile() {
//        CismetThreadPool.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    final XStream x = new XStream(new Dom4JDriver());
//                    clipboardBeans = (List<CidsBean>) x.fromXML(new FileReader(Main.verdisDirectory + Main.fs + "flaechenClipboardBackup.xml"));
//                    clipboardModus = Modus.COPY;
//                } catch (Exception exception) {
//                    LOG.error("Beim Laden des Flaechen-ClipboardBackups ist etwas schiefgegangen", exception);
//                }
//            }
//        });
    }

    /**
     * DOCUMENT ME!
     */
    public void deleteStoreFile() {
    }
}
