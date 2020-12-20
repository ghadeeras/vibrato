package vibrato.dspunits.filters.fir;

import org.junit.Test;
import vibrato.testtools.DspUnitTestBase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DelayTest extends DspUnitTestBase {

    @Test
    public void testDelayingInput() {
        forAnyOf(integersBetween(1, 256), 16, delay ->
            forAnyOf(integersBetween(0, 256), 16, signalSize -> {
                double[] input = randomInput(signalSize);
                double[] output = apply(Delay.create(delay), input);
                assertThat(output.length, equalTo(input.length));
                for (int i = 0; i < input.length; i++) {
                    assertThat(output[i], equalTo(i < delay ? 0 : input[i - delay]));
                }
            })
        );
    }

}
