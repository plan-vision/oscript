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


package oscript.data;


import oscript.util.*;



/**
 * Inner class are basically just regular classes, except that we insert
 * an extra arg when calling the constructor... the instance of the
 * enclosing java object.
 * <p>
 * The way this works now is a bunch of extra overhead, and a bit lame,
 * because we end up creating a <code>JavaInnerClassWrapper</code> for
 * every access to the inner class (ie. OuterClass.InnerClass), because
 * we need to associate the object with the class instance.  This means
 * we end up doing the init stuff, not once per class, but once per
 * class per outer object.
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.4
 */
public class JavaInnerClassWrapper extends JavaClassWrapper
{
  /**
   * The instance of the enclosing java class.
   */
  private Value obj;
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param obj          the instance of the outer object
   * @param javaClass    the java class this object is a wrapper for
   */
  public JavaInnerClassWrapper( Value obj, Class javaClass )
  {
    super( oscript.classwrap.ClassWrapGen.getNonWrapperClass(javaClass) );
    
    this.obj = obj;
  }
  
  /*=======================================================================*/
  /**
   * Overloaded to add the "this" object to the argument list, because it
   * is an implicit arg to the inner class constructor.
   */
  protected Object doConstruct( StackFrame sf, MemberTable args, boolean isWrapper )
  {
    if( args == null )
      args = new OArray(1);
    int len = args.length();
    args.ensureCapacity(len+1);
    for( int i=len-1; i>=0; i-- )
      args.referenceAt(i+1).opAssign( args.referenceAt(i) );
    args.referenceAt(0).opAssign(obj);
    return super.doConstruct( sf, args, isWrapper );
  }
  
  /*=======================================================================*/
  /**
   * For testing... see test/testJava.os
   */
  static class StaticClass
  {
    public StaticClass()
    {
    }
    
    class NonStaticClass
    {
      public NonStaticClass()
      {
      }
    }
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
 *   eval: (c-set-offset 'case-label '+)
 *   eval: (c-set-offset 'inclass '+)
 *   eval: (c-set-offset 'inline-open '0)
 *   End:
 */
