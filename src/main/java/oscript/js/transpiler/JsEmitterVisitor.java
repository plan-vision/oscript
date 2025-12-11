package oscript.js.transpiler;

import java.util.Enumeration;

import oscript.data.Reference;
import oscript.syntaxtree.AdditiveExpression;
import oscript.syntaxtree.AssignmentExpression;
import oscript.syntaxtree.ConditionalExpression;
import oscript.syntaxtree.ConditionalStatement;
import oscript.syntaxtree.EvaluationUnit;
import oscript.syntaxtree.Expression;
import oscript.syntaxtree.ExpressionBlock;
import oscript.syntaxtree.FunctionCallExpressionListBody;
import oscript.syntaxtree.FunctionCallExpressionList;
import oscript.syntaxtree.FunctionCallPrimaryPostfix;
import oscript.syntaxtree.IdentifierPrimaryPrefix;
import oscript.syntaxtree.Literal;
import oscript.syntaxtree.LogicalAndExpression;
import oscript.syntaxtree.LogicalOrExpression;
import oscript.syntaxtree.MultiplicativeExpression;
import oscript.syntaxtree.Node;
import oscript.syntaxtree.NodeChoice;
import oscript.syntaxtree.NodeList;
import oscript.syntaxtree.NodeListInterface;
import oscript.syntaxtree.NodeListOptional;
import oscript.syntaxtree.NodeOptional;
import oscript.syntaxtree.NodeSequence;
import oscript.syntaxtree.NodeToken;
import oscript.syntaxtree.PrimaryExpression;
import oscript.syntaxtree.PrimaryExpressionNotFunction;
import oscript.syntaxtree.PrimaryPostfix;
import oscript.syntaxtree.PrimaryPostfixWithTrailingFxnCallExpList;
import oscript.syntaxtree.PrimaryPrefix;
import oscript.syntaxtree.PrimaryPrefixNotFunction;
import oscript.syntaxtree.Program;
import oscript.syntaxtree.ProgramFile;
import oscript.syntaxtree.PropertyIdentifierPrimaryPostfix;
import oscript.syntaxtree.ReturnStatement;
import oscript.syntaxtree.ScopeBlock;
import oscript.syntaxtree.ShiftExpression;
import oscript.syntaxtree.ThisScopeQualifierPrimaryPostfix;
import oscript.syntaxtree.TypeExpression;
import oscript.syntaxtree.UnaryExpression;
import oscript.syntaxtree.VariableDeclaration;
import oscript.syntaxtree.VariableDeclarationBlock;
import oscript.visitor.ObjectDepthFirst;

final class JsEmitterVisitor extends ObjectDepthFirst {

    private final JsSourceBuilder out = new JsSourceBuilder();
    private String lastExpression = "Value.UNDEFINED";

    JsSourceBuilder emitProgram(ProgramFile file) {
        out.append("(function(oscript){");
        out.indent();
        out.line("const Value = oscript.data.Value;");
        out.line("const Symbol = oscript.data.Symbol;");
        out.line("const OBoolean = oscript.data.OBoolean;");
        out.line("const OExactNumber = oscript.data.OExactNumber;");
        out.line("const OInexactNumber = oscript.data.OInexactNumber;");
        out.line("const OString = oscript.data.OString;");
        out.line("return function(scope,sf){");
        out.indent();
        out.line("let _r = Value.UNDEFINED;");
        file.accept(this, null);
        out.line("return _r;");
        out.dedent();
        out.line("};");
        out.dedent();
        out.line("})(oscript)");
        return out;
    }

    @Override
    public Object visit(ProgramFile n, Object argu) {
        n.f1.accept(this, argu);
        return null;
    }

    @Override
    public Object visit(Program n, Object argu) {
        n.f0.accept(this, argu);
        return null;
    }

    @Override
    public Object visit(NodeList n, Object argu) {
        for (Enumeration e = n.elements(); e.hasMoreElements();) {
            ((Node) e.nextElement()).accept(this, argu);
        }
        return null;
    }

