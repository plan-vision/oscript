/*=============================================================================
 *     Copyright Texas Instruments 2003.  All Rights Reserved.
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

package oscript.util;

import java.util.*;

import oscript.exceptions.*;
import oscript.data.*;
import oscript.NodeEvaluator;

/**
 * The "chain" of stack frames is used to track execution context of a
 * particular thread and, when debugging is enabled, give the debugger a chance
 * to run breakpoints.
 * <p>
 * Where possible, the head of the chain of stack frames is passed on the stack,
 * but in cases where it cannot be, such as when control passes to java code and
 * back, a hashtable is used to map the current thread to a
 * <code>StackFrame</code>. To access the current stack frame, or create one if
 * needed, use {@link #currentStackFrame}.
 * <p>
 * While on the interface, the stack frame behaves as a chain of
 * <code>StackFrame</code> objects, behind the scenes an array is used for the
 * stack, and a fly-weight pattern is used for the stack frame objects. This way
 * we (1) avoid extra memory allocations, and (2) can have different
 * implementations of {@link #setLineNumber} depending on whether debugging is
 * enabled or not. (Debugging is automatically enabled when a breakpoint is
 * set.)
 * <p>
 * In order to maintain this allusion, calls to {@link NodeEvaluator#evalNode}
 * must go through the {@link #evalNode} call-gate.
 * <p>
 * The stack frame object is intentionally not thread safe, since it is only
 * accessed from a single thread context. Because of the use of the fly- weight
 * pattern, a stack frame object no longer validly represents a stack frame that
 * has exited, either by normally or via an exception. Because of this, any code
 * that wishes to save a reference to a stack frame object must {@link #clone}
 * it.
 * <p>
 * Because the <code>StackFrame</code> is only accessed from a single thread
 * context, it can provide a lightweight mechanism to allocate storage for
 * {@link BasicScope} objects. This can be used in cases where the scope object
 * only exists on the stack, and is not held after the program enclosed by the
 * scope has finished execution, ie. there is no function enclosed by the scope.
 * For cases of an enclosed function, the scope storage must be allocated from
 * the heap so that it can be valid at some point in the future when the
 * enclosed function is potentially called.
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 1
 */
public abstract class StackFrame {
	/**
	 * note impericially derived stack size number based on a value sufficiently
	 * large that a StackOverFlowException occurs before the stack frame index
	 * reaches this value.
	 */
	private static final int STACK_SIZE = 2048; // reduct when growing stack implemented??
	/**
	 * it would be nice to support paging the member stack, so we can keep it
	 * smaller, and possible save/restore it thru the same call gate as
	 * <code>idx[0]</code> and <code>membersIdx[0]</code> are saved/restored.
	 */
	private static final int MEMBERS_STACK_SIZE = 8192;

	// NEW @ SINGLE THREADED!
	public static final StackFrame currentStackFrame = new RegularStackFrame();
	
	protected StackFrame regularStackFrame;
	/**
	 * The current index, boxed in array so it can be shared between the two
	 * stack-frame instances (regular & debug)
	 */
	protected final int[] idx;

	/**
	 * The node evaluator, which has file, and id info needed when filling in stack
	 * trace.
	 */
	protected final NodeEvaluator[] nes;

	/**
	 * The current line number at each stack frame.
	 */
	protected final int[] lines;

	/**
	 * The current scopes at each stack frame.
	 */
	protected final Scope[] scopes;

	/**
	 * The list of scopes allocated at the current frame, which should be recycled
	 * once the stack frame is released.
	 */
	protected final StackFrameBasicScope[] scopeLists;

	/**
	 * The pool of members that can be used (and re-used) by scope objects that only
	 * ever exist on the stack.
	 */
	private final Reference[] members;

	/**
	 * The index of the first available slot in the members stack, boxed in an array
	 * so it can be shared between the two stack-frame instances (regular & debug)
	 */
	private final int[] membersIdx;

	/**
	 * The number of slots in <code>members</code>.
	 */
	private final int membersCount;

