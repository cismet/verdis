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
package de.cismet.verdis;

import Sirius.navigator.connection.SessionManager;

import java.sql.Date;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;

import de.cismet.cids.custom.util.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.FlaechePropertyConstants;
import de.cismet.verdis.commons.constants.FlaechenartPropertyConstants;
import de.cismet.verdis.commons.constants.FlaecheninfoPropertyConstants;
import de.cismet.verdis.commons.constants.VerdisConstants;
import de.cismet.verdis.commons.constants.VerdisMetaClassConstants;

import de.cismet.verdis.gui.Main;
import de.cismet.verdis.gui.RegenFlaechenTabellenPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FlaechenClipboard {

    //~ Static fields/initializers ---------------------------------------------

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlaechenClipboard.class);
    private static RegenFlaechenTabellenPanel flaechenTable = Main.getCurrentInstance().getRegenFlaechenTabellenPanel();

    //~ Instance fields --------------------------------------------------------

    private Collection<CidsBean> clipboardFlaecheBeans = new ArrayList<CidsBean>();
    private List<FlaechenClipboardListener> listeners = new ArrayList<FlaechenClipboardListener>();
    private boolean isCutted = false;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   listener  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean addListener(final FlaechenClipboardListener listener) {
        return listeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   listener  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeListener(final FlaechenClipboardListener listener) {
        return listeners.remove(listener);
    }

    /**
     * DOCUMENT ME!
     */
    private void fireClipboardChanged() {
        for (final FlaechenClipboardListener listener : listeners) {
            listener.clipboardChanged();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void paste() {
        if (isPastable()) {
            try {
                int notPastableCounter = 0;
                final int numOfClipBoardItems = clipboardFlaecheBeans.size();
                for (final CidsBean clipboardFlaecheBean : clipboardFlaecheBeans) {
                    if (isPastable(clipboardFlaecheBean)) {
                        final CidsBean pasteBean = createPastedBean(clipboardFlaecheBean);
                        flaechenTable.addBean(pasteBean);
                    } else {
                        notPastableCounter++;
                    }
                }
                clipboardFlaecheBeans.clear();
                if (notPastableCounter < numOfClipBoardItems) {
                    fireClipboardChanged();
                }
                if (notPastableCounter > 0) {
                    LOG.info(notPastableCounter
                                + " flaecheBean(s) not pasted because the flaecheinfoBean of this bean(s) was still assigned to a flaecheBean of the current kassenzeichen");
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
    private CidsBean createPastedBean(final CidsBean clipboardBean) throws Exception {
        final CidsBean pasteBean = flaechenTable.getModel().deepcloneBean(clipboardBean);

        final int id = flaechenTable.getTableHelper().getNextNewBeanId();
        pasteBean.setProperty(FlaechePropertyConstants.PROP__ID, id);
        pasteBean.getMetaObject().setID(id);

        if (clipboardBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO) != null) {
            final int flaecheninfoId = (Integer)clipboardBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__ID);
            final CidsBean flaecheninfoBean = SessionManager.getProxy()
                        .getMetaObject(
                                flaecheninfoId,
                                CidsAppBackend.getInstance().getVerdisMetaClass(
                                    VerdisMetaClassConstants.MC_FLAECHENINFO).getId(),
                                VerdisConstants.DOMAIN)
                        .getBean();
            pasteBean.setProperty(FlaechePropertyConstants.PROP__FLAECHENINFO, flaecheninfoBean);
        }

        pasteBean.setProperty(FlaechePropertyConstants.PROP__BEMERKUNG, null);
        pasteBean.setProperty(
            FlaechePropertyConstants.PROP__FLAECHENBEZEICHNUNG,
            flaechenTable.getValidFlaechenname(
                (Integer)clipboardBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__FLAECHENART
                            + "."
                            + FlaechenartPropertyConstants.PROP__ID)));
        final Calendar cal = Calendar.getInstance();
        pasteBean.setProperty(FlaechePropertyConstants.PROP__DATUM_ERFASSUNG, new Date(cal.getTime().getTime()));
        cal.add(Calendar.MONTH, 1);
        final SimpleDateFormat vDat = new SimpleDateFormat("yy/MM");
        pasteBean.setProperty(FlaechePropertyConstants.PROP__DATUM_VERANLAGUNG, vDat.format(cal.getTime()));

        return pasteBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   clipboardFlaecheBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isPastable(final CidsBean clipboardFlaecheBean) {
        if (clipboardFlaecheBean == null) {
            return false;
        }

        for (final CidsBean flaecheBean : flaechenTable.getAllBeans()) {
            final int id = (Integer)flaecheBean.getProperty(FlaechePropertyConstants.PROP__FLAECHENINFO + "."
                            + FlaecheninfoPropertyConstants.PROP__ID);
            final int ownId = (Integer)clipboardFlaecheBean.getProperty(
                    FlaechePropertyConstants.PROP__FLAECHENINFO
                            + "."
                            + FlaecheninfoPropertyConstants.PROP__ID);
            if (id == ownId) {
                return false;
            }
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isPastable() {
        return !clipboardFlaecheBeans.isEmpty();
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
        return flaechenTable.getSelectedBeans().isEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean cutOrCopy(final Collection<CidsBean> flaecheBeans) {
        if ((flaecheBeans != null) && !flaecheBeans.isEmpty()) {
            if (!checkNotPasted()) {
                return false;
            }
            try {
                clipboardFlaecheBeans.clear();
                for (final CidsBean flaecheBean : flaecheBeans) {
                    this.clipboardFlaecheBeans.add(CidsBeanSupport.deepcloneCidsBean(flaecheBean));
                }
                fireClipboardChanged();
                return true;
            } catch (Exception ex) {
                LOG.error("error while copying or cutting cidsbean", ex);
                clipboardFlaecheBeans.clear();
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
            final Collection<CidsBean> selectedBeans = getSelectedFlaechenBean();
            cutOrCopy(selectedBeans);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void cut() {
        if (isCutable()) {
            final Collection<CidsBean> selectedBeans = getSelectedFlaechenBean();
            isCutted = cutOrCopy(selectedBeans);
            if (isCutted) {
                for (final CidsBean selectedBean : selectedBeans) {
                    flaechenTable.removeBean(selectedBean);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> getSelectedFlaechenBean() {
        return flaechenTable.getSelectedBeans();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkNotPasted() {
        int answer = JOptionPane.YES_OPTION;
        if (isCutted && (clipboardFlaecheBeans != null)) {
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
