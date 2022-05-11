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

/**
 * A <code>ProgrammingErrorException</code> is thrown in cases that should
 * only be reached due to a programming error, unimplemented code, etc.
 * 
 * @author Rob Clark
 * @version 0.1
 */
public class ProgrammingErrorException extends RuntimeException
{
  private Throwable targetException;
  
  /*=======================================================================*/
  /**
   * 
   * 
   */
  public ProgrammingErrorException( String msg )
  {
    super("programming error: " + msg);
  }
  
  public ProgrammingErrorException( Throwable t )
  {
    super("this shouldn't happen: " + t);
    targetException = t;
  }
  
  public void printStackTrace()
  {
    printStackTrace( System.err );
  }
  
  public void printStackTrace( java.io.PrintStream ps )
  {
    printStackTrace( new java.io.PrintWriter( new java.io.OutputStreamWriter(ps) ) );
  }
  
  public void printStackTrace( java.io.PrintWriter pw )
  {
    if( targetException != null )
    {
      pw.println("--- Target Exception: ------------------");
      targetException.printStackTrace(pw);
      pw.println("----------------------------------------");
    }
    super.printStackTrace(pw);
    pw.flush();
  }
}



/*
 *   Local Variables:
 *   tab-width: 2
 *   indent-tabs-mode: nil
 *   mode: java
 *   c-indentation-style: java
 *   c-basic-offset: 2
 *   eval: (c-set-offset 'substatement-open '0)
 *   End:
 */

