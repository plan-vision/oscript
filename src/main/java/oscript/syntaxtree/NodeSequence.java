//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

import java.util.*;

/**
 * Represents a sequence of nodes nested within a choice, list,
 * optional list, or optional, e.g. ( A B )+ or [ C D E ]
 */
public class NodeSequence implements NodeListInterface {
   public NodeSequence(int n) {
      nodes = new Vector(n);
   }

   public NodeSequence(Node firstNode) {
      nodes = new Vector();
      addNode(firstNode);
   }

   public void addNode(Node n) {
      nodes.addElement(n);
   }

   public Node elementAt(int i)  { return (Node)nodes.elementAt(i); }
   public Enumeration elements() { return nodes.elements(); }
   public int size()             { return nodes.size(); }
   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }

   public Vector nodes;
}

