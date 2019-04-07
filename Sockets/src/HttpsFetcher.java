import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * A simple re-implementation of {@link URLFetcher} that uses sockets instead
 * of a URL connection to fetch headers and content.
 */
public class HttpsFetcher extends URLFetcher {

	/**
	 * Fetches the headers and content for the specified URL. The content is
	 * placed as a list of all the lines fetched under the "Content" key.
	 *
	 * @param url the url to fetch
	 * @return a map with the headers and content
	 * @throws IOException if unable to fetch headers and content
	 */
	public static Map<String, List<String>> fetchURL(URL url) throws IOException {
		// used to store all headers and content
		// use the same data structure as URLConnection.getHeaderFields()
		Map<String, List<String>> results = new HashMap<>();

		String protocol = url.getProtocol();
		String host = url.getHost();
		String resource = url.getFile().isEmpty() ? "/" : url.getFile();

		// set default port based on protocol
		boolean https = protocol != null && protocol.equalsIgnoreCase("https");
		int defaultPort = https ? 443 : 80;
		int port = url.getPort() < 0 ? defaultPort : url.getPort();

		try (
				// open up a network socket to the webserver
				Socket socket = https ?
						SSLSocketFactory.getDefault().createSocket(host, port) :
						SocketFactory.getDefault().createSocket(host, port);

				// create a writer for the request
				PrintWriter request = new PrintWriter(socket.getOutputStream());

				// create a reader for the response
				InputStreamReader input = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
				BufferedReader response = new BufferedReader(input);
		) {
			// write request to socket
			request.printf("GET %s HTTP/1.1\r\n", resource);
			request.printf("Host: %s\r\n", host);
			request.printf("Connection: close\r\n");
			request.printf("\r\n");
			request.flush();

			// fetch status line from server
			String line = response.readLine();

			// add status line in same way as URLConnection.getHeaderFields()
			results.put(null, Arrays.asList(line));

			// process remaining headers
			while ((line = response.readLine()) != null) {
				// detect empty line between headers and content
				if (line.trim().isEmpty()) {
					break;
				}

				String[] split = line.split(":\\s+", 2);
				assert split.length == 2;

				// handle cases where header appears more than once
				results.putIfAbsent(split[0], new ArrayList<>());
				results.get(split[0]).add(split[1]);
			}

			// process remaining content
			List<String> lines = new ArrayList<>();
			while ((line = response.readLine()) != null) {
				lines.add(line);
			}

			results.put(CONTENT, lines);
		}

		return results;
	}

	/**
	 * See {@link #fetchURL(URL)} for details.
	 *
	 * @throws MalformedURLException if unable to convert String to URL
	 * @see #fetchURL(URL)
	 */
	public static Map<String, List<String>> fetchURL(String url) throws MalformedURLException, IOException {
		return fetchURL(new URL(url));
	}

	/**
	 * Demonstrates the {@link #fetchURL(URL)} method.
	 *
	 * @param args unused
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String[] urls = new String[] {
				"http://www.cs.usfca.edu/",  // 302 -> https
				"https://www.cs.usfca.edu/", // 302 -> myusf
				"https://www.cs.usfca.edu/~cs212/", // 200
				"https://www.cs.usfca.edu/~cs212/simple/double_extension.html.txt", // text/plain
				"https://www.cs.usfca.edu/~cs212/nowhere.html" // 404
		};

		for (String url : urls) {
			var results = fetchURL(url);

			System.out.println(url);

			for (var entry : results.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}

			System.out.println();
		}
	}

}