//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> "."
 * f1 -> "this"
 * </PRE>
 */
public class ThisScopeQualifierPrimaryPostfix implements Node {
   public NodeToken f0;
   public NodeToken f1;

   public ThisScopeQualifierPrimaryPostfix(NodeToken n0, NodeToken n1) {
      f0 = n0;
      f1 = n1;
   }

   public ThisScopeQualifierPrimaryPostfix() {
      f0 = new NodeToken(".");
      f1 = new NodeToken("this");
   }

   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

