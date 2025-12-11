package oscript.js;

import oscript.NodeEvaluator;
import oscript.NodeEvaluatorFactory;
import oscript.data.Symbol;
import oscript.interpreter.InterpretedNodeEvaluatorFactory;
import oscript.js.transpiler.JsTranspilationResult;
import oscript.js.transpiler.JsTranspiler;
import oscript.syntaxtree.Node;
import oscript.syntaxtree.ProgramFile;

/**
 * Factory that wraps the regular interpreter in a GraalJS-backed
 * {@link NodeEvaluator}. This enables running Oscript code via the JavaScript
 * engine while retaining the semantics of the existing interpreter/compiler.
 */
public final class JsNodeEvaluatorFactory implements NodeEvaluatorFactory {

    private final NodeEvaluatorFactory delegateFactory;

    public JsNodeEvaluatorFactory() {
        this(new InterpretedNodeEvaluatorFactory());
    }

    public JsNodeEvaluatorFactory(NodeEvaluatorFactory delegateFactory) {
        this.delegateFactory = delegateFactory;
    }

    @Override
    public NodeEvaluator createNodeEvaluator(String name, Node node) {
        if (node instanceof ProgramFile) {
            JsTranspilationResult result = JsTranspiler.transpile(name, (ProgramFile) node);
            int id = name.endsWith(".os") ? -1 : Symbol.getSymbol(name).getId();
            return new JsTranspilingNodeEvaluator(result, id);
        }

        NodeEvaluator delegate = delegateFactory.createNodeEvaluator(name, node);
        return new JsNodeEvaluator(delegate);
    }
}
