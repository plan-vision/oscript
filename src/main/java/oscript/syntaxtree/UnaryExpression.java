//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> ( ( "++" | "--" | "+" | "-" | "~" | "!" ) )?
 * f1 -> PostfixExpression()
 * </PRE>
 */
public class UnaryExpression implements Node {
   public NodeOptional f0;
   public PostfixExpression f1;

   public UnaryExpression(NodeOptional n0, PostfixExpression n1) {
      f0 = n0;
      f1 = n1;
   }

   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

