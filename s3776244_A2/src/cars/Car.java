package cars;

import exceptions.InvalidDate;
import utilities.DateTime;
import utilities.DateUtilities;
import utilities.MiRidesUtilities;

/*
 * Class:		Car
 * Description:	The class represents a car in a ride sharing system. 
 * Author:		Yuchen Wu - s3776244
 */
public class Car
{
	// Car attributes
	private String regNo;
	private String make;
	private String model;
	private String driverName;
	private int passengerCapacity;

	// Tracking bookings
	private Booking[] currentBookings;
	private Booking[] pastBookings;
	private boolean available;
	private int bookingSpotAvailable = 0;
	private double tripFee = 0;

	// Constants
	private final double STANDARD_BOOKING_FEE = 1.5;
	private final int MAXIUM_PASSENGER_CAPACITY = 10;
	private final int MINIMUM_PASSENGER_CAPACITY = 1;

	
	
		public Car(String regNo, String make, String model, String driverName, int passengerCapacity)

	{
		setRegNo(regNo); // Validates and sets registration number
		setPassengerCapacity(passengerCapacity); // Validates and sets passenger capacity

		this.make = make;
		this.model = model;
		this.driverName = driverName;
		available = true;
		currentBookings = new Booking[5];
		pastBookings = new Booking[10];
	}

	/*
	 * Checks to see if the booking is permissible such as a valid date, number of
	 * passengers, and general availability. Creates the booking only if conditions
	 * are met and assigns the trip fee to be equal to the standard booking fee.
	 */

	/*
	 * ALGORITHM BEGIN CHECK if car has five booking CHECK if car has a booking on
	 * date requested CHECK if the date requested is in the past. CHECK if the
	 * number of passengers requested exceeds the capacity of the car. IF any checks
	 * fail return false to indicate the booking operation failed ELSE CREATE the
	 * booking ADD the booking to the current booking array UPDATE the available
	 * status if there are now five current bookings. RETURN true to indicate the
	 * success of the booking. END
	 * 
	 * TEST Booking a car to carry 0, 10, & within/without passenger capacity.
	 * Booking car on date prior to today Booking a car on a date that is more than
	 * 7 days in advance. Booking car on a date for which it is already booked
	 * Booking six cars
	 */

	public boolean book(String firstName, String lastName, DateTime required, int numPassengers)
	{
		boolean booked = false;
		// Does car have five bookings
		available = bookingAvailable();
		boolean dateAvailable = notCurrentlyBookedOnDate(required);
		// Date is within range, not in past and within the next week
		boolean dateValidSeven = dateIsValidSeven(required);
		// Number of passengers does not exceed the passenger capacity and is not zero.
		boolean validPassengerNumber = numberOfPassengersIsValid(numPassengers);

		// Booking is permissible
		if (available && dateAvailable && dateValidSeven && validPassengerNumber)
			//if not booked on date, date is valid, number of passenger is valid, then make a new booking
		{
			tripFee = STANDARD_BOOKING_FEE;
			Booking booking = new Booking(firstName, lastName, required, numPassengers, this);
			currentBookings[bookingSpotAvailable] = booking;
			bookingSpotAvailable++;
			booked = true;
		}
		return booked;
		//returns booked
	}
	
	public boolean bookSilverCar(String firstName, String lastName, DateTime required, int numPassengers)
	{
		boolean booked = false;
		available = bookingAvailable();
		boolean dateAvailable = notCurrentlyBookedOnDate(required);
		boolean dateValidThree = dateIsValidThree(required);
		boolean validPassengerNumber = numberOfPassengersIsValid(numPassengers);

		if (available && dateAvailable && dateValidThree && validPassengerNumber)
		{
			tripFee = STANDARD_BOOKING_FEE;
			Booking booking = new Booking(firstName, lastName, required, numPassengers, this);
			currentBookings[bookingSpotAvailable] = booking;
			bookingSpotAvailable++;
			booked = true;
		}
		return booked;
	}

	/*
	 * Completes a booking based on the name of the passenger and the booking date.
	 */
	public String completeBooking(String firstName, String lastName, DateTime dateOfBooking, double kilometers)
	{
		// Find booking in current bookings by passenger and date
		int bookingIndex = getBookingByDate(firstName, lastName, dateOfBooking);

		if (bookingIndex == -1)
		{
			return "Booking not found.";
		}

		return completeBooking(bookingIndex, kilometers);
	}

