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


package oscript;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import oscript.data.Function;
import oscript.data.JavaClassWrapper;
import oscript.data.JavaMethodWrapper;
import oscript.data.OArray;
import oscript.data.OBoolean;
import oscript.data.OExactNumber;
import oscript.data.OException;
import oscript.data.OIllegalArgumentException;
import oscript.data.OInexactNumber;
import oscript.data.OJavaException;
import oscript.data.ONoSuchMemberException;
import oscript.data.ONullReferenceException;
import oscript.data.OObject;
import oscript.data.OString;
import oscript.data.OUnsupportedOperationException;
import oscript.data.Proxy;
import oscript.data.Reference;
import oscript.data.Scope;
import oscript.data.Value;


/**
 * Built in functions, etc...
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.14
 */
public class OscriptBuiltins
{
  private static InputStream in  = System.in;
  private static PrintStream out = System.out;
  private static PrintStream err = System.err;
  
  /**
   * sorted by priority, values are list of runnables at that
   * priority level.
   */
  private static TreeMap atExitRunnableMap = null;
  
  static
  {
    Runtime.getRuntime().addShutdownHook( new Thread()
    {
      public void run()
      {
        runAtExitRunnables();
      }
    });
  }
  
  private static void runAtExitRunnables()
  {
    while( atExitRunnableMap != null )
    {
      TreeMap map = atExitRunnableMap;
      atExitRunnableMap = null;
      
      for( Iterator mitr=map.values().iterator(); mitr.hasNext(); )
      {
        LinkedList list = (LinkedList)(mitr.next());
//  System.err.println("run: " + list);
        
        for( Iterator litr=list.iterator(); litr.hasNext(); )
        {
          try
          {
            ((Runnable)(litr.next())).run();
          }
          catch(Throwable t)
          {
            t.printStackTrace();
          }
        }
      }
    }
  }
  
  /*=======================================================================*/
  /**
   * 
   */
  static final void init()
  {
    Scope globalScope = OscriptInterpreter.getGlobalScope();
    
    // add built-in types:
    int attr = Reference.ATTR_CONST;
    globalScope.createMember("Array",attr).opAssign( OArray.TYPE );
    globalScope.createMember("Boolean",attr).opAssign( OBoolean.TYPE );
    globalScope.createMember("ExactNumber",attr).opAssign( OExactNumber.TYPE );
    globalScope.createMember("Exception",attr).opAssign( OException.TYPE );
    globalScope.createMember("NoSuchMemberException",attr).opAssign( ONoSuchMemberException.TYPE );
    globalScope.createMember("NullReferenceException",attr).opAssign( ONullReferenceException.TYPE );
    globalScope.createMember("IllegalArgumentException",attr).opAssign( OIllegalArgumentException.TYPE );
    globalScope.createMember("UnsupportedOperationException",attr).opAssign( OUnsupportedOperationException.TYPE );
    globalScope.createMember("JavaException",attr).opAssign( OJavaException.TYPE );
    globalScope.createMember("InexactNumber",attr).opAssign( OInexactNumber.TYPE );
    globalScope.createMember("String",attr).opAssign( OString.TYPE );
    globalScope.createMember("Object",attr).opAssign( OObject.TYPE );
    globalScope.createMember("Function",attr).opAssign( Function.TYPE );
    globalScope.createMember("JavaClass",attr).opAssign( JavaClassWrapper.TYPE );
    globalScope.createMember("JavaMethod",attr).opAssign( JavaMethodWrapper.TYPE );
    globalScope.createMember("Proxy",attr).opAssign( Proxy.TYPE );
    OscriptHost.me.onInit();
  }
  
  /*=======================================================================*/
  /**
   * Set the input stream.
   * 
   * @param in           the stream to use for input
   */
  public static void setIn( InputStream in )
  {
    OscriptBuiltins.in = in;
  }
  public static InputStream getIn()
  {
    return in;
  }
  
  /*=======================================================================*/
  /**
   * Set the output stream.
   * 
   * @param out          the stream to use for output
   */
  public static void setOut( PrintStream out )
  {
    OscriptBuiltins.out = out;
  }
  public static PrintStream getOut()
  {
    return out;
  }
  
  /*=======================================================================*/
  /**
   * Set the error stream.
   * 
   * @param err          the stream to use for error output
   */
  public static void setErr( PrintStream err )
  {
    OscriptBuiltins.err = err;
  }
  public static PrintStream getErr()
  {
    return err;
  }

  /*=======================================================================*/
  /**
   * Register a hook to be called at system exit.  The runnables are
   * prioritized, and the ones with higher priority (lower numerical
   * <code>priority</code> value) will be invoked first.
   * 
   * @param r            runnable to call
   * @param priority     lower numerical value is higher priority
   */
  public static synchronized void atExit( Runnable r, final int priority )
  {
    if( atExitRunnableMap == null )
      atExitRunnableMap = new TreeMap();
    
    Integer p = Integer.valueOf(priority);
    LinkedList runnableList = (LinkedList)(atExitRunnableMap.get(p));
    if( runnableList == null )
    {
      runnableList = new LinkedList() {
          public String toString()
          {
            return "[pri=" + priority + ", list=" + super.toString() + "]";
          }
        };
      atExitRunnableMap.put( p, runnableList );
    }
    
    runnableList.add(r);
  }
  public static void atExit( Runnable r )
  {
    atExit( r, Integer.MAX_VALUE / 2 );
  }
  
  public static final long runStringTest( int cnt )
  {
    String str = "0123456789";
    String res = "";
    long t = System.currentTimeMillis();
    for( int i=0; i<cnt; i++ )
      res += str;
    str=res; // silent compiler
    return System.currentTimeMillis() - t;
  }
}


/**
 * Here for now... for convenience of built-in functions/constructors/etc.
 */
class OBuiltinFunction extends Value
{
  private OString[] argNames;
  private OString   name;
  
  OBuiltinFunction( OString name, OString[] argNames )
  {
    super();
    
    this.name     = name;
    this.argNames = argNames;
  }
  
  protected Value getTypeImpl()
  {
    return Function.TYPE;
  }
  
  public String toString()
  {
    String str = "<OBuiltinFunction: " + name + "( ";
    
    if( argNames == null )
    {
      str += "...";
    }
    else
    {
      if( argNames.length > 0 )
      {
        str += argNames[0];
        
        for( int i=1; i<argNames.length; i++ )
        {
          str += argNames[i];
        }
      }
    }
    
    str += " )>";
    
    return str;
  }
  
  public String castToString() { return toString(); }
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
