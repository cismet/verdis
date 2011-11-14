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

package de.cismet.cids.custom.util;

import de.cismet.validation.Validator;
import de.cismet.validation.display.EmbeddedValidatorDisplay;
import de.cismet.validation.validator.AggregatedValidator;
import de.cismet.validation.validator.BindingValidator;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Binding;

/**
 *
 * @author jruiz
 */
public class BindingValidationSupport {

    public static Validator attachBindingValidationToAllTargets(BindingGroup bindingGroup) {
        final AggregatedValidator validators = new AggregatedValidator();
        for (final Binding binding : bindingGroup.getBindings()) {
            final JComponent targetComponent = (JComponent) binding.getTargetObject();
            // nur jtextfields die keine strings representieren
            if(targetComponent instanceof JTextField) {
                final Validator validator = new BindingValidator(binding);
                validator.attachDisplay(EmbeddedValidatorDisplay.getEmbeddedDisplayFor(targetComponent));
                validators.add(validator);
            }
        }
        return validators;
    }
}
