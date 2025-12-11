package oscript.js;

/**
 * Builds a lightweight object exposing the OScript API surface to GraalJS so
 * transpiled code can call the Java bridge functions directly.
 */
final class JsHostExports {

    private JsHostExports() {
    }

    static JsOscriptEmul create() {
        return JsOscriptEmul.me;
    }
}
