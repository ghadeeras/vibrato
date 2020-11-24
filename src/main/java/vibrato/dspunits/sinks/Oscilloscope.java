package vibrato.dspunits.sinks;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import vibrato.dspunits.DspSink;
import vibrato.dspunits.DspUnit;
import vibrato.oscillators.Operation;
import vibrato.oscillators.State;
import vibrato.vectors.Buffer;
import vibrato.vectors.RealVector;

import java.util.concurrent.atomic.AtomicLong;

public class Oscilloscope implements DspUnit, State {

    private final RealVector input;
    private final Canvas canvas;

    private final AtomicLong loadingCount;
    private final AtomicLong renderingCount;
    private final double[] inputSamples;
    private final RealVector inputCopy;

    private final Loading loading = new Loading();
    private final Timer timer = new Timer();

    public Oscilloscope(RealVector input, Canvas canvas) {
        this.input = input;
        this.canvas = canvas;
        this.loadingCount = new AtomicLong(0);
        this.renderingCount = new AtomicLong(0);
        this.inputSamples = new double[input.size()];
        this.inputCopy = new Buffer(inputSamples);
    }

    public static DspSink<RealVector> renderingOn(Canvas canvas) {
        return input -> {
            Oscilloscope oscilloscope = new Oscilloscope(input, canvas);
            oscilloscope.timer.start();
            return oscilloscope;
        };
    }

    private void load() {
        if (loadingCount.get() == renderingCount.get()) {
            doLoad();
            loadingCount.incrementAndGet();
        }
    }

    private void doLoad() {
        for (var i = 1; i < input.size(); i++) {
            inputSamples[i] = input.value(i);
        }
    }

    private void render() {
        if (loadingCount.get() - renderingCount.get() == 1) {
            doRender();
            renderingCount.incrementAndGet();
        }
    }

    private void doRender() {
        var gc = canvas.getGraphicsContext2D();
        var width = canvas.getWidth();
        var height = canvas.getHeight();
        gc.save();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);
        drawGrid(gc, width / 4, height / 4);
        gc.moveTo(x(0, width), y(0, height));
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.beginPath();
        for (int i = 1; i < inputCopy.size(); i++) {
            gc.lineTo(x(i, width), y(i, height));
        }
        gc.stroke();
        gc.restore();
    }

    private void drawGrid(javafx.scene.canvas.GraphicsContext gc, double unitX, double unitY) {
        gc.setLineWidth(0.25);
        gc.setStroke(Color.GREY);
        gc.strokeLine(0, 1 * unitY, 4 * unitX, 1 * unitY);
        gc.strokeLine(0, 2 * unitY, 4 * unitX, 2 * unitY);
        gc.strokeLine(0, 3 * unitY, 4 * unitX, 3 * unitY);
        gc.strokeLine(1 * unitX, 0, 1 * unitX, 4 * unitY);
        gc.strokeLine(2 * unitX, 0, 2 * unitX, 4 * unitY);
        gc.strokeLine(3 * unitX, 0, 3 * unitX, 4 * unitY);
    }

    private double x(int i, double width) {
        return i * width / inputCopy.size();
    }

    private double y(int i, double height) {
        return (2 - inputCopy.value(i)) * height / 4;
    }

    @Override
    public Operation[] operations() {
        return DspUnit.ops(loading);
    }

    private class Timer extends AnimationTimer {

        public Timer() {
        }

        @Override
        public void handle(long now) {
            render();
        }

    }

    private class Loading implements Operation {

        @Override
        public State state() {
            return Oscilloscope.this;
        }

        @Override
        public void readPhase() {
            load();
        }

        @Override
        public void writePhase() {
        }

    }

}
