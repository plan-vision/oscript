/*=============================================================================
 *     Copyright Texas Instruments 2005.  All Rights Reserved.
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
 */


package oscript.util;

import oscript.data.*;

public final class MemberTableImpl extends Value
{
	private int sz; // the actual size of the array, sz <= len
	private Reference[] members;
	protected MemberTableImpl next;
	private static final int MIN = 4;

	@Override
	public void reinit(int len) {
		if (len < MIN)
			len = MIN;
		if (this.members == null || this.members.length < len) 
			this.members = new Reference[len];
		else
			for (int i = 0; i < sz; i++)
				members[i] = null;
		this.sz = 0;
	}

	@Override
	public void reset() {
		for (int i = 0; i < sz; i++)
			members[i] = null;
		this.sz = 0;
	}

	@Override
	public void free() {
		members = null;
	}

	@Override
	public Reference referenceAt(final int idx) {
		final int t = idx + 1;
		ensureCapacity(t);
		if (sz < t)
			sz = t;		
		Reference r = members[idx];
		if (r != null)
			return r;
		return members[idx] = new Reference();
	}

	@Override
	public int length() {
		return sz;
	}

	@Override
	public MemberTable safeCopy() {
		Reference[] newMembers = new Reference[sz];
		System.arraycopy(members, 0, newMembers, 0, sz);
		return new OArray(newMembers, sz);
	}

	@Override
	public void push1(Value val) {
		ensureCapacity(sz+1);
		
		Reference r = members[sz];
		if (r != null) r.reset(val);
		else members[sz]=new Reference(val);
		sz++;
	}

	@Override
	public void push2(Value val1, Value val2) {
		ensureCapacity(sz+2);
		
		Reference r = members[sz];
		if (r != null) r.reset(val1);
		else members[sz]=new Reference(val1);
		sz++;

		r = members[sz];
		if (r != null) r.reset(val2);
		else members[sz]=new Reference(val2);
		sz++;
	}

	@Override
	public void push3(Value val1, Value val2, Value val3) {
		ensureCapacity(sz+3);
		
		Reference r = members[sz];
		if (r != null) r.reset(val1);
		else members[sz]=new Reference(val1);
		sz++;

		r = members[sz];
		if (r != null) r.reset(val2);
		else members[sz]=new Reference(val2);
		sz++;

		r = members[sz];
		if (r != null) r.reset(val3);
		else members[sz]=new Reference(val3);
		sz++;
	}

	@Override
	public void push4(Value val1, Value val2, Value val3, Value val4) {
		ensureCapacity(sz+4);
		
		Reference r = members[sz];
		if (r != null) r.reset(val1);
		else members[sz]=new Reference(val1);
		sz++;

		r = members[sz];
		if (r != null) r.reset(val2);
		else members[sz]=new Reference(val2);
		sz++;

		r = members[sz];
		if (r != null) r.reset(val3);
		else members[sz]=new Reference(val3);
		sz++;
		
		r = members[sz];
		if (r != null) r.reset(val4);
		else members[sz]=new Reference(val4);
		sz++;		
	}

	@Override
	public String toString() {
		return "[" + hashCode() + ": sz=" + sz + "]";
	}

	public void ensureCapacity(int nsz) {
		final int idx = nsz - 1;
		if (idx >= members.length) {
			Reference old[] = members;
			int nlen = members.length;
			if (nlen < MIN) nlen = MIN;
			while (idx >= nlen) nlen <<= 1;
			members = new Reference[nlen];
			if (sz > 0)
				System.arraycopy(old, 0, members, 0, sz);
		}
	}

	@Override
	protected Value getTypeImpl() {
		return null;
	}

}

