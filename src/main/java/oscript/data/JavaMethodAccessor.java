package oscript.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import oscript.OscriptHost;
import oscript.data.JavaBridge.JavaCallableAccessor;

public final class JavaMethodAccessor implements JavaCallableAccessor {

    public Class[] getParameterTypes(Object javaCallable) {
        return ((Method) javaCallable).getParameterTypes();
    }

    public Object call(Object javaCallable, Object javaObject, Object[] args)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object result = ((Method) javaCallable).invoke(javaObject, args); 
        return OscriptHost.me.wrapJavaMethodCallResult(result);     
    }
}
