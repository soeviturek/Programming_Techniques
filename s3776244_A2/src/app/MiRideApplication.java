package app;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.omg.CORBA.PUBLIC_MEMBER;

import cars.Car;
import cars.SilverServiceCar;
import exceptions.InvalidDate;
import exceptions.InvalidId;
import utilities.DateTime;
import utilities.MiRidesUtilities;
/*
 * Class:			MiRideApplication
 * Description:		The system manager the manages the 
 *              	collection of data. 
 * Author:			Yuchen Wu - s3776244
 */
public class MiRideApplication
{
	private Car[] cars = new Car[15]; //question how to generate both arrays into one
	private int itemCount = 0;
	private String[] availableCars;
	
	//File Handling
	private final String CAR_FILENAME = "car.txt";
	public MiRideApplication()
	{
		//seedData();
	}
	//create car

		public String createCar(String id, String make, String model, String driverName, int numPassengers, String serviceType, Double bookingFee, String[] refreshmentsInput) throws InvalidId, IllegalArgumentException

		{
		
		String validId = isValidId(id);
		if(isValidId(id).contains("Error:"))
		{
			return validId;
		}
		try {
		if(!checkIfCarExists(id)) {
			if (serviceType.equals("SS")){
				int arrayIndex = 0;
				if(refreshmentsInput.length < 3) {
					throw new InvalidId("Less than 3 items in refreshments!");
				}
				
				for(int i = 0; i<refreshmentsInput.length - 1;i++) {
					if(refreshmentsInput[arrayIndex].equals(refreshmentsInput[i+1])) {
						throw new InvalidId("Duplicated items!");
					}
			}
				cars[itemCount] = new SilverServiceCar(id, make, model, driverName, numPassengers, bookingFee, refreshmentsInput);
				}
			else if(serviceType.equals("SD")){
				cars[itemCount] = new Car(id, make, model, driverName, numPassengers);
			}
			itemCount++;
			return "New Car added successfully for registion number: " + cars[itemCount-1].getRegistrationNumber();
		}
		return "Error: Already exists in the system.";
		} catch(InvalidId rError) {
			System.out.println(rError);
			return null;
			
		}catch(IllegalArgumentException iae) {
			System.out.println("Number of passenger is wrong.");
			return null;
			
		}
	}
	public String[] book(DateTime dateRequired)
	{
		int numberOfAvailableCars = 0;
		// finds number of available cars to determine the size of the array required.
		try {
		for(int i=0; i<cars.length; i++)
		{
			if(cars[i] != null)
			{
				if(!cars[i].isCarBookedOnDate(dateRequired))
				{
					numberOfAvailableCars++;
				}else {
					throw new InvalidDate("Car is already booked on date");
				}
			}
		}
		if(numberOfAvailableCars == 0)
		{
			String[] result = new String[0];
			return result;
		}
		availableCars = new String[numberOfAvailableCars];
		int availableCarsIndex = 0;
		// Populate available cars with registration numbers
		for(int i=0; i<cars.length;i++)
		{
			
			if(cars[i] != null)
			{
				if(!cars[i].isCarBookedOnDate(dateRequired))
				{
					availableCars[availableCarsIndex] = availableCarsIndex + 1 + ". " + cars[i].getRegistrationNumber();
					availableCarsIndex++;
				}
			}
		}
		return availableCars;
	}catch(InvalidDate dError) {
		System.out.println(dError);
		return null;
		} 
	}
	
	public String book(String firstName, String lastName, DateTime required, int numPassengers, String registrationNumber)
	{			
		Car car = getCarById(registrationNumber);
		try {
		if(car != null)
        {
			if(car.book(firstName, lastName, required, numPassengers))
			{

				String message = "Thank you for your booking. \n" + car.getDriverName() 
		        + " will pick you up on " + required.getFormattedDate() + ". \n"
				+ "Your booking reference is: " + car.getBookingID(firstName, lastName, required);
				return message;
			}
			else if(car.dateIsValidSeven(required) == false){
				throw new InvalidDate("Cannot book over 7 days");
			}else if(car.dateIsValidThree(required) == false){
				throw new InvalidDate("Cannot book over 3 days");
			}
        }
        else{
            return "Car with registration number: " + registrationNumber + " was not found.";
        }
		return null;
	}catch(InvalidDate timeError) {
		System.out.println(timeError);
		String message = "Booking could not be completed.";
		return message;}
	}
	
