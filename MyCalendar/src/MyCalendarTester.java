import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.format.DateTimeFormatter;

import java.io.*;
import java.util.Scanner;

import java.util.ArrayList;

/**
 * Programming Assignment 1 - My First Calendar
 * @author Quang Le
 * Last update: 09/11/2022
 */
public class MyCalendarTester {
	public static LocalDate todayCal;
	public static LocalDate firstDayOfMonthCal;
	public static MyCalendar calendar;
	public static String fileName = new String("events.txt");
	public static Scanner sc;
	public static int spaceNeeded;
	
	/**
	 * A main method to test all functionalities of a calendar
	 * @param args
	 */
	public static void main(String[] args) {
		
		todayCal = LocalDate.now();
		firstDayOfMonthCal = LocalDate.of(todayCal.getYear(), todayCal.getMonth(), 1);
		calendar = new MyCalendar();
		
		printMonthCalendar(todayCal, firstDayOfMonthCal);
		
		readFile(fileName);	// Read events.txt file, and populate calendar by adding all events
		calendar.sortAllEvents();
		calendar.sortRecurringEvents();
		
		System.out.println("Loading is done!");
		
		sc = new Scanner(System.in);
		System.out.print("\nMAIN MENU: [V]iew by, [C]reate, [G]o to, [E]vent list, [D]elete, [Q]uit \n - ");
		String input = sc.nextLine();
		
		while (!input.equalsIgnoreCase("q")) {
			
			if (input.equalsIgnoreCase("v")) {
				viewBy();
			}
			else if (input.equalsIgnoreCase("c")) {
				Boolean success = create();
				// Call the create method again to try to create event, this is because it fails due to conflict time
				while (!success) {
					success = create();
				}
			}
			else if (input.equalsIgnoreCase("g")) {
				goTo();
			}
			else if (input.equalsIgnoreCase("e")) {
				eventList();
			}
			else if (input.equalsIgnoreCase("d")) {
				delete();
			}
			else {
				System.out.println("\nError! Please Enter your choice again!");
			}
			
			System.out.print("\nMAIN MENU: [V]iew by, [C]reate, [G]o to, [E]vent list, [D]elete, [Q]uit \n - ");
			input = sc.nextLine();
		}
		
		saveFile();	// Save all events to a file
			
	}
	
	/**
	 * Print out the month calendar
	 * @param today - a LocalDate object of today
	 * @param firstDayOfMonth - a LocalDate object of the first day of the month
	 */
	public static void printMonthCalendar(LocalDate today, LocalDate firstDayOfMonth) {
		int currentMonthValue = firstDayOfMonth.getMonthValue();
		LocalDate nextDay = firstDayOfMonth.plusDays(1);
		
		System.out.println(firstDayOfMonth.getMonth() + " " + firstDayOfMonth.getYear());
		String dayHeader = new String("Su   Mo   Tu   We   Th   Fr   Sa");
		System.out.println(dayHeader);
		
		// Get the first 2 letters of day of week of the first day in month
		String initialDOW = firstDayOfMonth.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).substring(0,2);
		
		// Find the initial index for the first day of month in the calendar
		int initialIndex = dayHeader.indexOf(initialDOW);
		String space = new String(" ");
		
		while (initialIndex > 0) {
			System.out.print(" ");
			initialIndex--;
		}
		printDay(firstDayOfMonth);
		