    @Override
    public Object visit(NodeListOptional n, Object argu) {
        if (n.present()) {
            for (Enumeration e = n.elements(); e.hasMoreElements();) {
                ((Node) e.nextElement()).accept(this, argu);
            }
        }
        return null;
    }

    @Override
    public Object visit(EvaluationUnit n, Object argu) {
        n.f0.accept(this, argu);
        return null;
    }

    @Override
    public Object visit(ScopeBlock n, Object argu) {
        out.line("{");
        out.indent();
        n.f1.accept(this, argu);
        out.dedent();
        out.line("}");
        return null;
    }

    @Override
    public Object visit(VariableDeclarationBlock n, Object argu) {
        n.f0.accept(this, argu);
        return null;
    }

    @Override
    public Object visit(VariableDeclaration n, Object argu) {
        int permissions = getPermissions(n.f0, Reference.ATTR_PROTECTED);
        String name = n.f2.tokenImage;
        out.line("const " + name + " = scope.createMember(Symbol.getSymbol('" + escape(name) + "').getId(), " + permissions + ");");
        if (n.f3.present()) {
            NodeSequence seq = (NodeSequence) n.f3.node;
            String expr = emitExpression((Node) seq.elementAt(1));
            out.line(assign(name, expr));
        }
        return null;
    }

    @Override
    public Object visit(ExpressionBlock n, Object argu) {
        String expr = emitExpression(n.f0);
        out.line("_r = " + expr + ";");
        lastExpression = expr;
        return null;
    }

    @Override
    public Object visit(ReturnStatement n, Object argu) {
        if (n.f1.present()) {
            String expr = emitExpression((Node) n.f1.node);
            out.line("return " + expr + ";");
        } else {
            out.line("return Value.UNDEFINED;");
        }
        return null;
    }

    @Override
    public Object visit(ConditionalStatement n, Object argu) {
        String cond = emitExpression(n.f2);
        out.line("if((" + cond + ").castToBooleanSoft()){");
        out.indent();
        n.f4.accept(this, argu);
        out.dedent();
        if (n.f5.present()) {
            out.line("} else {");
            out.indent();
            ((Node) n.f5.node).accept(this, argu);
            out.dedent();
        }
        out.line("}");
        return null;
    }

    private String emitExpression(Node node) {
        Object res = node.accept(this, Boolean.TRUE);
        return (res instanceof String) ? (String) res : lastExpression;
    }

    @Override
    public Object visit(AssignmentExpression n, Object argu) {
        String val = emitExpression(n.f0);
        for (int i = 0; i < n.f1.size(); i++) {
            NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
            NodeToken op = (NodeToken) ((NodeChoice) seq.elementAt(0)).choice;
            String rhs = emitExpression((Node) seq.elementAt(1));
            val = emitAssignment(op.kind, val, rhs);
        }
        return val;
    }

    private String emitAssignment(int op, String left, String right) {
        switch (op) {
            case oscript.parser.OscriptParserConstants.ASSIGN:
                return assign(left, right);
            case oscript.parser.OscriptParserConstants.PLUSASSIGN:
                return assign(left, left + ".bopPlus(" + right + ")");
            case oscript.parser.OscriptParserConstants.MINUSASSIGN:
                return assign(left, left + ".bopMinus(" + right + ")");
            case oscript.parser.OscriptParserConstants.STARASSIGN:
                return assign(left, left + ".bopMultiply(" + right + ")");
            case oscript.parser.OscriptParserConstants.SLASHASSIGN:
                return assign(left, left + ".bopDivide(" + right + ")");
            case oscript.parser.OscriptParserConstants.REMASSIGN:
                return assign(left, left + ".bopRemainder(" + right + ")");
            case oscript.parser.OscriptParserConstants.ANDASSIGN:
                return assign(left, left + ".bopBitwiseAnd(" + right + ")");
            case oscript.parser.OscriptParserConstants.ORASSIGN:
                return assign(left, left + ".bopBitwiseOr(" + right + ")");
            case oscript.parser.OscriptParserConstants.XORASSIGN:
                return assign(left, left + ".bopBitwiseXor(" + right + ")");
            case oscript.parser.OscriptParserConstants.LSHIFTASSIGN:
                return assign(left, left + ".bopLeftShift(" + right + ")");
            case oscript.parser.OscriptParserConstants.RSIGNEDSHIFTASSIGN:
                return assign(left, left + ".bopSignedRightShift(" + right + ")");
            case oscript.parser.OscriptParserConstants.RUNSIGNEDSHIFTASSIGN:
                return assign(left, left + ".bopUnsignedRightShift(" + right + ")");
            default:
                return assign(left, "Value.UNDEFINED");
        }
    }

