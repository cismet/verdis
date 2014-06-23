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
import de.cismet.validation.ValidatorListener;
import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class AggregatedValidator extends AbstractValidator implements ValidatorListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AggregatedValidator.class);

    //~ Instance fields --------------------------------------------------------

    private final Collection<Validator> validators = new ArrayList();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Validator> getValidators() {
        return validators;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        // concurrent exception vermeiden dur kopie
        final Collection<Validator> copyOfValidators = new ArrayList<Validator>();
        for (final Validator validator : validators) {
            copyOfValidators.add(validator);
        }
        for (final Validator validator : copyOfValidators) {
            remove(validator);
        }
        copyOfValidators.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   validator  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean add(final Validator validator) {
        validator.validate();
        validator.addListener(this);
        final boolean result = this.validators.add(validator);
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   validator  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean remove(final Validator validator) {
        validator.removeListener(this);
        final boolean result = this.validators.remove(validator);
        return result;
    }

    @Override
    public void stateChanged(final Validator source, final ValidatorState state) {
        validate();
    }

    @Override
    protected ValidatorState performValidation() {
        ValidatorState worstState = null;
        for (final Validator validator : this.validators) {
            final ValidatorState valState = validator.getState();
            if (valState.compareTo(worstState) > 0) {
                worstState = new ValidatorStateImpl(valState.getType(),
                        valState.getMessage(),
                        valState.getHintAction());
            }
        }
        return worstState;
    }
}
