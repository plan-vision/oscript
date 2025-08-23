/*=============================================================================
 *     Copyright Texas Instruments 2000-2003.  All Rights Reserved.
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
import oscript.OscriptHost;
import oscript.util.StackFrame;
import oscript.util.MemberTable;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Utilities to convert between script and java types.
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.29
 */
public class JavaBridge
{
  private static LinkedList functionTransformList = new LinkedList();
  
  /*=======================================================================*/
  /**
   * This abstract class is implemented by transformers that understand how
   * to transform a script object (function) to a certain type of java class.
   * For example, this can be used to register a tranformer that can
   * make a wrapper that implements Runnable, or ActionListener.  This way
   * script code can simply pass a script function to java code that
   * expects to take a, for example, ActionListener.
   */
  public static abstract class FunctionTransformer
  {
    private Class targetClass;
    
    /**
     * Class Constructor
     * 
     * @param targetClass   the class to tranfrom script object to
     */
    public FunctionTransformer( Class targetClass )
    {
      this.targetClass = targetClass;
    }
    
    /**
     * Get the type of the class that this tranformer understands how
     * to transform to.
     */
    public Class getTargetClass()
    {
      return targetClass;
    }
    
    /**
     * Perform the transform, and return a java object that is an
     * instance of the class returned by {@link #getTargetClass}.
     */
    public abstract Object transform( Value fxn );
  }
  
  /**
   */
  public static void registerFunctionTransformer( FunctionTransformer ft )
  {
    functionTransformList.add(ft);
  }
  
  /*=======================================================================*/
  /**
   * This is used by java class wrappers to convert the return type back
   * to a java type:
   */
  public static Object convertToJavaObject( Value scriptObj, String javaTypeStr )
  {
    try
    {
      return convertToJavaObject( scriptObj, JavaClassWrapper.forName(javaTypeStr) );
    }
    catch(ClassNotFoundException e)
    {
      e.printStackTrace();
      throw new ProgrammingErrorException("class not found: " + e.getMessage());
    }
  }
  public static Object convertToJavaObject( Value scriptObj, Class cls )
  {
    Object[] javaArgs = new Object[1];
    
    if( convertArgs( new Class[] { cls },
                     javaArgs,
                     new OArray( new Value[] { scriptObj } ) ) > 0 )
    {
      // conversion possible
      return javaArgs[0];
    }
    
    // conversion not possible:
    throw PackagedScriptObjectException.makeExceptionWrapper(
      new OUnsupportedOperationException("cannot convert to: " + cls.getName())
    );
  }
  
  /*=======================================================================*/
  
  /**
   * Abstracts {@link Method} and {@link Constructor} differences
   */
  public interface JavaCallableAccessor
  {
    Class[] getParameterTypes( Object javaCallable );
    Object call( Object javaCallable, Object javaObject, Object[] args )
      throws InvocationTargetException, InstantiationException, IllegalAccessException;
  }
  
