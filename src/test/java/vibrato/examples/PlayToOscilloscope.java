package vibrato.examples;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import vibrato.dspunits.DspSystem;
import vibrato.dspunits.filters.BufferingWindow;
import vibrato.dspunits.filters.Mixer;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.dspunits.sinks.Oscilloscope;
import vibrato.dspunits.sources.AudioSource;
import vibrato.oscillators.MasterOscillator;

import javax.sound.sampled.AudioInputStream;

public class PlayToOscilloscope extends Application {

    @Override
    public void start(Stage stage) {
        var stream = DspApp.openAudioInputStream(getParameters().getRaw().get(0));
        var canvas = new Canvas(512, 512);

        var oscillator = new MasterOscillator(stream.getFormat().getFrameRate());
        var player = new AudioPlayer(stream, canvas);
        player.connectTo(oscillator);
        oscillator.spawnOscillationThread(DspApp::pressedEnter);

        stage.setTitle("Audio Plotter");
        stage.setScene(scene(group(canvas)));
        stage.show();
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
            var source = AudioSource.from(stream, stream.getFormat());
            define(source)
                .then(Mixer.average())
                .then(
                    define(fork(2)).then(AudioSink.of(stream.getFormat())),
                    define(BufferingWindow.ofSize(512)).then(Oscilloscope.renderingOn(canvas))
                );
        }

    }

}