	/**
	 * Pool of available, pre-allocated SFA's. Whenever possible, allocating a SFA
	 * will re-use a SFA from the pool, to avoid dynamic memory allocation
	 * 
	 * @see #allocateMemberTable(int)
	 */
	private StackFrameMemberTable sfaPool = null; // XXX should be shared between both StackFrame objects...

	/**
	 * Pool of available, pre-allocated scope objects. Whenever possible, allocating
	 * a new scope will re-use one from the pool, in order to avoid dynamic memory
	 * allocation.
	 * 
	 * @see #allocateBasicScope(Scope, SymbolTable)
	 */
	private StackFrameBasicScope basicScopePool = null; // XXX should be shared between both StackFrame objects

	/**
	 * Pool of available, pre-allocated fxn-scope objects. Whenever possible,
	 * allocating a new scope will re-use one from the pool, in order to avoid
	 * dynamic memory allocation.
	 * 
	 * @see #allocateFunctionScope(Function, Scope, SymbolTable, MemberTable)
	 */
	private StackFrameFunctionScope functionScopePool = null; // XXX should be shared between both StackFrame objects

	/**
	 * Class Constructor.
	 */
	private StackFrame(int[] idx, NodeEvaluator[] nes, int[] lines, Scope[] scopes, StackFrameBasicScope[] scopeLists,
			Reference[] members, int[] membersIdx) {
		this.idx = idx;
		this.nes = nes;
		this.lines = lines;
		this.scopes = scopes;
		this.scopeLists = scopeLists;

		this.members = members;
		this.membersIdx = membersIdx;

		membersCount = (int) ((members != null) ? members.length : 0);
	}

	/**
	 * Push a new stack frame onto the stack, and pass it to <code>ne</code>'s
	 * {@link #evalNode} method, returning the result.
	 * 
	 * @param ne    the node-evaluator for the node to evaluate
	 * @param scope the scope to evalute in
	 * @return the result
	 */
	public final Object evalNode(NodeEvaluator ne, Scope scope) {
		StackFrame sf = regularStackFrame;

		int idx = ++this.idx[0];
		int membersIdx = this.membersIdx[0];

		// grow stack, if needed:
		if (idx >= nes.length)
			throw PackagedScriptObjectException.makeExceptionWrapper(new OException("stack overflow"));

		nes[idx] = ne;

		try {
			return ne.evalNode(sf, scope);
		} catch (PackagedScriptObjectException e) {
			if (e.val instanceof OException)
				((OException) (e.val)).preserveStackFrame();
			throw e;
		} finally {
			// reset members, since it is possible this didn't happen in the
			// compiler... if the compiler gets better about always resetting,
			// then we won't need this here:
			for (int i = this.membersIdx[0] - 1; i >= membersIdx; i--)
				if (this.members[i] != null)
					this.members[i].reset();

			StackFrameBasicScope scopeList = scopeLists[idx];
			scopeLists[idx] = null;
			while (scopeList != null) {
				StackFrameBasicScope head = scopeList;
				scopeList = scopeList.next;
				head.next = basicScopePool;
				basicScopePool = head;
			}

			// so things can be GC'd:
			scopes[idx] = null;
			nes[idx] = null; // is this needed??
			this.idx[0] = idx - 1;
			this.membersIdx[0] = membersIdx;
		}
	}

	/**
	 * Called by node evaluator to store line number info, and to give the debugger
	 * a chance to see if we've hit a breakpoint.
	 * 
	 * @param scope the current scope
	 * @param line  the current line number
	 */
	public void setLineNumber(Scope scope, int line) {
		int idx = this.idx[0];
		scopes[idx] = scope;
		lines[idx] = line;
	}

	/**
	 * Called by node evaluator to store line number info, and to give the debugger
	 * a chance to see if we've hit a breakpoint. This method is used by the
	 * compiler in cases where the scope hasn't changed sinced last line number, to
	 * save a few instructions.
	 * 
	 * @param scope the current scope
	 * @param line  the current line number
	 */
	public void setLineNumber(int line) {
		lines[idx[0]] = line;
	}

