//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

/**
 * Grammar production:
 * <PRE>

 * f0 -> ( "static" | "const" | "private" | "protected" | "public" )*
 * </PRE>
 */
public class Permissions implements Node {
   public NodeListOptional f0;

   public Permissions(NodeListOptional n0) {
      f0 = n0;
   }

   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

