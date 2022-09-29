import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * A class represents a calendar which holds all scheduled events (recurring, and one-time)
 * @author Quang Le
 */
public class MyCalendar {
	
	private ArrayList<Event> recurringEvents;	// An ArrayList stores only recurring events 
	private ArrayList<Event> allEvents;			// An ArrayList stores all events including recurring and one-time events
	
	/**
	 * Constructs a calendars with two ArrayLists to store events
	 */
	public MyCalendar() {
		recurringEvents = new ArrayList<Event>();
		allEvents = new ArrayList<Event>();
	}
	
	// Getter methods
	public ArrayList<Event> getRecurringEvents() {
		return recurringEvents;
	}
	
	public ArrayList<Event> getAllEvents() {
		return allEvents;
	}
	
	/**
	 * Sort the allEvents array
	 */
	public void sortAllEvents() {
		allEvents.sort(new EventComparator());
	}
	
	/**
	 * Sort the recurringEvents array
	 */
	public void sortRecurringEvents() {
		recurringEvents.sort(new EventComparator());
	}
	
	/**
	 * Add an event to appropriate ArrayList(s)
	 * @param recurring - whether this event is recurring or not
	 * @param name - name of the event
	 * @param startDate - start date of the event
	 * @param endDate - end date of the event
	 * @param startTime - start time of the event
	 * @param endTime - end time of the event
	 * @return the event that is added to ArrayList(s)
	 */
	public Event add(Boolean recurring, String name, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
		Event conflictEvent = null;
		
		// Create time interval for the event
		TimeInterval timeInterval = new TimeInterval(startTime, endTime);
		
		// Add to recurringEvent array if it's recurring
		if (recurring) {
			Event existEvent = findRecurEvent(name); // Check if it's already in the recurringEvents array
			
			// Create event and add to recurringEvent array if it's not in the array yet
			if ( existEvent == null) {
				recurringEvents.add(new Event(recurring, name, startDate, endDate, timeInterval));
			}
			// Update the event in the recurringEvent array if it's already in it
			else {
				String letterDOWRep = existEvent.letterDayOfWeek(startDate);
				// Only add that representing letter for day of week once
				if (!existEvent.getDayOfWeekReps().contains(letterDOWRep)) {
					existEvent.addDayOfWeekReps(letterDOWRep);
				}
			}
		}
		
		// Checking if the date and time conflicts with another events
		for (Event e : allEvents) {
			if (startDate.equals(e.getStartDate())) {
				if (e.getTime().overlap(timeInterval)) {
					conflictEvent = e;
					return conflictEvent; 	// Return if there's a conflicted event
				}
			}
		}
		// Add to allEvents array either recurring or one-time, and not conflicted with any other events
		allEvents.add(new Event(recurring, name, startDate, endDate, timeInterval));
		return conflictEvent;
	}
	
	/**
	 * Delete all one-time events on a specific date
	 * @param date - a specific date 
	 * @return true if there are some events deleted on that date, false if not
	 */
	public Boolean deleteOneTimeEventsByDate(LocalDate date) {
		ArrayList<Event> eventsRemove = new ArrayList<>();
		Boolean hasEvent = false;
		for (Event e : allEvents) {
			if (e.getStartDate().equals(date) && !e.getRecurring()) {
				eventsRemove.add(e);
				hasEvent = true;
			}
		}
		for (Event e : eventsRemove) {
			allEvents.remove(e);
			System.out.println("Event: " + e.getName() + " - Deleted!");
		}
		return hasEvent;
	}
	
	/**
	 * Delete a specific recurring event in the allEvents array
	 * @param targetEvent - a recurring event user wants to delete
	 */
	public void deleteRecurringEventsInAllEventsArr(Event targetEvent) {
		ArrayList<Event> eventsToBeRemoved = new ArrayList<>();
		for (Event e : allEvents) {
			if (e.getName().equals(targetEvent.getName()) && e.getEndDate() == targetEvent.getEndDate()) {
				eventsToBeRemoved.add(e);
			}
		}
		for (Event e : eventsToBeRemoved) {
			allEvents.remove(e);
		}
	}

	/**
	 * Find an event in the recurringEvent array
	 * @param name - name of the event
	 * @return that event if exists in the recurringEvents array, null if not
	 */
	public Event findRecurEvent(String name) {
		for (Event e : recurringEvents) {
			if (name.equals(e.getName())) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Find all events on a specific day (including both recurring and one-time events)
	 * @param day - a specific date
	 * @return ArrayList includes all events
	 */
	public ArrayList<Event> findEventsInADay(LocalDate day) {
		ArrayList<Event> eventsOfDay = new ArrayList<Event>();
		for (Event e : allEvents) {
			if (day.equals(e.getStartDate())) {
				eventsOfDay.add(e);
			}
		}
		return eventsOfDay;
	}
	
	/**
	 * Find all one-time events on a specific day (doesn't include recurring events)
	 * @param day - a specific date
	 * @return ArrayList includes all events
	 */
	public ArrayList<Event> findOneTimeEventsInADay(LocalDate day) {
		ArrayList<Event> eventsOfDay = new ArrayList<Event>();
		for (Event e : allEvents) {
			if (day.equals(e.getStartDate()) && !e.getRecurring()) {
				eventsOfDay.add(e);
			}
		}
		return eventsOfDay;
	}
	
	/**
	 * Check if a specific day has any event
	 * @param day - a specific date
	 * @return true if it has event, false if not
	 */
	public Boolean isDayHasEvent(LocalDate day) {
		for (Event e : allEvents) {
			if (day.equals(e.getStartDate())) {
				return true;
			}
		}
		return false;
	}
}