    @Override
    public Object visit(ConditionalExpression n, Object argu) {
        String left = emitExpression(n.f0);
        if (n.f1.present()) {
            NodeListInterface list = (NodeListInterface) n.f1.node;
            String t = emitExpression((Node) list.elementAt(1));
            String f = emitExpression((Node) list.elementAt(3));
            return "((" + left + ").castToBooleanSoft() ? " + t + " : " + f + ")";
        }
        return left;
    }

    @Override
    public Object visit(LogicalOrExpression n, Object argu) {
        String val = emitExpression(n.f0);
        for (int i = 0; i < n.f1.size(); i++) {
            NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
            String rhs = emitExpression((Node) seq.elementAt(1));
            val = "((" + val + ").castToBooleanSoft() ? " + val + " : " + rhs + ")";
        }
        return val;
    }

    @Override
    public Object visit(LogicalAndExpression n, Object argu) {
        String val = emitExpression(n.f0);
        for (int i = 0; i < n.f1.size(); i++) {
            NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
            String rhs = emitExpression((Node) seq.elementAt(1));
            val = "((" + val + ").castToBooleanSoft() ? " + rhs + " : " + val + ")";
        }
        return val;
    }

    @Override
    public Object visit(ShiftExpression n, Object argu) {
        String val = emitExpression(n.f0);
        for (int i = 0; i < n.f1.size(); i++) {
            NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
            NodeToken op = (NodeToken) ((NodeChoice) seq.elementAt(0)).choice;
            String rhs = emitExpression((Node) seq.elementAt(1));
            switch (op.tokenImage) {
                case "<<":
                    val = val + ".bopLeftShift(" + rhs + ")";
                    break;
                case ">>":
                    val = val + ".bopSignedRightShift(" + rhs + ")";
                    break;
                case ">>>":
                    val = val + ".bopUnsignedRightShift(" + rhs + ")";
                    break;
                default:
                    val = "Value.UNDEFINED";
                    break;
            }
        }
        return val;
    }

    @Override
    public Object visit(AdditiveExpression n, Object argu) {
        String val = emitExpression(n.f0);
        for (int i = 0; i < n.f1.size(); i++) {
            NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
            NodeToken op = (NodeToken) ((NodeChoice) seq.elementAt(0)).choice;
            String rhs = emitExpression((Node) seq.elementAt(1));
            switch (op.tokenImage) {
                case "+":
                    val = val + ".bopPlus(" + rhs + ")";
                    break;
                case "-":
                    val = val + ".bopMinus(" + rhs + ")";
                    break;
                default:
                    val = "Value.UNDEFINED";
                    break;
            }
        }
        return val;
    }

    @Override
    public Object visit(MultiplicativeExpression n, Object argu) {
        String val = emitExpression(n.f0);
        for (int i = 0; i < n.f1.size(); i++) {
            NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
            NodeToken op = (NodeToken) ((NodeChoice) seq.elementAt(0)).choice;
            String rhs = emitExpression((Node) seq.elementAt(1));
            switch (op.tokenImage) {
                case "*":
                    val = val + ".bopMultiply(" + rhs + ")";
                    break;
                case "/":
                    val = val + ".bopDivide(" + rhs + ")";
                    break;
                case "%":
                    val = val + ".bopRemainder(" + rhs + ")";
                    break;
                default:
                    val = "Value.UNDEFINED";
                    break;
            }
        }
        return val;
    }

