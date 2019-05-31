package app;

import java.io.IOException;
import java.util.Scanner;

import javax.activity.InvalidActivityException;

import exceptions.InvalidDate;
import exceptions.InvalidId;
import utilities.DateTime;
import utilities.DateUtilities;

/*
 * Class:		Menu
 * Description:	The class a menu and is used to interact with the user. 
 * Author:		Yuchen Wu - s3776244
 */
public class Menu
{
	private Scanner console = new Scanner(System.in);
	private MiRideApplication application = new MiRideApplication(); //important
	// Allows me to turn validation on/off for testing business logic in the
	// classes.
	private boolean testingWithValidation = true;

	/*
	 * Runs the menu in a loop until the user decides to exit the system.
	 */
	public void run() throws InvalidId, InvalidDate, IOException
	{
		final int MENU_ITEM_LENGTH = 2;
		String input;
		String choice = "";
		do
		{
			printMenu();

			input = console.nextLine().toUpperCase();

			if (input.length() != MENU_ITEM_LENGTH)
			{
				System.out.println("Error - selection must be two characters!");
			} else
			{
				System.out.println();

				switch (input)
				{
				case "CC":
					createCar();
					break;
				case "BC":
					book();
					break;
				case "CB":
					completeBooking();
					break;
				case "DA"://display all cars
					System.out.println("Enter car type: ");
					String carTypeString = console.nextLine();
					System.out.println("Enter sort direction(A/D): ");
					String sortDirectionString = console.nextLine();
					System.out.println(application.displayAllBookings(carTypeString, sortDirectionString));

					break;
		//search car option
		//search specific car
				case "SS":
					System.out.print("Enter Registration Number: ");
					System.out.println(application.specificCar(console.nextLine()));								
					break;
					
		// search avalaible car based on date
				case "SA":
					searchAvailable();
					break;
					
				case "SD":
					application.seedData();
					break;
				case "LC":
					application.loadCar();
					break;
				case "SC":
					application.saveCar();
					break;
				case "EX":
					choice = "EX";
					System.out.println("Exiting Program ... Goodbye!");
					break;
				default:
					System.out.println("Error, invalid option selected!");
					System.out.println("Please try Again...");
				}
			}

		} while (choice != "EX");
	}

	/*
	 * Creates cars for use in the system available or booking.
	 */
	private void createCar() throws InvalidId
	{
		String id = "", make, model, driverName, serviceType;
		double standardBookingFee =0;
		String[] refreshments = new String[10];
		int numPassengers = 0;

		System.out.print("Enter registration number: ");
		id = promptUserForRegNo();
		if (id.length() != 0)
		{
			// Get details required for creating a car.
			System.out.print("Enter Make: ");
			make = console.nextLine();

			System.out.print("Enter Model: ");
			model = console.nextLine();

			System.out.print("Enter Driver Name: ");
			driverName = console.nextLine();

			System.out.print("Enter number of passengers: ");
			numPassengers = promptForPassengerNumbers();
			
			System.out.print("Enter service type: ");
			serviceType = console.nextLine();
			boolean result = application.checkIfCarExists(id);
			if(!result){
				if(serviceType.equals("SS")) {				
					int ArrayIndex =0;
					int stringStartIndex =0;
					int stringEndIndex=0;
					
					boolean hasComma;
					String refreshmentsString;
					System.out.print("Enter refreshments: ");					
					refreshmentsString = console.nextLine();
					

					for(int i =0; i<refreshmentsString.length() - 1; i++) {
						if(i != refreshmentsString.length()-1) {
							String checkString = refreshmentsString.substring(i,i+1);
							if(checkString.equals(",")) {
								stringEndIndex = i;
								refreshments[ArrayIndex] = refreshmentsString.substring(stringStartIndex,stringEndIndex);
								stringStartIndex= i+1;
								ArrayIndex++;
							}
					}
						if(i == refreshmentsString.length()-1) {
							String checkString2 = refreshmentsString.substring(i);
							if(checkString2.equals(",")) {
								stringEndIndex= i;
								refreshments[ArrayIndex] = refreshmentsString.substring(stringStartIndex,stringEndIndex);
								stringStartIndex= i+1;
								ArrayIndex++;
						}
					}else{
						refreshments[ArrayIndex] = refreshmentsString.substring(stringStartIndex);
					}
				}
					System.out.print("Enter standard booking fees: ");
					standardBookingFee = console.nextDouble();
					
					String carRegistrationNumber = application.createCar(id, make, model, driverName, numPassengers, serviceType, standardBookingFee, refreshments);
					System.out.println(carRegistrationNumber);
				
				}else if(serviceType.equals("SD")){
					String carRegistrationNumber = application.createCar(id, make, model, driverName, numPassengers, serviceType, 1.5, null);
					System.out.println(carRegistrationNumber);}
				else {
					System.out.println("Wrong service type. Please try again!");
				}
		}
	
			} else
			{
				System.out.println("Error - Already exists in the system");
			}
		
		}
	

