package vibrato.fourier;

import vibrato.complex.ComplexBuffer;

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
