Original project

http://objectscript.sourceforge.net/

Patched and simplified for embedding in visionr-engine
 
## JavaScript backend sketch

See `docs/js-backend.md` for a proposal on emitting JavaScript for GraalVM instead of JVM bytecode. The notes outline how to reuse the existing parser and runtime while targeting the polyglot `js` engine. A minimal GraalJS-backed execution path is available by starting the JVM with `-Doscript.target.js=true`.
