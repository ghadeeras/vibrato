package vibrato.examples;

import vibrato.dspunits.DspSystem;
import vibrato.functions.Linear;
import vibrato.functions.RealFunction;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.DoubleStream;

public abstract class DspApp extends DspSystem {

    private static volatile boolean pressedEnter = false;
    private static ExecutorService executorService = null;

    protected DspApp(double clockSpeed) {
        super(clockSpeed);
    }

    public static boolean pressedEnter() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
            executorService.execute(DspApp::waitForEnter);
        }
        if (pressedEnter) {
            executorService.shutdown();
            executorService = null;
        }
        return pressedEnter;
    }

    private static void waitForEnter() {
        try {
            System.out.println();
            System.out.println("Waiting until the [Enter] key is pressed ...");
            pressedEnter = System.in.read() != 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static AudioInputStream openAudioInputStream(String... args) {
        if (args.length < 1) {
            return exit("One argument specifying the path to an audi file is required");
        }
        String mediaFileName = args[0];
        try {
            return AudioSystem.getAudioInputStream(new File(mediaFileName));
        } catch (UnsupportedAudioFileException | IOException e) {
            return exit(e.getMessage());
        }
    }

    public static <T> T exit(String message) {
        System.out.println(message);
        System.exit(1);
        return null;
    }

    protected double[] randomSamples(double min, double max, int count) {
        return dynamicRange(min, max, randomSamples(count));
    }

    private double[] randomSamples(int count) {
        return new Random(sameSeed()).doubles(count).toArray();
    }

    protected long sameSeed() {
        return getClass().getSimpleName().hashCode();
    }

    private static double[] dynamicRange(double min, double max, double[] wave) {
        double mn = DoubleStream.of(wave).min().orElse(-1);
        double mx = DoubleStream.of(wave).max().orElse(+1);
        RealFunction f = Linear.linear(mn, min, mx, max);
        return DoubleStream.of(wave).map(f::apply).toArray();
    }

}
