package alvin.study.testing.assertj.assertion;

/**
 * Entry point for assertions of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
public class BddAssertions extends org.assertj.core.api.BDDAssertions {

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

  protected BddAssertions() {
      // empty
  }
}