  /**
   * Since choosing the correct method to call, and correct constructor to
   * call, uses the same algorithm, instead of duplicating the logic in two
   * places, it is handled by this method.  Having it in one place also
   * makes it easier to explore optimizations in the future.
   * 
   * @param accessor
   * @param id         the symbol (name) of the method/constructor
   * @param javaObject the java object, to pass to {@link JavaCallableAccessor#call}
   * @param javaCallables   the candidate methods/constructors
   * @param sf         the current stack frame
   * @param args       the args
   * @return the return value of {@link JavaCallableAccessor#call}
   */
  public static final Object call( 
          JavaCallableAccessor accessor, int id, Object javaObject, 
          Object[] javaCallables, StackFrame sf, MemberTable args )
  {
    int alen = (args == null) ? 0 : args.length();
    Object   bestCallable = null;
    int      bestJavaArgsScore = 0;
    Object[] bestJavaArgs = null;
    Object[] javaArgs = null;
    
    for( int i=0; i<javaCallables.length; i++ )
    {
      Class[] parameterTypes = accessor.getParameterTypes( javaCallables[i] );
      
      if( parameterTypes.length == alen )
      {
        if( javaArgs == null )
          javaArgs = new Object[alen];
        
        int javaArgsScore = JavaBridge.convertArgs( parameterTypes, javaArgs, args );
        
        if( javaArgsScore > bestJavaArgsScore )
        {
          bestJavaArgs      = javaArgs;
          bestJavaArgsScore = javaArgsScore;
          bestCallable      = javaCallables[i];
          
          javaArgs = null;
        }
      }
    }
    
    if( bestCallable != null )
    {
      try
      {
        return accessor.call( bestCallable, javaObject, bestJavaArgs );
      }
      catch(InvocationTargetException e)
      {
        Throwable t = e.getTargetException();
        
        throw OJavaException.convertException(t);
      }
      catch(Throwable e)         // XXX
      {
        throw OJavaException.convertException(e);
      }
    }
    else
    {
      /* if we get here, we didn't find a callable with the
       * correct number of args:
       */
      LinkedList candidateList = new LinkedList();
      
      for( int i=0; i<javaCallables.length; i++ )
      {
        Class[] parameterTypes = accessor.getParameterTypes( javaCallables[i] );
        
        if( parameterTypes.length == alen )
          candidateList.add(parameterTypes);
      }
      
      Value name = Symbol.getSymbol(id);
      
      if( candidateList.size() == 0 )
      {
        throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
      }
      else
      {
for( int i=0; i<args.length(); i++ )
	OscriptHost.me.error(i+": " + args.referenceAt(i).getType()+ ", " + args.referenceAt(i));
        String msg = "wrong arg types!  Possible candidates:\n";
        for( Iterator itr=candidateList.iterator(); itr.hasNext(); )
        {
          Class[] parameterTypes = (Class[])(itr.next());
          msg += "  " + name.castToString() + "(";
          
          for( int i=0; i<parameterTypes.length; i++ )
          {
            if( i != 0 )
              msg += ", ";
            msg += parameterTypes[i].getName();
          }
          
          msg += ") (" + JavaBridge.convertArgs( parameterTypes, new Object[alen], args ) + ")\n";
        }
        
        throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException(msg) );
      }
    }
  }
  
  /*=======================================================================*/
  /**
   * Utility to convert args to javaArgs of the types specified by
   * parameterTypes.  Each array should be of the same length.  This
   * will return a score of the conversion.  A score of less than or
   * equal to zero indicates that the conversion is not possible.  A
   * higher score is better.
   */
  public static int convertArgs( Class[] parameterTypes, Object[] javaArgs, MemberTable args )
  {
    int score = Integer.MAX_VALUE;
    if( (args == null) || (args.length() == 0) )
      return score;
    
    int argslength = args.length();
    
    if( (javaArgs.length != argslength) || (parameterTypes.length < argslength) )
      throw new ProgrammingErrorException("bad monkey, no banana");
    
    for( int i=0; (i<argslength) && (score > 0); i++ )
    {
    	final Class pt = parameterTypes[i];
    	
      // in case it is a reference:
      Value arg = args.referenceAt(i).unhand();
      
      if( ( (arg == Value.NULL) || (arg == Value.UNDEFINED) ) &&
          !(pt.isPrimitive() || Value.class.isAssignableFrom(pt)) )
      {
        // null can be assigned to any non-primitive
        javaArgs[i] = null;
      }
      else if (pt == BigDecimal.class) {
    	  Object jobj = arg.castToJavaObject();
    	  if (jobj instanceof BigDecimal)
    		  javaArgs[i] = jobj;
    	  else
    		  javaArgs[i] = new BigDecimal(arg.castToString());
      }
      else if( pt.isArray() )
      {
        try
        {
          int len = arg.length();
          Class componentType = pt.getComponentType();
          
          if( arg instanceof OString )
          {
            if( componentType == Character.TYPE )
            {
              // we want methods that take a String to be preferred over
              // methods that take a char[]
              score--;
              javaArgs[i] = arg.castToString().toCharArray();
            }
            else
            {
              // don't support converting a string to any other sort of array:
              return 0;
            }
          }
          else if( OArray.isJavaArray(arg) && 
                   compatibleJavaArray( componentType, arg.castToJavaObject() ) )
          {
            javaArgs[i] = arg.castToJavaObject();
          }
          else if( len > 0 )
          {
            Class[]  arrParameterTypes = new Class[len];
            Value[]  arrArgs = new Value[len];
            
            arrParameterTypes[0] = componentType;
            arrArgs[0]           = arg.elementAt( OExactNumber.makeExactNumber(0) );
            for( int j=1; j<len; j++ )
            {
              arrParameterTypes[j] = arrParameterTypes[0];
              arrArgs[j]           = arg.elementAt( OExactNumber.makeExactNumber(j) );
            }
            
            // primitive types need to be handled specially...
            if( arrParameterTypes[0].isPrimitive() )
            {
              // convert into temporary array:
              Object[] tmpArr = new Object[len];
              score -= Integer.MAX_VALUE - convertArgs( arrParameterTypes, tmpArr, new OArray(arrArgs) );
              
              if( score <= 0 )
                return score;
              
              // now copy to final destination:
              javaArgs[i] = Array.newInstance( arrParameterTypes[0], len );
              
              for( int j=0; j<len; j++ )
                Array.set( javaArgs[i], j, tmpArr[j] );
            }
            else
            {
              Object[] arrJavaArgs = (Object[])(Array.newInstance( arrParameterTypes[0], len ));
              score -= Integer.MAX_VALUE - convertArgs( arrParameterTypes, arrJavaArgs, new OArray(arrArgs) );
              javaArgs[i] = arrJavaArgs;
            }
          }
          else
          {
            score--;
            javaArgs[i] = Array.newInstance( pt.getComponentType(), 0 );
          }
        }
        catch(PackagedScriptObjectException e)
        {
          return 0;
        }
      }
      else if( pt.isPrimitive() )
      {
        if( pt == Boolean.TYPE )
        {
          try
          {
            javaArgs[i] = arg.castToBoolean() ? Boolean.TRUE : Boolean.FALSE;
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( pt == Character.TYPE )
        {
          try
          {
            String str = arg.castToString();
            if( (str != null) && (str.length() == 1) )
              javaArgs[i] = Character.valueOf( str.charAt(0) );
            else
              return 0;
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( pt == Byte.TYPE )
        {
          try
          {
            long val = arg.castToExactNumber();
            
            if( (long)((byte)val) != val )
              return 0;
            
            if( ! arg.bopInstanceOf( OExactNumber.TYPE ).castToBoolean() )
              score--;
            
            javaArgs[i] = Byte.valueOf( (byte)val );
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( pt == Short.TYPE )
        {
          try
          {
            long val = arg.castToExactNumber();
            
            if( (long)((short)val) != val )
              return 0;
            
            if( ! arg.bopInstanceOf( OExactNumber.TYPE ).castToBoolean() )
              score--;
            
            javaArgs[i] = Short.valueOf( (short)val );
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( pt == Integer.TYPE )
        {
          try
          {
            long val = arg.castToExactNumber();
            
            if( (long)((int)val) != val )
              return 0;
            
            if( ! arg.bopInstanceOf( OExactNumber.TYPE ).castToBoolean() )
              score--;
            
            javaArgs[i] = Integer.valueOf( (int)val );
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( pt == Long.TYPE )
        {
          try
          {
            javaArgs[i] = Long.valueOf( arg.castToExactNumber() );
            
            if( ! arg.bopInstanceOf( OExactNumber.TYPE ).castToBoolean() )
              score--;
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( pt == Float.TYPE )
        {
          try
          {
            double val = arg.castToInexactNumber();
            
            if( (double)((float)val) != val )
              return 0;
            
            if( ! arg.bopInstanceOf( OInexactNumber.TYPE ).castToBoolean() )
              score--;
            
            javaArgs[i] = Float.valueOf( (float)val );
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( pt == Double.TYPE )
        {
          try
          {
            javaArgs[i] = Double.valueOf( arg.castToInexactNumber() );
            
            if( ! arg.bopInstanceOf( OInexactNumber.TYPE ).castToBoolean() )
              score--;
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else
        {
          return 0;
        }
      }
      else
      {
        Object obj = arg.castToJavaObject();
        
        // to deal with NULL/UNDEFINED:
        if( obj == null )
          obj = arg;
        
        if( pt.isAssignableFrom( obj.getClass() ) )
        {
          if( pt != obj.getClass() )
            score--;
          
          javaArgs[i] = obj;
        }
        else if( pt.isAssignableFrom( arg.getClass() ) )
        {
          if( pt != arg.getClass() )
            score--;
          
          javaArgs[i] = arg;
        }
        else
        {
          boolean transformed = false;
          
          for( Iterator itr=functionTransformList.iterator(); itr.hasNext(); )
          {
            FunctionTransformer ft = (FunctionTransformer)(itr.next());
            
            if( pt.isAssignableFrom( ft.getTargetClass() ) )
            {
              javaArgs[i] = ft.transform(arg);
              transformed = true;
              break;
            }
          }
          
          if( !transformed )
            return 0;
        }
      }
    }
    
    return score;
  }
  
  private static final boolean compatibleJavaArray( Class componentType, Object javaArr )
  {
    Class t = javaArr.getClass().getComponentType();
    if( t == null )
      return false;
    return componentType.isAssignableFrom(t);
  }
  
  /*=======================================================================*/
  /**
   * Convert a java object to a script object.  Some java types can be
   * converted back to native script types, rather than need a wrapper,
   * so this handles that conversion.
   * 
   * @param javaObject   the java object to make a wrapper for
   */
  public final static Value convertToScriptObject( Object javaObject ) {   
      // java 21+
      return switch (javaObject) {
          case null       -> Value.NULL;
          case Value v    -> v;
          // Inexact numbers first
          case Float v    -> OInexactNumber.makeInexactNumber(v);
          case Double v   -> OInexactNumber.makeInexactNumber(v);
    
          // Big numbers
          case BigDecimal v -> new JavaObjectWrapper(v);
    
          // Other exact numbers
          case Number v  -> OExactNumber.makeExactNumber(v.longValue());
    
          case String v    -> new OString(v);
          case Boolean v   -> OBoolean.makeBoolean(v);
          case Character v -> new OString(v.toString());
          case Class<?> v  -> JavaClassWrapper.getClassWrapper(v);
    
          // ==== FULL ARRAY LIST ====
          case Object[] a   -> OArray.makeArray(a);  // reference type array
          case int[] a      -> OArray.makeArray(a);
          case long[] a     -> OArray.makeArray(a);
          case double[] a   -> OArray.makeArray(a);
          case float[] a    -> OArray.makeArray(a);
          case short[] a    -> OArray.makeArray(a);
          case byte[] a     -> OArray.makeArray(a);
          case char[] a     -> OArray.makeArray(a);
          case boolean[] a  -> OArray.makeArray(a);
    
          default -> new JavaObjectWrapper(javaObject);
      };
  
  }
  public final static Value convertToScriptObject( long longVal )
  {
    return OExactNumber.makeExactNumber(longVal);
  }
  public final static Value convertToScriptObject( double doubleVal )
  {
    return OInexactNumber.makeInexactNumber(doubleVal);
  }
  public final static Value convertToScriptObject( boolean javaObject )
  {
    return OBoolean.makeBoolean(javaObject);
  }
  public final static Value convertToScriptObject( String javaObject )
  {
    if( javaObject == null )
    {
      return Value.NULL;
    }
    else
    {
      return new OString(javaObject);
    }
  }
}
