package alvin.study.testing.assertj.assertion;

/**
 * Entry point for soft assertions of different data types.
 */
@javax.annotation.Generated(value="assertj-assertions-generator")
public class SoftAssertions extends org.assertj.core.api.SoftAssertions {

  /**
   * Creates a new "soft" instance of <code>{@link alvin.study.testing.testcase.model.UserAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public alvin.study.testing.testcase.model.UserAssert assertThat(alvin.study.testing.testcase.model.User actual) {
    return proxy(alvin.study.testing.testcase.model.UserAssert.class, alvin.study.testing.testcase.model.User.class, actual);
  }

}
