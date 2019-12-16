package vibrato.fourier;

import vibrato.complex.ComplexBuffer;
import vibrato.vectors.RealVector;

public class Conv {

    private final FFT fft;
    private final IFFT ifft;
    private final ComplexBuffer frequencyResponse;
    private final ComplexBuffer.Pointer frequencyResponsePointer;

    public Conv(RealVector impulseResponse, int n) {
        fft = new FFT(n);
        ifft = new IFFT(n);
        frequencyResponse = new ComplexBuffer(n);
        frequencyResponsePointer = frequencyResponse.pointer();
        fft.transform(impulseResponse, frequencyResponsePointer);
    }

    public void convolve(RealVector vector, ComplexBuffer.Pointer outputPointer) {
        fft.transform(vector, outputPointer);
        for (int i = 0; i < frequencyResponse.size(); i++) {
            outputPointer.slideTo(i).mul(frequencyResponsePointer.slideTo(i));
        }
        ifft.transform(outputPointer, outputPointer);
    }

}
