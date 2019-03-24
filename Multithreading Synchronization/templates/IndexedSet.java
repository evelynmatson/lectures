import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class IndexedSet<E> {

	private final Set<E> set;

	public IndexedSet() {
		this(false);
	}

	public IndexedSet(boolean sorted) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public boolean add(E element) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public boolean addAll(Collection<E> elements) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public int size() {
		return set.size();
	}

	public boolean contains(E element) {
		return set.contains(element);
	}

	public E get(int index) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public String toString() {
		return set.toString();
	}

	public Set<E> unsortedCopy() {
		return new HashSet<E>(set);
	}

	public SortedSet<E> sortedCopy() {
		return new TreeSet<E>(set);
	}

	public static void main(String[] args) {
		ArrayList<String> elements = new ArrayList<>();
		Collections.addAll(elements, "ant", "fox", "fly", "bee");

		IndexedSet<String> sorted = new IndexedSet<>(true);
		IndexedSet<String> unsorted = new IndexedSet<>();

		sorted.addAll(elements);
		unsorted.addAll(elements);

		System.out.println(sorted);
		System.out.println(sorted.unsortedCopy());

		System.out.println(unsorted);
		System.out.println(unsorted.sortedCopy());

		System.out.println(sorted.get(0));
		System.out.println(unsorted.get(0));

		System.out.println(sorted.get(3));
		System.out.println(unsorted.get(3));

		System.out.println(sorted.get(10));
		System.out.println(unsorted.get(10));

	}
}
