package exceptions;

/*
 * Class:		InvalidBooking
 * Description:	The class inherits from parent class Exception, represents a type of exception that occurs when the booking, booking date in particular, is invalid in a ride sharing system. 
 * Author:		Yuchen Wu - s3776244
 */

public class InvalidDate extends Exception{

	private String errorMessage;
	public InvalidDate() {}
	public InvalidDate(String description) {
		super(description);
		errorMessage = description;
		}
		//print Error Message
		
	/*
	 * this creates the string that will be printed
	 */
		
	/*
	*ALGORITHM
	* BEGIN
	* RETURN errorMessege String
	* END
	*/	
	public String toString() {
		return errorMessage;
	}
}
