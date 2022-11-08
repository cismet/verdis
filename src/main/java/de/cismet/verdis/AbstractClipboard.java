/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import de.cismet.cids.custom.util.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.verdis.commons.constants.VerdisConstants;

import de.cismet.verdis.gui.CidsBeanComponent;
import de.cismet.verdis.gui.CidsBeanTable;
import de.cismet.verdis.gui.Main;
import de.cismet.verdis.gui.kassenzeichen_geometrie.KassenzeichenGeometrienList;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractClipboard {

    //~ Static fields/initializers ---------------------------------------------

    private static volatile org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractClipboard.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(DummyClipboard.class, new ClipboardDeserializer());
        module.addSerializer(AbstractClipboard.class, new ClipboardSerializer());
        OBJECT_MAPPER.registerModule(module);
    }

    //~ Instance fields --------------------------------------------------------

    private final File clipboardFile;

    private Integer fromKassenzeichen;
    private final Collection<CidsBean> clipboardBeans = new ArrayList<>();
    private boolean isCutted = false;

    private volatile CidsBeanComponent component;
    private volatile List<ClipboardListener> listeners = new ArrayList<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractClipboard object.
     *
     * @param  component      DOCUMENT ME!
     * @param  clipboardFile  DOCUMENT ME!
     */
    public AbstractClipboard(final CidsBeanComponent component, final File clipboardFile) {
        this.component = component;
        this.clipboardFile = clipboardFile;
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
            listener.clipboardChanged(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public final Collection<CidsBean> getClipboardBeans() {
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
                final ArrayList<CidsBean> removedBeans = new ArrayList<>();
                final ArrayList<CidsBean> pastedBeans = new ArrayList<>();

                for (final CidsBean clipboardBean : clipboardBeans) {
                    if (isPastable(clipboardBean)) {
                        final CidsBean pasteBean = createPastedBean(clipboardBean);
                        getComponent().addBean(pasteBean);
                        pastedBeans.add(pasteBean);
                        removedBeans.add(clipboardBean);
                    } else {
                        notPastableCounter++;
                    }
                }
                clipboardBeans.removeAll(removedBeans);
                if (getComponent() instanceof CidsBeanTable) {
                    ((CidsBeanTable)getComponent()).selectCidsBeans(pastedBeans);
                } else if (getComponent() instanceof KassenzeichenGeometrienList) {
                    ((KassenzeichenGeometrienList)getComponent()).selectCidsBeans(pastedBeans);
                }

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
     * @return  DOCUMENT ME!
     */
    public Integer getFromKassenzeichen() {
        return fromKassenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isCutted() {
        return isCutted;
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
     * @param   cidsBeans      DOCUMENT ME!
     * @param   kassenzeichen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean cutOrCopy(final Collection<CidsBean> cidsBeans, final Integer kassenzeichen) {
        if ((cidsBeans != null) && !cidsBeans.isEmpty()) {
            if (!checkNotPasted()) {
                return false;
            }
            try {
                fromKassenzeichen = kassenzeichen;
                clipboardBeans.clear();
                for (final CidsBean cidsBean : cidsBeans) {
                    this.clipboardBeans.add(CidsBeanSupport.deepcloneCidsBean(cidsBean));
                }
                fireClipboardChanged();
                return true;
            } catch (Exception ex) {
                LOG.error("error while copying or cutting cidsbean", ex);
                fromKassenzeichen = null;
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
            cutOrCopy(
                selectedBeans,
                (Integer)CidsAppBackend.getInstance().getCidsBean().getProperty(
                    VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHENNUMMER));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isCutted  DOCUMENT ME!
     */
    protected final void setIsCutted(final boolean isCutted) {
        this.isCutted = isCutted;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fromKassenzeichen  DOCUMENT ME!
     */
    protected final void setFromKassenzeichen(final Integer fromKassenzeichen) {
        this.fromKassenzeichen = fromKassenzeichen;
    }

    /**
     * DOCUMENT ME!
     */
    public void cut() {
        if (isCutable()) {
            final Collection<CidsBean> selectedBeans = getSelectedBeans();
            isCutted = cutOrCopy(
                    selectedBeans,
                    (Integer)CidsAppBackend.getInstance().getCidsBean().getProperty(
                        VerdisConstants.PROP.KASSENZEICHEN.KASSENZEICHENNUMMER));
            if (isCutted) {
                for (final CidsBean selectedBean : selectedBeans) {
                    component.removeBean(selectedBean);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        clipboardBeans.clear();
        fireClipboardChanged();
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
        if (isCutted && (clipboardBeans != null) && !clipboardBeans.isEmpty()) {
            answer = JOptionPane.showConfirmDialog(
                    Main.getInstance(),
                    "In der Verdis-Zwischenablage befinden sich noch Daten die\nausgeschnitten und noch nicht wieder eingef\u00FCgt wurden.\nMöchten Sie diese Daten jetzt verwerfen ?",
                    "Ausschneiden",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
        }
        return answer == JOptionPane.YES_OPTION;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void loadFromFile() throws Exception {
        getClipboardBeans().clear();
        if (clipboardFile.exists() && (clipboardFile.length() > 0)) {
            final DummyClipboard dummy = OBJECT_MAPPER.readValue(clipboardFile, DummyClipboard.class);
            if (dummy.getClipboardBeans().isEmpty()) {
                setFromKassenzeichen(null);
                setIsCutted(false);
            } else {
                setFromKassenzeichen(dummy.getFromKassenzeichen());
                setIsCutted(dummy.isCutted());
                getClipboardBeans().addAll(dummy.getClipboardBeans());
            }
        } else {
            setFromKassenzeichen(null);
            setIsCutted(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void writeToFile() {
        try {
            if (getClipboardBeans().isEmpty()) {
                if (clipboardFile.exists()) {
                    clipboardFile.delete();
                }
            } else {
                if (!clipboardFile.exists()) {
                    clipboardFile.createNewFile();
                }
                try(final FileWriter writer = new FileWriter(clipboardFile)) {
                    writer.write(toJson());
                    writer.flush();
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class ClipboardSerializer extends StdSerializer<AbstractClipboard> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ClipboardSerializer object.
         */
        public ClipboardSerializer() {
            super(AbstractClipboard.class);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void serialize(final AbstractClipboard clipboard, final JsonGenerator jg, final SerializerProvider sp)
                throws IOException, JsonGenerationException {
            final Integer fromKassenzeichen = clipboard.getFromKassenzeichen();
            final boolean isCutted = clipboard.isCutted();
            final Collection<CidsBean> clipboardBeans = clipboard.getClipboardBeans();

            jg.writeStartObject();
            jg.writeNumberField("fromKassenzeichen", fromKassenzeichen);
            jg.writeBooleanField("isCutted", isCutted);
            if (clipboardBeans instanceof Collection) {
                jg.writeArrayFieldStart("clipboardBeans");
                for (final CidsBean clipboardBean : clipboardBeans) {
                    jg.writeObject(OBJECT_MAPPER.readValue(clipboardBean.toJSONString(false), HashMap.class));
                }
                jg.writeEndArray();
            }
            jg.writeEndObject();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class ClipboardDeserializer extends StdDeserializer<DummyClipboard> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ClipboardDeserializer object.
         */
        public ClipboardDeserializer() {
            super(AbstractClipboard.class);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public DummyClipboard deserialize(final JsonParser jp, final DeserializationContext dc) throws IOException,
            JsonProcessingException {
            try {
                final ObjectNode on = jp.readValueAsTree();
                final Integer fromKassenzeichen = on.get("fromKassenzeichen").asInt();
                if (fromKassenzeichen == null) {
                    throw new RuntimeException("invalid Clipboard: fromKassenzeichen is missing");
                }
                if (!on.has("isCutted")) {
                    throw new RuntimeException("invalid Clipboard: isCutted is missing");
                }
                final boolean isCutted = on.get("isCutted").asBoolean();
                final JsonNode beansNode = on.get("clipboardBeans");
                if (beansNode == null) {
                    throw new RuntimeException("invalid Clipboard: clipboardBeans is missing");
                }

                final Collection<CidsBean> clipboardBeans = new ArrayList<>();
                final Iterator<JsonNode> iterator = beansNode.iterator();
                while (iterator.hasNext()) {
                    final JsonNode beanNode = iterator.next();
                    clipboardBeans.add(CidsBean.createNewCidsBeanFromJSON(false, beanNode.toString()));
                }

                return new DummyClipboard(fromKassenzeichen, isCutted, clipboardBeans);
            } catch (final Exception ex) {
                return null;
            }
        }
    }
}
