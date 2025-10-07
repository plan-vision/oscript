package oscript;

import oscript.data.Value;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.MemberTable;
import oscript.util.StackFrame;
public abstract class OscriptHost {

	public static OscriptHost me;
	public abstract Value callValueAsFunction(Value val,StackFrame sf, MemberTable args ) throws PackagedScriptObjectException;
	public abstract void warn(String msg);
	public abstract void error(String msg);
	public abstract String getVirtualFile(String path);
	public abstract void onInit();
	public abstract NodeEvaluatorFactory newCompiledNodeEvaluatorFactory();
	public abstract Class getClassByName(String className) throws ClassNotFoundException;
	public abstract void publishJavaApis(String packageName,String []classes);	
	//public abstract Object /* class */ compilerMakeClass(byte []data); // only if compiler supported

	// default
	public String nodeNameToClassName(String name) {
		if (name == null || name.isEmpty()) return name;
		StringBuilder b = new StringBuilder(name.length());
		char cc[] = name.toCharArray();
		if (!Character.isJavaIdentifierStart(cc[0]))
			b.append(escapeClassNameChar(cc[0]));
		else
			b.append(cc[0]);
		for (int i=1;i<name.length();i++) {
			char c = cc[i];
			if (!Character.isJavaIdentifierPart(c))
				b.append(escapeClassNameChar(c));	
			else
				b.append(c);
		}
		return b.toString();
	}
	public static final String escapeClassNameChar(char c) {
		switch (c) {
			case '.' :
			case '/' : return "$";
		}
		return "$"+Integer.toHexString((int)c);
	}
	
	// MAY INTERCEPT unwrapping of results for replacing object proxies with wrappers 
	// TODO DOCU used only for JAX-WS WSDL generated generic soap wrappers
	// see compile.native.js after native-image extra generated java source processing 
	// ONLY for native build!
	public abstract Object wrapJavaMethodCallResult(Object result);
	public static interface ClassMappingCallback {
	    public Object map(Object c);
	}
	public static interface ClassMappingUnproxifyCallback {
        public Object __unmap();
	}

}
