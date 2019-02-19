import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("javadoc")
public class ParallelStreamDemo {

	public static long countWordsConcat(Path path, String word, Function<String, String[]> tokenize)
			throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

			long count = 0;

			// TODO

			return count;
		}
	}

	public static long countWordsBuffer(Path path, String word, Function<String, String[]> tokenize)
			throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

			long count = 0;

			// TODO

			return count;
		}
	}

	public static long countWordsNormal(Path path, String word, Function<String, String[]> tokenize)
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

			long count = 0;

			// TODO

			return count;
		}
	}

	public static long countWordsStream(Path path, String word, Function<String, String[]> tokenize)
			throws IOException {
		try (
				BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
				Stream<String> stream = reader.lines();
		) {

			long count = 0;

			// TODO

			return count;
		}
	}

	public static long countWordsParallelStream(Path path, String word,
			Function<String, String[]> tokenize) throws IOException {
		try (
				BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
				Stream<String> stream = reader.lines();
		) {

			long count = 0;

			// TODO

			return count;
		}
	}

	public static void main(String[] args) throws IOException {
		Path path = Paths.get("1400-0.txt");
		String word = "great";

		Function<String, String[]> tokenize = line -> line.toLowerCase().split("[^\\p{Alpha}]+");
		System.out.println(Arrays.toString(
				tokenize.apply("The Project Gutenberg EBook of Great Expectations, by Charles Dickens")));

		Instant start = Instant.now();

//	long count = countWordsConcat(path, word, tokenize);
//	long count = countWordsBuffer(path, word, tokenize);
//	long count = countWordsNormal(path, word, tokenize);
//	long count = countWordsStream(path, word, tokenize);
		long count = countWordsParallelStream(path, word, tokenize);

		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();

		String format = "Found %d occurrences of \"%s\" in %f seconds.";
		System.out.printf(format, count, word, seconds);
	}

}
