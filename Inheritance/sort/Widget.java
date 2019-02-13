/**
 * A simple class that knows how to compare against other objects of this class
 * by implementing the {@link Comparable} interface.
 */
public class Widget implements Comparable<Widget> {

	/** The widget ID. */
	public int id;

	/** The widget name. */
	public String name;

	/**
	 * Initializes the widget.
	 *
	 * @param id the widget id
	 * @param name the widget name
	 */
	public Widget(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * By default, {@link #Widget} objects will be sorted by the widget id.
	 */
	@Override
	public int compareTo(Widget other) {
		return Integer.compare(this.id, other.id);
	}
}
