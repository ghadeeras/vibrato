package vibrato.system;

import vibrato.dspunits.DspUnit;
import vibrato.dspunits.Wire;
import vibrato.dspunits.filters.iir.SecondOrderFilter;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.dspunits.sources.AudioSource;
import vibrato.dspunits.sources.RandomSource;
import vibrato.dspunits.sources.WaveSource;
import vibrato.functions.DiscreteSignal;
import vibrato.oscillators.MasterOscillator;
import vibrato.oscillators.Oscillator;
import vibrato.vectors.RealValue;

import javax.sound.sampled.AudioFormat;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DspSystem {

    private final MasterOscillator masterOscillator = new MasterOscillator(this);
    private final Map<Oscillator.State, List<Oscillator>> stateToOscillators = new HashMap<>();

    protected final double masterClockSpeed;
    protected final double zHertz;
    protected final double zSecond;

    public DspSystem(double masterClockSpeed) {
        this.masterClockSpeed = masterClockSpeed;
        this.zHertz = 2 * Math.PI / masterClockSpeed;
        this.zSecond = 1 / zHertz;
    }

    protected final double clockSpeed(Oscillator oscillator) {
        return masterClockSpeed / oscillator.absolutePeriod();
    }

    protected final double zHertz(Oscillator oscillator) {
        return zHertz * oscillator.absolutePeriod();
    }

    protected final double zSecond(Oscillator oscillator) {
        return zSecond / oscillator.absolutePeriod();
    }

    public MasterOscillator masterOscillator() {
        return masterOscillator;
    }

    public void declareConnection(Oscillator.State state, Oscillator oscillator) {
        List<Oscillator> oscillators = stateToOscillators.computeIfAbsent(state, s -> new LinkedList<>());
        oscillators.stream().filter(oscillator::conflictsWith).findFirst().ifPresent(conflictingOscillator -> {
            throw new RuntimeException("Oscillators conflict: " + oscillator + " and " + conflictingOscillator);
        });
        oscillators.add(oscillator);
    }

    public <U extends DspUnit> U connect(U unit, Oscillator oscillator) {
        unit.connectTo(oscillator);
        return unit;
    }

    public <U extends DspUnit> U connect(U unit) {
        return connect(unit, masterOscillator);
    }

    protected AudioSource audioSource(AudioFormat format, InputStream inputStream) {
        return connect(new AudioSource(format, inputStream));
    }

    protected WaveSource periodicWaveSource(DiscreteSignal signal, int period) {
        return waveSource(signal, period, true);
    }

    protected WaveSource transientWaveSource(DiscreteSignal signal, int length) {
        return waveSource(signal, length, true);
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
