package oscript.data;


import oscript.util.StackFrame;


/**
 * TODO
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 11930 $
 * @date 	$LastChangedDate: 2012-01-17 16:27:36 +0100 (Di, 17 Jan 2012) $
 * @project VisionR Server 
 */
public abstract class FunctionValueWrapperSimple extends ValueWrapperTempReference {

	public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
		return get();
	}
	
	@Override
	public Value castToSimpleValue() {
		return get();
	}
}
