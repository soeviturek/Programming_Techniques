package cars;


import exceptions.InvalidDate;
import exceptions.InvalidId;
import utilities.DateTime;

/*
 * Class:		SilverServiceCar
 * Description:	The class inherits from parent class Car, represents a silver service car in a ride sharing system. 
 * Author:		Yuchen Wu - s3776244
 */
public class SilverServiceCar extends Car{
	
	//additional car attributes
	private String[] refreshment;
	
	private double STANDARD_BOOKING_FEE =0;
	private double tripFee = 0;
	
	//constructor
	
	public SilverServiceCar(String regNo, String make, String model, String driverName, int passengerCapacity, double bookingFee, String[] refreshment) throws InvalidId{
		super(regNo, make, model, driverName, passengerCapacity);
		this.STANDARD_BOOKING_FEE = bookingFee;
		this.refreshment = refreshment;	
		int arrayIndex =0;
		if(refreshment.length < 3) {
			throw new InvalidId("Less than 3 items in refreshments!");
		}
		
		for(int i = 0; i<refreshment.length - 1;i++) {
			if(refreshment[arrayIndex].equals(refreshment[i+1])) {
				throw new InvalidId("Duplicated items!");
			}}
			if(arrayIndex < refreshment.length) {
				arrayIndex++;
			}
		}
	
	@Override
	public boolean book(String firstName, String lastName, DateTime required, int numPassengers) {
		return super.bookSilverCar(firstName, lastName, required, numPassengers);
	}
	
	
	//get human readable details
	/*ALGORITHM
	 * BEGIN
	 *   create int index = 0
	 *   create a string builder sb
	 *   override the parent method callGetRecordMarker
	 *   override the parent method getsilverDetails
	 *   append all the informations to the string
	 *   for loop, go through the refreshment arraay
	 *     if the array spot != null
	 *       append the refreshment to the string
	 *       
	 *   APPEND all the infos
	 *   RETURN sb.toString
	 *   END
	 */
	@Override
	public String getDetails() {
		int index =0;
		StringBuilder sb = new StringBuilder();
		
		sb.append(super.callGetRecordMarker());
		sb.append(super.getSilverDetails());
		sb.append(String.format("%-15s %s\n", "Standard Fee:", STANDARD_BOOKING_FEE));
		sb.append(String.format("%-15s\n", "Refreshments Available:"));
		for(int i=0; i<refreshment.length; i++) {
			if(refreshment[index] != null) {
				sb.append(String.format("%-15s %s\n", "Item " + (index+1), refreshment[index] ));
				index++;
			}
		}
		sb.append(String.format("%-15s \n", "CURRENT BOOKINGS:"));
		sb.append(super.getCurrentBookings());
		sb.append(String.format("%-15s \n", "PAST BOOKINGS:"));
		sb.append(super.getPastBookings());
		return sb.toString();
	}

    @Override
    public String toString(){

    	StringBuilder sb = new StringBuilder();
    	super.silverCarToString();
    	super.getCurrentBookingToString();
    	super.getPastBookingToString();
    	return sb.toString();
    }
	
}