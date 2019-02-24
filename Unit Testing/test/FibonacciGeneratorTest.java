
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/*
 * Each JUnit class usually only has one target class it is testing. So we need
 * to create a new test class for our generator.
 */

@SuppressWarnings("javadoc")
public class FibonacciGeneratorTest {

	/*
	 * Lets test a few big Fibonacci numbers. There are some large values online at
	 * https://oeis.org/A000045/list and at:
	 * https://onlinelibrary.wiley.com/doi/pdf/10.1002/9781118033067.app3
	 */

	@Nested
	public class FibonacciNumberTests {

		@Test
		public void testFibonacci20() {
			BigInteger expected = BigInteger.valueOf(6765);
			assertEquals(expected, FibonacciGenerator.fibonacciNumber(20));
		}

		@Test
		public void testFibonacci40() {
			BigInteger expected = new BigInteger("102334155");
			assertEquals(expected, FibonacciGenerator.fibonacciNumber(40));
		}

		@Test
		public void testFibonacci100() {
			BigInteger expected = new BigInteger("354224848179261915075");
			assertEquals(expected, FibonacciGenerator.fibonacciNumber(100));
		}

		/*
		 * What if you wanted to specify a LOT of test cases, like making sure our
		 * fibonacciNumber() method produced the correct first 10 values. That is a lot
		 * of little tests to write. Instead, we can use parameterized tests to generate
		 * tests from an expected "value source".
		 *
		 * THIS IS AN EXPERIMENTAL FEATURE. You may need to update your version of
		 * Eclipse and update its installed packages for this code to work.
		 */

		/*
		 * There are lots of value sources. If you need to specify more than one
		 * parameter and those parameters have different types, consider a CSV source or
		 * creating a custom enum object to store the parameters (see below).
		 */

		@ParameterizedTest(name = "{0}") // use toString() as name
		@EnumSource(FibonacciNumber.class)
		void testFibonacciNumbers(FibonacciNumber number) {
			BigInteger expected = number.value;
			BigInteger actual = FibonacciGenerator.fibonacciNumber(number.ordinal());
			assertEquals(expected, actual);
		}

		/*
		 * This is pretty neat, but comes with downsides. If you run this entire group
		 * of tests, then you can still select a single test in the JUnit view to run it
		 * individually, but you cannot setup a Run Configuration in Eclipse manually to
		 * run an individual test anymore.
		 *
		 * Instead, you can limit which enums are considered. Try this:
		 *
		 * @EnumSource(value = FIBONACCI_NUMBERS.class, names = { "F3", "F4" })
		 */
	}

	public static enum FibonacciNumber {
		/*
		 * The "ordinal" of the enum is set automatically starting at 0 and incrementing
		 * by one each time. We'll use this for the index, and then call the enum
		 * constructor to set the expected value for that index.
		 */
		F0(0), F1(1), F2(1), F3(2), F4(3), F5(5), F6(8), F7(13), F8(21), F9(34);

		public final BigInteger value;

		/*
		 * Enum constructors must be private. The only way to create an enum instance is
		 * using the initialization list above.
		 */
		private FibonacciNumber(long value) {
			this.value = BigInteger.valueOf(value);
		}

		@Override
		public String toString() {
			return "f(" + this.ordinal() + ") = " + this.value.toString();
		}
	}

	@Nested
	public class FibonacciNumbersTests {
		/*
		 * If you need more flexibility than provided by the parameterized tests, you
		 * could use dynamic tests instead, which dynamically generate the test methods
		 * when compiled and run.
		 *
		 * THIS IS AN EXPERIMENTAL FEATURE. You may need to update your version of
		 * Eclipse and update its installed packages for this code to work.
		 */

		/*
		 * We want to make sure the lists generated are correct. We can specify one list
		 * of the first 10 expected values and use sublists for other tests. We also
		 * want to make sure both the size of the lists and the values in those lists
		 * are correct.
		 */

		/*
		 * First you need to think about each test you want to generate.
		 */

		public DynamicTest generateSizeTest(int index) {
			List<BigInteger> actual = FibonacciGenerator.fibonacciNumbers(index);
			return dynamicTest("size for limit = " + index, () -> {
				assertEquals(index, actual.size());
			});
		}

		public DynamicTest generateContentTest(int index, List<BigInteger> values) {
			List<BigInteger> expected = values.subList(0, index);
			List<BigInteger> actual = FibonacciGenerator.fibonacciNumbers(index);
			return dynamicTest("content for limit = " + index, () -> {
				assertEquals(expected, actual);
			});
		}

		/*
		 * Now we need to specify the values and generate the stream of tests.
		 */
		@TestFactory
		public Stream<DynamicTest> testFibonacciNumber() {
			List<BigInteger> expected = List.of(0, 1, 1, 2, 3, 5, 8, 13, 21, 34).stream()
					.map(item -> BigInteger.valueOf(item)).collect(Collectors.toList());

			Stream<DynamicTest> testSizes = IntStream.range(0, expected.size())
					.mapToObj(i -> generateSizeTest(i));

			Stream<DynamicTest> testContents = IntStream.range(0, expected.size())
					.mapToObj(i -> generateContentTest(i, expected));

			return Stream.concat(testSizes, testContents);
		}

		/*
		 * Again, this is neat but has more downsides. Since the tests are dynamically
		 * generated, it is difficult to select just one individual test to run at a
		 * specific time. You can select a single method, but not the individual tests
		 * generated by that method until you have compiled and run the method at least
		 * once already. Then, you can select the individual tests in the JUnit view on
		 * Eclipse.
		 */
	}

	/*
	 * You want to make sure things work as expected, AND that they fail as expected
	 * too. While it is possible to extend the Fibonacci sequence into the negative
	 * numbers, that is not supported by our code. Therefore, there should be some
	 * indication of this when negative numbers are used.
	 *
	 * This also tends to be where infinite loops can be triggered, so we will start
	 * using timeouts in these tests.
	 */

	@Nested
	public class ExceptionTests {

		public final Duration TIMEOUT = Duration.ofMillis(100);

		@Test
		public void testNegativeFibonacciNumber() {
			// This is the core action we are testing.
			Executable action = () -> {
				FibonacciGenerator.fibonacciNumber(-10);
			};

			// We want to assert this action DOES throw an exception
			Executable wrapper = () -> {
				assertThrows(IllegalArgumentException.class, action);
			};

			// But, in case this breaks things, we don't want to wait forever.
			assertTimeout(TIMEOUT, wrapper);
		}

		@Test
		public void testNegativeFibonacciNumbers() {
			// We do not need to separate all of the lambda expressions...
			assertTimeout(TIMEOUT, () -> {
				assertThrows(IllegalArgumentException.class, () -> {
					FibonacciGenerator.fibonacciNumbers(-1);
				});
			});
		}

		@Test
		public void testZeroFibonacciNumbers() {
			// Zero should work without exceptions.
			assertTimeout(TIMEOUT, () -> {
				FibonacciGenerator.fibonacciNumbers(0);
			});
		}
	}
}
