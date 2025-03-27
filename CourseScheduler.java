import java.io.*;
import java.util.*;

public class CourseScheduler {

	public static void generateSchedule(List<Course> courses, List<Instructor> instructors,
			List<Classroom> classrooms) {

		Map<Integer, Map<String, String[]>> schedule = new HashMap<>();
		String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
		String[] times = { "09:20-10:10", "10:20-11:10", "11:20-12:10", "13:20-14:10", "14:20-15:10", "15:20-16:10",
				"16:20-17:10" };

		for (int year = 1; year <= 4; year++) {
			schedule.put(year, new HashMap<>());
			for (String day : days) {
				schedule.get(year).put(day, new String[7]);
			}
		}

		courses = sortCoursesByPriority(courses);
		courses = sortCoursesByPriority(courses);
		courses = prioritizedShuffle(courses);
		checkForConflicts(schedule, times);

		Map<String, Map<String, Integer>> teacherDailyLoad = new HashMap<>();

		for (Course course : courses) {
		    boolean theoreticalScheduled = scheduleCourse(course, schedule, teacherDailyLoad,
		            course.getTheoreticalHours(), times, days, true);
		    boolean practicalScheduled = scheduleCourse(course, schedule, teacherDailyLoad,
		            course.getPracticalHours(), times, days, false);

		    if (!theoreticalScheduled || !practicalScheduled) {
		        System.out.println("Conflict: Could not place course: " + course.getName() + 
		                           " (Reason: No available slots or classrooms)");
		    }
		}


		printScheduleByYear(schedule, times);
	}

	private static boolean scheduleCourse(Course course, Map<Integer, Map<String, String[]>> schedule,
	        Map<String, Map<String, Integer>> teacherDailyLoad, int hoursToSchedule, String[] times, String[] days,
	        boolean isTheoretical) {

	    int scheduledHours = 0;

	    for (String day : days) {
	        if (day.equals("Friday") && isTheoretical)
	            continue;

	        for (int timeIndex = 0; timeIndex < times.length; timeIndex++) {
	            if (scheduledHours >= hoursToSchedule)
	                return true;

	            String[] slots = schedule.get(course.getYearLevel()).get(day);

	            boolean isSlotAvailable = isTheoretical || (timeIndex + hoursToSchedule <= times.length);
	            if (slots[timeIndex] == null && isSlotAvailable
	                    && canTeacherTeachAtTime(course.getInstructor(), day, timeIndex, teacherDailyLoad)) {

	                boolean canPlaceAllPracticalHours = true;
	                if (!isTheoretical) {
	                    for (int i = 0; i < hoursToSchedule; i++) {
	                        if (timeIndex + i >= times.length || slots[timeIndex + i] != null) {
	                            canPlaceAllPracticalHours = false;
	                            break;
	                        }
	                    }
	                }

	                if (isTheoretical || canPlaceAllPracticalHours) {
	                    for (int i = 0; i < (isTheoretical ? 1 : hoursToSchedule); i++) {
	                        slots[timeIndex + i] = course.getName() + " (" + course.getInstructor().getName() + ", "
	                                + course.getClassroom().getName() + ", "
	                                + (isTheoretical ? "Theoretical" : "Practical") + ")";
	                        updateTeacherDailyLoad(course.getInstructor(), day, teacherDailyLoad);
	                    }
	                    scheduledHours += isTheoretical ? 1 : hoursToSchedule;
	                }
	            }
	        }
	    }

	    return scheduledHours >= hoursToSchedule;
	}


	private static boolean canTeacherTeachAtTime(Instructor instructor, String day, int timeIndex,
			Map<String, Map<String, Integer>> teacherDailyLoad) {
		teacherDailyLoad.putIfAbsent(instructor.getName(), new HashMap<>());
		teacherDailyLoad.get(instructor.getName()).putIfAbsent(day, 0);

		return teacherDailyLoad.get(instructor.getName()).get(day) < 4;
	}

	private static void updateTeacherDailyLoad(Instructor instructor, String day,
			Map<String, Map<String, Integer>> teacherDailyLoad) {
		teacherDailyLoad.get(instructor.getName()).put(day, teacherDailyLoad.get(instructor.getName()).get(day) + 1);
	}

