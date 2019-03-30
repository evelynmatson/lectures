import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@SuppressWarnings("javadoc")
public class ListingBenchmark {

	public static final int WARMUP_ROUNDS = 10;
	public static final int TIMED_ROUNDS = 20;

	/*
	 * This is really meant to demonstrate just how bad we can make the runtime
	 * with a poor multithreading implementation, and the difference using a
	 * work queue can make on the runtime. However, even with the work queue, it
	 * might be slower than single threading for this particular problem since it
	 * has so many write operations versus read operations.
	 */

	public static void main(String[] args) {
		// TODO Change this to a large directory on your system!
		Path test = Path.of("..");
		Set<Path> expected = SerialDirectoryListing.list(test);

		new Benchmarker("Serial") {
			@Override
			public Set<Path> run(Path path) {
				return SerialDirectoryListing.list(test);
			}
		}.benchmark(test, expected);

		new Benchmarker("Slow") {
			@Override
			public Set<Path> run(Path path) {
				return SlowMultithreadedDirectoryListing.list(test);
			}
		}.benchmark(test, expected);

		new Benchmarker("Multi") {
			@Override
			public Set<Path> run(Path path) {
				return MultithreadedDirectoryListing.list(test);
			}
		}.benchmark(test, expected);

		new Benchmarker("Queue") {
			@Override
			public Set<Path> run(Path path) {
				return WorkQueueDirectoryListing.list(test);
			}
		}.benchmark(test, expected);

		new Benchmarker("Executor") {
			@Override
			public Set<Path> run(Path path) {
				return ExecutorDirectoryListing.list(test);
			}
		}.benchmark(test, expected);
	}

	private static abstract class Benchmarker {
		public String name;

		public Benchmarker(String name) {
			this.name = name;
		}

		public abstract Set<Path> run(Path path);

		public void benchmark(Path path, Set<Path> expected) {
			System.out.print(String.format("%20s: ", name));

			Set<Path> actual = run(path);

			// verify results first
			if (!actual.equals(expected)) {
				System.err.printf("Unexpected results! Expected %d elements, found %d elements.",
						expected.size(), actual.size());
			}

			// warmup
			for (int i = 0; i < WARMUP_ROUNDS; i++) {
				run(path);
			}

			// timed
			Instant start = Instant.now();
			for (int i = 0; i < TIMED_ROUNDS; i++) {
				run(path);
			}
			Instant end = Instant.now();

			// averaged result
			Duration elapsed = Duration.between(start, end);
			double average = (double) elapsed.toMillis() / TIMED_ROUNDS;
			System.out.printf("%8.2fms%n", average);
		}
	}
}
