public class RegularClassroom extends Classroom {
	private int capacity;

	public RegularClassroom(String name, int capacity, boolean isAvailable) {
		super(name, isAvailable, capacity);
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}
}