	public String completeBooking(String firstName, String lastName, DateTime dateOfBooking, double kilometers)
	{
		String result = "";
		
		// Search all cars for bookings on a particular date.
		for(int i = 0; i <cars.length; i++)
		{
			if (cars[i] != null)
			{
				if(cars[i].isCarBookedOnDate(dateOfBooking))
				{
					return cars[i].completeBooking(firstName, lastName, dateOfBooking, kilometers);
				}
//				else
//				{
//					
//				}
//				if(!result.equals("Booking not found"))
//				{
//					return result;
//				}
			}
		}
		return "Booking not found.";
	}
	
	public String completeBooking(String firstName, String lastName, String registrationNumber, double kilometers)
	{
		String carNotFound = "Car not found";
		Car car = null;
		// Search for car with registration number
		for(int i = 0; i <cars.length; i++)
		{
			if (cars[i] != null)
			{
				if (cars[i].getRegistrationNumber().equals(registrationNumber))
				{
					car = cars[i];
					break;
				}
			}
		}

		if (car == null)
		{
			return carNotFound;
		}
		if (car.getBookingByName(firstName, lastName) != -1)
		{
			return car.completeBooking(firstName, lastName, kilometers);
		}
		return "Error: Booking not found.";
	}
	
	public boolean getBookingByName(String firstName, String lastName, String registrationNumber)
	{
		String bookingNotFound = "Error: Booking not found";
		Car car = null;
		// Search for car with registration number
		for(int i = 0; i <cars.length; i++)
		{
			if (cars[i] != null)
			{
				if (cars[i].getRegistrationNumber().equals(registrationNumber))
				{
					car = cars[i];
					break;
				}
			}
		}
		
		if(car == null)
		{
			return false;
		}
		if(car.getBookingByName(firstName, lastName) == -1)
		{
			return false;
		}
		return true;
	}

	public String specificCar(String regNo) //search by regNo
	{
		for(int i = 0; i < cars.length; i++)
		{
			if(cars[i] != null)
			{
				if(cars[i].getRegistrationNumber().equals(regNo))
				{
					return cars[i].getDetails();
				}
			}
		}
		return "Error: The car could not be located.";
	}
	//search by date
	public String searchAvailable(String carType,DateTime bookingDate) 
	{
		for(int i = 0; i < cars.length; i++)
		{
			if(cars[i] != null)
			{
				if(cars[i].notCurrentlyBookedOnDate(bookingDate) && cars[i].bookingAvailable())
				{
					return cars[i].getDetails();
				}
			}
		}
		return "Error: No car were found on this date.";
	}
	