	/*
	 * Completes a booking based on the name of the passenger.
	 */
	public String completeBooking(String firstName, String lastName, double kilometers)
	{
		int bookingIndex = getBookingByName(firstName, lastName);

		if (bookingIndex == -1)
		{
			return "Booking not found.";
		} else
		{
			return completeBooking(bookingIndex, kilometers);
		}
	}

	/*
	 * Checks the current bookings to see if any of the bookings are for the current
	 * date. ALGORITHM BEGIN CHECK All bookings IF date supplied matches date for
	 * any booking date Return true ELSE Return false END
	 * 
	 */
	public boolean isCarBookedOnDate(DateTime dateRequired)
	{
		boolean carIsBookedOnDate = false;
		for (int i = 0; i < currentBookings.length; i++)
		{
			if (currentBookings[i] != null)
			{
				if (DateUtilities.datesAreTheSame(dateRequired, currentBookings[i].getBookingDate()))
				{
					carIsBookedOnDate = true;
				}
			}
		}
		return carIsBookedOnDate;
	}

	/*
	 * Retrieves a booking id based on the name and the date of the booking
	 */
	public String getBookingID(String firstName, String lastName, DateTime dateOfBooking)
	{
		System.out.println();
		for (int i = 0; i < currentBookings.length; i++)
		{
			if (currentBookings[i] != null)
			{
				Booking booking = currentBookings[i];
				boolean firstNameMatch = booking.getFirstName().toUpperCase().equals(firstName.toUpperCase());
				boolean lastNameMatch = booking.getLastName().toUpperCase().equals(lastName.toUpperCase());
				int days = DateTime.diffDays(dateOfBooking, booking.getBookingDate());
				if (firstNameMatch && lastNameMatch && days == 0)
				{
					return booking.getID();
				}
			}
		}
		return "Booking not found";
	}

	/*
	 * Human readable presentation of the state of the car.
	 */

	public String getDetails()
	{   
		int getCIndex = 0;
		int getPIndex = 0;
		StringBuilder sb = new StringBuilder();

		sb.append(getRecordMarker());
		sb.append(String.format("%-15s %s\n", "Reg No:", regNo));
		sb.append(String.format("%-15s %s\n", "Make & Model:", make + " " + model));

		sb.append(String.format("%-15s %s\n", "Driver Name:", driverName));
		sb.append(String.format("%-15s %s\n", "Capacity:", passengerCapacity));

		if (bookingAvailable())
		{
			sb.append(String.format("%-15s %s\n", "Available:", "YES"));
		} else
		{
			sb.append(String.format("%-15s %s\n", "Available:", "NO"));
		}

		sb.append(String.format("%-15s \n", "CURRENT BOOKINGS: "));
		sb.append(getCurrentBookings());

		sb.append(String.format("%-15s \n", "PAST BOOKINGS:"));
		sb.append(getPastBookings());
		
		return sb.toString();
	}
	public String getSilverDetails() {
		StringBuilder sb = new StringBuilder();

		sb.append(getRecordMarker());
		sb.append(String.format("%-15s %s\n", "Reg No:", regNo));
		sb.append(String.format("%-15s %s\n", "Make & Model:", make + " " + model));

		sb.append(String.format("%-15s %s\n", "Driver Name:", driverName));
		sb.append(String.format("%-15s %s\n", "Capacity:", passengerCapacity));

		if (bookingAvailable())
		{
			sb.append(String.format("%-15s %s\n", "Available:", "YES"));
		} else
		{
			sb.append(String.format("%-15s %s\n", "Available:", "NO"));
		}
		return sb.toString();
	}
	public String getCurrentBookings() {
		StringBuilder sb = new StringBuilder();
		int getIndex = 0;
		for(int i =0; i<currentBookings.length; i++){
			if(currentBookings[getIndex] != null) {
				sb.append(String.format(currentBookings[getIndex].getDetails()));
				getIndex++;
			}		
		}
		return sb.toString();
	}
	public String getPastBookings() {
		StringBuilder sb = new StringBuilder();
		int getIndex = 0;
		for(int i =0; i<pastBookings.length; i++){
			if(pastBookings[getIndex] != null) {
				sb.append(String.format(pastBookings[getIndex].getDetails()));
				getIndex++;
			}
			
		}
		return sb.toString();
	}

	/*
	 * Computer readable state of the car
	 */

