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

/**
 * Demonstrates the impact parallel streams can make on runtime.
 */
public class ParallelStreamDemo {

	/**
	 * Calculates the number of times the word provided appears in a file using the
	 * tokenize function to split lines into tokens. This method uses an extremely
	 * inefficient approach due to string concatenation and reading the entire file
	 * into memory at once.
	 *
	 * @param path     the path to read
	 * @param word     the word to count
	 * @param tokenize the function to break lines into tokens
	 * @return the number of times the word appeared in the file
	 * @throws IOException
	 */
	public static long countWordsConcat(Path path, String word, Function<String, String[]> tokenize)
			throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String text = "";
			String line;
			long count = 0;

			while ((line = reader.readLine()) != null) {
				text += line + "\n";
			}

			for (String token : tokenize.apply(text)) {
				if (token.equals(word)) {
					count++;
				}
			}

			return count;
		}
	}

	/**
	 * Calculates the number of times the word provided appears in a file using the
	 * tokenize function to split lines into tokens. This method uses an inefficient
	 * approach due to reading the entire file into memory at once.
	 *
	 * @param path     the path to read
	 * @param word     the word to count
	 * @param tokenize the function to break lines into tokens
	 * @return the number of times the word appeared in the file
	 * @throws IOException
	 */
	public static long countWordsBuffer(Path path, String word, Function<String, String[]> tokenize)
			throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			StringBuffer text = new StringBuffer();
			String line;
			long count = 0;

			while ((line = reader.readLine()) != null) {
				text.append(line);
				text.append("\n");
			}

			for (String token : tokenize.apply(text.toString())) {
				if (token.equals(word)) {
					count++;
				}
			}

			return count;
		}
	}

	/**
	 * Calculates the number of times the word provided appears in a file using the
	 * tokenize function to split lines into tokens. This method uses an efficient
	 * straightforward synchronous approach.
	 *
	 * @param path     the path to read
	 * @param word     the word to count
	 * @param tokenize the function to break lines into tokens
	 * @return the number of times the word appeared in the file
	 * @throws IOException
	 */
	public static long countWordsNormal(Path path, String word, Function<String, String[]> tokenize)
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

			String line;
			long count = 0;

			while ((line = reader.readLine()) != null) {
				for (String token : tokenize.apply(line)) {
					if (token.equals(word)) {
						count++;
					}
				}
			}

			return count;
		}
	}

	/**
	 * Calculates the number of times the word provided appears in a file using the
	 * tokenize function to split lines into tokens. This method uses an efficient
	 * synchronous streaming approach.
	 *
	 * @param path     the path to read
	 * @param word     the word to count
	 * @param tokenize the function to break lines into tokens
	 * @return the number of times the word appeared in the file
	 * @throws IOException
	 */
	public static long countWordsStream(Path path, String word, Function<String, String[]> tokenize)
			throws IOException {
		try (
				BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
				Stream<String> stream = reader.lines();
		) {

			return stream.flatMap(line -> Arrays.stream(tokenize.apply(line)))
					.filter(token -> token.equals(word)).count();
		}
	}

	/**
	 * Calculates the number of times the word provided appears in a file using the
	 * tokenize function to split lines into tokens. This method uses an efficient
	 * parallelized streaming approach.
	 *
	 * @param path     the path to read
	 * @param word     the word to count
	 * @param tokenize the function to break lines into tokens
	 * @return the number of times the word appeared in the file
	 * @throws IOException
	 */
	public static long countWordsParallelStream(Path path, String word,
			Function<String, String[]> tokenize) throws IOException {
		try (
				BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
				Stream<String> stream = reader.lines();
		) {

			return stream.parallel().flatMap(line -> Arrays.stream(tokenize.apply(line)))
					.filter(token -> token.equals(word)).count();
		}
	}

	/**
	 * Demonstrates this class.
	 *
	 * @param args unused
	 *
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Path path = Paths.get("1400-0.txt");
		String word = "great";

		Function<String, String[]> tokenize = line -> line.toLowerCase().split("[^\\p{Alpha}]+");
		System.out.println(Arrays.toString(
				tokenize.apply("The Project Gutenberg EBook of Great Expectations, by Charles Dickens")));

		Instant start = Instant.now();

//		long count = countWordsConcat(path, word, tokenize);
//		long count = countWordsBuffer(path, word, tokenize);
//		long count = countWordsNormal(path, word, tokenize);
//		long count = countWordsStream(path, word, tokenize);
		long count = countWordsParallelStream(path, word, tokenize);

		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();

		String format = "Found %d occurrences of \"%s\" in %f seconds.";
		System.out.printf(format, count, word, seconds);
	}

}
