package vibrato.fourier;

import vibrato.complex.ComplexBuffer;

/**
 * This class implements the Inverse Fast Fourier Transform algorithm that transforms signals from their
 * frequency-domain representation (complex frequency samples) to their time-domain representation (complex time
 * samples).
 *
 * The reason the output is also complex is that there is no guarantee that the client code would not supply frequency
 * samples such that: sample(f) = conjugate(sample(-f)).
 */
public class IFFT extends FFT {

    public IFFT(int minimumSamplesCount) {
        super(minimumSamplesCount);
    }

    @Override
    protected double getWAngle(int index, int maxIndex) {
        return -super.getWAngle(index, maxIndex);
    }

    @Override
    protected void processInput(ComplexBuffer.Pointer pointer) {
        double normalizationFactor = 1.0D / frequencySamplesCount();
        for (int i = 0; i < pointer.buffer().size(); i++) {
            pointer.slideTo(i).scale(normalizationFactor);
        }
    }

}