	/**
	 * Allocate a scope from the stack. The basic-scope is freed automatically when
	 * the stack-frame is disposed
	 */
	public final BasicScope allocateBasicScope(Scope prev, SymbolTable smit) {
		int idx = this.idx[0];
		StackFrameBasicScope scope;
		if (basicScopePool != null) {
			scope = basicScopePool;
			scope.reinit(prev, smit);
			basicScopePool = basicScopePool.next;
		} else {
			scope = new StackFrameBasicScope(prev, smit);
		}
		scope.next = scopeLists[idx];
		scopeLists[idx] = scope;
		return scope;
	}

	/**
	 * A scope allocated by the stack-frame, which can be recycled
	 */
	private final class StackFrameBasicScope extends BasicScope {
		StackFrameBasicScope next;

		StackFrameBasicScope(Scope previous, SymbolTable smit) {
			super(previous, smit, allocateMemberTable(smit.size()));
		}

		final void reinit(Scope previous, SymbolTable smit) {
			this.previous = previous;
			this.smit = smit;
			if (members instanceof StackFrameMemberTable)
				((StackFrameMemberTable) members).reinit(smit.size());
			else
				members = allocateMemberTable(smit.size());
		}
	}
	
	public final FunctionScope allocateFunctionScope(Function fxn, Scope prev, SymbolTable smit, MemberTable members) {
		return allocateFunctionScope(fxn,prev,smit,members,null);
	}

	/**
	 * Allocate a fxn-scope from the stack. The fxn-scope must be freed by the
	 * caller.
	 */
	public final FunctionScope allocateFunctionScope(Function fxn, Scope prev, SymbolTable smit, MemberTable members,Value _that/*,Value _super */) {
		StackFrameFunctionScope scope;
		if (functionScopePool != null) {
			scope = functionScopePool;
			scope.reinit(fxn, prev, smit, members,_that/*,_super*/);
			functionScopePool = functionScopePool.next;
		} else {
			scope = new StackFrameFunctionScope(fxn, prev, smit, members,_that/*,_super*/);
		}
		return scope;
	}

	/** 
	 * A scope allocated by the stack-frame, which can be recycled
	 */
	public final class StackFrameFunctionScope extends FunctionScope {
		
		// custom override "this"
		private Value _that;
		private OArray tmpArrNull; // auto NULL arguments if not provided, initialized once if needed, kept again if reused
		
		//private Value _super;
		StackFrameFunctionScope next;
		StackFrameFunctionScope(Function fxn, Scope prev, SymbolTable smit, MemberTable members,Value _that/*,Value _super*/) {
			super(fxn, prev, smit, members);
			this._that=_that;
			//this._super=_super;
		}
		final void reinit(Function fxn, Scope prev, SymbolTable smit, MemberTable members,Value _that/*,Value _super*/) {
			this.fxn = fxn;
			this.previous = prev;
			this.smit = smit;
			this.members = members;
			this._that=_that;
			//this._super=_super;
		}
		@Override
		public final void free() {
			/*super.free();
			// @ 20.12.2025 FREE MEMBERS (reuse member table, important for memory cleanup!)
			this.members.free();
			this.members=null;
			if (this.tmpArrNull != null && this.tmpArrNull.length() > 0)
				this.tmpArrNull.trimSizeSetRefNull(0);
			this._that=null;
			//this._super=null;
			this.next = functionScopePool;
			functionScopePool = this;*/
			
			/* ORIGINAL */
			super.free();
			//this.members.free(); // NEW @ EXPERIMENTAL 
		    this.members = null;
		    this.next = functionScopePool;
		    functionScopePool = this;
		    /* EXTRA */
			if (this.tmpArrNull != null && this.tmpArrNull.length() > 0)
				this.tmpArrNull.trimSizeSetRefNull(0);
			this._that=null;
		}
		
		// custom impl with 'in' auto vargs 
		@Override
		public Value lookupInScope(int id) throws PackagedScriptObjectException {
			Value val = getMemberImpl(id);
			if (val == null) {
				// optional generic VARGS as 'in' support 			
				if (id == Symbols.VARGS_IN && members instanceof StackFrameMemberTable) {		
					StackFrameMemberTable sfm = (StackFrameMemberTable)members;
					return sfm.getAutoVArgs(fxn.fd.nargs/*real number of args*/);
				}
				if (previous != null)
					val = previous.lookupInScope(id);
			}
			return val;
		}
		
