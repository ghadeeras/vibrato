package vibrato.utils;

import org.junit.Test;
import vibrato.testtools.TestBase;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class FixedPointSampleTest extends TestBase {

    @Test
    public void test() throws IOException {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream();
        inputStream.connect(outputStream);

        Stream.of(8, 16, 24, 32, 40, 48).forEach(bps -> forAnyOf(booleans(), signed -> forAnyOf(booleans(), bigEndian -> {
            System.out.println("BPS: " + bps+ ". Signed: " + signed + ". Big-Endian: " + bigEndian);
            FixedPointSample sample = new FixedPointSample(bps, signed, bigEndian);
            forAnyOf(doublesBetween(-1, 1), writtenSample -> {
                sample.write(outputStream, writtenSample);
                String writtenBytes = sample.contentAsString();

                double readSample = sample.read(inputStream);
                String readBytes = sample.contentAsString();

                assertThat(readBytes, equalTo(writtenBytes));
                assertThat(readBytes.split("\\s").length, equalTo(bps / 8));
//                System.out.println(writtenSample + " --> " + sample.contentAsString() + " --> " + readSample);

                double error = Math.abs(readSample - writtenSample);
                assertThat(error, lessThanOrEqualTo(sample.roundingError));
            });
        })));
    }

    @Test
    public void testBigEndian() throws IOException {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream();
        inputStream.connect(outputStream);

        Stream.of(8, 16, 24, 32, 40, 48).forEach(bps -> forAnyOf(booleans(), signed -> {
            FixedPointSample bigEndianSample = new FixedPointSample(bps, signed, true);
            FixedPointSample smallEndianSample = new FixedPointSample(bps, signed, false);
            forAnyOf(doublesBetween(-1, 1), writtenSample -> {
                bigEndianSample.write(outputStream, writtenSample);
                smallEndianSample.write(outputStream, writtenSample);
                byte[] writtenBytes = new byte[2 * bps / 8];
                try {
                    int count = inputStream.read(writtenBytes);
                    assertThat(count, equalTo(writtenBytes.length));
                    for (int i = 0; i < count / 2; i++) {
                        assertThat(writtenBytes[i], equalTo(writtenBytes[count - 1 - i]));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }));
    }

    @Test
    public void testSigned() throws IOException {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream();
        inputStream.connect(outputStream);

        Stream.of(8, 16, 24, 32, 40, 48).forEach(bps -> forAnyOf(booleans(), bigEndian -> {
            FixedPointSample sampleSigned = new FixedPointSample(bps, true, bigEndian);
            FixedPointSample sampleNotSigned = new FixedPointSample(bps, false, bigEndian);
            double bitValue = 1d / (1L << (bps - 1));
            double unsignedZero = 1 + bitValue;

            forAnyOf(doublesBetween(-1, -bitValue), writtenSample -> {
                sampleSigned.write(outputStream, writtenSample);
                double readSample = sampleNotSigned.read(inputStream);
                double error = readSample - writtenSample - unsignedZero;
                assertThat(error, lessThanOrEqualTo(sampleSigned.roundingError));
            });

            forAnyOf(doublesBetween(bitValue, 1), writtenSample -> {
                sampleSigned.write(outputStream, writtenSample);
                double readSample = sampleNotSigned.read(inputStream);
                double error = writtenSample - readSample - unsignedZero;
                assertThat(error, lessThanOrEqualTo(sampleSigned.roundingError));
            });

            sampleSigned.write(outputStream, 0);
            double readSample = sampleNotSigned.read(inputStream);
            double error = readSample + unsignedZero;
            assertThat(error, lessThanOrEqualTo(sampleSigned.roundingError));
        }));
    }

    @Test
    public void testUnsupportedBPSs() {
        Set<Integer> supportedBPSs = Stream.of(8, 16, 24, 32, 40, 48).collect(toSet());
        Predicate<Integer> notSupportedBPSs = bps -> !supportedBPSs.contains(bps);
        forAnyOf(positiveIntegers().filter(notSupportedBPSs), bps -> forAnyOf(booleans(), signed -> forAnyOf(booleans(), bigEndian -> {
            try {
                new FixedPointSample(bps, signed, bigEndian);
                fail();
            } catch (Exception e) {
                assertThat(e, instanceOf(UnsupportedOperationException.class));
            }
        })));
    }

}
