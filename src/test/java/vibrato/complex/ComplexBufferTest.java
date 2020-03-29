package vibrato.complex;

import org.junit.Test;
import vibrato.testtools.TestBase;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ComplexBufferTest extends TestBase {

    private ComplexBuffer complexBuffer = new ComplexBuffer(64);

    @Test
    public void testPointers() {
        ComplexBuffer.Pointer pointer = complexBuffer.pointer();
        ComplexBuffer.Pointer anotherPointer = complexBuffer.pointer();
        forAnyOf(integersBetween(0, complexBuffer.size() - 1), index -> {
            forAnyOf(complexNumbers(), c -> {
                pointer.slideTo(index).set(c);
                assertThat(complexBuffer.realParts().value(index), equalTo(c.real()));
                assertThat(complexBuffer.imaginaryParts().value(index), equalTo(c.imaginary()));

                anotherPointer.slideTo(index);
                assertThat(anotherPointer.real(), equalTo(c.real()));
                assertThat(anotherPointer.imaginary(), equalTo(c.imaginary()));
            });
        });
    }

}
