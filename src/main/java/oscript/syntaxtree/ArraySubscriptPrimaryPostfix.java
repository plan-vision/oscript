//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> "["
 * f1 -> Expression()
 * f2 -> ( ".." Expression() )?
 * f3 -> "]"
 * </PRE>
 */
public class ArraySubscriptPrimaryPostfix implements Node {
   public NodeToken f0;
   public Expression f1;
   public NodeOptional f2;
   public NodeToken f3;

   public ArraySubscriptPrimaryPostfix(NodeToken n0, Expression n1, NodeOptional n2, NodeToken n3) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
   }

   public ArraySubscriptPrimaryPostfix(Expression n0, NodeOptional n1) {
      f0 = new NodeToken("[");
      f1 = n0;
      f2 = n1;
      f3 = new NodeToken("]");
   }

   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