    @Override
    public Object visit(UnaryExpression n, Object argu) {
        String val = emitExpression(n.f1);
        if (n.f0.present()) {
            NodeToken op = (NodeToken) n.f0.node;
            switch (op.tokenImage) {
                case "++":
                    val = assign(val, val + ".uopIncrement()");
                    break;
                case "--":
                    val = assign(val, val + ".uopDecrement()");
                    break;
                case "+":
                    val = "(" + val + ".uopPlus())";
                    break;
                case "-":
                    val = "(" + val + ".uopMinus())";
                    break;
                case "~":
                    val = "(" + val + ".uopBitwiseNot())";
                    break;
                case "!":
                    val = "(" + val + ".uopLogicalNot())";
                    break;
                default:
                    break;
            }
        }
        return val;
    }

    @Override
    public Object visit(TypeExpression n, Object argu) {
        return emitExpression(n.f0);
    }

    @Override
    public Object visit(PrimaryExpression n, Object argu) {
        String expr = emitExpression(n.f0);
        for (int i = 0; i < n.f1.size(); i++) {
            expr = emitPostfix((Node) n.f1.elementAt(i), expr);
        }
        return expr;
    }

    @Override
    public Object visit(PrimaryExpressionNotFunction n, Object argu) {
        String expr = emitExpression(n.f0);
        for (int i = 0; i < n.f1.size(); i++) {
            expr = emitPostfix((Node) n.f1.elementAt(i), expr);
        }
        return expr;
    }

    @Override
    public Object visit(PrimaryPrefix n, Object argu) {
        return emitExpression(n.f0);
    }

    @Override
    public Object visit(PrimaryPrefixNotFunction n, Object argu) {
        return emitExpression(n.f0);
    }

    @Override
    public Object visit(IdentifierPrimaryPrefix n, Object argu) {
        String name = n.f0.tokenImage;
        return "scope.lookupInScope(Symbol.getSymbol('" + escape(name) + "').getId())";
    }

    @Override
    public Object visit(Literal n, Object argu) {
        return emitLiteral((NodeToken) n.f0.choice);
    }

    private String emitLiteral(NodeToken token) {
        switch (token.kind) {
            case oscript.parser.OscriptParserConstants.INTEGER_LITERAL:
                return "OExactNumber.makeExactNumber(" + token.tokenImage + ")";
            case oscript.parser.OscriptParserConstants.FLOATING_POINT_LITERAL:
                return "OInexactNumber.makeInexactNumber(" + token.tokenImage + ")";
            case oscript.parser.OscriptParserConstants.STRING_LITERAL: {
                String raw = token.tokenImage;
                String inner = raw.substring(1, raw.length() - 1);
                return "OString.makeString('" + escape(inner) + "')";
            }
            case oscript.parser.OscriptParserConstants.TRUE:
                return "OBoolean.makeBoolean(true)";
            case oscript.parser.OscriptParserConstants.FALSE:
                return "OBoolean.makeBoolean(false)";
            case oscript.parser.OscriptParserConstants.NULL:
                return "Value.NULL";
            case oscript.parser.OscriptParserConstants.UNDEFINED:
                return "Value.UNDEFINED";
            default:
                return "Value.UNDEFINED";
        }
    }

    @Override
    public Object visit(PrimaryPostfix n, Object argu) {
        return emitPostfix(n.f0.choice, (String) argu);
    }

    @Override
    public Object visit(PrimaryPostfixWithTrailingFxnCallExpList n, Object argu) {
        return emitPostfix(n.f0.choice, (String) argu);
    }

