/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.validation.validator;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.validation.Validator;
import de.cismet.validation.ValidatorDisplay;
import de.cismet.validation.ValidatorListener;
import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public abstract class AbstractValidator implements Validator {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AbstractValidator.class);
    private static final ValidatorState STATE_NONE = new ValidatorStateImpl(ValidatorState.Type.NONE, "", null);

    //~ Instance fields --------------------------------------------------------

    private ValidatorState state = STATE_NONE;
    private final Collection<ValidatorListener> listeners = new ArrayList();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractValidator object.
     */
    public AbstractValidator() {
        setState(STATE_NONE);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ValidatorState getState() {
        return this.state;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  state  DOCUMENT ME!
     */
    protected final void setState(final ValidatorState state) {
        // sicherstellen, dass nicht null gesetzt werden kann
        final ValidatorState setState = (state == null) ? STATE_NONE : state;
        final ValidatorState thisState = (this.state == null) ? STATE_NONE : this.state;

        final boolean hasChanged = (!setState.equals(thisState));

        if (hasChanged) {
            this.state = setState;
            fireStateChanged(setState);
        }
    }

    @Override
    public boolean addListener(final ValidatorListener listener) {
        return this.listeners.add(listener);
    }

    @Override
    public boolean removeListener(final ValidatorListener listener) {
        return this.listeners.remove(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  state  DOCUMENT ME!
     */
    protected void fireStateChanged(final ValidatorState state) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("fire state changed");
        }
        for (final ValidatorListener listener : this.listeners) {
            listener.stateChanged(this, state);
        }
    }

    @Override
    public Validator attachDisplay(final ValidatorDisplay display) {
        display.addValidator(this);
        return this;
    }

    @Override
    public ValidatorState validate() {
        final ValidatorState state = performValidation();
        setState(state);
        return state;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract ValidatorState performValidation();
}
