public class LabClassroom extends Classroom {
	private int capacity;

	public LabClassroom(String name, int capacity, boolean isAvailable) {
		super(name, isAvailable, capacity);
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}
}