		// custom impl : missing arguments null
		@Override
		protected Value getInstanceMemberImpl(int id) {
			final int idx = smit.get(id);
			if (idx < 0)
				return null;
			
			// ORIGINAL CODE :  if(idx >= members.length())  return null;
			// NEW : AUTO NULL ARGS
			final int meml = members instanceof StackFrame.StackFrameMemberTable ? ((StackFrame.StackFrameMemberTable)members).initialLen : members.length();
			final int reli = idx-meml;
			if (reli >= 0) {  // original code was return null @ 20.12.2025 (accessing not provided arguments)
				if (tmpArrNull == null)
					tmpArrNull = new OArray(Math.max(4,reli+1)); // min 4
				else {
					final Reference r = tmpArrNull._referenceAtIfExists(reli);
					if (r != null)
						return r;
				}
				tmpArrNull.ensureCapacity(reli);
				final Reference r=tmpArrNull.referenceAt(reli);
				r.resetNULL(); // INITIAL NULL, may be reassigned
				return r;
			}
			final Reference ref = members.referenceAt(idx);
			if ((ref == null) || (ref.getAttr() == Reference.ATTR_INVALID))
				return null;
			return ref;
		}

		@Override
		public Value getThis()
		{
	    	if (_that != null)
	    		return _that;
	    	return super.getThis();
		}
		
		@Override
	    public Value getThis(Value val) {
	    	if (_that != null)
	    		return _that;
	    	return super.getThis(val);
	    }
	    
/*	    @Override
	    public Value getSuper() {
	    	if (_that != null)
	    		return _super;
	    	return super.getSuper();
	    }*/
	}

	/**
	 * Allocate from the stack.
	 */
	public final MemberTable allocateMemberTable(int sz) {
		StackFrameMemberTable sfa;
		if (sfaPool != null) {
			sfa = sfaPool;
			sfaPool = sfaPool.next;
		} else {
			sfa = new StackFrameMemberTable();
		}
		sfa.reinit(sz);
		return sfa;
	}

	/**
	 * An array object which uses the pre-allocated stack. Note that this array is
	 * not thread safe, because it is only intended to be used from the thread
	 * associated with this stack.
	 */
	public final class StackFrameMemberTable implements MemberTable {
		private int off; // offset into 'members'
		private int len; // length of our part of 'members'
		private int sz; // the actual size of the array, sz <= len
		private int savedOff; // saved 'off' value if we have copyOutOfStack()'d
		protected int initialLen; // save len in reinit(len) 
		private OArray aVArgs;
		
		StackFrameMemberTable next;

		private Reference[] members;

		public void reinit(int len) {
			this.members = StackFrame.this.members;
			this.off = membersIdx[0];
			this.len = initialLen = len;
			this.sz = 0;
			if ((len + off) > membersCount)
				throw new ProgrammingErrorException("failed to allocate from stack for " + this + ", idx=" + idx[0]);
			membersIdx[0] = off + len;
		}

		// nargs > original function args 
		// len > number of actual arguments for this call
		public Value getAutoVArgs(int nargs) {
			if (aVArgs != null)
				return aVArgs;
			if (len <= nargs) /* provided <= actual */
				return aVArgs=FunctionData.__empty; // EMPTY OARRAY (no extra arguments)
			aVArgs = new OArray(len-nargs);
			for (int i=nargs;i<len;i++) 
				aVArgs.push1(members[i+off].unhand());
			return aVArgs;
		}

		/**
		 * grow the member table by copying out of stack (or out of current array)
		 * 
		 * @param grow the number of elements to grow the table by
		 */
		private final void copyOutOfStack(int grow) {
			Reference[] newMembers = new Reference[len + grow];
			if (members == StackFrame.this.members) {
				System.arraycopy(members, off, newMembers, 0, sz);
				// need to null out entries from shared stack to ensure
				// that no-one else tries to recycle a Reference object
				// while it is still used by the safe-copy
				for (int i = off + sz - 1; i >= off; i--)
					members[i] = null;

				if ((off + len) == membersIdx[0])
					membersIdx[0] = off;

				savedOff = off;
			} else {
				System.arraycopy(members, off, newMembers, 0, sz);
			}
			members = newMembers;
			off = 0;
			len += grow;
		}

