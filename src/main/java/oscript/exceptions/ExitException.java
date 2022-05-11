package oscript.exceptions;

/**
 * Marker class for exception thrown on "exit(code)" inside a VScript
 *
 * @author plan.vision group
 * @version CVS $ cvs version $
 * VisionR Portal Project
 */
public class ExitException extends RuntimeException {

	private int code = 0;
	
	public ExitException( String str, int code ) {
		super( str );
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}

}
