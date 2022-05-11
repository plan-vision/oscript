package oscript.exceptions;

public class VRollbackForRetryException extends ThreadDeath
{
	public Exception sqlException;
	public VRollbackForRetryException(Exception e) {
		//super("VRRETRY",e,0,null);
		sqlException=e;
	}
	public String toString() {
		return sqlException.toString();
	}
}
