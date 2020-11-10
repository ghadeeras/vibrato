package vibrato.music.synthesis.generators;

import vibrato.complex.ComplexBuffer;
import vibrato.fourier.FFT;
import vibrato.fourier.IFFT;
import vibrato.functions.RealFunction;
import vibrato.interpolators.Interpolator;
import vibrato.utils.DspUtils;
import vibrato.vectors.CircularBuffer;

import java.util.stream.Stream;

public interface WaveTable {

    int size();

    double sample(double phase, double samplingIncrement, Interpolator interpolator);

    WaveTable withCachedInterpolation(Interpolator interpolator);

    WaveTable withoutCachedInterpolation();

    double[] baseWave();

    default WaveTable antiAliased() {
        return new AntiAliased(baseWave());
    }

    static WaveTable create(double... wave) {
        return new Simple(wave);
    }

    class AntiAliased implements WaveTable {

        private final WaveTable[] waves;

        private AntiAliased(WaveTable[] waves) {
            this.waves = waves;
        }

        private AntiAliased(double... wave) {
            int levels = DspUtils.bitCount(wave.length - 1);
            waves = new Simple[levels - 1];

            double[] baseWave = baseWave(wave, levels);
            waves[0] = new Simple(baseWave);

            ComplexBuffer spectrum = FFT.fft(baseWave);
            double power = power(spectrum);

            IFFT ifft = new IFFT(baseWave.length);
            ComplexBuffer complexWave = new ComplexBuffer(baseWave.length);

            int waveSize = baseWave.length;
            int stride = 1;
            double remainingPower = power;
            for (int i = 1; i < waves.length; i++) {
                waveSize >>= 1;
                stride <<= 1;
                remainingPower -= lpf(spectrum, waveSize);
                ifft.transform(spectrum.pointer(), complexWave.pointer());
                double amplification = remainingPower > 0 ? Math.sqrt(power / remainingPower) : 1;
                waves[i] = new Simple(complexWave.realParts().asSignal().amplify(amplification).samples(waveSize, 0, stride));
            }
        }

        public double[] baseWave(double[] wave, int levels) {
            int baseWaveSize = 1 << levels;
            return Interpolator.cubic.resample(wave, baseWaveSize);
        }

        public double power(ComplexBuffer spectrum) {
            double power = 0;
            ComplexBuffer.Pointer pointer = spectrum.pointer();
            for (int i = 0; i < spectrum.size(); i++) {
                power += pointer.slideTo(i).lengthSquared();
            }
            return power;
        }

        private double lpf(ComplexBuffer waveFFT, int waveSize) {
            double power = 0;
            ComplexBuffer.Pointer waveFFTPointer = waveFFT.pointer();
            for (int i = waveSize / 2; i < waveSize; i++) {
                power += waveFFTPointer.slideTo(i).lengthSquared();
                waveFFTPointer.setRI(0, 0);
                power += waveFFTPointer.slideTo(waveFFT.size() - i).lengthSquared();
                waveFFTPointer.setRI(0, 0);
            }
            return power;
        }

        @Override
        public int size() {
            return waves[0].size();
        }

        @Override
        public double sample(double phase, double samplingIncrement, Interpolator interpolator) {
            int level = level(samplingIncrement);
            return waves[level].sample(phase / (1 << level), 1, interpolator);
        }

        @Override
        public WaveTable withCachedInterpolation(Interpolator interpolator) {
            WaveTable[] waves = Stream.of(this.waves)
                .map(waveTable -> waveTable.withCachedInterpolation(interpolator))
                .toArray(WaveTable[]::new);
            return new AntiAliased(waves);
        }

        @Override
        public WaveTable withoutCachedInterpolation() {
            WaveTable[] waves = Stream.of(this.waves)
                .map(WaveTable::withoutCachedInterpolation)
                .toArray(WaveTable[]::new);
            return new AntiAliased(waves);
        }

        @Override
        public double[] baseWave() {
            return waves[0].baseWave();
        }

        private int level(double samplingIncrement) {
            int level = 0;
            for (double increment = samplingIncrement; increment > 1 && level < waves.length - 1; increment /= 2) {
                level++;
            }
            return level;
        }

    }

    class Simple implements WaveTable {

        private final CircularBuffer wave;

        private Simple(double... wave) {
            this(new CircularBuffer(wave));
        }

        private Simple(CircularBuffer wave) {
            this.wave = wave;
        }

        @Override
        public int size() {
            return wave.size();
        }

        @Override
        public double sample(double phase, double samplingIncrement, Interpolator interpolator) {
            return interpolator.value(wave, phase);
        }

        @Override
        public WaveTable withCachedInterpolation(Interpolator interpolator) {
            final RealFunction[] interpolations = new RealFunction[size()];
            for (int i = 0; i < interpolations.length; i++) {
                interpolations[i] = interpolator.asFunction(wave, i);
            }
            return new WithCachedInterpolatorWaveTable(interpolations);
        }

        @Override
        public WaveTable withoutCachedInterpolation() {
            return this;
        }

        @Override
        public double[] baseWave() {
            return wave.asSignal().samples(size(), 0, 1);
        }

        private static class WithCachedInterpolatorWaveTable implements WaveTable {

            private final RealFunction[] interpolations;

            private WithCachedInterpolatorWaveTable(RealFunction[] interpolations) {
                this.interpolations = interpolations;
            }

            @Override
            public int size() {
                return interpolations.length;
            }

            @Override
            public double sample(double phase, double samplingIncrement, Interpolator interpolator) {
                double index = Math.floor(phase);
                double fraction = phase - index;
                int i = (int) index % interpolations.length;
                return interpolations[i >= 0 ? i : i + interpolations.length].apply(fraction);
            }

            @Override
            public WaveTable withCachedInterpolation(Interpolator otherInterpolator) {
                return withoutCachedInterpolation().withCachedInterpolation(otherInterpolator) ;
            }

            @Override
            public WaveTable withoutCachedInterpolation() {
                return new Simple(baseWave());
            }

            @Override
            public double[] baseWave() {
                return Stream.of(interpolations)
                    .mapToDouble(interpolation -> interpolation.apply(0))
                    .toArray();
            }

        }
    }

}
