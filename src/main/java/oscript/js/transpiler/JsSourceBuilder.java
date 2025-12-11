package oscript.js.transpiler;

/**
 * Utility for producing readable JavaScript with consistent indentation. The
 * emitter relies on this helper to keep formatting predictable without adding
 * dependencies on a separate templating engine.
 */
final class JsSourceBuilder {

    private final StringBuilder out = new StringBuilder();
    private int indent = 0;

    JsSourceBuilder append(String text) {
        out.append(text);
        return this;
    }

    JsSourceBuilder newline() {
        out.append('\n');
        for (int i = 0; i < indent; i++) {
            out.append(' ');
            out.append(' ');
        }
        return this;
    }

    JsSourceBuilder line(String text) {
        return newline().append(text);
    }

    void indent() {
        indent++;
    }

    void dedent() {
        indent = Math.max(0, indent - 1);
    }

    @Override
    public String toString() {
        return out.toString();
    }
}
