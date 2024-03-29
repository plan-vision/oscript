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

import oscript.OscriptHost;
import oscript.data.*;

/**
 * When a script object is thrown, it is packaged as an instance of this
 * class.
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.19
 */
public class PackagedScriptObjectException extends RuntimeException
{
  /**
   * The wrapped exception object.
   */
  public Value val;
  
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param val          the packaged script object
   */
  public PackagedScriptObjectException( Value val )
  {
    super();
    this.val = val;
  }
  
  /*=======================================================================*/
  /**
   * Use this method to get a new exception to throw... eventually we
   * might play tricks like caching a pre-allocated exception per thread.
   * 
   * @param val          the script "exception" object to wrap
   * @return a real java exception (ie <code>PackagedScriptObjectException</code>
   */
  public static final PackagedScriptObjectException makeExceptionWrapper( Value val )
  {
    return new PackagedScriptObjectException(val);
  }
  
  /**
   * A helper for evaluating "throw" statements, so script code can throw
   * java exceptions
   */
  public static final PackagedScriptObjectException makeExceptionWrapper2( Value val )
  {
    if( (val instanceof JavaObjectWrapper) && (val.castToJavaObject() instanceof Throwable) )
      return makeExceptionWrapper( new OJavaException( (Throwable)(val.castToJavaObject()), val ) );
    else
      return makeExceptionWrapper(val);
  }
  
  /*=======================================================================*/
  /**
   * 
   */
  public Throwable fillInStackTrace()
  {
    return this;
  }
  
  /*=======================================================================*/
  /**
   * 
   */
  public String getMessage()
  {
  if( getMessageLevel > 0 ) {
	  OscriptHost.me.error(getClass().getName() + ": " + hashCode() + "  ->  " + val.getType() + "/" + val.getClass().getName() + ": " + val.hashCode());
  if( getMessageLevel > 5 ) {
	  OscriptHost.me.error("detected getMessage() loop... doh!");
    return "<<<unknown exception message>>>";
  }
}
try {
  getMessageLevel++;
    return val.castToString();
} finally {
  getMessageLevel--;
}
  }
  // XXX used to detect a problem with infinite loop... seems to be that if val
  // is itself a OJavaException with a link back to this PackagedScriptObjectException
  // that end up in a loop!
  private int getMessageLevel = 0;
}



/*
 *   Local Variables:
 *   tab-width: 2
 *   indent-tabs-mode: nil
 *   mode: java
 *   c-indentation-style: java
 *   c-basic-offset: 2
 *   eval: (c-set-offset 'substatement-open '0)
 *   eval: (c-set-offset 'case-label '+)
 *   eval: (c-set-offset 'inclass '+)
 *   eval: (c-set-offset 'inline-open '0)
 *   End:
 */

