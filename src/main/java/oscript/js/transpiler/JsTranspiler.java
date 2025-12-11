package oscript.js.transpiler;

import oscript.interpreter.InterpretedNodeEvaluator;
import oscript.syntaxtree.ProgramFile;
import oscript.util.OpenHashSymbolTable;
import oscript.util.SymbolTable;

/**
 * Entry point for converting parsed Oscript code into JavaScript. The emitted
 * source wraps the program in a function that accepts {@code scope} and
 * {@code StackFrame} parameters, mirroring the calling convention used by
 * {@link InterpretedNodeEvaluator}.
 */
public final class JsTranspiler {

    private JsTranspiler() {
    }

    public static JsTranspilationResult transpile(String name, ProgramFile file) {
        JsEmitterVisitor emitter = new JsEmitterVisitor();
        JsSourceBuilder builder = emitter.emitProgram(file);

        // For now, we mirror the interpreter's symbol table behavior by
        // providing empty SMITs for all permission levels.
        SymbolTable[] smits = new SymbolTable[]{
            new OpenHashSymbolTable(3, 0.67f),
            new OpenHashSymbolTable(3, 0.67f),
            new OpenHashSymbolTable(3, 0.67f)
        };

        return new JsTranspilationResult(builder.toString(), smits);
    }
}
