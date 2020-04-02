package vibrato.examples;

import vibrato.dspunits.DspSystem;
import vibrato.oscillators.MasterOscillator;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public abstract class DspApp extends DspSystem {

    protected DspApp(double clockSpeed) {
        super(clockSpeed);
    }

    public static void loop(MasterOscillator oscillator) {
        System.out.println();
        System.out.println("Press [Enter] to stop.");
        while (!pressedEnter()) {
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() - time < 100) {
                oscillator.oscillate(100);
            }
        }
    }

    public static boolean pressedEnter() {
        try {
            return System.in.available() > 0;
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

}
