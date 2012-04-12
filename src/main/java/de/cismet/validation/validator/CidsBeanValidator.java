/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.validation.validator;

import org.apache.log4j.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.HashMap;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public abstract class CidsBeanValidator extends AbstractValidator implements PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(CidsBeanValidator.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private String property;
    private HashMap<String, CidsBeanFollower> cidsBeanFollowerMap = new HashMap<String, CidsBeanFollower>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBeanValidator object.
     *
     * @param  cidsBean  DOCUMENT ME!
     * @param  property  DOCUMENT ME!
     */
    public CidsBeanValidator(final CidsBean cidsBean, final String property) {
        this.cidsBean = cidsBean;
        this.property = property;
        addTriggerProperty(property);
        init();
        validate();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    protected void init() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  property  DOCUMENT ME!
     */
    public final void addTriggerProperty(final String property) {
        final CidsBeanFollower follower = new CidsBeanFollower(cidsBean, property);
        cidsBeanFollowerMap.put(property, follower);
        follower.addPropertyChangeListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  property  DOCUMENT ME!
     */
    public final void removeTriggerProperty(final String property) {
        final CidsBeanFollower follower = cidsBeanFollowerMap.remove(property);
        if (follower != null) {
            follower.removePropertyChangeListener(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProperty() {
        return property;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (cidsBeanFollowerMap.containsKey(evt.getPropertyName())) {
            validate();
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CidsBeanFollower implements PropertyChangeListener {

        //~ Instance fields ----------------------------------------------------

        private CidsBean cidsBean;
        private String property;
        private CidsBean[] followBeans;
        private String[] followProps;
        private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CidsBeanFollower object.
         *
         * @param  cidsBean  DOCUMENT ME!
         * @param  property  DOCUMENT ME!
         */
        public CidsBeanFollower(final CidsBean cidsBean, final String property) {
            this.cidsBean = cidsBean;
            this.property = property;
            refreshFollows();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            // refresh falls zwischenbean sich verändert hat
            for (int index = 0; index < (followBeans.length - 1); index++) {
                final CidsBean oldCidsBean = followBeans[index];
                if (evt.getSource().equals(oldCidsBean) && evt.getPropertyName().equals(followProps[index])) {
                    refreshFollows();
                    final String[] subArray = new String[index + 1];
                    System.arraycopy(followProps, 0, subArray, 0, index + 1);
                    propertyChangeSupport.firePropertyChange(implode(subArray, "."), oldCidsBean, followBeans[index]);
                    return;
                }
            }

            final int lastIndex = followProps.length - 1;
            if ((followProps != null) && (followBeans != null) && evt.getPropertyName().equals(followProps[lastIndex])
                        && evt.getSource().equals(followBeans[lastIndex])) {
                propertyChangeSupport.firePropertyChange(property, evt.getOldValue(), evt.getNewValue());
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param   stringArray  DOCUMENT ME!
         * @param   delimiter    DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private String implode(final String[] stringArray, final String delimiter) {
            if (stringArray.length == 0) {
                return "";
            } else {
                final StringBuilder sb = new StringBuilder();
                sb.append(stringArray[0]);
                for (int index = 1; index < stringArray.length; index++) {
                    sb.append(delimiter);
                    sb.append(stringArray[index]);
                }
                return sb.toString();
            }
        }

        /**
         * Add PropertyChangeListener.
         *
         * @param  listener  DOCUMENT ME!
         */
        public void addPropertyChangeListener(final PropertyChangeListener listener) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }

        /**
         * Remove PropertyChangeListener.
         *
         * @param  listener  DOCUMENT ME!
         */
        public void removePropertyChangeListener(final PropertyChangeListener listener) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }

        /**
         * DOCUMENT ME!
         */
        private void refreshFollows() {
            // erstmal sauber machen
            if (followBeans != null) {
                for (final CidsBean followBean : followBeans) {
                    if (followBean != null) {
                        followBean.removePropertyChangeListener(this);
                    }
                }
            }
            followProps = new String[0];
            followBeans = new CidsBean[0];

            // ist property gesetzt ?
            if (property != null) {
                // property zerstückeln und platz für die entsprechenden beans schaffen
                followProps = property.split("\\.");
                followBeans = new CidsBean[followProps.length];

                // erste bean ist immer die basisbean
                followBeans[0] = cidsBean;
                for (int index = 0; index < followProps.length; index++) {
                    final CidsBean cidsBeanAtIndex = followBeans[index];
                    if (cidsBeanAtIndex != null) {
                        cidsBeanAtIndex.addPropertyChangeListener(this);
                        // nächste bean setzen außer für die letzte property
                        if (index < (followProps.length - 1)) {
                            followBeans[index + 1] = (CidsBean)followBeans[index].getProperty(followProps[index]);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