		public void reset() {
			for (int i = off + sz - 1; i >= off; i--)
				members[i].reset();
		}

		public void free() {
			if (members == StackFrame.this.members) {
				reset();
				membersIdx[0] = off;
			} else {
				membersIdx[0] = savedOff;
			}

			// free the StackFrameMemberTable to the pool:
			members = null;
			aVArgs = null;
			next = sfaPool;
			sfaPool = this;
		}

		// auto assign NULL of idx < len ? NEEDED ? TODO ?
		public Reference referenceAt(int idx) {
			idx += off;
			Reference r = members[idx];
			if (r != null)
				return r;
			return members[idx] = new Reference();
		}
		
		public int length() {
			return sz;
		}

		public void ensureCapacity(int sz) {
			sz++;
			int grow = sz - len;
			if (grow > 0) {
				if (members == StackFrame.this.members) {
					// if off+len == membersIdx, then we are the topmost array on the stack
					// so it is safe to grow up:
					if (membersIdx[0] == (off + len)) {
						len += grow;
						membersIdx[0] += grow;
					} else {
						copyOutOfStack(grow);
					}
				} else if ((sz + off) > members.length) {
					copyOutOfStack(sz + off - members.length);
				}
			}

			if (sz > this.sz)
				this.sz = sz;
		}

		public MemberTable safeCopy() {
			if (members == StackFrame.this.members)
				copyOutOfStack(0);
			// we need to use this OArray constructor to ensure that the same Reference
			// objects in the members table get used... otherwise there could be problems
			// with the compiler's cached Reference's (as local vars) getting out of
			// sync:
			return new OArray(members, sz);
		}

		public void push1(Value val) {
			int idx = sz;
			ensureCapacity(idx);
			referenceAt(idx).reset(val);
		}

		public void push2(Value val1, Value val2) {
			int idx = sz;
			ensureCapacity(idx + 1);
			referenceAt(idx++).reset(val1);
			referenceAt(idx).reset(val2);
		}

		public void push3(Value val1, Value val2, Value val3) {
			int idx = sz;
			ensureCapacity(idx + 2);
			referenceAt(idx++).reset(val1);
			referenceAt(idx++).reset(val2);
			referenceAt(idx).reset(val3);
		}

		public void push4(Value val1, Value val2, Value val3, Value val4) {
			int idx = sz;
			ensureCapacity(idx + 3);
			referenceAt(idx++).reset(val1);
			referenceAt(idx++).reset(val2);
			referenceAt(idx++).reset(val3);
			referenceAt(idx).reset(val4);
		}

		public String toString() {
			return "[" + hashCode() + ": off=" + off + ", sz=" + sz + ", len=" + len + "]";
		}
	}

	/**
	 * Convenience wrapper for {@link #getId}, mainly provided for the benefit of
	 * script code that probably doesn't want to know about ids, and just wants to
	 * think in terms of names.
	 */
	public oscript.data.Value getName() {
		int id = getId();
		if (id == -1)
			return null;
		return Symbol.getSymbol(id);
	}

	/**
	 * The function name for the current stack frame, if there is one, otherwise
	 * <code>-1</code>.
	 */
	public final int getId() {
		return nes[idx[0]].getId();
	}

	/**
	 * The current line number in the current stack frame.
	 */
	public final int getLineNumber() {
		return lines[idx[0]];
	}

	/**
	 * The current scope in the current line number.
	 */
	public final Scope getScope() {
		return scopes[idx[0]];
	}

	/**
	 * Return an iterator of stack frames, starting with the top of the stack, and
	 * iterating to root of stack.
	 */
	public Iterator iterator() {
		return new CollectionIterator(new Iterator() {

			private int idx = StackFrame.this.idx[0];

			public boolean hasNext() {
				return idx > 0;
			}

			public Object next() {
				return new ReadOnlyStackFrame(idx--, nes, lines, scopes);
			}

			public void remove() {
				throw new UnsupportedOperationException("remove");
			}

		});
	}

