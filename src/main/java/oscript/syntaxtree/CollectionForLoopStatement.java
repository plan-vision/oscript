//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

/**
 * Grammar production:
 * <PRE>

 * f0 -> "for"
 * f1 -> "("
 * f2 -> PreLoopStatement()
 * f3 -> ":"
 * f4 -> Expression()
 * f5 -> ")"
 * f6 -> EvaluationUnit()
 * </PRE>
 */
public class CollectionForLoopStatement implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public PreLoopStatement f2;
   public NodeToken f3;
   public Expression f4;
   public NodeToken f5;
   public EvaluationUnit f6;
   public boolean hasVarInScope = true;  // assume the worst
   public boolean hasFxnInScope = true;  // assume the worst
   public Node translated = null;

   public CollectionForLoopStatement(NodeToken n0, NodeToken n1, PreLoopStatement n2, NodeToken n3, Expression n4, NodeToken n5, EvaluationUnit n6, boolean hasVarInScope, boolean hasFxnInScope) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
      f5 = n5;
      f6 = n6;
      this.hasVarInScope = hasVarInScope;
      this.hasFxnInScope = hasFxnInScope;
   }

   public CollectionForLoopStatement(PreLoopStatement n0, Expression n1, EvaluationUnit n2) {
      f0 = new NodeToken("for");
      f1 = new NodeToken("(");
      f2 = n0;
      f3 = new NodeToken(":");
      f4 = n1;
      f5 = new NodeToken(")");
      f6 = n2;
   }

   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

