import javax.sound.sampled.*;
import java.io.File;

public class Test {

    public static void main(String[] args) throws Exception {
        File mp3 = new File("assets/fly-me-to-the-moon-climax.mp3");

        try (AudioInputStream in = AudioSystem.getAudioInputStream(mp3)) {

            AudioFormat baseFormat = in.getFormat();

            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            try (AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in)) {

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
                try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {

                    line.open(decodedFormat);
                    line.start();

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = din.read(buffer, 0, buffer.length)) != -1) {
                        line.write(buffer, 0, bytesRead);
                    }

                    line.drain();
                }
            }
        }
    }
}