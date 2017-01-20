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

import java.util.HashMap;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.CidsBeanDeepPropertyListener;

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
    private String triggerdByProperty;
    private HashMap<String, CidsBeanDeepPropertyListener> cidsBeanFollowerMap =
        new HashMap<String, CidsBeanDeepPropertyListener>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBeanValidator object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  triggerProperties  DOCUMENT ME!
     */
    public CidsBeanValidator(final CidsBean cidsBean, final String... triggerProperties) {
        this.cidsBean = cidsBean;
        for (final String tiggerProperty : triggerProperties) {
            addTriggerProperty(tiggerProperty);
        }
        if (triggerProperties.length > 0) {
            triggerdByProperty = triggerProperties[0];
        }
        validate();
        triggerdByProperty = null;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  property  DOCUMENT ME!
     */
    private void addTriggerProperty(final String property) {
        final CidsBeanDeepPropertyListener follower = new CidsBeanDeepPropertyListener(cidsBean, property);
        cidsBeanFollowerMap.put(property, follower);
        follower.addPropertyChangeListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  property  DOCUMENT ME!
     */
    public final void removeTriggerProperty(final String property) {
        final CidsBeanDeepPropertyListener follower = cidsBeanFollowerMap.remove(property);
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
    protected String getTriggerdByProperty() {
        return triggerdByProperty;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (cidsBeanFollowerMap.containsKey(evt.getPropertyName())) {
            triggerdByProperty = evt.getPropertyName();
            validate();
            triggerdByProperty = null;
        }
    }
}
