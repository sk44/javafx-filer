/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.jfxfiler.models;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 *
 * @author sk
 */
@RunWith(Theories.class)
public class FileSizeFormatterTest {

    public static class Fixture {

        private final long arg;
        private final String expected;

        public Fixture(long arg, String expected) {
            this.arg = arg;
            this.expected = expected;
        }
    }

    @DataPoints
    public static Fixture[] FIXTURES = {
        new Fixture(999, "999B"),
        new Fixture(1000, "1.0K"),
        new Fixture(999999, "999.9K"),
        new Fixture(1000000, "1.0M"),
        new Fixture(999999999, "999.9M"),
        new Fixture(1000000000, "1.0T"),};

    @Theory
    public void testFormat(Fixture fixture) {
        assertThat(FileSizeFormatter.format(fixture.arg), is(fixture.expected));
    }
}
