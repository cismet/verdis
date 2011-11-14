package de.cismet.validation.display;

import de.cismet.validation.Validator;
import de.cismet.validation.ValidatorDisplay;
import de.cismet.validation.ValidatorState;
import de.cismet.validation.validator.AggregatedValidator;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.apache.log4j.Logger;

public class EmbeddedValidatorDisplay extends JLabel
        implements ValidatorDisplay {

    private static final Logger LOG = Logger.getLogger(EmbeddedValidatorDisplay.class);
    private static final int ALPHA_MAX = 255;
    private static final int ALPHA_MIN = 0;
    private static final Map<JComponent, EmbeddedValidatorDisplay> VALIDATOR_MAP = new HashMap();
    private final AggregatedValidator aggValidator = new AggregatedValidator();
    private int alpha = ALPHA_MIN;
    private Thread outFaderThread;
    private ImageIcon valid = new ImageIcon(getClass().getResource("/de/cismet/validation/green.png"));
    private ImageIcon warning = new ImageIcon(getClass().getResource("/de/cismet/validation/orange.png"));
    private ImageIcon error = new ImageIcon(getClass().getResource("/de/cismet/validation/red.png"));

    public static EmbeddedValidatorDisplay getEmbeddedDisplayFor(JComponent component) {
        if (!VALIDATOR_MAP.containsKey(component)) {
            VALIDATOR_MAP.put(component, new EmbeddedValidatorDisplay(component));
        }
        return (EmbeddedValidatorDisplay) VALIDATOR_MAP.get(component);
    }

    private EmbeddedValidatorDisplay(JComponent component) {
        setHorizontalAlignment(RIGHT);
        setVerticalAlignment(TOP);
        setCursor(Cursor.getPredefinedCursor(0));
        setVisible(true);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                ValidatorState state = EmbeddedValidatorDisplay.this.aggValidator.getState();
                if ((state != null)
                        && (state.getHintAction() != null)) {
                    state.getHintAction().actionPerformed(null);
                }
            }
        });
        component.setLayout(new BorderLayout());
        component.add(this, BorderLayout.EAST);

        this.aggValidator.addListener(this);
    }

    @Override
    public void addValidator(Validator validator) {
        this.aggValidator.add(validator);
        stateChanged(validator.getState());
    }

    @Override
    public void removeValidator(Validator validator) {
        this.aggValidator.remove(validator);
        stateChanged(validator.getState());
    }

    @Override
    public Collection<Validator> getValidators() {
        return aggValidator.getValidators();
    }


    @Override
    public final void stateChanged(final ValidatorState state) {
        refresh();
    }

    private void refresh() {
        ValidatorState state = this.aggValidator.getState();

        if (state != null) {
            if ((this.outFaderThread != null) && (this.outFaderThread.isAlive())) {
                this.outFaderThread.interrupt();
            }
            setToolTipText(state.getMessage());
            setAlpha(ALPHA_MIN);
            setVisible(true);
            switch (state.getType()) {
                case ERROR:
                    setIcon(this.error);
                    putClientProperty("state", "ERROR");
                    break;
                case WARNING:
                    setIcon(this.warning);
                    putClientProperty("state", "WARNING");
                    break;
                case VALID:
                    setIcon(this.valid);
                    putClientProperty("state", "VALID");
                    this.outFaderThread = new OutFader();
                    this.outFaderThread.start();
            }
        } else {
            setVisible(false);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Composite oldComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.SrcOver);
        Color c = getParent().getBackground();
        Color color = new Color(c.getRed(), c.getGreen(), c.getBlue(), this.alpha);
        g2.setColor(color);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setComposite(oldComposite);
    }

    public void setAlpha(int alpha) {
        if (alpha < ALPHA_MIN) {
            alpha = ALPHA_MIN;
        }
        if (alpha > ALPHA_MAX) {
            alpha = ALPHA_MAX;
        }
        this.alpha = alpha;
        repaint();
    }

    class OutFader extends Thread {

        private static final int FADE_TIME = 2000;
        private static final int REFRESH_INTERVAL = 50;
        private static final int WAIT_BEFORE_FADEOUT_IN_MS = 2000;

        OutFader() {
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long duration = 0;
            do {
                duration = System.currentTimeMillis() - startTime;
                if (duration > WAIT_BEFORE_FADEOUT_IN_MS) {
                    float factor = (float) (duration - WAIT_BEFORE_FADEOUT_IN_MS) / FADE_TIME;
                    EmbeddedValidatorDisplay.this.setAlpha((int) Math.floor(ALPHA_MAX * factor));
                }
                try {
                    Thread.sleep(REFRESH_INTERVAL);
                } catch (InterruptedException ex) {
                    EmbeddedValidatorDisplay.LOG.debug("error while sleeping", ex);
                    interrupt();
                }
                if (isInterrupted()) {
                    return;
                }
            } while (duration < WAIT_BEFORE_FADEOUT_IN_MS + FADE_TIME);
            EmbeddedValidatorDisplay.this.setAlpha(ALPHA_MAX);
            EmbeddedValidatorDisplay.this.setVisible(false);
            EmbeddedValidatorDisplay.this.getParent().repaint();
        }
    }
}
