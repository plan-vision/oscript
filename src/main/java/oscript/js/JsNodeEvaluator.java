package oscript.js;

import java.io.File;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import oscript.NodeEvaluator;
import oscript.data.Scope;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.StackFrame;
import oscript.util.SymbolTable;

/**
 * A {@link NodeEvaluator} that delegates execution to a JavaScript function
 * running inside a GraalJS {@link Context}. The JavaScript stub directly calls
 * back into the existing Java evaluator so execution remains under GraalJS
 * while still using the interpreter/compiler semantics unchanged.
 */
public final class JsNodeEvaluator extends NodeEvaluator {

    private static final String WRAPPER_SOURCE = "(function(delegate){return function(scope,stackFrame){return delegate.evalNode(stackFrame,scope);};})";

    private final NodeEvaluator delegate;
    private final File file;
    private final int id;
    private final SymbolTable[] smits = new SymbolTable[3];
    private final Value jsFunction;

    public JsNodeEvaluator(NodeEvaluator delegate) {
        this.delegate = delegate;
        this.file = delegate.getFile();
        this.id = delegate.getId();
        this.smits[0] = delegate.getSharedMemberIndexTable(ALL);
        this.smits[1] = delegate.getSharedMemberIndexTable(PUBPROT);
        this.smits[2] = delegate.getSharedMemberIndexTable(PRIVATE);
        Context context = Context.newBuilder("js").allowAllAccess(true).build();
        Value factory = context.eval("js", WRAPPER_SOURCE);
        this.jsFunction = factory.execute(delegate);
    }

    @Override
    public Object evalNode(StackFrame sf, Scope scope) throws PackagedScriptObjectException {
        Value res = jsFunction.execute(scope, sf);
        return res.isHostObject() ? res.asHostObject() : res;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public SymbolTable getSharedMemberIndexTable(int perm) {
        switch (perm) {
            case PUBPROT:
                return smits[1];
            case PRIVATE:
                return smits[2];
            case ALL:
            default:
                return smits[0];
        }
    }
}