	/*
	 * Book a car by finding available cars for a specified date.
	 */
	private boolean book() throws InvalidDate
	{
		System.out.println("Enter date car required: ");
		System.out.println("format DD/MM/YYYY)");
		String dateEntered = console.nextLine();
		int day = Integer.parseInt(dateEntered.substring(0, 2));
		int month = Integer.parseInt(dateEntered.substring(3, 5));
		int year = Integer.parseInt(dateEntered.substring(6));
		DateTime dateRequired = new DateTime(day, month, year);
		
		if(!DateUtilities.dateIsNotInPast(dateRequired) || !DateUtilities.dateIsNotMoreThan7Days(dateRequired))
		{
			System.out.println("Date is invalid, must be within the coming week.");
			return false;
		}
		
		
		String[] availableCars = application.book(dateRequired);
		for (int i = 0; i < availableCars.length; i++)
		{
			System.out.println(availableCars[i]);
		}
		if (availableCars.length != 0)
		{
			System.out.println("Please enter a number from the list:");
			int itemSelected = Integer.parseInt(console.nextLine());
			
			String regNo = availableCars[itemSelected - 1];
			regNo = regNo.substring(regNo.length() - 6);
			System.out.println("Please enter your first name:");
			String firstName = console.nextLine();
			System.out.println("Please enter your last name:");
			String lastName = console.nextLine();
			System.out.println("Please enter the number of passengers:");
			int numPassengers = Integer.parseInt(console.nextLine());
			String result = application.book(firstName, lastName, dateRequired, numPassengers, regNo);

			System.out.println(result);
		} else
		{
			System.out.println("There are no available cars on this date.");
		}
		return true;
	}
	
