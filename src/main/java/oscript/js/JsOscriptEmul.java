package oscript.js;

import oscript.data.OArray;
import oscript.data.OBoolean;
import oscript.data.OExactNumber;
import oscript.data.OInexactNumber;
import oscript.data.OString;
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

    public final Value TRUE = OBoolean.TRUE;
    public final Value FALSE = OBoolean.FALSE;
    public final Value UNDEFINED = Value.UNDEFINED;
    public final Value NULL = Value.NULL;

    private JsOscriptEmul() {
    }

    public Symbol SYMB_GET(int id) {
        return Symbol.getSymbol(id);
    }

    public int SYMB_ID(String name) {
        return Symbol.getSymbol(name).getId();
    }

    public OArray NEW_ARR0() {
        return new OArray();
    }

    public OArray NEW_ARR(Value[] items) {
        return new OArray(items);
    }

    public Value INVOKE(Value callee, Value[] args) {
        return callee.callAsFunction(args);
    }

    public Value INVOKEC(Value callee, Value[] args) {
        return callee.callAsConstructor(args);
    }

    public Value POSTINC(Value value) {
        Value original = value.unhand();
        value.opAssign(value.uopIncrement());
        return original;
    }

    public Value POSTDEC(Value value) {
        Value original = value.unhand();
        value.opAssign(value.uopDecrement());
        return original;
    }

    public Value SCOPE_CM(Scope scope, int symbolId, int permissions) {
        return scope.createMember(symbolId, permissions);
    }

    public Value SCOPE_L(Scope scope, int symbolId) {
        return scope.lookupInScope(symbolId);
    }

    public Value SCOPE_THS(Scope scope) {
        return scope.getThis();
    }

    public Value SCOPE_SPR(Scope scope) {
        return scope.getSuper();
    }

    public Value SCOPE_GCLE(Scope scope) {
        return scope.getCallee();
    }

    public Value VAL_OA(Value target, Value value) {
        target.opAssign(value);
        return value;
    }

    public Value VAL_CB(Value value) {
        return OBoolean.makeBoolean(value.castToBooleanSoft());
    }

    public Value VAL_PLS(Value left, Value right) {
        return left.bopPlus(right);
    }

    public Value VAL_MNS(Value left, Value right) {
        return left.bopMinus(right);
    }

    public Value VAL_MUL(Value left, Value right) {
        return left.bopMultiply(right);
    }

    public Value VAL_DIV(Value left, Value right) {
        return left.bopDivide(right);
    }

    public Value VAL_bopRemainder(Value left, Value right) {
        return left.bopRemainder(right);
    }

    public Value VAL_bopBitwiseAnd(Value left, Value right) {
        return left.bopBitwiseAnd(right);
    }

    public Value VAL_bopBitwiseOr(Value left, Value right) {
        return left.bopBitwiseOr(right);
    }

    public Value VAL_bopBitwiseXor(Value left, Value right) {
        return left.bopBitwiseXor(right);
    }

    public Value VAL_bopLeftShift(Value left, Value right) {
        return left.bopLeftShift(right);
    }

    public Value VAL_bopSignedRightShift(Value left, Value right) {
        return left.bopSignedRightShift(right);
    }

    public Value VAL_bopUnsignedRightShift(Value left, Value right) {
        return left.bopUnsignedRightShift(right);
    }

    public Value VAL_EQ(Value left, Value right) {
        return left.bopEquals(right);
    }

    public Value VAL_NEQ(Value left, Value right) {
        return left.bopNotEquals(right);
    }

    public Value VAL_LT(Value left, Value right) {
        return left.bopLessThan(right);
    }

    public Value VAL_GT(Value left, Value right) {
        return left.bopGreaterThan(right);
    }

    public Value VAL_GTE(Value left, Value right) {
        return left.bopGreaterThanOrEquals(right);
    }

    public Value VAL_LEQ(Value left, Value right) {
        return left.bopLessThanOrEquals(right);
    }

    public Value VAL_IOF(Value left, Value right) {
        return left.bopInstanceOf(right);
    }

    public Value VAL_bopCast(Value left, Value right) {
        return left.bopCast(right);
    }

    public Value VAL_INC(Value value) {
        return value.uopIncrement();
    }

    public Value VAL_DEC(Value value) {
        return value.uopDecrement();
    }

    public Value VAL_OPLS(Value value) {
        return value.uopPlus();
    }

    public Value VAL_OMNS(Value value) {
        return value.uopMinus();
    }

    public Value VAL_uopBitwiseNot(Value value) {
        return value.uopBitwiseNot();
    }

    public Value VAL_uopLogicalNot(Value value) {
        return value.uopLogicalNot();
    }

    public Value VAL_GM(Value target, int symbol) {
        return target.getMember(symbol);
    }

    public Value VAL_EL(Value target, Value index) {
        return target.elementAt(index);
    }

    public Value MAKE_STR(String value) {
        return OString.makeString(value);
    }

    public Value MAKE_EN(Number value) {
        return OExactNumber.makeExactNumber(value.longValue());
    }

    public Value MAKE_IEN(Number value) {
        return OInexactNumber.makeInexactNumber(value.doubleValue());
    }
}
