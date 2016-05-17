/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.validation.validator;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class BindingValidator extends AbstractValidator implements BindingListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BindingValidator.class);

    //~ Instance fields --------------------------------------------------------

    private Binding binding;
    private Action action = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                getBinding().refreshAndNotify();
            }
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BindingValidator object.
     *
     * @param  binding  DOCUMENT ME!
     */
    public BindingValidator(final Binding binding) {
        setBinding(binding);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  binding  DOCUMENT ME!
     */
    public final void setBinding(final Binding binding) {
        if (this.binding != null) {
            this.binding.removeBindingListener(this);
        }
        this.binding = binding;
        if (this.binding != null) {
            this.binding.addBindingListener(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Binding getBinding() {
        return this.binding;
    }

    @Override
    public void bindingBecameBound(final Binding binding) {
    }

    @Override
    public void bindingBecameUnbound(final Binding binding) {
    }

    @Override
    public void syncFailed(final Binding binding, final Binding.SyncFailure failure) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fehler bei der Wertkonvertierung: " + failure.toString());
        }
        setState(new ValidatorStateImpl(
                ValidatorState.Type.ERROR,
                "Fehler bei der Wertkonvertierung. Hier klicken um den vorherigen Wert wiederherzustellen.",
                this.action));
    }

    @Override
    public void syncWarning(final Binding binding, final Binding.SyncFailure failure) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fehler bei der Wertkonvertierung: " + failure.toString());
        }
        setState(new ValidatorStateImpl(
                ValidatorState.Type.WARNING,
                "Fehler bei der Wertkonvertierung. Hier klicken um den vorherigen Wert wiederherzustellen.",
                this.action));
    }

    @Override
    public void synced(final Binding binding) {
        setState(new ValidatorStateImpl(ValidatorState.Type.VALID));
    }

    @Override
    public void sourceChanged(final Binding binding, final PropertyStateEvent event) {
    }

    @Override
    public void targetChanged(final Binding binding, final PropertyStateEvent event) {
    }

    @Override
    public ValidatorState performValidation() {
        return getState();
    }
}
