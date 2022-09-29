import java.time.LocalTime;

/**
 * A class represents time interval of starting time and ending time of an event
 * @author Quang Le
 */
public class TimeInterval {
	private LocalTime startTime;
	private LocalTime endTime;
	
	/**
	 * Constructs a time interval using starting time and ending time
	 * @param startTime - the starting time
	 * @param endTime - the ending time
	 */
	public TimeInterval(LocalTime startTime, LocalTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	// Getter and setter methods
	public LocalTime getStartTime() {
		return startTime;
	}
	
	public LocalTime getEndTime() {
		return endTime;
	}
	
	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}
	
	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	
	/**
	 * Check if the current time interval overlaps with another time interval
	 * @param time2 - another time interval to compare
	 * @return true if overlaps, false if not
	 */
	public Boolean overlap(TimeInterval time2) {
		if (this.startTime.isAfter(time2.getStartTime()) && this.startTime.isBefore(time2.getEndTime())) {
			return true;
		}
		else if (this.endTime.isBefore(time2.getEndTime()) && this.endTime.isAfter(time2.getStartTime())) {
			return true;
		}
		else {
			return false;
		}
	}
	


}
