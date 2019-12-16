package vibrato.utils;

import javax.sound.sampled.AudioFormat;
import java.io.*;

public class AudioUtils {

    public static AudioFormat readAudioFormat(InputStream audioStream) {
        try {
            DataInputStream s = new DataInputStream(audioStream);
            boolean signed = s.readBoolean();
            float sampleRate = s.readFloat();
            int sampleSizeInBits = s.readInt();
            int channels = s.readInt();
            int frameSize = s.readInt();
            float frameRate = s.readFloat();
            boolean bigEndian = s.readBoolean();
            return new AudioFormat(encoding(signed), sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void writeAudioFormat(OutputStream audioStream, AudioFormat audioFormat) {
        try {
            DataOutputStream s = new DataOutputStream(audioStream);
            s.writeBoolean(isSigned(audioFormat));
            s.writeFloat(audioFormat.getSampleRate());
            s.writeInt(audioFormat.getSampleSizeInBits());
            s.writeInt(audioFormat.getChannels());
            s.writeInt(audioFormat.getFrameSize());
            s.writeFloat(audioFormat.getFrameRate());
            s.writeBoolean(audioFormat.isBigEndian());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static AudioFormat.Encoding encoding(boolean signed) {
        return signed ? AudioFormat.Encoding.PCM_SIGNED : AudioFormat.Encoding.PCM_UNSIGNED;
    }

    private static boolean isSigned(AudioFormat audioFormat) {
        return AudioFormat.Encoding.PCM_SIGNED.equals(audioFormat.getEncoding());
    }

}
