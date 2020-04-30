package vibrato.dspunits.filters.fourier;

import vibrato.complex.ComplexBuffer;
import vibrato.dspunits.DspSink;
import vibrato.dspunits.DspSource;
import vibrato.dspunits.DspUnit;
import vibrato.dspunits.filters.Conductivity;
import vibrato.dspunits.filters.Line;
import vibrato.fourier.FFT;
import vibrato.vectors.RealVector;

public class FastFourierTransformer implements DspSink<RealVector> {

    private RealVector input;

    private final FFT fft;

    private final ComplexBuffer.Pointer outputPointer;
    private final Conductivity conductivity;

    private final DspSource<RealVector> realParts;
    private final DspSource<RealVector> imaginaryParts;
    private final Line lengthParts;
    private final Line angleParts;

    private FastFourierTransformer(int minSpectrumSize) {
        this.fft = new FFT(minSpectrumSize);
        var output = new ComplexBuffer(fft.frequencySamplesCount());

        this.outputPointer = output.pointer();
        this.conductivity = new Conductivity(this::transformation);

        this.realParts = DspSource.create(wrap(output.lengthParts()));
        this.imaginaryParts = DspSource.create(wrap(output.angleParts()));
        this.lengthParts = new Line(wrap(output.lengthParts()));
        this.angleParts = new Line(wrap(output.angleParts()));
    }

    private void transformation() {
        fft.transform(input, outputPointer);
    }

    private RealVector wrap(RealVector parts) {
        return RealVector.window(parts.size(), i -> {
            conductivity.conduct();
            return parts.apply(i);
        });
    }

    @Override
    public DspUnit apply(RealVector input) {
        this.input = input;
        return DspUnit.create(conductivity);
    }

    public DspSource<RealVector> real() {
        return realParts;
    }

    public DspSource<RealVector> imaginary() {
        return imaginaryParts;
    }

    public DspSource<RealVector> length() {
        return lengthParts;
    }

    public DspSource<RealVector> angle() {
        return angleParts;
    }

    public static FastFourierTransformer withMinimumSpectrumSize(int minSize) {
        return new FastFourierTransformer(minSize);
    }

}
