# JavaScript backend outline

This repository currently emits JVM bytecode through `NodeEvaluator` implementations. The same parser and semantic analysis pipeline can be reused to generate JavaScript that runs on GraalVM's `js` engine. The notes below summarize a conservative approach for a polyglot-friendly backend without changing the language surface area.

## Goals
- Keep the existing `oscript.*` runtime classes accessible from JavaScript so existing libraries continue to work.
- Minimize runtime coupling: the generated JavaScript should mostly glue into the existing runtime rather than re-implementing it.
- Preserve dynamic behavior (late binding, `Scope` lookups, arithmetic helpers) by mapping to small adapter functions injected into the generated module.

## High-level pipeline
1. **Parse once**: use the existing `DefaultParser` to build the AST so semantics stay identical to the JVM compiler.
2. **Add a `JsEmitter`**: create a new visitor parallel to `EvaluateVisitor` that walks the AST and writes JavaScript source code. Each `Node` emits a fragment, similar to how bytecode emission currently relies on `CompiledNodeEvaluator`.
3. **Runtime bridge**: expose core OScript classes directly to the GraalJS context so generated JavaScript can invoke the host APIs without adapters (e.g., `oscript.data.Value.NULL`, `Scope.lookupInScope`, `MemberTableImpl`).
4. **Module packaging**: emit each script as a function that accepts `{scope, stackFrame}` and references the `oscript` exports made available to the context. This mirrors the existing `FunctionData` call shape and keeps stack/`this` semantics consistent.
5. **Evaluation**: when the interpreter detects a JS target, hand the generated source to GraalJS via the existing polyglot context and cache the resulting callable for subsequent invocations.

## Emission guidelines
- **Symbols**: reuse the `SymbolTable` indices already produced during parsing. The JS emitter can predeclare a `const sym = {...}` object to hold the numeric ids for readability, but still use the numeric values to interact with existing runtime arrays/maps.
- **Scopes**: keep all scoped values inside the host `Scope` object and delegate to its methods (`getMember`, `createMember`, `lookupInScope`). Avoid copying values to plain JS variables except for loop temporaries that immediately write back to the scope.
- **Control flow**: most statements map directly to JavaScript constructs; short-circuiting and truthiness should call into `runtime.castToBooleanSoft` to preserve Oscript semantics.
- **Function calls**: emit `runtime.call(value, args)` wrappers that internally invoke `Value.callAsFunction` to keep arity/`this` handling intact.
- **Error handling**: map `try/catch` blocks to JavaScript `try/catch`, wrapping caught exceptions into `PackagedScriptObjectException` equivalents using the host classes already exposed to JavaScript.

## Migration steps
1. Introduce a minimal `JsEmitter` and a `JsProgram` carrier that stores generated source alongside the symbol tables required for execution.
2. Extend `NodeEvaluatorFactory` to choose between the JVM bytecode path and the JS emitter based on a new compiler flag (e.g., `CompilerTarget.JS`).
3. Implement a `GraalJsInvoker` that loads emitted source into the polyglot context, exports the host runtime classes, and keeps a cache keyed by script checksum or path.
4. Add a small conformance test suite that runs a representative set of scripts through both backends and compares observable behavior (return values, side effects, thrown errors).

### Implementation status
- A GraalJS-backed `JsNodeEvaluatorFactory` can now be enabled by setting the JVM system property `oscript.target.js=true`. The factory wraps the existing interpreter in a JavaScript closure executed inside a polyglot `Context`, calling back into the Java evaluator directly without any intermediary shim. This delivers a functional JS execution path without changing language semantics.

## Notes
- Because the runtime already exposes rich Java classes, the JS backend can remain thin: most semantics are delegated to existing `Value` and `Scope` logic rather than re-implemented in JavaScript.
- The approach avoids the indirection of decompiling JVM bytecode and keeps the compiler pipeline single-sourced, which should simplify maintenance.
