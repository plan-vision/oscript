package oscript.js;

import java.io.File;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import oscript.NodeEvaluator;
import oscript.data.Scope;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.js.transpiler.JsTranspilationResult;
import oscript.util.StackFrame;
import oscript.util.SymbolTable;

/**
 * {@link NodeEvaluator} implementation backed by transpiled JavaScript source.
 * The generated function runs inside a dedicated GraalJS {@link Context} that
 * is primed with core OScript classes (via {@link JsHostExports}) so the
 * transpiled JavaScript can call into the existing runtime directly.
 */
final class JsTranspilingNodeEvaluator extends NodeEvaluator {

    private final JsTranspilationResult transpilation;
    private final int id;
    private final Value jsFunction;

    JsTranspilingNodeEvaluator(JsTranspilationResult transpilation, int id) {
        this.transpilation = transpilation;
        this.id = id;
        Context context = Context.newBuilder("js").allowAllAccess(true).build();
        context.getBindings("js").putMember("oscript", JsHostExports.create());
        this.jsFunction = context.eval("js", transpilation.getSource());
    }

    @Override
    public Object evalNode(StackFrame sf, Scope scope) throws PackagedScriptObjectException {
        Value res = jsFunction.execute(scope, sf);
        return res.isHostObject() ? res.asHostObject() : res;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public SymbolTable getSharedMemberIndexTable(int perm) {
        SymbolTable[] smits = transpilation.getSharedMemberIndexTables();
        return smits[Math.min(perm, smits.length - 1)];
    }
}
