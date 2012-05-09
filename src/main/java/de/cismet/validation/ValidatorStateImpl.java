/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
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
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ValidatorStateImpl implements ValidatorState {

    //~ Instance fields --------------------------------------------------------

    private Type type = Type.NONE;
    private String message;
    private Action hintAction;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ValidatorStateImpl object.
     *
     * @param  type  DOCUMENT ME!
     */
    public ValidatorStateImpl(final Type type) {
        this(type, null, null);
    }

    /**
     * Creates a new ValidatorStateImpl object.
     *
     * @param  type     DOCUMENT ME!
     * @param  message  DOCUMENT ME!
     */
    public ValidatorStateImpl(final Type type, final String message) {
        this(type, message, null);
    }

    /**
     * Creates a new ValidatorStateImpl object.
     *
     * @param  state       DOCUMENT ME!
     * @param  message     DOCUMENT ME!
     * @param  hintAction  DOCUMENT ME!
     */
    public ValidatorStateImpl(final Type state, final String message, final Action hintAction) {
        setType(state);
        setMessage(message);
        setHintAction(hintAction);
    }

    //~ Methods ----------------------------------------------------------------

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

    /**
     * DOCUMENT ME!
     *
     * @param  type  DOCUMENT ME!
     */
    protected final void setType(final Type type) {
        this.type = type;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  message  DOCUMENT ME!
     */
    protected final void setMessage(final String message) {
        this.message = message;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hintAction  DOCUMENT ME!
     */
    protected final void setHintAction(final Action hintAction) {
        this.hintAction = hintAction;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   o  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public int compareTo(final ValidatorState o) {
        if (o == null) {
            return 1;
        }
        return typeToValue(getType()) - typeToValue(o.getType());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   type  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int typeToValue(final Type type) {
        int value = 0;
        switch (type) {
            case VALID: {
                value = 1;
                break;
            }
            case WARNING: {
                value = 2;
                break;
            }
            case ERROR: {
                value = 3;
                break;
            }
        }
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValidatorStateImpl other = (ValidatorStateImpl)obj;
        if (this.type != other.type) {
            return false;
        }
        if ((this.message == null) ? (other.message != null) : (!this.message.equals(other.message))) {
            return false;
        }
        if ((this.hintAction != other.hintAction)
                    && ((this.hintAction == null) || !this.hintAction.equals(other.hintAction))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = (13 * hash) + this.type.hashCode();
        hash = (13 * hash) + ((this.message != null) ? this.message.hashCode() : 0);
        hash = (13 * hash) + ((this.hintAction != null) ? this.hintAction.hashCode() : 0);
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
