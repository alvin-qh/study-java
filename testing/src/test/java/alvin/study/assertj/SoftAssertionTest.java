package alvin.study.assertj;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class SoftAssertionTest {
    @Test
    void soft_shouldSoftAssertionWork() {
        var softly = new SoftAssertions();

        softly.assertThat("George Martin").as("great authors").isEqualTo("JRR Tolkien");
        softly.assertThat(42).as("response to Everything").isGreaterThan(100);
        softly.assertThat("Gandalf").isEqualTo("Sauron");

        softly.assertAll();
    }
}