	public boolean seedData()
	{
		for(int i = 0; i < cars.length; i++)
		{
			if(cars[i] != null)
			{
				return false;
			}
		}
		// 2 cars not booked
		Car honda = new Car("SIM194", "Honda", "Accord Euro", "Henry Cavill", 5);
		cars[itemCount] = honda;
		honda.book("Craig", "Cocker", new DateTime(1), 3);
		itemCount++;
		
		Car lexus = new Car("LEX666", "Lexus", "M1", "Angela Landsbury", 3);
		cars[itemCount] = lexus;
		lexus.book("Craig", "Cocker", new DateTime(1), 3);
		itemCount++;
		
		//2 not booked silver service cars
		try {
		Car benz = new SilverServiceCar("LEO666", "Benz", "Mercedes-Maybach S 650", "James Wu", 5, 10.0, new String[]{"Citrus","Orange","Apple"});
		cars[itemCount] = benz;

		itemCount++;
		
		Car mazda = new SilverServiceCar("YUC234", "Mazda", "MX-5", "Yuchen Wu", 4, 5.0, new String[]{"Orange mint","Strawberry mint","Chocolate"});
		cars[itemCount] = mazda;
		itemCount++;

		//2 booked and not completed silver service cars
				Car mazdaspeed3 = new SilverServiceCar("BIL233", "Mazda", "speed3", "Jim Raynor", 4, 4.5, new String[]{"Mint","Banna","Peach"});
				cars[itemCount] = mazdaspeed3;
				mazdaspeed3.book("Arcturus", "Mengsk", new DateTime(1), 1);
				
				Car peugeot1007 = new SilverServiceCar("PGO213", "Peugeot", "1007", "Sarah Kerrigan", 5, 5.5, new String[]{"Lavandar","Mint","Citrus"});
				cars[itemCount] = peugeot1007;
				peugeot1007.book("Valarian", "Mengsk", new DateTime(1), 2);
				
				//2 booked and completed silver service cars
				Car honda_accord = new SilverServiceCar("HDA218", "Honda", "Accord", "Brave Heart", 4, 7.5, new String[]{"Vanila"});
				cars[itemCount] = honda_accord;
				honda_accord.book("Tychus", "Findley", new DateTime(1), 1);
				honda_accord.completeBooking("Tychus", "Findley", 99);
				
				Car chevrolet_equinox = new SilverServiceCar("CNM218", "Chevrolet", "Equinox", "Xukun Cai", 2, 99.99, new String[]{"Basketball dust"});
				cars[itemCount] = chevrolet_equinox;
				honda_accord.book("Ned", "Kelly", new DateTime(1), 2);
				honda_accord.completeBooking("Ned", "Kelly", 9998);
	}catch (InvalidId e) {
	}
		
		// 2 cars booked
		Car bmw = new Car("BMW256", "Mini", "Minor", "Barbara Streisand", 4);
		cars[itemCount] = bmw;
		itemCount++;
		bmw.book("Craig", "Cocker", new DateTime(1), 3);
		
		Car audi = new Car("AUD765", "Mazda", "RX7", "Matt Bomer", 6);
		cars[itemCount] = audi;
		itemCount++;
		audi.book("Rodney", "Cocker", new DateTime(1), 4);
		
		
		
		// 1 car booked five times (not available)
		Car toyota = new Car("TOY765", "Toyota", "Corola", "Tina Turner", 7);
		cars[itemCount] = toyota;
		itemCount++;
		toyota.book("Rodney", "Cocker", new DateTime(1), 3);
		toyota.book("Craig", "Cocker", new DateTime(2), 7);
		toyota.book("Alan", "Smith", new DateTime(3), 3);
		toyota.book("Carmel", "Brownbill", new DateTime(4), 7);
		toyota.book("Paul", "Scarlett", new DateTime(5), 7);
		toyota.book("Paul", "Scarlett", new DateTime(6), 7);
		toyota.book("Paul", "Scarlett", new DateTime(7), 7);
		
		// 1 car booked five times (not available)
		Car rover = new Car("ROV465", "Honda", "Rover", "Jonathon Ryss Meyers", 7);
		cars[itemCount] = rover;
		itemCount++;
		rover.book("Rodney", "Cocker", new DateTime(1), 3);
		//rover.completeBooking("Rodney", "Cocker", 75);
		DateTime inTwoDays = new DateTime(2);
		rover.book("Rodney", "Cocker", inTwoDays, 3);
		rover.completeBooking("Rodney", "Cocker", inTwoDays,75);
		return true;
	}

