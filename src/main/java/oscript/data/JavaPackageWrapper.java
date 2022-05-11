/*=============================================================================
 *     Copyright Texas Instruments 2000-2004.  All Rights Reserved.
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

import oscript.exceptions.*;


/**
 * A wrapper for a java package.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public final class JavaPackageWrapper extends OObject
{
  /**
   * The java package this is a wrapper for.
   */
  private String javaPackageName;
  
  /**
   * The type object for an instance of ExactNumber.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.JavaPackageWrapper");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "JavaPackage";
  public final static String[] MEMBER_NAMES   = new String[] {
                                                      "castToString",
                                                      "getMember",
                                                    };
  
  private static java.util.Set declaredPackageSet = new java.util.HashSet();
  
  /*=======================================================================*/
  /**
   * Used to declare the existance of a java package.  A hack to work around
   * the fact that the java vm cannot always tell us what packages exist.
   * 
   * @param javaPackageName   the java package name
   */
  public static void declarePackage( String javaPackageName )
  {
    int idx;
    while( (idx=javaPackageName.lastIndexOf('.')) > 0 )
    {
      declaredPackageSet.add(javaPackageName);
      javaPackageName = javaPackageName.substring( 0, idx );
    }
  }
  
  /*=======================================================================*/
  /**
   * Factory method for package-wrappers.
   * 
   * @param javaPackageName   the java package name
   * @return the package wrapper, or <code>null</code> if no such package
   */
public static Value getPackageWrapper( String javaPackageName )
  {
    // XXX should packages be intern'd?
    if( (Package.getPackage(javaPackageName) != null) || 
        declaredPackageSet.contains(javaPackageName) )
      return new JavaPackageWrapper(javaPackageName);
    return null;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param javaPackage  the java package this is a wrapper for
   */
  private JavaPackageWrapper( String javaPackageName )
  {
    super();
    this.javaPackageName = javaPackageName;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public JavaPackageWrapper( oscript.util.MemberTable args )
  {
    super();
    if( args.length() != 1 )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
    String s=args.referenceAt(0).castToString();
    if (!s.startsWith("oscript") && !s.startsWith("external")) 
    {
        String packagesRoot = "com.planvision.visionr.core.scripting.oscript.api";
        if (s.length() == 0)
        	s = packagesRoot;
        else
        	s = packagesRoot+"."+s;
    	
    }
    javaPackageName = s;
  }
  
  /*=======================================================================*/
  /**
   * Get the type of this object.  The returned type doesn't have to take
   * into account the possibility of a script type extending a built-in
   * type, since that is handled by {@link #getType}.
   * 
   * @return the object's type
   */
  protected Value getTypeImpl()
  {
    return TYPE;
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>String</code> value.
   * 
   * @return a String value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public String castToString()
    throws PackagedScriptObjectException
  {
    return "[package: " + javaPackageName + "]";
  }
  
  public String getPackageName() {
	  return javaPackageName;
  }
  /*=======================================================================*/
  /**
   * Get a member of this object.
   * 
   * @param id           the id of the symbol that maps to the member
   * @param exception    whether an exception should be thrown if the
   *   member object is not resolved
   * @return a reference to the member
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value getMember( int id, boolean exception )
    throws PackagedScriptObjectException
  {
    Integer iid = Integer.valueOf(id);
    Object val = memberCache.get(iid);
    
    if( val == null )
    {
      String path = javaPackageName + "." + Symbol.getSymbol(id).castToString();
      try
      {
        // first see if it is a class:
        val = JavaClassWrapper.getClassWrapper(path);
      }
      catch(ClassNotFoundException e)
      {
        // if not a class, try and load as package:
        val = getPackageWrapper(path);
      }
      
      memberCache.put( iid, (val != null) ? val : (val=Boolean.FALSE) );
    }
    
    if( val == Boolean.FALSE )
      return super.getMember( id, exception );
    
    return (Value)val;
  }
  
  /*=======================================================================*/
  /**
   * Derived classes that implement {@link #getMember} should also
   * implement this.
   * 
   * @param s   the set to populate
   * @param debugger  <code>true</code> if being used by debugger, in
   *   which case both public and private/protected field names should 
   *   be returned
   * @see #getMember
   */
  protected void populateMemberSet( java.util.Set s, boolean debugger )
  {
    // only list what is alreay in the member-cache... a bit lame but I
    // don't see a better way of doing this:
    for( java.util.Iterator itr=memberCache.keySet().iterator(); itr.hasNext(); )
    {
      Integer iid = (Integer)(itr.next());
      if( memberCache.get(iid) != Boolean.FALSE )
        s.add( Symbol.getSymbol( iid.intValue() ) );
    }
  }
  
  private final java.util.Hashtable memberCache = new java.util.Hashtable();
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

