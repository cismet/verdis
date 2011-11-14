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
public interface ValidatorState extends Comparable<ValidatorState> {

    public static enum Type {VALID, WARNING, ERROR};

    public Type getType();

    public String getMessage();

    public Action getHintAction();

    public boolean isError();

    public boolean isWarning();

    public boolean isValid();

}
