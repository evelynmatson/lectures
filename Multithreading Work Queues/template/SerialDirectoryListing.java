import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SerialDirectoryListing {

	private static final Logger log = LogManager.getLogger();

	public static Set<Path> list(Path path) {
		HashSet<Path> paths = new HashSet<>();

		// TODO

		return paths;
	}

	private static void list(Path path, Set<Path> paths) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			// TODO
		}
		catch (IOException ex) {
			log.catching(Level.DEBUG, ex);
		}
	}

	public static void main(String[] args) {
		list(Path.of(".")).stream().forEach(System.out::println);
	}
}
