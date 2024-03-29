package alvin.study.testing.testcase.model;

import java.util.Objects;
import org.assertj.core.api.AbstractObjectAssert;

/**
 * {@link User} specific assertions - Generated by CustomAssertionGenerator.
 */
@javax.annotation.Generated(value="assertj-assertions-generator")
public class UserAssert extends AbstractObjectAssert<UserAssert, User> {

  /**
   * Creates a new <code>{@link UserAssert}</code> to make assertions on actual User.
   * @param actual the User we want to make assertions on.
   */
  public UserAssert(User actual) {
    super(actual, UserAssert.class);
  }

  /**
   * An entry point for UserAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one can write directly: <code>assertThat(myUser)</code> and get specific assertion with code completion.
   * @param actual the User we want to make assertions on.
   * @return a new <code>{@link UserAssert}</code>
   */
  @org.assertj.core.util.CheckReturnValue
  public static UserAssert assertThat(User actual) {
    return new UserAssert(actual);
  }

  /**
   * Verifies that the actual User's id is equal to the given one.
   * @param id the given id to compare the actual User's id to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's id is not equal to the given one.
   */
  public UserAssert hasId(int id) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting id of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // check
    int actualId = actual.getId();
    if (actualId != id) {
      failWithMessage(assertjErrorMessage, actual, id, actualId);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual User's name is equal to the given one.
   * @param name the given name to compare the actual User's name to.
   * @return this assertion object.
   * @throws AssertionError - if the actual User's name is not equal to the given one.
   */
  public UserAssert hasName(String name) {
    // check that actual User we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting name of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    String actualName = actual.getName();
    if (!Objects.deepEquals(actualName, name)) {
      failWithMessage(assertjErrorMessage, actual, name, actualName);
    }

    // return the current assertion for method chaining
    return this;
  }

}
