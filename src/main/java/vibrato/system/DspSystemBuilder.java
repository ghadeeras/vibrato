package vibrato.system;

import vibrato.dspunits.DspUnit;
import vibrato.dspunits.Wire;
import vibrato.dspunits.filters.iir.SecondOrderFilter;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.dspunits.sources.AudioSource;
import vibrato.dspunits.sources.RandomSource;
import vibrato.dspunits.sources.WaveSource;
import vibrato.functions.DiscreteSignal;
import vibrato.oscillators.Oscillator;
import vibrato.vectors.RealValue;

import javax.sound.sampled.AudioFormat;
import java.io.InputStream;

public class DspSystemBuilder<S extends DspSystem<?>> {

    private final S dspSystem;

    public DspSystemBuilder(S dspSystem) {
        this.dspSystem = dspSystem;
    }

    private <U extends DspUnit> U connect(U unit) {
        return dspSystem.connect(unit);
    }

    protected S dspSystem() {
        return dspSystem;
    }

    protected AudioSource audioSource(AudioFormat format, InputStream inputStream) {
        return connect(new AudioSource(format, inputStream));
    }

    protected WaveSource periodicWaveSource(DiscreteSignal signal, int period) {
        return waveSource(signal, period, true);
    }

    protected WaveSource transientWaveSource(DiscreteSignal signal, int length) {
        return waveSource(signal, length, false);
    }

    private WaveSource waveSource(DiscreteSignal signal, int length, boolean periodic) {
        return connect(new WaveSource(signal, length, periodic));
    }

    protected RandomSource randomSource(Long seed, boolean gaussian) {
        return connect(new RandomSource(seed, gaussian));
    }

    protected SecondOrderFilter secondOrderBPF(RealValue input, int constantGain, double peakFrequency, double bandWidth, double cutOffGain) {
        SecondOrderFilter.Coefficients coefficients = SecondOrderFilter.bpf(constantGain, peakFrequency, bandWidth, cutOffGain);
        return connect(new SecondOrderFilter(input, coefficients));
    }

    protected Wire wire(RealValue input) {
        return connect(new Wire(input));
    }

    protected AudioSink audioSink(RealValue input, AudioFormat format) {
        return connect(new AudioSink(input, format));
    }

    protected RealValue multiplication(RealValue firstInput, RealValue... otherInputs) {
        return wire(() -> {
            double result = firstInput.value();
            for (RealValue input : otherInputs) {
                result *= input.value();
            }
            return result;
        });
    }

    protected RealValue average(RealValue firstInput, RealValue... otherInputs) {
        int count = 1 + otherInputs.length;
        RealValue sum = addition(firstInput, otherInputs);
        return wire(() -> sum.value() / count);
    }

    protected RealValue addition(RealValue firstInput, RealValue... otherInputs) {
        return wire(() -> {
            double result = firstInput.value();
            for (RealValue input : otherInputs) {
                result += input.value();
            }
            return result;
        });
    }

}
