package oscript.js;

import oscript.data.OArray;
import oscript.data.Scope;
import oscript.data.Symbol;
import oscript.data.Value;

/**
 * Placeholder singleton exposing the OScript runtime API to generated
 * JavaScript code. All methods are stubs and should be implemented with the
 * appropriate bridge logic.
 */
public final class JsOscriptEmul {

    public static final JsOscriptEmul me = new JsOscriptEmul();

    public final Value TRUE = null;
    public final Value FALSE = null;
    public final Value UNDEFINED = null;
    public final Value NULL = null;

    private JsOscriptEmul() {
    }

    public Symbol SYMB_GET(int id) {
        // TODO implement symbol lookup
        return null;
    }

    public int SYMB_ID(String name) {
        // TODO implement symbol creation
        return 0;
    }

    public OArray NEW_OARRAY(Value... items) {
        // TODO implement array creation
        return null;
    }

    public Value INVOKE(Value callee, Value[] args) {
        // TODO implement function invocation
        return null;
    }

    public Value INVOKEC(Value callee, Value[] args) {
        // TODO implement constructor invocation
        return null;
    }

    public Value POSTINC(Value value) {
        // TODO implement post increment
        return null;
    }

    public Value POSTDEC(Value value) {
        // TODO implement post decrement
        return null;
    }

    public Value SCOPE_createMember(Scope scope, int symbolId, int permissions) {
        // TODO implement member creation
        return null;
    }

    public Value SCOPE_lookupInScope(Scope scope, int symbolId) {
        // TODO implement scope lookup
        return null;
    }

    public Value SCOPE_getThis(Scope scope) {
        // TODO return "this" for the given scope
        return null;
    }

    public Value SCOPE_getSuper(Scope scope) {
        // TODO return "super" for the given scope
        return null;
    }

    public Value SCOPE_getCallee(Scope scope) {
        // TODO return callee for the given scope
        return null;
    }

    public Value VAL_opAssign(Value target, Value value) {
        // TODO implement assignment
        return null;
    }

    public Value VAL_castToBooleanSoft(Value value) {
        // TODO implement boolean casting
        return null;
    }

    public Value VAL_bopPlus(Value left, Value right) {
        // TODO implement addition
        return null;
    }

    public Value VAL_bopMinus(Value left, Value right) {
        // TODO implement subtraction
        return null;
    }

    public Value VAL_bopMultiply(Value left, Value right) {
        // TODO implement multiplication
        return null;
    }

    public Value VAL_bopDivide(Value left, Value right) {
        // TODO implement division
        return null;
    }

    public Value VAL_bopRemainder(Value left, Value right) {
        // TODO implement remainder
        return null;
    }

    public Value VAL_bopBitwiseAnd(Value left, Value right) {
        // TODO implement bitwise and
        return null;
    }

    public Value VAL_bopBitwiseOr(Value left, Value right) {
        // TODO implement bitwise or
        return null;
    }

    public Value VAL_bopBitwiseXor(Value left, Value right) {
        // TODO implement bitwise xor
        return null;
    }

    public Value VAL_bopLeftShift(Value left, Value right) {
        // TODO implement left shift
        return null;
    }

    public Value VAL_bopSignedRightShift(Value left, Value right) {
        // TODO implement signed right shift
        return null;
    }

    public Value VAL_bopUnsignedRightShift(Value left, Value right) {
        // TODO implement unsigned right shift
        return null;
    }

    public Value VAL_bopEquals(Value left, Value right) {
        // TODO implement equality
        return null;
    }

    public Value VAL_bopNotEquals(Value left, Value right) {
        // TODO implement inequality
        return null;
    }

    public Value VAL_bopLessThan(Value left, Value right) {
        // TODO implement less-than
        return null;
    }

    public Value VAL_bopGreaterThan(Value left, Value right) {
        // TODO implement greater-than
        return null;
    }

    public Value VAL_bopGreaterThanOrEquals(Value left, Value right) {
        // TODO implement greater-than-or-equals
        return null;
    }

    public Value VAL_bopLessThanOrEquals(Value left, Value right) {
        // TODO implement less-than-or-equals
        return null;
    }

    public Value VAL_bopInstanceOf(Value left, Value right) {
        // TODO implement instanceof
        return null;
    }

    public Value VAL_bopCast(Value left, Value right) {
        // TODO implement cast
        return null;
    }

    public Value VAL_uopIncrement(Value value) {
        // TODO implement increment
        return null;
    }

    public Value VAL_uopDecrement(Value value) {
        // TODO implement decrement
        return null;
    }

    public Value VAL_uopPlus(Value value) {
        // TODO implement unary plus
        return null;
    }

    public Value VAL_uopMinus(Value value) {
        // TODO implement unary minus
        return null;
    }

    public Value VAL_uopBitwiseNot(Value value) {
        // TODO implement bitwise not
        return null;
    }

    public Value VAL_uopLogicalNot(Value value) {
        // TODO implement logical not
        return null;
    }

    public Value VAL_getMember(Value target, int symbol) {
        // TODO implement member access
        return null;
    }

    public Value VAL_elementAt(Value target, Value index) {
        // TODO implement array access
        return null;
    }

    public Value MAKE_string(String value) {
        // TODO implement string creation
        return null;
    }

    public Value MAKE_exactNumber(String value) {
        // TODO implement exact number creation
        return null;
    }

    public Value MAKE_inexactNumber(String value) {
        // TODO implement inexact number creation
        return null;
    }
}