	//display all cars
	public String displayAllBookings(String carType, String sortDirection)
	{
		if(itemCount == 0)
		{
			return "No cars have been added to the system.";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Summary of all cars: ");
		sb.append("\n");
		Car[] displayCar = filterCars(carType);
		sortCars(sortDirection, displayCar);  
		for (int i = 0; i < displayCar.length-1; i++){
			sb.append(String.format(displayCar[i].getDetails()));
			}
		return sb.toString();
	}
	
	public Car[] filterCars(String carType) {
		int filtersize = 0;
		  for(int i= 0; i<cars.length; i++) {
			  if(carType.equals("SD")&& cars[i] instanceof Car && cars[i] != null || carType.equals("SS")&& cars[i] instanceof SilverServiceCar && cars[i] != null) {
				filtersize++;  
			  }
		  }
		  int filterIndex = 0;
		  Car filteredCars[] = new Car[filtersize];
		  for(int i =0; i<filtersize; i++) {
			  if((carType.equals("SD")&& cars[i] instanceof Car && cars[i] != null) || (carType.equals("SS")&& cars[i]instanceof SilverServiceCar && cars[i] != null)) {
				  filteredCars[filterIndex]= cars[i];
				  filterIndex++; 
			  }
			  }

		  
		  return filteredCars;
	}
	public Car[] sortCars(String sortDirection, Car[] carsArray) {
		boolean bubbleOccured = true;
		  int filterIndex = 0;
		  while(bubbleOccured == true) {
			  bubbleOccured = false;
			  for(int i=0; i<carsArray.length -1; i++) {
				  if((sortDirection.equals("D")&& carsArray[i] != null && carsArray[i].getRegistrationNumber().compareTo(carsArray[i+1].getRegistrationNumber()) <0) || (sortDirection.equals("A")&& carsArray[i] != null && carsArray[i].getRegistrationNumber().compareTo(carsArray[i+1].getRegistrationNumber()) >0)){
					  Car temporary = carsArray[i];
					  carsArray[i] = carsArray[i+1];
					  carsArray[i+1] = temporary;
					  bubbleOccured = true;
				  }
		                                          }		  
		  							   } 
		  return carsArray;
		
	}
	public String displayBooking(String id, String seatId)
	{
		Car booking = getCarById(id);
		if(booking == null)
		{
			return "Booking not found";
		}
		return booking.getDetails();
	}
	
	public String isValidId(String id)
	{
		return MiRidesUtilities.isRegNoValid(id);
	}
	
	public String isValidPassengerCapacity(int passengerNumber)
	{
		return MiRidesUtilities.isPassengerCapacityValid(passengerNumber);
	}

	public boolean checkIfCarExists(String regNo)
	{
		Car car = null;
		if (regNo.length() != 6)
		{
			return false;
		}
		car = getCarById(regNo);
		if (car == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private Car getCarById(String regNo)
	{
		Car car = null;

		for (int i = 0; i < cars.length; i++)
		{
			if(cars[i] != null)
			{
				if (cars[i].getRegistrationNumber().equals(regNo))
				{
					car = cars[i];
					return car;
				}
			}
		}
		return car;
	}
	/*
	 * loading and writing the file
	 */
	public void loadCar() throws IOException
    {
        System.out.printf("Loading %s... ", CAR_FILENAME);
        
        if(!new File(CAR_FILENAME).isFile())
        {
            return;
        }
        
        try(BufferedReader reader = new BufferedReader(new FileReader(CAR_FILENAME)))
        {
            while(reader.ready())
                addCar(Car.getFileCar(reader.readLine()));
        }
        
        System.out.println("done.");
    }
	public boolean addCar(Car car)
    {
        if(itemCount == cars.length) {
            return false;}
        
        cars[itemCount++] = car;
        return true;
    }
	
    public void saveCar() throws IOException
    {
        System.out.printf("Saving %s... ", CAR_FILENAME);
        
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(CAR_FILENAME))) 
        {
            for(int i = 0; i < cars.length; i++)
            {
                writer.write(cars[i].toString());
                writer.write('\n');
            }
        }
        
        System.out.println("done.");
    }
    
   
}
