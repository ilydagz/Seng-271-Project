import java.util.*;

public class Main {
	public static void main(String[] args) {

		String instructorsFile = "instructors.txt";
		String classroomsFile = "classrooms.txt";
		String coursesFile = "courses2.txt";

		List<Instructor> instructors = CourseScheduler.loadInstructorsFromFile(instructorsFile);
		List<Classroom> classrooms = CourseScheduler.loadClassroomsFromFile(classroomsFile);
		List<Course> courses = CourseScheduler.loadCoursesFromFile(coursesFile, instructors, classrooms);

		CourseScheduler.generateSchedule(courses, instructors, classrooms);
	}
}