	public StackFrame getSafeCopy() {
		return (StackFrame) clone();
	}

	/**
	 * Clone the stack frame, which is necessary in cases where you need to keep a
	 * reference to the stack frame (and it's parents) after the original stack
	 * frame has exited, such as to store in an exception.
	 */
	public final Object clone() {
		int idx = this.idx[0];

		NodeEvaluator[] nes = new NodeEvaluator[idx + 1];
		System.arraycopy(this.nes, 0, nes, 0, idx + 1);

		int[] lines = new int[idx + 1];
		System.arraycopy(this.lines, 0, lines, 0, idx + 1);

		Scope[] scopes = new Scope[idx + 1];
		for (int i = 0; i < scopes.length; i++)
			if (this.scopes[i] != null)
				scopes[i] = this.scopes[i].getSafeCopy();

		return new ReadOnlyStackFrame(idx, nes, lines, scopes);
	}

	/**
	 * Convert to string, to print out a line in the stack-trace.
	 */
	public String toString() {
		String fileline = getName() + ":" + getLineNumber();
		int id = getId();
		if (id == -1)
			return fileline;
		return Symbol.getSymbol(id) + " (" + fileline + ")";
	}

	public int hashCode() {
		return getId() ^ getLineNumber();
	}

	public boolean equals(Object obj) {
		if (obj instanceof StackFrame) {
			StackFrame other = (StackFrame) obj;
			return (other.getId() == getId()) && (other.getLineNumber() == getLineNumber());
		}
		return false;
	}

	/* ======================================================================= */
	/**
	 * StackFrame fly-weight to use when not debugging.
	 */
	private static class RegularStackFrame extends StackFrame {
		RegularStackFrame(int[] idx, NodeEvaluator[] nes, int[] lines, Scope[] scopes,
				StackFrameBasicScope[] scopeLists, Reference[] members, int[] membersIdx) {
			super(idx, nes, lines, scopes, scopeLists, members, membersIdx);
			regularStackFrame = this;
		}
		RegularStackFrame() {
			this(new int[] { 0 }, new NodeEvaluator[STACK_SIZE], new int[STACK_SIZE], new Scope[STACK_SIZE],
					new StackFrameBasicScope[STACK_SIZE], new Reference[MEMBERS_STACK_SIZE], new int[] { 1 } 
			// XXX for debugging, need to not start at 0
			);
		}
	}
	/* ======================================================================= */
	/**
	 * A read-only copy of a stack frame, used for a variety of purposes.
	 */
	private static class ReadOnlyStackFrame extends RegularStackFrame {
		ReadOnlyStackFrame(int idx, NodeEvaluator[] nes, int[] lines, Scope[] scopes) {
			super(new int[] { idx }, nes, lines, scopes, null, null, null);
		}
		public void setLineNumber(Scope scope, int line) {
			throw new ProgrammingErrorException("cloned stack frames are read-only");
		}
		public void setLineNumber(int line) {
			throw new ProgrammingErrorException("cloned stack frames are read-only");
		}
		public StackFrame getSafeCopy() {
			return this;
		}
	}

	/* ======================================================================= */
	/**
	 * For debugging
	 */

	public static final void dumpStack(java.io.OutputStream out) {
		dumpStack(new java.io.OutputStreamWriter(out));
	}

	public static final void dumpStack(java.io.Writer out) {
		java.io.PrintWriter ps;

		if (out instanceof java.io.PrintWriter)
			ps = (java.io.PrintWriter) out;
		else
			ps = new java.io.PrintWriter(out);

		for (Iterator itr = currentStackFrame.iterator(); itr.hasNext();)
			ps.println(" at " + itr.next());

		ps.flush();
	}
}

/*
 * Local Variables: tab-width: 2 indent-tabs-mode: nil mode: java
 * c-indentation-style: java c-basic-offset: 2 eval: (c-set-offset
 * 'substatement-open '0) eval: (c-set-offset 'case-label '+) eval:
 * (c-set-offset 'inclass '+) eval: (c-set-offset 'inline-open '0) End:
 */
