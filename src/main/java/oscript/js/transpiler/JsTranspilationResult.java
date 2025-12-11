package oscript.js.transpiler;

import oscript.util.SymbolTable;

/**
 * Container for the output of the JavaScript transpiler. The result keeps the
 * generated source along with the symbol tables that back any scopes created
 * by the emitted function.
 */
public final class JsTranspilationResult {

    private final String source;
    private final SymbolTable[] sharedMemberIndexTables;

    public JsTranspilationResult(String source, SymbolTable[] sharedMemberIndexTables) {
        this.source = source;
        this.sharedMemberIndexTables = sharedMemberIndexTables;
    }

    public String getSource() {
        return source;
    }

    public SymbolTable[] getSharedMemberIndexTables() {
        return sharedMemberIndexTables;
    }
}
