import org.junit.jupiter.api.Nested;

/*
 * We can use inheritance to combine multiple different test classes
 * into a single test suite. Note the nesting is preserved.
 */

@SuppressWarnings("javadoc")
public class FibonacciTestSuite {

	@Nested
	public class FibonacciPair extends FibonacciPairTest {

	}

	@Nested
	public class FibonacciGenerator extends FibonacciGeneratorTest {

	}
}
