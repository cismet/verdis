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

package de.cismet.validation;

import de.cismet.validation.display.EmbeddedValidatorDisplay;
import de.cismet.validation.validator.BindingValidator;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JComponent;

/**
 *
 * @author jruiz
 */
public class ValidatorHelper {

    public static void removeAllNoBindingValidatorFromDisplay(final JComponent component) {
        final ValidatorDisplay display = EmbeddedValidatorDisplay.getEmbeddedDisplayFor(component);
        final Collection<Validator> validatorsToRemove = new ArrayList<Validator>();
        for (final Validator validator : display.getValidators()) {
            if (! (validator instanceof BindingValidator)) {
                validatorsToRemove.add(validator);
            }
        }
        for (final Validator validator : validatorsToRemove) {
            display.removeValidator(validator);
        }
    }
}
