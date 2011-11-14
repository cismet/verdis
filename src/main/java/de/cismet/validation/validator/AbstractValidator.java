package de.cismet.validation.validator;

import de.cismet.validation.Validator;
import de.cismet.validation.ValidatorDisplay;
import de.cismet.validation.ValidatorListener;
import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Logger;

public abstract class AbstractValidator
        implements Validator {

    private static final Logger LOG = Logger.getLogger(AbstractValidator.class);
    private ValidatorState state = new ValidatorStateImpl(ValidatorState.Type.VALID, "", null);
    private Collection<ValidatorListener> listeners = new ArrayList();

    @Override
    public ValidatorState getState() {
        return this.state;
    }

    protected void setState(final ValidatorState state) {
        boolean hasChanged;
        if ((this.state == null) && (state == null)) {
            hasChanged = false;
        } else {
            hasChanged = ((this.state == null) && (state != null)) || ((this.state != null) && (state == null)) || (!state.equals(this.state));
        }

        if (hasChanged) {
            this.state = state;
            fireStateChanged(state);
        }
    }

    @Override
    public boolean addListener(ValidatorListener listener) {
        return this.listeners.add(listener);
    }

    @Override
    public boolean removeListener(ValidatorListener listener) {
        return this.listeners.remove(listener);
    }

    protected void fireStateChanged(final ValidatorState state) {
        LOG.debug("fire state changed");
        for (ValidatorListener listener : this.listeners) {
            listener.stateChanged(state);
        }
    }

    @Override
    public void attachDisplay(ValidatorDisplay display) {
        display.addValidator(this);
    }

    @Override
    public void validate() {
        setState(performValidation());
    }

    protected abstract ValidatorState performValidation();

}
