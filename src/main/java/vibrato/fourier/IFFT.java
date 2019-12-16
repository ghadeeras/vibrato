package vibrato.fourier;

import vibrato.complex.ComplexBuffer;

public class IFFT extends FFT {

    public IFFT() {
        this(1024);
    }

    public IFFT(int minimumFrequencySamplesCount) {
        super(minimumFrequencySamplesCount);
    }

    @Override
    protected double getWAngle(int index) {
        return -super.getWAngle(index);
    }

    @Override
    protected void processInput(ComplexBuffer.Pointer pointer) {
        double normalizationFactor = 1.0D / frequencySamplesCount();
        for (int i = 0; i < pointer.buffer().size(); i++) {
            pointer.slideTo(i).mul(normalizationFactor);
        }
    }

}
