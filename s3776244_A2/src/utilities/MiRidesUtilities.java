package utilities;

public class MiRidesUtilities 
{
	private final static int ID_LENGTH = 6;
	
	public static String isRegNoValid(String regNo)
	{
		int regNoLength = regNo.length();
		if(regNoLength != ID_LENGTH)
		{
			return "Error: registration number must be 6 characters";
		}
		boolean letters = regNo.substring(0,3).matches("[a-zA-Z]+");
		if (!letters) {
			return "Error: The registration number should begin with three alphabetical characters.";
		}
		boolean numbers = regNo.substring(3).matches("[0-9]+");
		if (!numbers) {
			return "Error: The registration number should end with three numeric characters.";
		}
		return regNo;
	}

	public static String isPassengerCapacityValid(int passengerCapacity)
	{
		if(passengerCapacity > 0 && passengerCapacity < 10)
		{
			return "OK";
		}
		else
		{
			return "Error: Passenger capacity must be between 1 and 9.";
		}
	}
}
