package oscript.js;

import java.util.LinkedHashMap;
import java.util.Map;

import org.graalvm.polyglot.proxy.ProxyObject;

import oscript.compiler.CompiledInnerNodeEvaluator;
import oscript.compiler.CompiledNodeEvaluator;
import oscript.data.Function;
import oscript.data.FunctionData;
import oscript.data.OBoolean;
import oscript.data.OExactNumber;
import oscript.data.OInexactNumber;
import oscript.data.OString;
import oscript.data.Scope;
import oscript.data.Symbol;
import oscript.data.Value;
import oscript.util.MemberTableImpl;
import oscript.util.OpenHashSymbolTable;
import oscript.util.StackFrame;
import oscript.util.SymbolTable;

/**
 * Builds a lightweight object graph exposing core OScript classes to GraalJS
 * so transpiled code can reference them directly (for example
 * {@code oscript.data.Value}).
 */
final class JsHostExports {

    private JsHostExports() {
    }

    static ProxyObject create() {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("compiler", ProxyObject.fromMap(compiler()));
        root.put("data", ProxyObject.fromMap(data()));
        root.put("util", ProxyObject.fromMap(util()));
        return ProxyObject.fromMap(root);
    }

    private static Map<String, Object> compiler() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("CompiledInnerNodeEvaluator", CompiledInnerNodeEvaluator.class);
        map.put("CompiledNodeEvaluator", CompiledNodeEvaluator.class);
        return map;
    }

    private static Map<String, Object> data() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Function", Function.class);
        map.put("FunctionData", FunctionData.class);
        map.put("OBoolean", OBoolean.class);
        map.put("OExactNumber", OExactNumber.class);
        map.put("OInexactNumber", OInexactNumber.class);
        map.put("OString", OString.class);
        map.put("Scope", Scope.class);
        map.put("Symbol", Symbol.class);
        map.put("Value", Value.class);
        return map;
    }

    private static Map<String, Object> util() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("MemberTableImpl", MemberTableImpl.class);
        map.put("OpenHashSymbolTable", OpenHashSymbolTable.class);
        map.put("StackFrame", StackFrame.class);
        map.put("SymbolTable", SymbolTable.class);
        return map;
    }
}