		// Continue printing out the other dates of month in calendar
		while (nextDay.getMonthValue() == currentMonthValue) {
			// Space between dates
			while (spaceNeeded > 0) {
				System.out.print(" ");
				spaceNeeded--;
			}
			// Go to next line if it's Sunday
			if (nextDay.getDayOfWeek().getValue() == 7) {
				System.out.println();
			}
		
			printDay(nextDay);
			
			if (nextDay.getDayOfMonth() >= 10) {
				spaceNeeded--;
			}
			
			// Get the next day of calendar
			nextDay = nextDay.plusDays(1);
		}
	}
	
	/**
	 * Print out each date of month in month calendar 
	 * @param day - a LocalDate object of the date to be printed out
	 */
	public static void printDay(LocalDate day) {
		// The first month calendar will use [] for today
		if (day.equals(todayCal) && calendar.getAllEvents().isEmpty()) {
			System.out.print("[" + day.getDayOfMonth() + "]");
			spaceNeeded = 2;
		}
		else if (calendar.getAllEvents().isEmpty()) {
			System.out.print(day.getDayOfMonth());
			spaceNeeded = 4;
		}
		// Any month calendar after the first one will use {} to mark dates with events
		else if (!calendar.getAllEvents().isEmpty()) {
			if (calendar.isDayHasEvent(day)) {
				System.out.print("{" + day.getDayOfMonth() + "}");
				spaceNeeded = 2;
			}
			else {
				System.out.print(day.getDayOfMonth());
				spaceNeeded = 4;
			}
		}
	}
	
	/**
	 * Read file of initial events and load them to calendar
	 * @param name - the name of events' file
	 * @throws FileNotFOundException if system can't find the file
	 */
	public static void readFile(String name) {
		try {
			File file = new File(name);
			sc = new Scanner(file);
			System.out.println("\n");
			while (sc.hasNextLine()) {
				String line1 = sc.nextLine();
				String line2 = sc.nextLine();
				if (Character.isDigit(line2.charAt(0))) {
					addOneEvent(line1, line2);	// Add event to one-time event array
				}
				else {
					addRecurEvent(line1, line2);	// Add event to recurring event array
				}
			}
			sc.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("Error! File doeesn't exists.");
		}
		
	}
	
	/**
	 * Add the one-time event from the file to our data structures (array list)
	 * @param name - string name from the event file
	 * @param time -  string time from the event file
	 */
	public static void addOneEvent(String name, String time) {
		String[] listStr = time.split(" ");
		String startDateStr = listStr[0];
		String startTimeStr = listStr[1];
		String endTimeStr = listStr[2];
		
		LocalDate startDateCal = convertToLocalDate(startDateStr);
		
		LocalTime startTimeCal = convertToLocalTime(startTimeStr);
		
		LocalTime endTimeCal = convertToLocalTime(endTimeStr);
		
		calendar.add(false, name, startDateCal, null, startTimeCal, endTimeCal);
	}
	
	/**
	 * Add the recurring event from the file to our data structures (array list)
	 * @param name - string name from the event file
	 * @param time - string time from the event file
	 */
	public static void addRecurEvent(String name, String time) {
		String[] listStr = time.split(" ");
		String dayOfWeekStr = listStr[0];
		String startTimeStr = listStr[1];
		String endTimeStr = listStr[2];
		String startDateStr = listStr[3];
		String endDateStr = listStr[4];
		
		LocalDate startDateCal = convertToLocalDate(startDateStr);
		LocalDate startDateCalCopy = startDateCal; 	// since LocalDate's immutable, we can get a copy it using this way
		
		LocalDate endDateCal = convertToLocalDate(endDateStr);
		LocalTime startTimeCal = convertToLocalTime(startTimeStr);
		LocalTime endTimeCal = convertToLocalTime(endTimeStr);
		
		// Add the recurring event every 7 days starting from the first date until the ending date
		while (startDateCal.isBefore(endDateCal)) {
			calendar.add(true, name, startDateCal, endDateCal, startTimeCal, endTimeCal);
			startDateCal = startDateCal.plusDays(7);
		}
		// Reset the startDate
		startDateCal = startDateCalCopy;
		
		// If there is other dates in the same week, continue adding it every 7 days
		for (int i=0; i < dayOfWeekStr.length()-1; i++) {
			
			int numDaysBetween = calculateDaysBetween(dayOfWeekStr.charAt(0), dayOfWeekStr.charAt(i+1));
			startDateCal = startDateCal.plusDays(numDaysBetween);
			
			while (startDateCal.isBefore(endDateCal)) {
				calendar.add(true, name, startDateCal, endDateCal, startTimeCal, endTimeCal);
				startDateCal = startDateCal.plusDays(7);
			}
		}	
	}
	
	/**
	 * Calculate the number of dates between two dates in the same week
	 * @param c1 - first letter of the first date
	 * @param c2 - second letter of the second date
	 * @return the number of dates 
	 */
	public static int calculateDaysBetween(Character c1, Character c2) {
		int c1Value = findValueCharacter(c1);
		int c2Value = findValueCharacter(c2);
		return c2Value - c1Value;
	}
	
	/**
	 * Find the value of the letter representing the day in the week
	 * @param c - letter represents the day of week
	 * @return the value
	 */
	public static int findValueCharacter(Character c) {
		if (c.equals('S')) return 0;
		else if (c.equals('M')) return 1;
		else if (c.equals('T')) return 2;
		else if (c.equals('W')) return 3;
		else if (c.equals('R')) return 4;
		else if (c.equals('F')) return 5;
		else return 6;
	}
	
	/**
	 * Convert a date string to LocalDate object
	 * @param str - string of date formatting MM/DD/YY
	 * @return LocalDate object
	 */
	public static LocalDate convertToLocalDate(String str) {
		String[] dateComponents = str.split("/");
		int month = Integer.parseInt(dateComponents[0]);
		int date = Integer.parseInt(dateComponents[1]);
		int year = Integer.parseInt(dateComponents[2]) + 2000;
		return LocalDate.of(year, month, date);
	}
	
	/** 
	 * Convert a time string LocalTime object
	 * @param str - string of time formatting HH:MM
	 * @return LocalTime object
	 */
	public static LocalTime convertToLocalTime(String str) {
		String[] startTimeComponents = str.split(":");
		int hour = Integer.parseInt(startTimeComponents[0]);
		int minute = Integer.parseInt(startTimeComponents[1]);
		return LocalTime.of(hour, minute);
	}
	
	/**
	 * The [V]iew by option from main menu
	 */
	public static void viewBy() {
		System.out.print("[D]ay view or [M]onth view? - ");
		String input = sc.nextLine();
		String userChoice = new String();
		while (!input.equalsIgnoreCase("d") && !input.equalsIgnoreCase("m")) {
			System.out.println(input);
			System.out.println("Error! Please enter your choice again!");
			System.out.print("[D]ay view or [M]onth view? - ");
			input = sc.nextLine();
		}
		// User chooses day view
		if (input.equalsIgnoreCase("d")) {
			do {
				if (userChoice.equalsIgnoreCase("p")) {
					todayCal = todayCal.minusDays(1);
				}
				else if (userChoice.equalsIgnoreCase("n")) {
					todayCal = todayCal.plusDays(1);
				}
				dayView(todayCal, false);
				userChoice = additionalViewByOptions();
				
			} while (!userChoice.equalsIgnoreCase("g"));
			
			
		}
		// User chooses month view
		else {
			do {
				if (userChoice.equalsIgnoreCase("p")) {
					todayCal = todayCal.minusMonths(1);
				}
				else if (userChoice.equalsIgnoreCase("n")) {
					todayCal = todayCal.plusMonths(1);
				}
				firstDayOfMonthCal = LocalDate.of(todayCal.getYear(), todayCal.getMonth(), 1);
				printMonthCalendar(todayCal, firstDayOfMonthCal);
				userChoice = additionalViewByOptions();
			} while (!userChoice.equalsIgnoreCase("g"));
			
		}
		
		// Reset todayCal and firstDayOfMonthCal
		todayCal = LocalDate.now();
		firstDayOfMonthCal = LocalDate.of(todayCal.getYear(), todayCal.getMonth(), 1);
	}
	
	/**
	 * Display the date along with scheduled events
	 * @param day - a specific date
	 * @param onlyOneTimeEvents - it includes one-time events only (true) or all events (false)
	 * @return
	 */
	public static ArrayList<Event> dayView(LocalDate day, Boolean onlyOneTimeEvents) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM d yyyy");
		System.out.println("\n " + formatter.format(day));
		ArrayList<Event> listEvents;
		if (!onlyOneTimeEvents) {
			listEvents = calendar.findEventsInADay(day);
		}
		else listEvents = calendar.findOneTimeEventsInADay(day);
		
		if (listEvents != null) {
			for (Event e : listEvents) {
				System.out.println(e.getName() + " : " + e.getTime().getStartTime() + " - " + e.getTime().getEndTime());
			}
		}
		return listEvents;
	}
	
	/** 
	 * Additional View by options for user to choose after choosing View by
	 * @return string represents option
	 */
	public static String additionalViewByOptions() {
		System.out.print("\n\n[P]revious or [N]ext or [G]o back to the main menu ? - ");
		String input = sc.nextLine();
		
		// Asking user to enter again if it's not an appropriate option
		while (!input.equalsIgnoreCase("p") && !input.equalsIgnoreCase("n") && !input.equalsIgnoreCase("g")) {
			System.out.println("Error! Please Enter your choice again!");
			System.out.print("\n[P]revious or [N]ext or [G]o back to the main menu ? - ");
			input = sc.nextLine();
		}
		return input;
	}
	
	/** 
	 * Create event option from main menu
	 * @return true if creating successfully, false if failing create the event due to conflict with an existing event
	 */
	public static Boolean create() {
		System.out.print("\nEnter the name for event: ");
		String name = sc.nextLine();
		LocalDate date = getDateFromUser();
		System.out.print("Enter start time ");
		LocalTime startTime = getTimeFromUser();
		System.out.print("Enter end time ");
		LocalTime endTime = getTimeFromUser();
		Event conflictEvent = null;
		
		// Validate the start and end time
		while (endTime.isBefore(startTime)) {
			System.out.println("Error! The end time you entered is before the start time");
			System.out.println("Please enter start time and end time for this event again!");
			System.out.print("Enter start time ");
			startTime = getTimeFromUser();
			System.out.print("Enter end time ");
			endTime = getTimeFromUser();
			
		}
		// Try creating the event
		conflictEvent = calendar.add(false, name, date, null, startTime, endTime);
		while (conflictEvent != null) {
			System.out.println("Attention: Conflict time - Can't create this event!");
			// Print out the existed conflict event
			System.out.println("This is the conflicted event: ");
			System.out.println(conflictEvent.getName() + " : " + conflictEvent.getTime().getStartTime() + " - " + conflictEvent.getTime().getEndTime());
			System.out.println("Please enter the information of this event again!");
			return false;
		}
		
		System.out.println("Successfully created event name '" + name + "'!");
		
		calendar.sortAllEvents();	// Sort the ArrayList after adding new event
		return true;
	}
	
	/**
	 * Go to a specific date option from main menu
	 */
	public static void goTo() {
		LocalDate date = getDateFromUser();
		dayView(date, false);
	}
	
	/**
	 * Event list option from main menu. (List all one-time and recurring events)
	 */
	public static void eventList() {
		int startYear = -1;
		System.out.println("\nONE-TIME EVENTS:");
		for (Event e : calendar.getAllEvents()) {
			LocalDate day = e.getStartDate();
			if (day.getYear() != startYear) {
				System.out.println("* " + day.getYear() + " *");
				startYear = e.getStartDate().getYear();
			}
			if (!e.getRecurring()) {
				System.out.println(day.getDayOfWeek() + ", " + day.getMonth() + " " + day.getDayOfMonth() + " " + day.getYear() + ": " + e.getName() + ", " + e.getTime().getStartTime() + " - " + e.getTime().getEndTime());
			}
		}
		
		System.out.println("\nRECURRING EVENTS");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		for (Event e : calendar.getRecurringEvents()) {
			System.out.println(e.getName());
			System.out.println(e.getDayOfWeekReps() + " " + e.getTime().getStartTime() + " " + e.getTime().getEndTime() + " " + e.getStartDate().format(formatter) + " " + e.getEndDate().format(formatter) + "\n");
		}
	}
	
	/**
	 * Delete option from main menu
	 */
	public static void delete() {
		String userChoice = additionalDeleteOptions();
		LocalDate date;
		Boolean hasEvent;
		Event foundEvent = null;
		// User chooses to delete an event by specifying the date and the name of it
		if (userChoice.equalsIgnoreCase("s")) {
			date = getDateFromUser();
			ArrayList<Event> events = dayView(date, true);
			if (events.size() > 0) {
				System.out.print("\nPlease enter the name of the event you want to delete: - ");
				userChoice = sc.nextLine();
				for (Event e : events) {
					if (e.getName().equalsIgnoreCase(userChoice)) {
						foundEvent = e;
					}
				}
				if (foundEvent != null) {
					calendar.getAllEvents().remove(foundEvent);
					System.out.println("Successfully deleted one-time event name '" + foundEvent.getName() + "'!");
				}
				else System.out.println("Error! There isn't any one-time event name '" + userChoice + "' to be deleted!");
				
			}
			else {
				System.out.println("There is no one-time event on this date to delete!");
			}
		}
		
		// User chooses to delete all scheduled one-time events in a specific date
		else if (userChoice.equalsIgnoreCase("a")) {
			date = getDateFromUser();
			hasEvent = calendar.deleteOneTimeEventsByDate(date);
			if (hasEvent) {
				System.out.println("Successfully deleted all one-time events on this date.");
			}
			else System.out.println("There isn't any one-time event on this date to be deleted!");
			
		}
		
		// User chooses to delete a recurring event by specifying its name
		else {
			System.out.print("Please enter the name of the recurring event to delete: ");
			userChoice = sc.nextLine();
			for (Event e : calendar.getRecurringEvents()) {
				if (e.getName().equalsIgnoreCase(userChoice)) {
					foundEvent = e;
				}
			}
			
			if (foundEvent != null) {
				// Remove it from the recurring event array
				calendar.getRecurringEvents().remove(foundEvent);
				
				// Remove it from the all event array
				calendar.deleteRecurringEventsInAllEventsArr(foundEvent);
				System.out.println("Successfully deleted recurring event name '" + foundEvent.getName() + "'!");
			}
			else {
				System.out.println("Error! There isn't any recurring event name '" + userChoice + "' to be deleted!");
			}
			
		}
	}
	
	/**
	 * Additional delete options after user chooses Delete option from main menu
	 * @return
	 */
	public static String additionalDeleteOptions() {
		System.out.print("\n[S]elected a date and name, [A]ll one-time events on a specific date, [DR] for deleting a recurring event.\n- ");
		String input = sc.nextLine();
		
		// Making sure user enter an appropriate choice
		while (!input.equalsIgnoreCase("s") && !input.equalsIgnoreCase("a") && !input.equalsIgnoreCase("dr")) {
			System.out.println("Error! Please Enter your choice again!");
			System.out.print("\n[S]elected a date and name of an one-time event, [A]ll one-time events on a specific date, [DR] for deleting a recurring event.\n- ");
			input = sc.nextLine();
		}
		return input;
	}
	
	
	/**
	 * Get a date from user following format MM/DD/YYYY
	 * @return LocalDate object for the date
	 */
	public static LocalDate getDateFromUser() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		System.out.println("\nEnter the date following format MM/DD/YYYY");
		String input = sc.nextLine();
		LocalDate date;
		try {
			date = LocalDate.parse(input, formatter);
			
		}
		catch (Exception e ) {
			System.out.println("Error! Please follow the format and enter the correct date again!");
			date = getDateFromUser();
		}
		return date;
	}
	
	/**
	 * Get a time from user following format HH:MM 
	 * @return LocalTime object for the time
	 */
	public static LocalTime getTimeFromUser() {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
		System.out.println("following format HH:MM");
		String input = sc.nextLine();
		LocalTime time;
		try {
			time = LocalTime.parse(input, formatter);
			
		}
		catch (Exception e ) {
			System.out.println("Error! Please follow the format and enter the correct time again!");
			System.out.print("Enter end time ");
			time = getTimeFromUser();
		}
		return time;
	}
	
	/**
	 * Save all scheduled events to the file that was used to load all data in the beginning
	 */
	public static void saveFile() {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy"); 
			File output = new File(fileName);
			if (!output.exists()) {
				output.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(output);
			BufferedWriter bufferWritter = new BufferedWriter(fileWriter);
			// Save recurring events first
			for (Event e : calendar.getRecurringEvents()) {
				bufferWritter.write(e.getName());
				bufferWritter.newLine();
				bufferWritter.write(e.getDayOfWeekReps() + " " + e.getTime().getStartTime() + " " + e.getTime().getEndTime() + " " + e.getStartDate().format(formatter) + " " + e.getEndDate().format(formatter));
				bufferWritter.newLine();
			}
			// Save one-time events
			for (Event e : calendar.getAllEvents()) {
				if (!e.getRecurring()) {
					bufferWritter.write(e.getName());
					bufferWritter.newLine();
					bufferWritter.write(e.getStartDate().format(formatter) + " " + e.getTime().getStartTime() + " " + e.getTime().getEndTime());
					bufferWritter.newLine();
				}
			}
			bufferWritter.close();
			System.out.println("All events have been saved to the file!");
		}
		catch (IOException e) {
			System.out.println("FAIL");
			}
		}

}
