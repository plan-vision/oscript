package oscript.exceptions;

public class VRollbackForRetryException extends RuntimeException
{
	public Exception sqlException;
	public VRollbackForRetryException(Exception e) {
		sqlException=e;
	}
	public String toString() {
		return sqlException.toString();
	}
}
