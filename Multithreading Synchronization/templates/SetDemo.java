import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetDemo {
	private static final Logger log = LogManager.getLogger();

	public static List<Integer> generateData(int size) {
		return new Random().ints(size).boxed().collect(Collectors.toList());
	}

	public static long timeMulti(List<Integer> source, IndexedSet<Integer> destination) {
		long time = System.nanoTime();

		Thread adder = new Thread(() -> {
			// TODO
			// log.debug("destination size is {}", destination.size());
		});

		Thread copy1 = new Thread(() -> {
			// TODO
			// log.debug("sorted size is {}", sorted.size());
		});

		Thread copy2 = new Thread(() -> {
			// TODO
			// log.debug("unsorted size is {}", unsorted.size());
		});

		adder.setPriority(Thread.MAX_PRIORITY);
		copy1.setPriority(Thread.NORM_PRIORITY);
		copy2.setPriority(Thread.NORM_PRIORITY);

		adder.start();
		copy1.start();
		copy2.start();

		try {
			adder.join();
			copy1.join();
			copy2.join();
		}
		catch (InterruptedException e) {
			log.catching(Level.DEBUG, e);
		}

		return System.nanoTime() - time;
	}

	public static long timeSingle(List<Integer> source, IndexedSet<Integer> destination) {
		long time = System.nanoTime();

		destination.addAll(source);
		log.debug("destination size is {}", destination.size());

		Set<Integer> sorted = destination.sortedCopy();
		log.debug("sorted size is {}", sorted.size());

		Set<Integer> unsorted = destination.unsortedCopy();
		log.debug("unsorted size is {}", unsorted.size());

		return System.nanoTime() - time;
	}

	public static void main(String[] args) {

		// TURN OFF LOGGING BEFORE RUNNING THIS!

		int size = 100; // 1000000;
		List<Integer> data = generateData(size);

		IndexedSet<Integer> set1 = new IndexedSet<>();
		SynchronizedSet<Integer> set2 = new SynchronizedSet<>();
		ConcurrentSet<Integer> set3 = new ConcurrentSet<>();

		double time1 = timeSingle(data, set1) / (double) 1000000000;
		System.out.printf("Indexed     : %.5f seconds%n", time1);

		double time2 = timeMulti (data, set2) / (double) 1000000000;
		System.out.printf("Synchronized: %.5f seconds%n", time2);

		double time3 = timeMulti (data, set3) / (double) 1000000000;
		System.out.printf("Concurrent  : %.5f seconds%n", time3);

		double speed1 = time1 / time2;
		double speed2 = time2 / time3;
		double speed3 = time1 / time3;

		System.out.println();

		String format = "%-12s is %.4fx %s than %-12s%n";
		System.out.printf(format, "Synchronized", speed1, time2 < time1 ? "faster" : "slower", "Indexed");
		System.out.printf(format, "Concurrent",   speed3, time3 < time1 ? "faster" : "slower", "Indexed");
		System.out.printf(format, "Concurrent",   speed2, time3 < time2 ? "faster" : "slower", "Synchronized");
	}
}
