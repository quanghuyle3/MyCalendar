import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * A class represent an event with all of its information
 * @author Quang Le
 */
public class Event {
	
	private Boolean recurring; 	// A boolean field - true if it's a recurring event, false if it's one-time event
	private String name;
	private LocalDate startDate;
	private LocalDate endDate;
	private TimeInterval time;
	private String dayOfWeekReps; // A representing string consists of all abbreviation letters of day of week in a week (SMTWRFA)
	
	/**
	 * Constructs an event with all necessary information 
	 * @param recurring - true if it's recurring, false if not
	 * @param name - name of the event
	 * @param startDate - the start date of the event
	 * @param endDate - the end date of the event
	 * @param time - the time interval of the event
	 */
	public Event(Boolean recurring, String name, LocalDate startDate, LocalDate endDate, TimeInterval time) {
		this.recurring = recurring;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.time = time;
		dayOfWeekReps = letterDayOfWeek(startDate);
	}
	
	// Getter and setter methods
	public Boolean getRecurring() {
		return recurring;
	}
	
	public String getName() {
		return name;
	}
	
	public LocalDate getStartDate() {
		return startDate;
	}
	
	public LocalDate getEndDate() {
		return endDate;
	}
	
	public TimeInterval getTime() {
		return time;
	}
	
	public String getDayOfWeekReps() {
		return dayOfWeekReps;
	}
	
	public void setRecurring(Boolean recurring) {
		this.recurring = recurring;
	}
	
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
	public void setTime(TimeInterval time) {
		this.time = time;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDayOfWeekReps(String dayOfWeekReps) {
		this.dayOfWeekReps = dayOfWeekReps;
	}
	
	public void addDayOfWeekReps(String s) {
		dayOfWeekReps += s;
	}
	
	/**
	 * Get the first representing letter of a day of week 
	 * @param date - a specific date
	 * @return the representing letter for a day of week
	 */
	public String letterDayOfWeek(LocalDate date) {
		String shortVersion = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
		if (shortVersion.equals("Sun")) return "S";
		else if (shortVersion.equals("Mon")) return "M";
		else if (shortVersion.equals("Tue")) return "T";
		else if (shortVersion.equals("Wed")) return "W";
		else if (shortVersion.equals("Thu")) return "R";
		else if (shortVersion.equals("Fri")) return "F";
		else return "A";
		
	}
}