	private static List<Course> sortCoursesByPriority(List<Course> courses) {
		courses.sort((c1, c2) -> {

			if (c1.getSemester().equalsIgnoreCase("fall") && !c2.getSemester().equalsIgnoreCase("fall")) {
				return -1;
			}
			if (!c1.getSemester().equalsIgnoreCase("fall") && c2.getSemester().equalsIgnoreCase("fall")) {
				return 1;
			}

			if (c1.isCommon() && c1.isMandatory() && (!c2.isCommon() || !c2.isMandatory())) {
				return -1;
			}
			if ((!c1.isCommon() || !c1.isMandatory()) && c2.isCommon() && c2.isMandatory()) {
				return 1;
			}

			if (c1.isCommon() && !c2.isCommon()) {
				return -1;
			}
			if (!c1.isCommon() && c2.isCommon()) {
				return 1;
			}

			if (c1.isMandatory() && !c2.isMandatory()) {
				return -1;
			}
			if (!c1.isMandatory() && c2.isMandatory()) {
				return 1;
			}

			return Integer.compare(c1.getYearLevel(), c2.getYearLevel());
		});
		return courses;
	}

	private static List<Course> prioritizedShuffle(List<Course> courses) {
		Map<Integer, List<Course>> priorityGroups = new HashMap<>();

		for (Course course : courses) {
			int priority = calculatePriority(course);
			priorityGroups.putIfAbsent(priority, new ArrayList<>());
			priorityGroups.get(priority).add(course);
		}

		List<Course> shuffledCourses = new ArrayList<>();
		for (List<Course> group : priorityGroups.values()) {
			Collections.shuffle(group);
			shuffledCourses.addAll(group);
		}

		return shuffledCourses;
	}

	private static int calculatePriority(Course course) {
		int priority = 0;
		if (course.isCommon() && course.isMandatory())
			priority += 10;
		if (course.getSemester().equalsIgnoreCase("fall"))
			priority += 5;
		if (course.isMandatory())
			priority += 3;
		return priority;
	}

	private static void checkForConflicts(Map<Integer, Map<String, String[]>> schedule, String[] times) {
		for (int year = 1; year <= 4; year++) {
			for (String day : new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" }) {
				String[] slots = schedule.get(year).get(day);
				for (int i = 0; i < slots.length; i++) {
					if (slots[i] != null && slots[i].contains(",")) {
						System.out.println("Conflict: Year " + year + ", " + day + " " + times[i]);
						System.out.println("Details: " + slots[i]);
					}

				}
			}
		}
	}

	private static void printScheduleByYear(Map<Integer, Map<String, String[]>> schedule, String[] times) {
		for (int year = 1; year <= 4; year++) {
			System.out.println("Year " + year + ":");
			for (String day : new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" }) {
				System.out.println(day + ":");
				String[] daySchedule = schedule.get(year).get(day);
				for (int i = 0; i < times.length; i++) {
					if (daySchedule[i] != null) {
						System.out.println("  " + times[i] + " -> " + daySchedule[i]);
					}
				}
			}
		}
	}

	public static List<Course> loadCoursesFromFile(String fileName, List<Instructor> instructors,
			List<Classroom> classrooms) {
		List<Course> courses = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;

				String[] details = line.split(",");
				if (details.length < 10)
					continue;

				String name = details[0].trim();
				int capacity = Integer.parseInt(details[2].trim());
				int theoreticalHours = Integer.parseInt(details[3].trim());
				int practicalHours = Integer.parseInt(details[4].trim());
				int yearLevel = Integer.parseInt(details[5].trim());
				String instructorName = details[6].trim();
				boolean isCommon = Boolean.parseBoolean(details[7].trim());
				boolean isMandatory = Boolean.parseBoolean(details[8].trim());
				String semester = details[9].trim();

				Instructor instructor = instructors.stream().filter(inst -> inst.getName().equals(instructorName))
						.findFirst().orElse(null);

				if (instructor != null) {
					for (Classroom classroom : classrooms) {
						if (classroom.isAvailable()) {
							classroom.setAvailable(false);
							Course course = new Course(name, instructor, capacity, classroom, theoreticalHours,
									practicalHours, yearLevel, isCommon, isMandatory, semester);
							courses.add(course);
							break;
						}
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return courses;
	}

	public static List<Instructor> loadInstructorsFromFile(String fileName) {
		List<Instructor> instructors = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;

				String[] details = line.split(",");
				if (details.length < 2)
					continue;

				String name = details[0].trim();
				Instructor instructor = new Instructor(name);
				instructors.add(instructor);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return instructors;
	}

	public static List<Classroom> loadClassroomsFromFile(String fileName) {
		List<Classroom> classrooms = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;

				String[] details = line.split(",");
				if (details.length < 4)
					continue;

				String name = details[0].trim();
				String type = details[1].trim();
				int capacity = Integer.parseInt(details[2].trim());
				boolean isAvailable = Boolean.parseBoolean(details[3].trim());

				Classroom classroom;
				if (type.equalsIgnoreCase("Lab")) {
					classroom = new LabClassroom(name, capacity, isAvailable);
				} else {
					classroom = new RegularClassroom(name, capacity, isAvailable);
				}
				classrooms.add(classroom);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classrooms;
	}
}
