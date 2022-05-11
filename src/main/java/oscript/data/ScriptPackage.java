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
import oscript.OscriptInterpreter;
import oscript.util.MemberTable;

import java.io.File;
import java.util.*;

/**
 * The implementation of a package system for scripts.  This handles 
 * automatically <code>import</code>ing script source files when the
 * have not yet been loaded, or are out of date.  The script package
 * system relies on a couple of coding conventions:
 * <ul>
 *   <li> The object foo.bar.FooBar is declared as a public function
 *        or variable in the source file <i>foo/bar/FooBar.os</i>
 *   <li> Only one public function or variable, <code>FooBar</code>,
 *        is declared in the source file <i>foo/bar/FooBar.os</i>.  
 *        Other variables or functions that are private (ie. not 
 *        <code>public</code>) to that source file may be declared 
 *        in the source file.
 *   <li> Any non-public variables/functions will be private to
 *        that source file.
 * </ul>
 * The way the package system is used:
 * <pre>
 *   const var foo = new ScriptPackage("/path/to/foo");
 *   var fb = new foo.bar.FooBar();
 * </pre>
 * When resolving the request for the member <code>FooBar</code>,
 * if the source file <i>/path/to/foo/bar/FooBar.os</i> has not yet
 * been loaded, or has been modified since the last time it was
 * accessed, the package system will create a new scope and import
 * the source file into that scope.  It will then return the public
 * variable/function <code>FooBar</code>.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public final class ScriptPackage extends OObject
{
  private String path;
  private Scope  parentScope;
  private oscript.util.SymbolMap memberTable;
  
  
  /**
   * The type object for an instance of ScriptPackage.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.ScriptPackage");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "ScriptPackage";
  public final static String[] MEMBER_NAMES   = new String[] {
                                                      "castToString",
                                                      "getMember",
                                                      "reset"
                                                    };
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param javaPackage  the java package this is a wrapper for
   * @param parentScope  the parent scope of this package
   */
  public ScriptPackage( String path, Scope parentScope )
  {
    super();
    
    this.path = path;
    this.parentScope = parentScope;
    
    reset();
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param javaPackage  the java package this is a wrapper for
   */
  public ScriptPackage( String path )
  {
    this( path, OscriptInterpreter.getGlobalScope() );
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public ScriptPackage( MemberTable args )
    throws PackagedScriptObjectException
  {
    this( argsToPath(args), argsToScope(args) );
  }
  
  private static final String argsToPath( MemberTable args )
    throws PackagedScriptObjectException
  {
    if( args.length() >= 1 )
      return args.referenceAt(0).castToString();
    else
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
  }
  
  private static final Scope argsToScope( MemberTable args )
  {
    if( args.length() == 2 )
      return (Scope)(args.referenceAt(1).unhand());
    else if( args.length() == 1 )
      return OscriptInterpreter.getGlobalScope();
    else
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
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
   * Clear the cached members.  This is handy if you need to force reload
   * members during development/debugging.  <i>Because of the potential
   * expense incurred in reloading all members the next time they are
   * accessed, this should only be used for development/debugging, and
   * should not be used by deployed code</i>
   */
  public void reset()
  {
    memberTable = new oscript.util.SymbolMap();
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
    return "[package: " + path + "]";
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
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value getMember( int id, boolean exception )
    throws PackagedScriptObjectException
  {
    Value val = getMemberImpl(id);
    
    if( val != null )
      return val;
    else
      return super.getMember( id, exception );
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
    for( Iterator itr=memberTable.keys(); itr.hasNext(); )
      s.add( Symbol.getSymbol( ((Integer)(itr.next())).intValue() ) );
  }
  
  private synchronized final Value getMemberImpl( int id )
  {
    try
    {
      Object val = memberTable.get(id);
      
      if( val == Boolean.FALSE )
        return null;
      
      CacheEntry ce = (CacheEntry)val;
      
      if( ce == null )
      {
        String basePath = path + "/" + Symbol.getSymbol(id).castToString();
        File file;
        
        // first check if file:
        file = OscriptInterpreter.resolve( basePath + ".os", false );
        if( file.exists() && file.canRead() && !file.isDirectory() )
        {
          ce = new CacheEntry( file, null);
        }
        else
        {
          file = OscriptInterpreter.resolve( basePath, false );
          if( file.exists() && file.isDirectory() )
            ce = new CacheEntry( 
              file, 
              new ScriptPackage(
                path + "/" + Symbol.getSymbol(id).castToString(),
                parentScope
              )
            );
        }
        
        if( ce == null )
        {
          memberTable.put( id, Boolean.FALSE );
          return null;
        }
        
        memberTable.put( id, ce );
      }
      
      // check for files that have changed since last access:
      synchronized(ce)
      {
        if( ce.entry == null )
        {
          Scope scope = new FileScope( parentScope, ce.file );
          try {
              OscriptInterpreter.eval( ce.file, scope );
          } catch (Throwable e) {
        	  throw OJavaException.convertException(e);
          }
          ce.entry = scope.getMember(id);
        }
      }
      
      return ce.entry;
    }
    /*catch(oscript.parser.ParseException e)
    {
      if(DEBUG) e.printStackTrace();
      throw OJavaException.convertException(e);
    }*/
    catch(java.io.IOException e)
    {
      if(DEBUG) e.printStackTrace();
      throw OJavaException.convertException(e);
    }
  }
    
  /**
   * An entry in the id -> member cache
   */
  private static class CacheEntry
  {
    CacheEntry( File file, Value entry)
    {
      this.file  = file;
      this.entry = entry;
    }
    final File file;
    Value entry;
  }
}

/**
 * Use a different type to denote the file-level scopes created by the script
 * package system.
 */
class FileScope
  extends BasicScope
{
  private File file;
  
  FileScope( Scope parentScope, File file )
  {
    super(parentScope);
    this.file = file;
  }
  
  public File getFile()
  {
    return file;
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
