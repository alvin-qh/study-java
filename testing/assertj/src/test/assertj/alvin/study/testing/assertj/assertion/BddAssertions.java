package alvin.study.testing.assertj.assertion;

/**
 * Entry point for BDD assertions of different data types.
 */
@javax.annotation.Generated(value = "assertj-assertions-generator")
public class BddAssertions {

    /**
     * Creates a new <code>{@link BddAssertions}</code>.
     */
    protected BddAssertions() {
        // empty
    }

    /**
     * Creates a new instance of <code>{@link alvin.study.testing.testcase.model.UserAssert}</code>.
     *
     * @param actual the actual value.
     * @return the created assertion object.
     */
    @org.assertj.core.util.CheckReturnValue
    public static alvin.study.testing.testcase.model.UserAssert then(alvin.study.testing.testcase.model.User actual) {
        return new alvin.study.testing.testcase.model.UserAssert(actual);
    }
}