	/*
	 * Complete bookings found by either registration number or booking date.
	 */
	private void completeBooking()
	{
		System.out.print("Enter Registration or Booking Date:");
		String response = console.nextLine();
		
		String result;
		// User entered a booking date
		if (response.contains("/"))
		{
			System.out.print("Enter First Name:");
			String firstName = console.nextLine();
			System.out.print("Enter Last Name:");
			String lastName = console.nextLine();
			System.out.print("Enter kilometers:");
			double kilometers = Double.parseDouble(console.nextLine());
			int day = Integer.parseInt(response.substring(0, 2));
			int month = Integer.parseInt(response.substring(3, 5));
			int year = Integer.parseInt(response.substring(6));
			DateTime dateOfBooking = new DateTime(day, month, year);
			result = application.completeBooking(firstName, lastName, dateOfBooking, kilometers);
			System.out.println(result);
		} else
		{
			
			System.out.print("Enter First Name:");
			String firstName = console.nextLine();
			System.out.print("Enter Last Name:");
			String lastName = console.nextLine();
			if(application.getBookingByName(firstName, lastName, response))
			{
				System.out.print("Enter kilometers:");
				double kilometers = Double.parseDouble(console.nextLine());
				result = application.completeBooking(firstName, lastName, response, kilometers);
				System.out.println(result);
			}
			else
			{
				System.out.println("Error: Booking not found.");
			}
		}
		
	}
	/*
	 * ALGORITHM
	 * BEGIN
	 *   sysout "enter car type"
	 *   carType = nextline
	 *   sysout "enter date"
	 *   sysout "formate dd/mm/yy"
	 *   string = next.line
	 *   substring 02 ,35 , 6 parse int
	 *   datetime searchDate = new DateTime
	 *   RETURN application.searchAvailable(type,date)
	 * 
	 * END
	 */
	private String searchAvailable() {
		System.out.println("Enter car type(SD or SS): ");
		String carType = console.nextLine();
		System.out.println("Enter date car required: ");
		System.out.println("format DD/MM/YYYY)");
		String dateEntered = console.nextLine();
		int day = Integer.parseInt(dateEntered.substring(0, 2));
		int month = Integer.parseInt(dateEntered.substring(3, 5));
		int year = Integer.parseInt(dateEntered.substring(6));
		DateTime searchDate = new DateTime(day, month, year);
		
		return application.searchAvailable(carType, searchDate);
	}
	private int promptForPassengerNumbers()
	{
		int numPassengers = 0;
		boolean validPassengerNumbers = false;
		// By pass user input validation.
		if (!testingWithValidation)
		{
			return Integer.parseInt(console.nextLine());
		} 
		else
		{
			while (!validPassengerNumbers)
			{
				numPassengers = Integer.parseInt(console.nextLine());

				String validId = application.isValidPassengerCapacity(numPassengers);
				if (validId.contains("Error:"))
				{
					System.out.println(validId);
					System.out.println("Enter passenger capacity: ");
					System.out.println("(or hit ENTER to exit)");
				} else
				{
					validPassengerNumbers = true;
				}
			}
			return numPassengers;
		}
	}

	/*
	 * Prompt user for registration number and validate it is in the correct form.
	 * Boolean value for indicating test mode allows by passing validation to test
	 * program without user input validation.
	 */
	private String promptUserForRegNo()
	{
		String regNo = "";
		boolean validRegistrationNumber = false;
		// By pass user input validation.
		if (!testingWithValidation)
		{
			return console.nextLine();
		} 
		else
		{
			while (!validRegistrationNumber)
			{
				regNo = console.nextLine();
				boolean exists = application.checkIfCarExists(regNo);
				if(exists)
				{
					// Empty string means the menu will not try to process
					// the registration number
					System.out.println("Error: Reg Number already exists");
					return "";
				}
				if (regNo.length() == 0)
				{
					break;
				}

				String validId = application.isValidId(regNo);
				if (validId.contains("Error:"))
				{
					System.out.println(validId);
					System.out.println("Enter registration number: ");
					System.out.println("(or hit ENTER to exit)");
				} else
				{
					validRegistrationNumber = true;
				}
			}
			return regNo;
		}
	}

	/*
	 * Prints the menu.
	 */
	private void printMenu()
	{
		System.out.printf("\n********** MiRide System Menu **********\n\n");

		System.out.printf("%-30s %s\n", "Create Car", "CC");
		System.out.printf("%-30s %s\n", "Book Car", "BC");
		System.out.printf("%-30s %s\n", "Complete Booking", "CB");
		System.out.printf("%-30s %s\n", "Display ALL Cars", "DA");
		System.out.printf("%-30s %s\n", "Search Specific Car", "SS");
		System.out.printf("%-30s %s\n", "Search Available Cars", "SA");
		System.out.printf("%-30s %s\n", "Seed Data", "SD");
		System.out.printf("%-30s %s\n", "Load Car", "LC");
		System.out.printf("%-30s %s\n", "Save Car", "SC");
		System.out.printf("%-30s %s\n", "Exit Program", "EX");
		System.out.println("\nEnter your selection: ");
		System.out.println("(Hit enter to cancel any operation)");
	}
}
