package oscript.js;

/**
 * Placeholder singleton exposing the OScript runtime API to generated
 * JavaScript code. All methods are stubs and should be implemented with the
 * appropriate bridge logic.
 */
public final class JsOscriptEmul {

    public static final JsOscriptEmul me = new JsOscriptEmul();

    public final Object TRUE = null;
    public final Object FALSE = null;
    public final Object UNDEFINED = null;
    public final Object NULL = null;

    private JsOscriptEmul() {
    }

    public Object SYMB_GET(int id) {
        // TODO implement symbol lookup
        return null;
    }

    public int SYMB_ID(String name) {
        // TODO implement symbol creation
        return 0;
    }

    public Object NEW_OARRAY(Object... items) {
        // TODO implement array creation
        return null;
    }

    public Object INVOKE(Object callee, Object args) {
        // TODO implement function invocation
        return null;
    }

    public Object INVOKEC(Object callee, Object args) {
        // TODO implement constructor invocation
        return null;
    }

    public Object POSTINC(Object value) {
        // TODO implement post increment
        return null;
    }

    public Object POSTDEC(Object value) {
        // TODO implement post decrement
        return null;
    }

    public Object SCOPE_createMember(Object scope, Object symbolId, int permissions) {
        // TODO implement member creation
        return null;
    }

    public Object SCOPE_lookupInScope(Object scope, Object symbolId) {
        // TODO implement scope lookup
        return null;
    }

    public Object SCOPE_getThis(Object scope) {
        // TODO return "this" for the given scope
        return null;
    }

    public Object SCOPE_getSuper(Object scope) {
        // TODO return "super" for the given scope
        return null;
    }

    public Object SCOPE_getCallee(Object scope) {
        // TODO return callee for the given scope
        return null;
    }

    public Object VAL_opAssign(Object target, Object value) {
        // TODO implement assignment
        return null;
    }

    public Object VAL_castToBooleanSoft(Object value) {
        // TODO implement boolean casting
        return null;
    }

    public Object VAL_bopPlus(Object left, Object right) {
        // TODO implement addition
        return null;
    }

    public Object VAL_bopMinus(Object left, Object right) {
        // TODO implement subtraction
        return null;
    }

    public Object VAL_bopMultiply(Object left, Object right) {
        // TODO implement multiplication
        return null;
    }

    public Object VAL_bopDivide(Object left, Object right) {
        // TODO implement division
        return null;
    }

    public Object VAL_bopRemainder(Object left, Object right) {
        // TODO implement remainder
        return null;
    }

    public Object VAL_bopBitwiseAnd(Object left, Object right) {
        // TODO implement bitwise and
        return null;
    }

    public Object VAL_bopBitwiseOr(Object left, Object right) {
        // TODO implement bitwise or
        return null;
    }

    public Object VAL_bopBitwiseXor(Object left, Object right) {
        // TODO implement bitwise xor
        return null;
    }

    public Object VAL_bopLeftShift(Object left, Object right) {
        // TODO implement left shift
        return null;
    }

    public Object VAL_bopSignedRightShift(Object left, Object right) {
        // TODO implement signed right shift
        return null;
    }

    public Object VAL_bopUnsignedRightShift(Object left, Object right) {
        // TODO implement unsigned right shift
        return null;
    }

    public Object VAL_bopEquals(Object left, Object right) {
        // TODO implement equality
        return null;
    }

    public Object VAL_bopNotEquals(Object left, Object right) {
        // TODO implement inequality
        return null;
    }

    public Object VAL_bopLessThan(Object left, Object right) {
        // TODO implement less-than
        return null;
    }

    public Object VAL_bopGreaterThan(Object left, Object right) {
        // TODO implement greater-than
        return null;
    }

    public Object VAL_bopGreaterThanOrEquals(Object left, Object right) {
        // TODO implement greater-than-or-equals
        return null;
    }

    public Object VAL_bopLessThanOrEquals(Object left, Object right) {
        // TODO implement less-than-or-equals
        return null;
    }

    public Object VAL_bopInstanceOf(Object left, Object right) {
        // TODO implement instanceof
        return null;
    }

    public Object VAL_bopCast(Object left, Object right) {
        // TODO implement cast
        return null;
    }

    public Object VAL_uopIncrement(Object value) {
        // TODO implement increment
        return null;
    }

    public Object VAL_uopDecrement(Object value) {
        // TODO implement decrement
        return null;
    }

    public Object VAL_uopPlus(Object value) {
        // TODO implement unary plus
        return null;
    }

    public Object VAL_uopMinus(Object value) {
        // TODO implement unary minus
        return null;
    }

    public Object VAL_uopBitwiseNot(Object value) {
        // TODO implement bitwise not
        return null;
    }

    public Object VAL_uopLogicalNot(Object value) {
        // TODO implement logical not
        return null;
    }

    public Object VAL_getMember(Object target, Object symbol) {
        // TODO implement member access
        return null;
    }

    public Object VAL_elementAt(Object target, Object index) {
        // TODO implement array access
        return null;
    }

    public Object MAKE_string(String value) {
        // TODO implement string creation
        return null;
    }

    public Object MAKE_exactNumber(String value) {
        // TODO implement exact number creation
        return null;
    }

    public Object MAKE_inexactNumber(String value) {
        // TODO implement inexact number creation
        return null;
    }
}
