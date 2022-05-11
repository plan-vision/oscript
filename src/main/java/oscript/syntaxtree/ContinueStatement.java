//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> "continue"
 * f1 -> ";"
 * </PRE>
 */
public class ContinueStatement implements Node {
   public NodeToken f0;
   public NodeToken f1;

   public ContinueStatement(NodeToken n0, NodeToken n1) {
      f0 = n0;
      f1 = n1;
   }

   public ContinueStatement() {
      f0 = new NodeToken("continue");
      f1 = new NodeToken(";");
   }

   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

