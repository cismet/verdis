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

import javax.swing.Action;

/**
 *
 * @author jruiz
 */
public class ValidatorStateImpl implements ValidatorState {

    private Type type = Type.VALID;
    private String message;
    private Action hintAction;

    public ValidatorStateImpl(final Type state, final String message, final Action hintAction) {
        setType(state);
        setMessage(message);
        setHintAction(hintAction);
    }

    public ValidatorStateImpl(final Type type, final String message) {
        this(type, message, null);
    }

    public ValidatorStateImpl(final Type type) {
       this(type, null, null);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Action getHintAction() {
        return hintAction;
    }

    protected final void setType(final Type type) {
        this.type = type;
    }

    protected final void setMessage(final String message) {
        this.message = message;
    }

    protected final void setHintAction(final Action hintAction) {
        this.hintAction = hintAction;
    }

    @Override
    public int compareTo(ValidatorState o) {
        if (o == null) {
            return 1;
        }
        return typeToValue(getType()) - typeToValue(o.getType());
    }

    private int typeToValue(final Type type) {
        int value = 0;
        switch (type) {
            case VALID:
                value = 1;
                break;
            case WARNING:
                value = 2;
                break;
            case ERROR:
                value = 3;
                break;
        }
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValidatorStateImpl other = (ValidatorStateImpl) obj;
        if (this.type != other.type) {
            return false;
        }
        if ((this.message == null) ? (other.message != null) : !this.message.equals(other.message)) {
            return false;
        }
        if (this.hintAction != other.hintAction && (this.hintAction == null || !this.hintAction.equals(other.hintAction))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + this.type.hashCode();
        hash = 13 * hash + (this.message != null ? this.message.hashCode() : 0);
        hash = 13 * hash + (this.hintAction != null ? this.hintAction.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean isValid() {
        return getType() == ValidatorState.Type.VALID;
    }

    @Override
    public boolean isWarning() {
        return getType() == ValidatorState.Type.WARNING;
    }

    @Override
    public boolean isError() {
        return getType() == ValidatorState.Type.ERROR;
    }

}
