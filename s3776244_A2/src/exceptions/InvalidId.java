package exceptions;

/*
 * Class:		InvalidId
 * Description:	The class inherits from parent class Exception, represents a type of exception that occurs when the id, refreshments in particular, is invalid in a ride sharing system. 
 * Author:		Yuchen Wu - s3776244
 */

public class InvalidId extends Exception{
	private String errorMessege;
	public InvalidId() {}
	
	public InvalidId(String description) {
		super(description);
		errorMessege = description;
	}
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
		return errorMessege;
	}

}
