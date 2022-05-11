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

}
