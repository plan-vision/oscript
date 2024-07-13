/*=============================================================================
 *     Copyright Texas Instruments 2000.  All Rights Reserved.
 *   
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * $ProjectHeader: OSCRIPT 0.155 Fri, 20 Dec 2002 18:34:22 -0800 rclark $
 */

package oscript.exceptions;

import java.io.PrintWriter;

import oscript.data.*;

/**
 * When a script object is thrown, it is packaged as an instance of this class.
 * 
 * @author Rob Clark (rob@ti.com) <!--$Format: " * @version $Revision$"$-->
 * @version 1.19
 */
public class PackagedScriptObjectException extends RuntimeException {
	/**
	 * The wrapped exception object.
	 */
	public Value val;
	private Throwable parent;
		
	@Override
	public void printStackTrace() {
		if (parent != null)
			parent.printStackTrace();
		super.printStackTrace();
	}
	
	@Override
	public void printStackTrace(PrintWriter s) {
		if (parent != null)
			parent.printStackTrace(s);
		super.printStackTrace(s);
	}
	/* ======================================================================= */
	/**
	 * Class Constructor.
	 * 
	 * @param val the packaged script object
	 */
	public PackagedScriptObjectException(Value val) {
		super();
		this.val = val;
		if (val instanceof OJavaException) {
			Object t = val.castToJavaObject();
			if (t instanceof Throwable) 
				parent = (Throwable) t;
		}
	}

	/* ======================================================================= */
	/**
	 * Use this method to get a new exception to throw... eventually we might play
	 * tricks like caching a pre-allocated exception per thread.
	 * 
	 * @param val the script "exception" object to wrap
	 * @return a real java exception (ie <code>PackagedScriptObjectException</code>
	 */
	public static final PackagedScriptObjectException makeExceptionWrapper(Value val) {

		return new PackagedScriptObjectException(val);
	}

	/**
	 * A helper for evaluating "throw" statements, so script code can throw java
	 * exceptions
	 */
	public static final PackagedScriptObjectException makeExceptionWrapper2(Value val) {
		if ((val instanceof JavaObjectWrapper) && (val.castToJavaObject() instanceof Throwable))
			return makeExceptionWrapper(new OJavaException((Throwable) (val.castToJavaObject()), val));
		else
			return makeExceptionWrapper(val);
	}

	/* ======================================================================= */
	public Throwable fillInStackTrace() {
		return this;
	}
	/* ======================================================================= */
	public String getMessage() {
		return val.toString();
	}
}
