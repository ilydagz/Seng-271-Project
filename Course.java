public class Course {
	private String name;
	private Instructor instructor;
	private int capacity;
	private Classroom classroom;
	private int theoreticalHours;
	private int practicalHours;
	private int yearLevel;
	private boolean isCommon;
	private boolean isMandatory;
	private String semester;
	
	public Course(String name, Instructor instructor, int capacity, Classroom classroom, int theoreticalHours,
            int practicalHours, int yearLevel, boolean isCommon, boolean isMandatory, String semester) {
		this.name = name;
		this.instructor = instructor;
		this.capacity = capacity;
		this.classroom = classroom;
		this.theoreticalHours = theoreticalHours;
		this.practicalHours = practicalHours;
		this.yearLevel = yearLevel;
		this.isCommon = isCommon;
		this.isMandatory = isMandatory;
		this.semester = semester;
	}

	public String getName() {
		return name;
	}

	public Instructor getInstructor() {
		return instructor;
	}

	public Classroom getClassroom() {
		return classroom;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getTheoreticalHours() {
		return theoreticalHours;
	}

	public int getPracticalHours() {
		return practicalHours;
	}

	public int getYearLevel() {
		return yearLevel;
	}

	public boolean isCommon() {
		return isCommon;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	
	public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
