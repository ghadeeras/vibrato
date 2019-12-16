package vibrato.dspunits;

import vibrato.oscillators.Operation;
import vibrato.vectors.RealVector;

public interface DspSource<O extends RealVector> extends DspUnit {

    O output();

    static <V extends RealVector> DspSource<V> create(V output, Operation... operations) {
        return new DspSource<V>() {

            @Override
            public V output() {
                return output;
            }

            @Override
            public Operation[] operations() {
                return operations;
            }

        };
    }

}
