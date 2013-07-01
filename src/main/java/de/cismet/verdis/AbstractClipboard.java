/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;

import de.cismet.cids.custom.util.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.gui.Main;

import de.cismet.verdis.interfaces.CidsBeanComponent;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractClipboard {

    //~ Static fields/initializers ---------------------------------------------

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractClipboard.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBeanComponent component;
    private Collection<CidsBean> clipboardBeans = new ArrayList<CidsBean>();
    private List<ClipboardListener> listeners = new ArrayList<ClipboardListener>();
    private boolean isCutted = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractClipboard object.
     *
     * @param  component  DOCUMENT ME!
     */
    public AbstractClipboard(final CidsBeanComponent component) {
        this.component = component;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   listener  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean addListener(final ClipboardListener listener) {
        return listeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   listener  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CidsBean> getClipboardBeans() {
        return clipboardBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBeanComponent getComponent() {
        return component;
    }

    /**
     * DOCUMENT ME!
     */
    public void paste() {
        if (isPastable()) {
            try {
                int notPastableCounter = 0;
                final int numOfClipBoardItems = clipboardBeans.size();
                final ArrayList<CidsBean> removedBeans = new ArrayList<CidsBean>();

                for (final CidsBean clipboardBean : clipboardBeans) {
                    if (isPastable(clipboardBean)) {
                        final CidsBean pasteBean = createPastedBean(clipboardBean);
                        getComponent().addBean(pasteBean);
                        removedBeans.add(clipboardBean);
                    } else {
                        notPastableCounter++;
                    }
                }

                clipboardBeans.removeAll(removedBeans);
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

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public abstract CidsBean createPastedBean(final CidsBean clipboardBean) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract boolean isPastable(final CidsBean clipboardBean);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isPastable() {
        return !clipboardBeans.isEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isCopyable() {
        return !isSelectionEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isCutable() {
        return !isSelectionEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isSelectionEmpty() {
        return component.getSelectedBeans().isEmpty();
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
                    component.removeBean(selectedBean);
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
        return component.getSelectedBeans();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkNotPasted() {
        int answer = JOptionPane.YES_OPTION;
        if (isCutted && clipboardBeans != null && !clipboardBeans.isEmpty()) {
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