	public String toString()//not booked
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-15s %s\\n","Not Booked"));
		sb.append(regNo + ":" + make + ":" + model);
		if (driverName != null)
		{
			sb.append(":" + driverName);
		}
		sb.append(":" + passengerCapacity);
		if (bookingAvailable())
		{
			sb.append(":" + "YES");
		} else
		{
			sb.append(":" + "NO");
		}
		getCurrentBookingToString();
		getPastBookingToString();

		return sb.toString();
	}
	//silverCar toString()
		public void silverCarToString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%-15s %s\\n","Not Booked"));
			sb.append(regNo + ":" + make + ":" + model);
			if (driverName != null)
			{
				sb.append(":" + driverName);
			}
			sb.append(":" + passengerCapacity);
			if (bookingAvailable())
			{
				sb.append(":" + "YES");
			} else
			{
				sb.append(":" + "NO");
			}
			sb.toString();
	}
	public void getCurrentBookingToString() { //Booked
		StringBuilder sb = new StringBuilder();
		int getIndex =0;
		for(int i =0; i<pastBookings.length; i++) {
			if(currentBookings[getIndex] != null){
				sb.append("|" + currentBookings[getIndex].getID() + ":" + currentBookings[getIndex].getBookingDate() + ":"+ currentBookings[getIndex].getFirstName() + currentBookings[getIndex].getLastName()+ ":" + currentBookings[getIndex].getKilometersTravelled() + ":" + currentBookings[getIndex].getKilometersTravelled() + ":" + currentBookings[getIndex].getFeeTrip() + ":" + currentBookings[getIndex].getRegNo());
			}
		}
		sb.toString();
	}
	public void getPastBookingToString() { //Completed
		StringBuilder sb = new StringBuilder();
		int getIndex =0;
		for(int i =0; i<pastBookings.length; i++) {
			if(pastBookings[getIndex] != null){
				sb.append("|" + pastBookings[getIndex].getID() + ":" + pastBookings[getIndex].getBookingDate() + ":"+ pastBookings[getIndex].getFirstName() + pastBookings[getIndex].getLastName()+ ":" + pastBookings[getIndex].getKilometersTravelled() + ":" + pastBookings[getIndex].getKilometersTravelled() + ":" + pastBookings[getIndex].getFeeTrip() + ":" + pastBookings[getIndex].getRegNo());
			}
		}
		sb.toString();
	}
	


	// Required getters
	public String getRegistrationNumber()
	{
		return regNo;
	}

	public String getDriverName()
	{
		return driverName;
	}

	public double getTripFee()
	{
		return tripFee;
	}
	public String getMake() {
		return make;
	}
	public String getModel() {
		return model;
	}


	/*
	 * Checks to see if any past bookings have been recorded
	 */
	private boolean hasBookings(Booking[] bookings)
	{
		boolean found = false;
		for (int i = 0; i < bookings.length; i++)
		{
			if (bookings[i] != null)
			{
				found = true;
			}
		}
		return found;
	}

	/*
	 * Processes the completion of the booking
	 */
	private String completeBooking(int bookingIndex, double kilometers)
	{
		tripFee = 0;
		Booking booking = currentBookings[bookingIndex];
		// Remove booking from current bookings array.
		currentBookings[bookingIndex] = null;
		bookingSpotAvailable = bookingIndex;

		// call complete booking on Booking object
		// double kilometersTravelled = Math.random()* 100;
		double fee = kilometers * (STANDARD_BOOKING_FEE * 0.3);
		tripFee += fee;
		booking.completeBooking(kilometers, fee, STANDARD_BOOKING_FEE);
		// add booking to past bookings
		for (int i = 0; i < pastBookings.length; i++)
		{
			if (pastBookings[i] == null)
			{
				pastBookings[i] = booking;
				break;
			}
		}
		String result = String.format("Thank you for riding with MiRide.\nWe hope you enjoyed your trip.\n$"
				+ "%.2f has been deducted from your account.", tripFee);
		tripFee = 0;
		return result;
	}

	/*
	 * Gets the position in the array of a booking based on a name and date. Returns
	 * the index of the booking if found. Otherwise it returns -1 to indicate the
	 * booking was not found.
	 */
	private int getBookingByDate(String firstName, String lastName, DateTime dateOfBooking)
	{
		System.out.println();
		for (int i = 0; i < currentBookings.length; i++)
		{
			if (currentBookings[i] != null)
			{
				Booking booking = currentBookings[i];
				boolean firstNameMatch = booking.getFirstName().toUpperCase().equals(firstName.toUpperCase());
				boolean lastNameMatch = booking.getLastName().toUpperCase().equals(lastName.toUpperCase());
				boolean dateMatch = DateUtilities.datesAreTheSame(dateOfBooking, currentBookings[i].getBookingDate());
				if (firstNameMatch && lastNameMatch && dateMatch)
				{
					return i;
				}
			}
		}
		return -1;
	}

	/*
	 * Gets the position in the array of a booking based on a name. Returns the
	 * index of the booking if found. Otherwise it returns -1 to indicate the
	 * booking was not found.
	 */
	public int getBookingByName(String firstName, String lastName)
	{
		for (int i = 0; i < currentBookings.length; i++)
		{
			if (currentBookings[i] != null)
			{
				boolean firstNameMatch = currentBookings[i].getFirstName().toUpperCase()
						.equals(firstName.toUpperCase());
				boolean lastNameMatch = currentBookings[i].getLastName().toUpperCase().equals(lastName.toUpperCase());
				if (firstNameMatch && lastNameMatch)
				{
					return i;
				}
			}
		}
		return -1;
	}

	/*
	 * A record marker mark the beginning of a record.
	 */
	private String getRecordMarker()
	{
		final int RECORD_MARKER_WIDTH = 60;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < RECORD_MARKER_WIDTH; i++)
		{
			sb.append("_");
		}
		sb.append("\n");
		return sb.toString();
	}
	public String callGetRecordMarker() {
		
		return getRecordMarker();
	}

	/*
	 * Checks to see if the number of passengers falls within the accepted range.
	 */
	private boolean numberOfPassengersIsValid(int numPassengers)
	{
		if (numPassengers >= MINIMUM_PASSENGER_CAPACITY && numPassengers < MAXIUM_PASSENGER_CAPACITY
				&& numPassengers <= passengerCapacity)
		{
			return true;
		}
		return false;
	}

	/*
	 * Checks that the date is not in the past or more than 7 days in the future.
	 */
	public boolean dateIsValidSeven(DateTime date)
	{
		return DateUtilities.dateIsNotInPast(date) && DateUtilities.dateIsNotMoreThan7Days(date);
	}//seven days
	public boolean dateIsValidThree(DateTime date)
	{
		return DateUtilities.dateIsNotInPast(date) && DateUtilities.dateIsNotMoreThan3Days(date);
	}//three days

	/*
	 * Indicates if a booking spot is available. If it is then the index of the
	 * available spot is assigned to bookingSpotFree.
	 */

	public boolean bookingAvailable(){
	//check if has 5 current bookings
		for (int i = 0; i < currentBookings.length; i++)
		{
			if (currentBookings[i] == null)
			{
				if(i == currentBookings.length - 1)
				{
					available = false;
				}
				else
				{
					available = true;
				}
				bookingSpotAvailable = i;
				return true;
			}
		}
		return false;
	}


	/*
	 * Checks to see if if the car is currently booked on the date specified.
	 */
	public boolean notCurrentlyBookedOnDate(DateTime date)
	{
		boolean foundDate = true;
		for (int i = 0; i < currentBookings.length; i++)
		{
			if (currentBookings[i] != null)
			{
				int days = DateTime.diffDays(date, currentBookings[i].getBookingDate());
				if (days == 0)
				{
					return false;
				}
			}
		}
		return foundDate;
	}

	/*
	 * Validates and sets the registration number
	 */
	private void setRegNo(String regNo)
	{
		if (!MiRidesUtilities.isRegNoValid(regNo).contains("Error:"))
		{
			this.regNo = regNo;
		} else
		{
			this.regNo = "Invalid";
		}
	}

	/*
	 * Validates and sets the passenger capacity
	 */
	private void setPassengerCapacity(int passengerCapacity)
	{
		boolean validPasengerCapcity = passengerCapacity >= MINIMUM_PASSENGER_CAPACITY
				&& passengerCapacity < MAXIUM_PASSENGER_CAPACITY;

		if (validPasengerCapcity)
		{
			this.passengerCapacity = passengerCapacity;
		} else
		{
			this.passengerCapacity = -1;
			throw new IllegalArgumentException("Passenger numer is not in the range");
		}
	}
	public static Car getFileCar(String text)
    {		
        String[] tokens = text.split(":");
        int passengerToken = Integer.parseInt(tokens[tokens.length]);
        
        
        return new Car(tokens[0], tokens[1], tokens[2], tokens[3], passengerToken);
    }


	}


