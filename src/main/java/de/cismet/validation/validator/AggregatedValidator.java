package de.cismet.validation.validator;

import de.cismet.validation.Validator;
import de.cismet.validation.ValidatorListener;
import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Logger;

public class AggregatedValidator extends AbstractValidator implements ValidatorListener {

    private static final Logger LOG = Logger.getLogger(AggregatedValidator.class);
    
    private final Collection<Validator> validators = new ArrayList();

    public Collection<Validator> getValidators() {
        return validators;
    }

    public void clear() {
        // concurrent exception vermeiden dur kopie
        Collection<Validator> copyOfValidators = new ArrayList<Validator>();
        for (final Validator validator : validators) {
            copyOfValidators.add(validator);
        }
        for (final Validator validator : copyOfValidators) {
            remove(validator);
        }
        copyOfValidators.clear();
    }

    public boolean add(Validator validator) {
        validator.addListener(this);
        final boolean result = this.validators.add(validator);
        validate();
        return result;
    }

    public boolean remove(Validator validator) {
        validator.removeListener(this);
        final boolean result = this.validators.remove(validator);
        validate();
        return result;
    }

    @Override
    public void stateChanged(final ValidatorState state) {
            validate();
    }

    @Override
    protected ValidatorState performValidation() {
        ValidatorState worstState = null;
        for (Validator validator : this.validators) {
            ValidatorState valState = validator.getState();
            if ((valState != null) && (valState.compareTo(worstState) > 0)) {
                worstState = new ValidatorStateImpl(valState.getType(), valState.getMessage(), valState.getHintAction());
            }
        }
        return worstState;
    }
}
