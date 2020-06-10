package vibrato.vectors;

import vibrato.complex.ComplexBuffer;
import vibrato.fourier.FFT;
import vibrato.fourier.IFFT;
import vibrato.functions.Linear;
import vibrato.functions.RealFunction;
import vibrato.interpolators.Interpolator;
import vibrato.utils.DspUtils;

import java.util.stream.DoubleStream;

public interface WaveTable {

    int size();

    double sample(double phase, double samplingIncrement, Interpolator interpolator);

    static double[] unBias(double[] wave) {
        double average = DoubleStream.of(wave).average().orElse(0);
        return DoubleStream.of(wave).map(v -> v - average).toArray();
    }

    static double[] dynamicRange(double min, double max, double[] wave) {
        double mn = DoubleStream.of(wave).min().orElse(-1);
        double mx = DoubleStream.of(wave).max().orElse(+1);
        RealFunction f = Linear.linear(mn, min, mx, max);
        return DoubleStream.of(wave).map(f::apply).toArray();
    }

    class AntiAliased implements WaveTable {

        private final CircularBuffer[] waves;

        public AntiAliased(double... wave) {
            int levels = DspUtils.bitCount(wave.length - 1);
            waves = new CircularBuffer[levels - 1];

            double[] baseWave = baseWave(wave, levels);
            waves[0] = new CircularBuffer(baseWave);

            ComplexBuffer spectrum = FFT.fft(waves[0]);
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
                waves[i] = new CircularBuffer(complexWave.realParts().asSignal().amplify(amplification).samples(waveSize, 0, stride));
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
            return interpolator.value(waves[level], phase / (1 << level));
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

        public Simple(double... wave) {
            this.wave = new CircularBuffer(wave);
        }

        @Override
        public int size() {
            return wave.size();
        }

        @Override
        public double sample(double phase, double samplingIncrement, Interpolator interpolator) {
            return interpolator.value(wave, phase);
        }

    }

}
