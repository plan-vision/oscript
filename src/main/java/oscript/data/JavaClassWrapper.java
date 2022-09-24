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


import java.lang.reflect.*;
import java.util.*;

import oscript.exceptions.*;
import oscript.util.*;
import oscript.OscriptHost;



/**
 * A wrapper for a java class.  Types should be intern'd.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class JavaClassWrapper extends Type
{
  protected Class javaClass;
  private int id = -1;
  
  transient protected JavaClassWrapperImpl impl;
  
  
  /**
   * Table of all java-class-wrappers.  A <code>JavaClassWrapper</code>
   * instance is intern'd, so there shouldn't be two instances representing
   * the same java class.
   */
  private static Hashtable classWrapperCache = new Hashtable();
  
  /**
   * The type object for an script java type.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.JavaClassWrapper");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "JavaClass";
  public final static String[] MEMBER_NAMES   = new String[] {
                       "isA",
                       "castToString",
                       "castToJavaObject",
                       "callAsConstructor",
                       "callAsExtends",
                       "getMember",
                       "getClassLoader",
                       "getName"
                     };
  
  
  /*=======================================================================*/
  /**
   * The class wrapper instances need to be intern'd, so the types work out
   * right... otherwise you might have multiple wrappers per java class (ie
   * type), which would confuse type checking...
   * 
   * @param javaClass    the java class this object is a wrapper for
   */
  public static synchronized JavaClassWrapper getClassWrapper( Class javaClass )
  {    
    JavaClassWrapper jcw = (JavaClassWrapper)(classWrapperCache.get(javaClass));
    
    if( jcw == null )
    {
      jcw = new JavaClassWrapper(javaClass);
      classWrapperCache.put( javaClass, jcw );
    }
    
    return jcw;
  }
  public static JavaClassWrapper getClassWrapper( String className )
    throws ClassNotFoundException
  {
    return getClassWrapper( forName(className) );
  }
  public static synchronized Class forName( String className )
    throws ClassNotFoundException
  {
    return OscriptHost.me.getClassWrapGenForClassName( className, true);
  }
  
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param javaClass    the java class this object is a wrapper for
   */
  public JavaClassWrapper( Class javaClass )
  {
    super();
    
    this.javaClass = javaClass;
    //init(); // INIT ON LOAD : VISIONR 
  }
  
  /**
   * Initialize this object.  Initialization is done on demand because
   * <code>impl</code> and <code>wrapperImpl</code> are transient, and
   * might not exist if this object gets unserialized...
   */
  public synchronized void init()
  {
	  
    if( impl == null )
    {
    	OscriptHost.me.warn("INIT JAVA CLASS WRAPPER "+javaClass.getName());
      this.id = Symbol.getSymbol( javaClass.getName() ).getId();
          
      impl = new JavaClassWrapperImpl( javaClass);
    }
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
  public String getName()
  {
    return javaClass.getName();
  }
  
  public Class getJavaClass()
  {
    return javaClass;
  }
  
  /*=======================================================================*/
  /**
   * If this object is a type, determine if an instance of this type is
   * an instance of the specified type, ie. if this is <code>type</code>,
   * or a subclass.
   * 
   * @param type         the type to compare this type to
   * @return <code>true</code> or <code>false</code>
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public boolean isA( Value type )
  {
    type = type.unhand();
    
    Class c;
    if( super.isA(type) )
      return true;
    
    if( ((c=javaClass.getSuperclass()) != null)  && getClassWrapper(c).isA(type) )
      return true;
    
    Class[] interfaces = javaClass.getInterfaces();
    
    for( int i=0; i<interfaces.length; i++ )
      if( getClassWrapper(interfaces[i]).isA(type) )
        return true;
    
    return false;
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
    return getName();
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>Object</code> value.
   * 
   * @return a java object
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Object castToJavaObject()
    throws PackagedScriptObjectException
  {
    return javaClass;
  }
  
  /*=======================================================================*/
  /**
   * Call this object as a constructor.
   * 
   * @param sf           the current stack frame
   * @param args         the arguments to the function, or <code>null</code> if none
   * @return the newly constructed object
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsConstructor( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
    return JavaBridge.convertToScriptObject( doConstruct( sf, args, false ) );
  }
  
  /*=======================================================================*/
  /**
   * Call this object as a parent class constructor.
   * 
   * @param sf           the current stack frame
   * @param scope        the object
   * @param args         the arguments to the function, or <code>null</code> if none
   * @return the value returned by the function
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsExtends( StackFrame sf, Scope scope, MemberTable args )
    throws PackagedScriptObjectException
  {
	  throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("java class is final; can't call as extend") );
    
  }
  
  // we have to leave this here, because it gets overloaded... 
  protected Object doConstruct( StackFrame sf, MemberTable args, boolean isWrapper )
  {
    if( impl == null ) init();
    return impl.doConstruct( this, sf, args );
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
    if( impl == null ) init();
    
    Value val = impl.getMemberImpl(id);
    
    // this should handle looking for inner-classes... 
    if( val == null )
    {
      try {
        val = JavaClassWrapper.getClassWrapper( forName( getInnerClassName( javaClass, id ) ) );
      } catch(ClassNotFoundException e) {}
    }
    
    if( val != null )
      return val;
    else
      return super.getMember( id, exception );
  }
  
  /*=======================================================================*/
  /**
   * Get a member of this type.  This is used to interface to the java
   * method of having members be attributes of a type.  Regular object-
   * script object's members are attributes of the object, but in the
   * case of java types (including built-in types), the members are
   * attributes of the type.
   * 
   * @param obj          an object of this type
   * @param id           the id of the symbol that maps to the member
   * @return a reference to the member, or null
   */
  protected Value getTypeMember( Value obj, int id )
  {
    if( impl == null ) init();
    
    obj = obj.unhand();
    
    Object javaObj;
    
    /* XXX hack:  (needed because ExactNumber, InexactNumber, etc. cast to 
     * Long, Double, etc)
     */
    if( (this instanceof BuiltinType) &&
        !(obj instanceof ScriptObject) )
      javaObj = obj;
    else
      javaObj = obj.castToJavaObject();
    
    Value val = getTypeMemberImpl( javaObj, id );
    
    /* XXX hack: (kinda) if we don't find the method, perhaps it is provided
     * by Value... this is sorta like multiple inheritance, but we need to
     * ensure that even java objects implement certain basic methods... if
     * an object is a ScriptObject, this is handled by the ScriptObject
     * class... except getMember() won't return the method... so it will work 
     * for stuff like (foo instanceof Foo), but not foo.bopInstanceOf(Foo)
     */
    if( val == null )
    {
      if( obj instanceof ScriptObject )
        val = ScriptObject.TYPE.getTypeMemberImpl( obj, Symbol.getSymbol("_" + Symbol.getSymbol(id).castToString()).getId() );
      else
        val = Value.TYPE.getTypeMemberImpl( obj, id );
    }
    
    /* If we still haven't found it, check if it is a static
     * member of the class:
     */
    if( val == null )
      val = impl.getMemberImpl(id);
    
    return val;
  }
  
  protected Value getTypeMemberImpl( Object javaObj, int id )
  {
    if( impl == null ) init();
    
    CacheEntry ce = impl.getTypeMemberCacheEntry(id);
    
    if( ce == null )
      return null;
    
    return ce.getMember( id, javaObj );
  }
  
  /*=======================================================================*/
  /**
   * Get the {@link ClassLoader} object for the java class this class is
   * a wrapper for.
   * 
   * @return the {@link ClassLoader} of the java class
   */
  public ClassLoader getClassLoader()
  {
    return javaClass.getClassLoader();
  }
  
  /*=======================================================================*/
  /**
   * Implements the reflection stuff... this is broken out into it's own
   * class because a <code>JavaClassWrapper</code> might have two of these
   * (which get initialized at different times), one for the regular class
   * and one for the wrapper class.  The wrapper class is only used when
   * script code extends a java type.  Also, since we need to create a
   * new instance of <code>JavaInnerClassWrapper</code> for each access,
   * we cache the impl's for inner classes, to avoid having to go thru
   * the expensive init() process multiple times for the same java class.
   */
  protected static class JavaClassWrapperImpl
  {
    Class javaClass;
    
    private Constructor[] constructors;
    
    /**
     * Cache of static fields, maps name (OString) -> Field or Method[]
     */
    private SymbolMap classMemberCache;
    
    /**
     * Cache of instance field, maps name (OString) -> Field or Method[]
     */
    private SymbolMap instanceMemberCache;
    
    private boolean initialized = false;
    
    /**
     * Class Constructor
     */
    JavaClassWrapperImpl( Class javaClass)
    {
      this.javaClass = javaClass;
    }
    
    synchronized void init()
    {
      /* Yes, we check this twice... the reasoning is that by checking it
       * before we call init(), we can avoid a method call (to a synchronized
       * method, no less).  There is a slight race condition there, so we
       * have to check it a second time within the protection of the held
       * monitor.
       */
      if( !initialized )
      {
       constructors = javaClass.getDeclaredConstructors();
        
        /* the handling of unaccessible classes could be a little more optimized,
         * but this will do for now...
         */
        boolean  isNotPublic = ! Modifier.isPublic( javaClass.getModifiers() );
        
        instanceMemberCache = new SymbolMap();
        classMemberCache    = new SymbolMap();
        
       
       
          Method[] methods = javaClass.getMethods();
          Field[]  fields  = javaClass.getFields(); //getDeclaredFields();
          
          for( int i=0; i<fields.length; i++ )
          {
            int id = Symbol.getSymbol( fields[i].getName() ).getId();
            
            if( Modifier.isStatic(fields[i].getModifiers()) )
              addFieldToCache( classMemberCache, id, fields[i] );
            else
              addFieldToCache( instanceMemberCache, id, fields[i] );
          }
          
          for( int i=0; i<methods.length; i++ )
          {
            Method method = methods[i];
            String methodName = method.getName();
            
            int id = Symbol.getSymbol(methodName).getId();
            
            if( isNotPublic )
              method = searchForAccessibleMethod( methodName, javaClass, method.getParameterTypes() );
            
            if( method != null )
            {
              if( Modifier.isStatic(method.getModifiers()) )
                addMethodToCache( classMemberCache, id, method );
              else
                addMethodToCache( instanceMemberCache, id, method );
            }
          }
       
        initialized = true;
      }
    }
    
    /**
     * basically we decide on the constructor with the closest matching args, 
     * and call it to create a new instance.
     */
    Object doConstruct( JavaClassWrapper jcw, StackFrame sf, MemberTable args )
    {
      if( !initialized ) init();
      
      return JavaBridge.call( constructorAccessor, jcw.id, null, constructors, sf, args );
    }
    
    // returns <code>null</code> if not found.  Does not throw exception
    // probably should be updated to return a CacheEntry for consistency
    Value getMemberImpl( int id )
    {
      if( !initialized ) init();
      
      CacheEntry ce = (CacheEntry)(classMemberCache.get(id));
      
      if( ce == null )
        return null;
      
      return ce.getMember( id, null );
    }
    
    CacheEntry getTypeMemberCacheEntry( int id )
    {
      if( !initialized ) init();
      
      Object ce = instanceMemberCache.get(id);
      
      if( ce == null )
      {
        ce = getInnerClass(id);
        
        if( ce == null )
          ce = getBeanAccessor(id);
        
        if( ce == null )
          ce = Boolean.FALSE;
        
        instanceMemberCache.put( id, ce );
      }
      
      if( ce == Boolean.FALSE )
        ce = null;
      
      return (CacheEntry)ce;
    }
    
    private CacheEntry getInnerClass( int id )
    {
      try
      {
        final Class innerClass = forName( getInnerClassName( javaClass, id ) );
        
        return new CacheEntry() {
          
          private JavaClassWrapperImpl[] impls;
          
          public Value getMember( int id, Object javaObj )
          {
            Value obj = JavaBridge.convertToScriptObject(javaObj);
            
            /* not synchronized on innerClassImplCache, to avoid deadlock when 
             * calling forName() triggers loading a class that does something 
             * that causes methods of this object to be called again
             */
            synchronized(javaClass)   // XXX is this ok to sync on?
            {
              JavaInnerClassWrapper jicw = null;
              
              if( impls == null )
              {
                jicw = new JavaInnerClassWrapper( obj, innerClass );
                jicw.init();
                
                impls = new JavaClassWrapperImpl[1];
                impls[0] = jicw.impl;
              }
              
              if( jicw == null )
              {
                jicw = new JavaInnerClassWrapper( obj, impls[0].javaClass );
                
                jicw.impl        = impls[0];
              }
              
              return jicw;
            }
          }
          
        };
      }
      catch(ClassNotFoundException e)
      {
        return null;
      }
    }
    
    /**
     * Check for "getter" and "setter" methods corresponding to <code>id</code>,
     * and if present return a bean-accessor, to allow access to properties of
     * java beans.
     */
    private CacheEntry getBeanAccessor( int id )
    {
      String name = Symbol.getSymbol(id).castToString();
      char l = name.charAt(0);
      if( ! Character.isLowerCase(l) )
        return null;
      
      final String cname = name.substring(0,1).toUpperCase() + 
        ((name.length() > 1) ? name.substring(1) : "");
      
      Object obj = instanceMemberCache.get(
        Symbol.getSymbol("get" + cname).getId()
      );
      final CacheEntry getterCE = (obj == Boolean.FALSE) ? null : (CacheEntry)obj;
      
      if( getterCE == null )
        return null;
      
      return new CacheEntry() {
        
        public Value getMember( final int id, final Object javaObj )
        {
           /* work-around to deal with the case of this getting called while a
            * script class that extends a java class is being constructed (but
            * is not yet linked to the java object).  Since no script types 
            * have bean properties to access, this is the easy work-around:
            * <p>
            * XXX Note work-around for work-around... I need to clean this up!!
            */
          if( (javaObj instanceof Value) && !(javaObj instanceof RegExpResult) )
            return null;
          
          return new AbstractReference() {
              
            private Value setter;
            private Value getter;
            
            public void opAssign( Value val )
              throws PackagedScriptObjectException
            {
              if( setter == null )
              {
                String setterName = "set" + cname;
                Object obj = instanceMemberCache.get(
                  Symbol.getSymbol(setterName).getId()
                );
                CacheEntry setterCE = (obj == Boolean.FALSE) ? null : (CacheEntry)obj;
                
                if( setterCE != null )
                  setter = setterCE.getMember( id, javaObj );
                
                if( setter == null )
                  throw noSuchMember(setterName);
              }
              
              setter.callAsFunction( new Value[] { val } );
            }
            
            protected Value get()
            {
              if( getter == null )
                getter = getterCE.getMember( id, javaObj );
              
              return getter.callAsFunction( Value.emptyArray );
            }
            
          };
        }
      };
    }
    
    void populateTypeMemberSet( Set s )
    {
      for( Iterator itr=instanceMemberCache.keys(); itr.hasNext(); )
      {
        int id = ((Integer)(itr.next())).intValue();
        if( instanceMemberCache.get(id) != Boolean.FALSE )
          s.add( Symbol.getSymbol(id) );
      }
    }
    
    void populateMemberSet( Set s )
    {
      for( Iterator itr=classMemberCache.keys(); itr.hasNext(); )
        s.add( Symbol.getSymbol( ((Integer)(itr.next())).intValue() ) );
    }
  }
  
  private static final JavaBridge.JavaCallableAccessor constructorAccessor = 
    new JavaBridge.JavaCallableAccessor() {
    
    public Class[] getParameterTypes( Object javaCallable )
    {
      return ((Constructor)javaCallable).getParameterTypes();
    }
    
    public Object call( Object javaCallable, Object javaObject, Object[] args )
      throws InvocationTargetException, InstantiationException, IllegalAccessException
    {
      return ((Constructor)javaCallable).newInstance(args);
    }
  };
  
  private static String getInnerClassName( Class javaClass, int id )
  {
    return javaClass.getName() + "$" + Symbol.getSymbol(id).castToString();
  }
  
  /* XXX not a good place for this, but...
  private static final String _arrayToString( Object[] objs )
  {
    String str = "[";
    for( int i=0; i<objs.length; i++ )
      str += objs[i] + ",";
    return str + "]";
  }*/
  
  /**
   */
  private interface CacheEntry
  {
    Value getMember( int id, Object obj );
  }
  
  /**
   * member-cache entry for fields
   */
  private static class FieldCacheEntry
    implements CacheEntry
  {
    private Field field;
    
    FieldCacheEntry(){}
    
    void add( Field field )
    {
      // unlike methods, we only keep track of one method.. but we need to pick
      // the one that isn't eclipsed by a field in a derived classe
      if( (this.field == null) || 
          this.field.getDeclaringClass().isAssignableFrom( field.getDeclaringClass() ) )
        this.field = field;
    }
    
    public Value getMember( int id, final Object obj )
    {
      return new AbstractReference() {
          
          public void opAssign( Value val )
            throws PackagedScriptObjectException
          {
            try {
              field.set( obj, val.castToJavaObject() );
            } catch(IllegalAccessException e) {
              throw OJavaException.convertException(e);
            }
          }
          
          protected Value get()
          {
            try {
              return JavaBridge.convertToScriptObject( field.get(obj) );
            } catch(IllegalAccessException e) {
              throw OJavaException.convertException(e);
            }
          }
          
        };
    }
  }
  
  /**
   * member-cache entry for methods
   */
  private static class MethodCacheEntry
    implements CacheEntry
  {
    private Vector   v;
    private Method[] methods;
    
    public void add( Method method )
    {
      if( v == null )
      {
        v = new Vector();
      }
      
      if( methods != null )
      {
        for( int i=0; i<methods.length; i++ )
          v.add( methods[i] );
        methods = null;
      }
      
      /* I'm not sure if we are supposed to be seeing duplicate methods, or not,
       * but I am (at least under JDK v1.3.1 macosx... I haven't tested others),
       * so try to deal with this in as sane a mannar as possibly by ignoring
       * overriden methods:
       */
      Class[] parameterTypes = method.getParameterTypes();
      for( int i=0; i<v.size(); i++ )
      {
        if( parameterTypesMatch( ((Method)(v.elementAt(i))).getParameterTypes(), parameterTypes ) )
        {
          if( ((Method)(v.elementAt(i))).getDeclaringClass().isAssignableFrom( method.getDeclaringClass() ) )
            v.set( i, method );
          return;
        }
      }
      
      v.add(method);
    }
    
    public synchronized Value getMember( int id, Object obj )
    {
      if( methods == null )
      {
        methods = new Method[v.size()];
        v.copyInto(methods);
        v = null;
      }
      return new JavaMethodWrapper( id, obj, methods );
    }
  }
  
  /**
   * get a member from the specified cache
   */
/*
  private static final Value getMemberFromCache( SymbolMap memberCache, int id, Object obj )
  {
    CacheEntry ce = (CacheEntry)(memberCache.get(id));
    
    if( ce != null )
      return ce.getMember( id, obj );
    
    return null;
  }
*/
  
  /**
   * add a field member to the specified cache
   */
  private static final void addFieldToCache( SymbolMap memberCache, int id, Field field )
  {
    FieldCacheEntry fce = (FieldCacheEntry)(memberCache.get(id));
    if( fce == null )
      memberCache.put( id, fce = new FieldCacheEntry() );
    fce.add(field);
  }
  
  /**
   * add a method member to the specified cache, or if a member already exists
   * for a method with the same name, append this method to the existing member
   */
  private static final void addMethodToCache( SymbolMap memberCache, int id, Method method )
  {
    Object obj = memberCache.get(id);
    
    if( (obj == null) || !(obj instanceof MethodCacheEntry) )
      memberCache.put( id, obj = new MethodCacheEntry() );
    
    MethodCacheEntry mce = (MethodCacheEntry)obj;
    
    mce.add(method);
  }
  
  private static final boolean parameterTypesMatch( Class[] p1, Class[] p2 )
  {
    if( p1.length == p2.length )
    {
      for( int i=0; i<p1.length; i++ )
        if( ! p1[i].equals(p2[i]) )
          return false;
      
      return true;
    }
    
    return false;
  }
  
  /*=======================================================================*/
  /**
   * Utility to search for accessible methods with the specified name and 
   * parameters.
   */
  static final Method searchForAccessibleMethod( String  name, 
                                                 Class   javaClass,
                                                 Class[] paramTypes )
  {
    if( Modifier.isPublic(javaClass.getModifiers()) )
    {
      try
      {
        Method method = javaClass.getDeclaredMethod( name, paramTypes );
        return method;
      }
      catch(NoSuchMethodException e)
      {
        // ignore
      }
    }
    
    // we got here, so what we are looking for isn't in this class... so
    // recursively check parent classes and interfaces:
    
    //    check interfaces:
    Class[] interfaceClasses = javaClass.getInterfaces();
    for( int i=0; i<interfaceClasses.length; i++ )
    {
      Method method = searchForAccessibleMethod( name, interfaceClasses[i], paramTypes );
      
      if( method != null )
        return method;
    }
    
    //    if not an interface, there are superclasses to check:
    Class superClass = javaClass.getSuperclass();
    if( superClass != null )
      return searchForAccessibleMethod( name, superClass, paramTypes );
    
    // if we get here, no match was found:
    return null;
  }
  
  /*=======================================================================*/
  /**
   * maintains unique-ness of a JavaClassWrapper when stuff gets serialized or
   * un-serialized
   */
  Object readResolve()
    throws java.io.ObjectStreamException
  {
    Object obj;
    
    synchronized(JavaClassWrapper.class)
    {
      obj = classWrapperCache.get(javaClass);
      
      if( obj == null )
      {
        obj = this;
        classWrapperCache.put( javaClass, obj );
      }
    }
    
    return obj;
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
  protected void populateMemberSet( Set s, boolean debugger )
  {
    if( impl == null ) init();
    impl.populateMemberSet(s);
  }
  
  /*=======================================================================*/
  /**
   * Derived classes that implement {@link #getTypeMember} should also
   * implement this.
   * 
   * @param s   the set to populate
   * @param debugger  <code>true</code> if being used by debugger, in
   *   which case both public and private/protected field names should 
   *   be returned
   * @see #getTypeMember
   */
  protected void populateTypeMemberSet( Set s, boolean debugger )
  {
    if( impl == null ) init();
    impl.populateTypeMemberSet(s);
  }
  /*=======================================================================*/
  /**
   * For use by test suite...
   */
  public static class Base
  {
    public static final int ID = 1;
    
    public int[] getFoo()
    {
      return new int[] { 1, 2, 3 };
    }
  }
  
  public static class Derived
    extends Base
  {
    public static final int ID = 2;
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
