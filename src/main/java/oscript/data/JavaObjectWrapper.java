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

import java.util.List;
import java.util.Map;

import oscript.exceptions.*;

/**
 * A wrapper for a java object.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class JavaObjectWrapper extends Value {
	private Object javaObject;
	private Value type;

	/* ======================================================================= */
	/**
	 * Class Constructor.
	 * 
	 * @param javaObject the java object this object is a wrapper for
	 */
	public JavaObjectWrapper(Object javaObject) {
		this.javaObject = javaObject;

		if (javaObject == null)
			throw new ProgrammingErrorException("javaObject shouldn't be null");
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
		if (type == null)
			type = JavaClassWrapper.getClassWrapper(javaObject.getClass());
		return type;
	}

	/* ======================================================================= */	
	public Object getMonitor() {
		return javaObject;
	}

	/* ======================================================================= */
	/**
	 * Convert this object to a native java <code>boolean</code> value.
	 * 
	 * @return a boolean value
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 */
	public boolean castToBoolean() throws PackagedScriptObjectException {
		Value fxn = getMember("booleanValue", false);

		if (fxn == null)
			return super.castToBoolean();
		else
			return fxn.callAsFunction().castToBoolean();
	}

	/* ======================================================================= */
	/**
	 * Convert this object to a native java <code>String</code> value.
	 * 
	 * @return a String value
	 * @throws PackagedScriptObjectException(NoSuchMethodException)
	 */
	public String castToString() throws PackagedScriptObjectException {
		String str = javaObject.toString();

		if (str == null)
			str = NULL.castToString();

		return str;
	}

	/* ======================================================================= */
	/**
	 * Convert this object to a native java <code>long</code> value.
	 * 
	 * @return a long value
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 */
	public long castToExactNumber() throws PackagedScriptObjectException {
		Value fxn = getMember("longValue", false);

		if (fxn == null)
			fxn = getMember("intValue", false);

		if (fxn == null)
			fxn = getMember("shortValue", false);

		if (fxn == null)
			fxn = getMember("byteValue", false);

		if (fxn == null)
			return super.castToExactNumber();
		else
			return fxn.callAsFunction().castToExactNumber();
	}

	/* ======================================================================= */
	/**
	 * Convert this object to a native java <code>double</code> value.
	 * 
	 * @return a double value
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 */
	public double castToInexactNumber() throws PackagedScriptObjectException {
		Value fxn = getMember("doubleValue", false);

		if (fxn == null)
			fxn = getMember("floatValue", false);

		if (fxn == null)
			return super.castToInexactNumber();
		else
			return fxn.callAsFunction().castToInexactNumber();
	}

	/* ======================================================================= */
	/**
	 * Convert this object to a native java <code>Object</code> value.
	 * 
	 * @return a java object
	 * @throws PackagedScriptObjectException(NoSuchMethodException)
	 */
	public Object castToJavaObject() throws PackagedScriptObjectException {
		return javaObject;
	}

	/* ======================================================================= */
	/**
	 * Perform the "==" operation.
	 * 
	 * @param val the other value
	 * @return the result
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 */
	public Value bopEquals(Value val) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) javaObject).compareTo(valJavaObject);

			return OBoolean.makeBoolean(r == 0);
		}

		if (javaObject.equals(valJavaObject))
			return OBoolean.TRUE;
		else
			return val.bopEqualsR(this, noSuchMember("=="));
	}

	public Value bopEqualsR(Value val, PackagedScriptObjectException e) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) valJavaObject).compareTo(javaObject);

			return OBoolean.makeBoolean(r == 0);
		}

		if (valJavaObject.equals(javaObject))
			return OBoolean.TRUE;
		else
			return super.bopEqualsR(val, e);
	}

	/* ======================================================================= */
	/**
	 * Perform the "!=" operation.
	 * 
	 * @param val the other value
	 * @return the result
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 */
	public Value bopNotEquals(Value val) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) javaObject).compareTo(valJavaObject);

			return OBoolean.makeBoolean(r != 0);
		}

		if (javaObject.equals(valJavaObject))
			return OBoolean.FALSE;
		else
			return val.bopNotEqualsR(this, noSuchMember("!="));
	}

	public Value bopNotEqualsR(Value val, PackagedScriptObjectException e) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) valJavaObject).compareTo(javaObject);

			return OBoolean.makeBoolean(r != 0);
		}

		if (valJavaObject.equals(javaObject))
			return OBoolean.FALSE;
		else
			return super.bopNotEqualsR(val, e);
	}

	/* ======================================================================= */
	/**
	 * Perform the "<" operation.
	 * 
	 * @param val the other value
	 * @return the result
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 */
	public Value bopLessThan(Value val) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) javaObject).compareTo(valJavaObject);

			return OBoolean.makeBoolean(r < 0);
		}

		return super.bopLessThan(val);
	}

	public Value bopLessThanR(Value val, PackagedScriptObjectException e) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) valJavaObject).compareTo(javaObject);

			return OBoolean.makeBoolean(r < 0);
		}

		return super.bopLessThanR(val, e);
	}

	/* ======================================================================= */
	/**
	 * Perform the ">" operation.
	 * 
	 * @param val the other value
	 * @return the result
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 */
	public Value bopGreaterThan(Value val) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) javaObject).compareTo(valJavaObject);

			return OBoolean.makeBoolean(r > 0);
		}

		return super.bopGreaterThan(val);
	}

	public Value bopGreaterThanR(Value val, PackagedScriptObjectException e) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) valJavaObject).compareTo(javaObject);

			return OBoolean.makeBoolean(r > 0);
		}

		return super.bopGreaterThanR(val, e);
	}

	/* ======================================================================= */
	/**
	 * Perform the "<=" operation.
	 * 
	 * @param val the other value
	 * @return the result
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 */
	public Value bopLessThanOrEquals(Value val) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) javaObject).compareTo(valJavaObject);

			return OBoolean.makeBoolean(r <= 0);
		}

		return super.bopLessThanOrEquals(val);
	}

	public Value bopLessThanOrEqualsR(Value val, PackagedScriptObjectException e) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) valJavaObject).compareTo(javaObject);

			return OBoolean.makeBoolean(r <= 0);
		}

		return super.bopLessThanOrEqualsR(val, e);
	}

	/* ======================================================================= */
	/**
	 * Perform the ">=" operation.
	 * 
	 * @param val the other value
	 * @return the result
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 */
	public Value bopGreaterThanOrEquals(Value val) throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) javaObject).compareTo(valJavaObject);

			return OBoolean.makeBoolean(r >= 0);
		}

		return super.bopGreaterThanOrEquals(val);
	}

	public Value bopGreaterThanOrEqualsR(Value val, PackagedScriptObjectException e)
			throws PackagedScriptObjectException {
		Object valJavaObject = val.castToJavaObject();

		if ((javaObject instanceof Comparable) && (valJavaObject instanceof Comparable)) {
			int r = ((Comparable) valJavaObject).compareTo(javaObject);

			return OBoolean.makeBoolean(r >= 0);
		}

		return super.bopGreaterThanOrEqualsR(val, e);
	}

	/* ======================================================================= */
	/**
	 * For types that implement <code>elementAt</code>, this returns the number of
	 * elements.
	 * 
	 * @return an integer length
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 * @see #elementAt
	 * @see #elementsAt
	 */
	public int length() throws PackagedScriptObjectException {
		if (javaObject instanceof Map)
			JavaBridge.convertToScriptObject(((Map) javaObject).size());
		else if (javaObject instanceof List)
			JavaBridge.convertToScriptObject(((List) javaObject).size());

		return super.length();
	}

	/* ======================================================================= */
	/**
	 * Get the specified index of this object, if this object is an array. If
	 * needed, the array is grown to the appropriate size.
	 * 
	 * @param idx the index to get
	 * @return a reference to the member
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 * @see #length
	 * @see #elementsAt
	 */
	public Value elementAt(Value idx) throws PackagedScriptObjectException {
		if (javaObject instanceof Map)
			return new MapAccessor((Map) javaObject, idx.castToJavaObject());
		else if (javaObject instanceof List)
			return new ListAccessor((List) javaObject, (int) (idx.castToExactNumber()));

		return super.elementAt(idx);
	}

	private static class MapAccessor extends AbstractReference {
		private Map map;
		private Object key;

		MapAccessor(Map map, Object key) {
			super();
			this.map = map;
			this.key = key;
		}

		public void opAssign(Value val) throws PackagedScriptObjectException {
			map.put(key, val.castToJavaObject());
		}

		protected Value get() {
			return JavaBridge.convertToScriptObject(map.get(key));
		}
	}

	private static class ListAccessor extends AbstractReference {
		private List list;
		private int idx;

		ListAccessor(List list, int idx) {
			super();
			this.list = list;
			this.idx = idx;
		}

		public void opAssign(Value val) throws PackagedScriptObjectException {
			list.set(idx, val.castToJavaObject());
		}

		protected Value get() {
			return JavaBridge.convertToScriptObject(list.get(idx));
		}
	}

	/* ======================================================================= */
	/**
	 * Get the specified range of this object, if this object is an array. This
	 * returns a copy of a range of the array.
	 * 
	 * @param idx1 the index index of the beginning of the range, inclusive
	 * @param idx2 the index of the end of the range, inclusive
	 * @return a copy of the specified range of this array
	 * @throws PackagedScriptObjectException(NoSuchMemberException)
	 * @see #length
	 * @see #elementAt
	 */
	public Value elementsAt(Value idx1, Value idx2) throws PackagedScriptObjectException {
		if (javaObject instanceof List) {
			List l = (List) javaObject;
			int i1 = (int) (idx1.castToExactNumber());
			int i2 = (int) (idx2.castToExactNumber());
			Value arr = new OArray(i2 - i1);
			for (int i = i1; i <= i2; i++)
				arr.elementAt(JavaBridge.convertToScriptObject(i)).opAssign(JavaBridge.convertToScriptObject(l.get(i)));
			return arr;
		}

		return super.elementsAt(idx1, idx2);
	}
}

/*
 * Local Variables: tab-width: 2 indent-tabs-mode: nil mode: java
 * c-indentation-style: java c-basic-offset: 2 eval: (c-set-offset
 * 'substatement-open '0) eval: (c-set-offset 'case-label '+) eval:
 * (c-set-offset 'inclass '+) eval: (c-set-offset 'inline-open '0) End:
 */
