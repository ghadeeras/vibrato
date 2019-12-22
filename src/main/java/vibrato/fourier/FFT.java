package vibrato.fourier;

import vibrato.complex.ComplexBuffer;
import vibrato.complex.ComplexNumber;
import vibrato.utils.DspUtils;
import vibrato.vectors.RealVector;

public class FFT {

    private final int n;
    private final int[] shuffledIndexes;
    private final ComplexNumber[] ws;
    private final ComplexNumber calculationResult = new ComplexNumber();

    private Data input;
    private Data output;

    public FFT() {
        this(1024);
    }

    public FFT(int minimumFrequencySamplesCount) {
        int bitCount = DspUtils.bitCount(minimumFrequencySamplesCount - 1);
        n = 1 << bitCount;
        shuffledIndexes = calculateShuffledIndexes(bitCount, n);
        ws = calculateWs(n);
        input = new Data(n);
        output = new Data(n);
    }

    private int[] calculateShuffledIndexes(int bitCount, int n) {
        int[] shuffledIndexes = new int[n];
        for (int i = 0; i < n; i++) {
            shuffledIndexes[i] = DspUtils.flipBits(i, bitCount);
        }
        return shuffledIndexes;
    }

    private ComplexNumber[] calculateWs(int n) {
        ComplexNumber[] ws = new ComplexNumber[n / 2];
        for (int i = 0; i < ws.length; i++) {
            ws[i] = ComplexNumber.createLA(1, getWAngle(i, ws.length));
        }
        return ws;
    }

    protected double getWAngle(int index, int maxIndex) {
        double ratio = (double) index / (double) maxIndex;
        return -Math.PI * ratio;
    }

    public int frequencySamplesCount() {
        return n;
    }

    public void transform(RealVector vector, ComplexBuffer.Pointer outputPointer) {
        readInput(vector);
        doTransform();
        writeOutput(outputPointer);
    }

    public void transform(ComplexBuffer.Pointer inputPointer, ComplexBuffer.Pointer outputPointer) {
        readInput(inputPointer);
        doTransform();
        writeOutput(outputPointer);
    }

    private void readInput(RealVector vector) {
        int size = vector.size();
        if (size <= n) {
            copy(vector, size);
            resetRemaining(size);
        } else {
            copy(vector, n);
            foldRemaining(vector);
        }
    }

    private void readInput(ComplexBuffer.Pointer pointer) {
        int size = pointer.buffer().size();
        if (size <= n) {
            copy(pointer, size);
            resetRemaining(size);
        } else {
            copy(pointer, n);
            foldRemaining(pointer);
        }
    }

    private void copy(RealVector vector, int count) {
        for (int i = 0; i < count; i++) {
            input.pointer1.slideTo(i).setXY(vector.value(i), 0);
        }
    }

    private void copy(ComplexBuffer.Pointer pointer, int count) {
        for (int i = 0; i < count; i++) {
            input.pointer1.slideTo(i).set(pointer.slideTo(i));
        }
    }

    private void resetRemaining(int from) {
        for (int i = from; i < n; i++) {
            input.pointer1.slideTo(i).setXY(0, 0);
        }
    }

    private void foldRemaining(RealVector vector) {
        int j = 0;
        for (int i = n; i < vector.size(); i++) {
            ComplexBuffer.Pointer outputAtJ = input.pointer1.slideTo(j);
            outputAtJ.setXY(outputAtJ.x() + vector.value(i), 0);
            if (++j < n) continue;
            j = 0;
        }
    }

    private void foldRemaining(ComplexBuffer.Pointer pointer) {
        int j = 0;
        for (int i = n; i < pointer.buffer().size(); i++) {
            input.pointer1.slideTo(j).add(pointer.slideTo(i));
            if (++j < n) continue;
            j = 0;
        }
    }

    private void doTransform() {
        processInput(input.pointer1.slideTo(0));
        shuffle();
        dftMerge();
        processOutput(output.pointer1.slideTo(0));
    }

    private void shuffle() {
        for (int i = 0; i < n; i++) {
            int j = shuffledIndexes[i];
            if (j < i) {
                continue;
            }
            if (i != j) {
                setIndexes(i, j);
                output.pointer1.set(input.pointer2);
                output.pointer2.set(input.pointer1);
            } else {
                output.pointer1.slideTo(i).set(input.pointer1.slideTo(i));
            }
        }
    }

    private void dftMerge() {
        int count = 2;
        for (int step = ws.length; step > 0; step >>>= 1) {
            swapData();
            dftMerge(count, step);
            count <<= 1;
        }
    }

    private void dftMerge(int count, int step) {
        for (int i = 0; i < n; i += count) {
            dftMerge(i, count, step);
        }
    }

    private void dftMerge(int index, int count, int step) {
        count >>>= 1;
        int index1 = index;
        int index2 = index + count;
        setIndexes(index1, index2);
        output.pointer1.set(input.pointer1).add(input.pointer2);
        output.pointer2.set(input.pointer1).sub(input.pointer2);
        index1++;
        index2++;
        count--;
        for (int i = step; count > 0; i+= step) {
            setIndexes(index1, index2);
            calculationResult.set(input.pointer2).mul(ws[i]);
            output.pointer1.set(input.pointer1).add(calculationResult);
            output.pointer2.set(input.pointer1).sub(calculationResult);
            index1++;
            index2++;
            count--;
        }
    }

    private void writeOutput(ComplexBuffer.Pointer outputPointer) {
        for (int i = 0; i < n; i++) {
            outputPointer.slideTo(i).set(output.pointer1.slideTo(i));
        }
    }

    protected void processInput(ComplexBuffer.Pointer pointer) {
    }

    protected void processOutput(ComplexBuffer.Pointer pointer) {
    }

    private void swapData() {
        Data data = input;
        input = output;
        output = data;
    }

    private void setIndexes(int index1, int index2) {
        input.at(index1, index2);
        output.at(index1, index2);
    }

    private static class Data {
        private final ComplexBuffer buffer;
        private final ComplexBuffer.Pointer pointer1;
        private final ComplexBuffer.Pointer pointer2;

        private Data(int size) {
            buffer = new ComplexBuffer(size);
            pointer1 = buffer.pointer();
            pointer2 = buffer.pointer();
        }

        private Data at(int index1, int index2) {
            pointer1.slideTo(index1);
            pointer2.slideTo(index2);
            return this;
        }

    }

}