    @Override
    public Object visit(FunctionCallExpressionList n, Object argu) {
        return emitArgs(n);
    }

    private String emitArgs(FunctionCallExpressionList list) {
        if (!list.f1.present()) {
            return "[]";
        }
        FunctionCallExpressionListBody body = (FunctionCallExpressionListBody) list.f1.node;
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(emitExpression(body.f0));
        for (int i = 0; i < body.f1.size(); i++) {
            NodeSequence seq = (NodeSequence) body.f1.elementAt(i);
            builder.append(", ");
            builder.append(emitExpression((Node) seq.elementAt(1)));
        }
        builder.append("]");
        return builder.toString();
    }

    private String emitPostfix(Node node, String base) {
        if (node instanceof FunctionCallPrimaryPostfix) {
            String args = emitArgs(((FunctionCallPrimaryPostfix) node).f0);
            return emitInvocation(base, args, false);
        }
        if (node instanceof PropertyIdentifierPrimaryPostfix) {
            String name = ((PropertyIdentifierPrimaryPostfix) node).f1.tokenImage;
            return "(" + base + ").getMember(Symbol.getSymbol('" + escape(name) + "').getId())";
        }
        if (node instanceof oscript.syntaxtree.ArraySubscriptPrimaryPostfix) {
            oscript.syntaxtree.ArraySubscriptPrimaryPostfix arr = (oscript.syntaxtree.ArraySubscriptPrimaryPostfix) node;
            String idx = emitExpression(arr.f1);
            return "(" + base + ").elementAt(" + idx + ")";
        }
        if (node instanceof oscript.syntaxtree.ThisScopeQualifierPrimaryPostfix) {
            return "(" + base + ").getMember(Symbol.getSymbol('this').getId())";
        }
        if (node instanceof PrimaryPostfix) {
            return emitPostfix(((PrimaryPostfix) node).f0.choice, base);
        }
        if (node instanceof PrimaryPostfixWithTrailingFxnCallExpList) {
            return emitPostfix(((PrimaryPostfixWithTrailingFxnCallExpList) node).f0.choice, base);
        }
        return base;
    }

    private String emitArgs(NodeListInterface list) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(emitExpression((Node) ((NodeSequence) list.elementAt(i)).elementAt(1)));
        }
        builder.append("]");
        return builder.toString();
    }

    private static String assign(String target, String value) {
        return target + ".opAssign(" + value + ")";
    }

    private String emitInvocation(String callee, String args, boolean constructor) {
        String method = constructor ? "callAsConstructor" : "callAsFunction";
        return "(() => { const _c = " + callee + "; const _a = " + args + "; const mt = sf.allocateMemberTable(_a.length);" +
                " for(let i=0;i<_a.length;i++){ mt.referenceAt(i).opAssign(_a[i]); }" +
                " return _c." + method + "(sf, mt); })()";
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n");
    }

    private static int getPermissions(oscript.syntaxtree.Permissions n, int attr) {
        for (int i = 0; i < n.f0.size(); i++) {
            NodeToken token = (NodeToken) n.f0.elementAt(i);
            switch (token.kind) {
                case oscript.parser.OscriptParserConstants.PRIVATE:
                    attr = (attr & 0xf0) | Reference.ATTR_PRIVATE;
                    break;
                case oscript.parser.OscriptParserConstants.PROTECTED:
                    attr = (attr & 0xf0) | Reference.ATTR_PROTECTED;
                    break;
                case oscript.parser.OscriptParserConstants.PUBLIC:
                    attr = (attr & 0xf0) | Reference.ATTR_PUBLIC;
                    break;
                case oscript.parser.OscriptParserConstants.STATIC:
                    attr |= Reference.ATTR_STATIC;
                    break;
                case oscript.parser.OscriptParserConstants.CONST:
                    attr |= Reference.ATTR_CONST;
                    break;
                default:
                    break;
            }
        }
        return attr;
    }
}
