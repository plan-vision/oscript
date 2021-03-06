//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> "while"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> EvaluationUnit()
 * </PRE>
 */
public class WhileLoopStatement implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public Expression f2;
   public NodeToken f3;
   public EvaluationUnit f4;

   public WhileLoopStatement(NodeToken n0, NodeToken n1, Expression n2, NodeToken n3, EvaluationUnit n4) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
   }

   public WhileLoopStatement(Expression n0, EvaluationUnit n1) {
      f0 = new NodeToken("while");
      f1 = new NodeToken("(");
      f2 = n0;
      f3 = new NodeToken(")");
      f4 = n1;
   }

   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

