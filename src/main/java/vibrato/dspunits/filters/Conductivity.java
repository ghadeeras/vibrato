package vibrato.dspunits.filters;

import vibrato.oscillators.Operation;
import vibrato.oscillators.State;

public class Conductivity implements Operation, State {

    private final Runnable computation;

    private boolean conductive;

    public Conductivity(Runnable computation) {
        this.computation = computation;
    }

    @Override
    public State state() {
        return this;
    }

    @Override
    public void readPhase() {
    }

    @Override
    public void writePhase() {
        conductive = false;
    }

    public void conduct() {
        if (!conductive) {
            conductive = true;
            computation.run();
        }
    }

}
