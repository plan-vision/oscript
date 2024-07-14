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

import oscript.data.Reference;
import oscript.data.Value;

public interface MemberTable 
{
	public void reset();
	public void free();
	public void reinit(int len);
	public Reference referenceAt(final int idx);
	public int length();
	public MemberTable safeCopy();
	public void push1(Value val);
	public void push2(Value val1, Value val2);
	public void push3(Value val1, Value val2, Value val3);
	public void push4(Value val1, Value val2, Value val3, Value val4);
	public void ensureCapacity(int nsz);
}

