public class Classroom {
	private String name;
	private boolean isAvailable;
	private int capacity;

	public Classroom(String name, boolean isAvailable, int capacity) {
		this.name = name;
		this.isAvailable = isAvailable;
		this.capacity = capacity;
	}

	public String getName() {
		return name;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public int getCapacity() {
		return capacity;
	}
}
