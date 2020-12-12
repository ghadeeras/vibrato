package vibrato.examples;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vibrato.dspunits.DspSystem;
import vibrato.dspunits.filters.BufferingWindow;
import vibrato.dspunits.sinks.Oscilloscope;
import vibrato.functions.Curve;
import vibrato.interpolators.Interpolator;
import vibrato.music.synthesis.base.EnvelopeGenerator;
import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.music.synthesis.generators.WaveTable;
import vibrato.oscillators.MainOscillator;
import vibrato.vectors.RealValue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class ShowOnOscilloscope extends Application {

    private static final int resolution = 768;
    private final AtomicBoolean done = new AtomicBoolean(false);

    @Override
    public void start(Stage stage) {
        var canvas = new Canvas(resolution, resolution);

        var oscillator = MainOscillator.create();
        var viewer = new Viewer(canvas);
        viewer.connectTo(oscillator);
        oscillator.spawnOscillationThread(closed(stage));

        stage.setTitle("Viewer");
        stage.setScene(scene(group(canvas)));
        stage.show();
    }

    private BooleanSupplier closed(Stage stage) {
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> done.set(true));
        return () -> {
            sleep();
            return done.get();
        };
    }

    private void sleep() {
        try {
            int nanos = 1_000_000_000 / resolution;
            Thread.sleep(nanos / 1_000_000, nanos % 1_000_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Scene scene(Group group) {
        return new Scene(group);
    }

    private Group group(Canvas canvas) {
        Group group = new Group();
        group.getChildren().add(canvas);
        return group;
    }

    public static void main(String[] args) {
        Application.launch(ShowOnOscilloscope.class, args);
    }

    private static class Viewer extends DspSystem {

        protected Viewer(Canvas canvas) {
            super(resolution);
            source()
                .through(BufferingWindow.create(resolution))
                .into(Oscilloscope.renderingOn(canvas));
        }

        private Source<RealValue> source() {
            var attackEnvelope = Curve
                .from(0.00 * zSecond, 0, 0)
                .to(0.03 * zSecond, 1, 0)
                .to(0.06 * zSecond, 0.5, 0)
                .to(0.09 * zSecond, 0.8, 0)
                .slopedAs(Curve.envelope)
                .create(Curve.smooth)
                .asSignal();
            var muteEnvelope = Curve
                .from(0.00 * zSecond, 1)
                .to(0.07 * zSecond, 0.25)
                .to(0.20 * zSecond, 0)
                .slopedAs(Curve.envelope)
                .create(Curve.smooth)
                .asSignal();
            var envelope = EnvelopeGenerator.create(attackEnvelope, muteEnvelope);
            var oscillator = WaveOscillator.create(WaveTable.create(1, -1, -1), Interpolator.truncating);
            return scalarConstant(2 * zHertz)
                .through(oscillator)
                .through(envelope);
        }

    }

}
