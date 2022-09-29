import java.util.Comparator;

/**
 * A class implements Comparator for sorting events which are hold in ArrayLists 
 * @author Quang Le
 */
public class EventComparator implements Comparator<Event>{
	public int compare(Event event1, Event event2) {
		if (event1.getStartDate().compareTo(event2.getStartDate()) != 0) {
			return event1.getStartDate().compareTo(event2.getStartDate());
		}
		else
			return event1.getTime().getStartTime().compareTo(event2.getTime().getStartTime());
		
	}
}
