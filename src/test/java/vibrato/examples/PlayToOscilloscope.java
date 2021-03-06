package vibrato.examples;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vibrato.dspunits.DspSource;
import vibrato.dspunits.DspSystem;
import vibrato.dspunits.filters.BufferingWindow;
import vibrato.dspunits.filters.Mixer;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.dspunits.sinks.Oscilloscope;
import vibrato.dspunits.sources.AudioSource;
import vibrato.fourier.FastFourierTransformer;
import vibrato.oscillators.MainOscillator;

import javax.sound.sampled.AudioInputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class PlayToOscilloscope extends Application {

    private static final int resolution = 768;
    private final AtomicBoolean done = new AtomicBoolean(false);

    @Override
    public void start(Stage stage) {
        var stream = DspApp.openAudioInputStream(getParameters().getRaw().get(0));
        var canvas = new Canvas(resolution, resolution);

        var oscillator = MainOscillator.create();
        var player = new AudioPlayer(stream, canvas);
        player.connectTo(oscillator);

        BooleanSupplier terminationCondition = closed(stage);
        oscillator.spawnOscillationThread(terminationCondition);

        stage.setTitle("Audio Plotter");
        stage.setScene(scene(group(canvas)));
        stage.show();
    }

    private BooleanSupplier closed(Stage stage) {
        BooleanSupplier terminationCondition = done::get;
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> done.set(true));
        return terminationCondition;
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
        Application.launch(PlayToOscilloscope.class, args);
    }

    private static class AudioPlayer extends DspSystem {

        protected AudioPlayer(AudioInputStream stream, Canvas canvas) {
            super(stream.getFormat().getFrameRate());

            var fft = FastFourierTransformer.create(resolution * 2);

            from(AudioSource.create(stream, stream.getFormat()))
                .into(
                    AudioSink.create(stream.getFormat()),
                    from(Mixer.average())
                        .through(BufferingWindow.create(fft.spectrumSize()))
                        .into(fft)
                );

            from(fft.length())
                .through(input -> DspSource.create(input.compressOnYAxis(20).window(fft.spectrumSize() / 2)))
                .into(Oscilloscope.renderingOn(canvas));
        }

    }

}
