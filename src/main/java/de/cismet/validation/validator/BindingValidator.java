package de.cismet.validation.validator;

import de.cismet.validation.ValidatorState;
import de.cismet.validation.ValidatorStateImpl;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;

public class BindingValidator extends AbstractValidator implements BindingListener {

    private static final Logger LOG = Logger.getLogger(BindingValidator.class);
    private Binding binding;
    private Action action = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            getBinding().refreshAndNotify();
        }
    };

    public BindingValidator(Binding binding) {
        setBinding(binding);
    }

    public final void setBinding(Binding binding) {
        if (this.binding != null) {
            this.binding.removeBindingListener(this);
        }
        this.binding = binding;
        if (this.binding != null) {
            this.binding.addBindingListener(this);
        }
    }

    public Binding getBinding() {
        return this.binding;
    }

    @Override
    public void bindingBecameBound(Binding binding) {
    }

    @Override
    public void bindingBecameUnbound(Binding binding) {
        setState(null);
    }

    @Override
    public void syncFailed(Binding binding, Binding.SyncFailure failure) {
        LOG.debug("Fehler bei der Wertkonvertierung: " + failure.toString());
        setState(new ValidatorStateImpl(ValidatorStateImpl.Type.ERROR, "Fehler bei der Wertkonvertierung. Hier klicken um den vorherigen Wert wiederherzustellen.", this.action));
    }

    @Override
    public void syncWarning(Binding binding, Binding.SyncFailure failure) {
        LOG.debug("Fehler bei der Wertkonvertierung: " + failure.toString());
        setState(new ValidatorStateImpl(ValidatorStateImpl.Type.WARNING, "Fehler bei der Wertkonvertierung. Hier klicken um den vorherigen Wert wiederherzustellen.", this.action));
    }

    @Override
    public void synced(Binding binding) {
        setState(null);
    }

    @Override
    public void sourceChanged(Binding binding, PropertyStateEvent event) {
    }

    @Override
    public void targetChanged(Binding binding, PropertyStateEvent event) {
    }

    @Override
    public ValidatorState performValidation() {
        return getState();
    }

}
