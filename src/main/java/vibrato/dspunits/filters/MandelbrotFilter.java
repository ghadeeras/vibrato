package vibrato.dspunits.filters;

import vibrato.complex.ComplexNumber;
import vibrato.dspunits.DspFilter;
import vibrato.dspunits.DspSource;
import vibrato.dspunits.DspUnit;
import vibrato.oscillators.Operation;
import vibrato.oscillators.State;
import vibrato.vectors.RealVector;

public class MandelbrotFilter implements State, DspSource<RealVector> {

    private final RealVector input;

    private final ComplexNumber half = ComplexNumber.createRI(.5, 0);
    private final ComplexNumber c = ComplexNumber.createRI(0, 0);
    private final ComplexNumber z = ComplexNumber.createRI(0, 0);
    private final RealVector output = RealVector.window(2, i -> i == 0 ? z.real() / 2: z.imaginary() / 2);
    private final NextZ nextZ = new NextZ();

    private MandelbrotFilter(RealVector input) {
        this.input = input;
    }

    @Override
    public RealVector output() {
        return output;
    }

    @Override
    public Operation[] operations() {
        return DspUnit.ops(nextZ);
    }

    private class NextZ implements Operation {

        @Override
        public State state() {
            return MandelbrotFilter.this;
        }

        @Override
        public void readPhase() {
            c.setRI(input.value(0), input.value(1));
        }

        @Override
        public void writePhase() {
            z.mul(z).add(c).minus(half);
            double l = z.length();
            if (l > 2) {
                z.scale(1 / l);
            }
        }

    }

    public static DspFilter<RealVector, RealVector> create() {
        return MandelbrotFilter::new;
    }

}
