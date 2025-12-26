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
import oscript.exceptions.*;

/**
 * A wrapper for a method of a java object. Because the method that is called is
 * determined by the arguments to the method when it is called, rather than when
 * it is dereferenced, this is actually a wrapper for an array of all methods in
 * a class with a certain name.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class JavaMethodWrapper extends Value implements Runnable {
    private int id;
    private Object javaObject;
    private Method[] methods;

    /**
     * The type object for an script java method.
     */
    public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
    public final static String TYPE_NAME = "JavaMethod";
    public final static String[] MEMBER_NAMES = new String[] { "castToString", "callAsFunction" };
    public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.JavaMethodWrapper");

    /* ======================================================================= */
    /**
     * Class Constructor.
     * 
     * @param id         the name of the method
     * @param javaObject the java-object this method is in
     * @param methods    the methods this is a wrapper for
     */
    JavaMethodWrapper(int id, Object javaObject, Method[] methods) {
        this.id = id;
        this.javaObject = javaObject;
        this.methods = methods;
    }

    /* ======================================================================= */
    /**
     * Get the type of this object. The returned type doesn't have to take into
     * account the possibility of a script type extending a built-in type, since
     * that is handled by {@link #getType}.
     * 
     * @return the object's type
     */
    protected Value getTypeImpl() {
        return TYPE;
    }

    /* ======================================================================= */
    /**
     * Implementing the runnable interface.
     */
    public void run() {
        callAsFunction();
    }
    /* ======================================================================= */
    /**
     * Return a hash code value for this object.
     * 
     * @return a hash code value
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return methods[0].hashCode();
    }

    /* ======================================================================= */
    /**
     * Compare two objects for equality.
     * 
     * @param obj the object to compare to this object
     * @return <code>true</code> if equals, else <code>false</code>
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return ((obj instanceof JavaMethodWrapper) && (((JavaMethodWrapper) obj).methods[0].equals(methods[0])));
    }

    /* ======================================================================= */
    /**
     * Perform the "==" operation.
     * 
     * @param val the other value
     * @return the result
     * @throws PackagedScriptObjectException(NoSuchMethodException)
     */
    public Value bopEquals(Value val) throws PackagedScriptObjectException {
        return OBoolean.makeBoolean(equals(val));
    }

    /* ======================================================================= */
    /**
     * Perform the "!=" operation.
     * 
     * @param val the other value
     * @return the result
     * @throws PackagedScriptObjectException(NoSuchMethodException)
     */
    public Value bopNotEquals(Value val) throws PackagedScriptObjectException {
        return OBoolean.makeBoolean(!equals(val));
    }

    /* ======================================================================= */
    /**
     * Convert this object to a native java <code>String</code> value.
     * 
     * @return a String value
     * @throws PackagedScriptObjectException(NoSuchMethodException)
     */
    public String castToString() throws PackagedScriptObjectException {
        return "[method: " + Symbol.getSymbol(id).castToString() + "]";
    }

    /* ======================================================================= */
    /**
     * Call this object as a function.
     * 
     * @param sf   the current stack frame
     * @param args the arguments to the function, or <code>null</code> if none
     * @return the value returned by the function
     * @throws PackagedScriptObjectException
     * @see Function
     */
    public Value callAsFunction(oscript.util.StackFrame sf, oscript.util.MemberTable args)
            throws PackagedScriptObjectException {
        return JavaBridge.convertToScriptObject(JavaBridge.call(methodAccessor, id, javaObject, methods, sf, args));
    }

    private static final JavaBridge.JavaCallableAccessor methodAccessor = new JavaMethodAccessor();
}

/*
 * Local Variables: tab-width: 2 indent-tabs-mode: nil mode: java
 * c-indentation-style: java c-basic-offset: 2 eval: (c-set-offset
 * 'substatement-open '0) eval: (c-set-offset 'case-label '+) eval:
 * (c-set-offset 'inclass '+) eval: (c-set-offset 'inline-open '0) End:
 */
