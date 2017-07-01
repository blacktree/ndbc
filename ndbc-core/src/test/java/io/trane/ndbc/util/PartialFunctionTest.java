package io.trane.ndbc.util;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

public class PartialFunctionTest {

  @Test
  public void apply() {
    Integer value = 2;
    PartialFunction<Integer, Integer> pf = PartialFunction.apply();
    assertEquals(value, pf.applyOrElse(1, () -> value));
  }

  private static interface TestInterface {
  }

  private static class TestClass1 implements TestInterface {
  }

  private static class TestClass2 implements TestInterface {
  }

  @Test
  public void when() {
    TestInterface value = new TestClass1();
    Integer result = 2;
    PartialFunction<TestInterface, Integer> pf = PartialFunction
        .when(TestClass1.class, v -> {
          assertEquals(v, value);
          return result;
        });
    assertEquals(result, pf.applyOrElse(value, this::notExpected));
  }

  @Test
  public void whenMismatch() {
    Integer result = 3;
    TestInterface value = new TestClass1();
    PartialFunction<TestInterface, Integer> pf = PartialFunction
        .when(TestClass2.class, v -> {
          assertEquals(v, value);
          return 2;
        });
    assertEquals(result, pf.applyOrElse(value, () -> result));
  }

  @Test
  public void orElsePredicate() {
    TestInterface value = new TestClass1();
    Integer result = 2;
    PartialFunction<TestInterface, Integer> pf = PartialFunction
        .<TestInterface, Integer>apply().orElse(v -> v == value, v -> result);
    assertEquals(result, pf.applyOrElse(value, this::notExpected));
  }

  @Test
  public void orElsePredicateMismatch() {
    Integer result = 3;
    TestInterface value = new TestClass1();
    PartialFunction<TestInterface, Integer> pf = PartialFunction
        .<TestInterface, Integer>apply().orElse(v -> v != value, v -> 2);
    assertEquals(result, pf.applyOrElse(value, () -> result));
  }

  @Test
  public void orElseClass() {
    TestInterface value = new TestClass1();
    Integer result = 2;
    PartialFunction<TestInterface, Integer> pf = PartialFunction
        .<TestInterface, Integer>apply().orElse(TestClass1.class, v -> {
          assertEquals(v, value);
          return result;
        });
    assertEquals(result, pf.applyOrElse(value, this::notExpected));
  }

  @Test
  public void orElseClassMismatch() {
    Integer result = 3;
    TestInterface value = new TestClass1();
    PartialFunction<TestInterface, Integer> pf = PartialFunction
        .<TestInterface, Integer>apply().orElse(TestClass2.class, v -> {
          assertEquals(v, value);
          return 2;
        });
    assertEquals(result, pf.applyOrElse(value, () -> result));
  }

  @Test
  public void lift() {
    Integer result = 33;
    PartialFunction<TestInterface, Integer> pf = PartialFunction.when(TestClass1.class, v -> result);
    assertEquals(Optional.of(result), pf.lift().apply(new TestClass1()));
  }

  @Test
  public void liftMismatch() {
    PartialFunction<TestInterface, Integer> pf = PartialFunction.when(TestClass2.class, v -> 33);
    assertEquals(Optional.empty(), pf.lift().apply(new TestClass1()));
  }

  private <T> T notExpected() {
    throw new IllegalStateException("Unexpected");
  }
}
