//
// Generated by JTB 1.2.1
//

package oscript.visitor;
import oscript.syntaxtree.*;

/**
 * All void visitors must implement this interface.
 */
public interface Visitor {
   //
   // void Auto class visitors
   //
   public void visit(NodeList n);
   public void visit(NodeListOptional n);
   public void visit(NodeOptional n);
   public void visit(NodeSequence n);
   public void visit(NodeToken n);

   //
   // User-generated visitor methods below
   //

   /**
    * <PRE>
    * f0 -> ( &lt;UNIX_SELF_EXECUTABLE_COMMENT&gt; )?
    * f1 -> Program(false)
    * f2 -> &lt;EOF&gt;
    * </PRE>
    */
   public void visit(ProgramFile n);

   /**
    * <PRE>
    * f0 -> ( EvaluationUnit() )*
    * </PRE>
    */
   public void visit(Program n);

   /**
    * <PRE>
    * f0 -> ScopeBlock()
    *       | VariableDeclarationBlock()
    *       | FunctionDeclaration()
    *       | TryStatement()
    *       | ForLoopStatement()
    *       | CollectionForLoopStatement()
    *       | WhileLoopStatement()
    *       | ConditionalStatement()
    *       | SynchronizedStatement()
    *       | ReturnStatement()
    *       | BreakStatement()
    *       | ContinueStatement()
    *       | ExpressionBlock()
    *       | ThrowBlock()
    *       | ImportBlock()
    *       | MixinBlock()
    *       | EvalBlock()
    * </PRE>
    */
   public void visit(EvaluationUnit n);

   /**
    * <PRE>
    * f0 -> "{"
    * f1 -> Program(false)
    * f2 -> "}"
    * </PRE>
    */
   public void visit(ScopeBlock n);

   /**
    * <PRE>
    * f0 -> VariableDeclaration()
    * f1 -> ";"
    * </PRE>
    */
   public void visit(VariableDeclarationBlock n);

   /**
    * <PRE>
    * f0 -> Expression()
    * f1 -> ";"
    * </PRE>
    */
   public void visit(ExpressionBlock n);

   /**
    * <PRE>
    * f0 -> "throw"
    * f1 -> Expression()
    * f2 -> ";"
    * </PRE>
    */
   public void visit(ThrowBlock n);

   /**
    * <PRE>

    * f0 -> "import"
    * f1 -> Expression()
    * f2 -> ";"
    * </PRE>
    */
   public void visit(ImportBlock n);

   /**
    * <PRE>

    * f0 -> "mixin"
    * f1 -> Expression()
    * f2 -> ";"
    * </PRE>
    */
   public void visit(MixinBlock n);

   /**
    * <PRE>

    * f0 -> "eval"
    * f1 -> Expression()
    * f2 -> ";"
    * </PRE>
    */
   public void visit(EvalBlock n);

   /**
    * <PRE>

    * f0 -> Permissions(true)
    * f1 -> "var"
    * f2 -> &lt;IDENTIFIER&gt;
    * f3 -> ( "=" Expression() )?
    * </PRE>
    */
   public void visit(VariableDeclaration n);

   /**
    * <PRE>

    * f0 -> Permissions(true)
    * f1 -> "function"
    * f2 -> &lt;IDENTIFIER&gt;
    * f3 -> "("
    * f4 -> ( Arglist() )?
    * f5 -> ")"
    * f6 -> ( "extends" PrimaryExpressionWithTrailingFxnCallExpList() FunctionCallExpressionList() )?
    * f7 -> "{"
    * f8 -> Program(true)
    * f9 -> "}"
    * </PRE>
    */
   public void visit(FunctionDeclaration n);

   /**
    * <PRE>
    * f0 -> Permissions(false)
    * f1 -> &lt;IDENTIFIER&gt;
    * f2 -> ( "," Permissions(false) &lt;IDENTIFIER&gt; )*
    * f3 -> ( "..." )?
    * </PRE>
    */
   public void visit(Arglist n);

   /**
    * <PRE>
    * f0 -> "try"
    * f1 -> EvaluationUnit()
    * f2 -> ( "catch" "(" Expression() &lt;IDENTIFIER&gt; ")" EvaluationUnit() )*
    * f3 -> ( "catch" "(" &lt;IDENTIFIER&gt; ")" EvaluationUnit() )?
    * f4 -> ( "finally" EvaluationUnit() )?
    * </PRE>
    */
   public void visit(TryStatement n);

   /**
    * <PRE>

    * f0 -> "for"
    * f1 -> "("
    * f2 -> (  PreLoopStatement() )?
    * f3 -> ";"
    * f4 -> ( Expression() )?
    * f5 -> ";"
    * f6 -> ( Expression() )?
    * f7 -> ")"
    * f8 -> EvaluationUnit()
    * </PRE>
    */
   public void visit(ForLoopStatement n);

   /**
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
   public void visit(CollectionForLoopStatement n);

   /**
    * <PRE>
    * f0 -> VariableDeclaration()
    *       | Expression()
    * </PRE>
    */
   public void visit(PreLoopStatement n);

   /**
    * <PRE>
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> EvaluationUnit()
    * </PRE>
    */
   public void visit(WhileLoopStatement n);

   /**
    * <PRE>
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> EvaluationUnit()
    * f5 -> ( "else" EvaluationUnit() )?
    * </PRE>
    */
   public void visit(ConditionalStatement n);

   /**
    * <PRE>
    * f0 -> "synchronized"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> EvaluationUnit()
    * </PRE>
    */
   public void visit(SynchronizedStatement n);

   /**
    * <PRE>
    * f0 -> "return"
    * f1 -> ( Expression() )?
    * f2 -> ";"
    * </PRE>
    */
   public void visit(ReturnStatement n);

   /**
    * <PRE>
    * f0 -> "break"
    * f1 -> ";"
    * </PRE>
    */
   public void visit(BreakStatement n);

   /**
    * <PRE>
    * f0 -> "continue"
    * f1 -> ";"
    * </PRE>
    */
   public void visit(ContinueStatement n);

   /**
    * <PRE>
    * f0 -> AssignmentExpression()
    * f1 -> ( "," AssignmentExpression() )*
    * </PRE>
    */
   public void visit(Expression n);

   /**
    * <PRE>
    * f0 -> "("
    * f1 -> ( FunctionCallExpressionListBody() )?
    * f2 -> ")"
    * </PRE>
    */
   public void visit(FunctionCallExpressionList n);

   /**
    * <PRE>
    * f0 -> AssignmentExpression()
    * f1 -> ( "," AssignmentExpression() )*
    * </PRE>
    */
   public void visit(FunctionCallExpressionListBody n);

   /**
    * <PRE>
    * f0 -> ConditionalExpression()
    * f1 -> ( ( "=" | "+=" | "-=" | "*=" | "/=" | "%=" | "&gt;&gt;=" | "&lt;&lt;=" | "&gt;&gt;&gt;=" | "&=" | "^=" | "|=" ) ConditionalExpression() )*
    * </PRE>
    */
   public void visit(AssignmentExpression n);

   /**
    * <PRE>
    * f0 -> LogicalOrExpression()
    * f1 -> ( "?" LogicalOrExpression() ":" LogicalOrExpression() )?
    * </PRE>
    */
   public void visit(ConditionalExpression n);

   /**
    * <PRE>
    * f0 -> LogicalAndExpression()
    * f1 -> ( "||" LogicalAndExpression() )*
    * </PRE>
    */
   public void visit(LogicalOrExpression n);

   /**
    * <PRE>
    * f0 -> BitwiseOrExpression()
    * f1 -> ( "&&" BitwiseOrExpression() )*
    * </PRE>
    */
   public void visit(LogicalAndExpression n);

   /**
    * <PRE>
    * f0 -> BitwiseXorExpression()
    * f1 -> ( "|" BitwiseXorExpression() )*
    * </PRE>
    */
   public void visit(BitwiseOrExpression n);

   /**
    * <PRE>
    * f0 -> BitwiseAndExpression()
    * f1 -> ( "^" BitwiseAndExpression() )*
    * </PRE>
    */
   public void visit(BitwiseXorExpression n);

   /**
    * <PRE>
    * f0 -> EqualityExpression()
    * f1 -> ( "&" EqualityExpression() )*
    * </PRE>
    */
   public void visit(BitwiseAndExpression n);

   /**
    * <PRE>
    * f0 -> RelationalExpression()
    * f1 -> ( ( "==" | "!=" ) RelationalExpression() )*
    * </PRE>
    */
   public void visit(EqualityExpression n);

   /**
    * <PRE>
    * f0 -> ShiftExpression()
    * f1 -> ( ( "&lt;" | "&gt;" | "&gt;=" | "&lt;=" | "instanceof" ) ShiftExpression() )*
    * </PRE>
    */
   public void visit(RelationalExpression n);

   /**
    * <PRE>
    * f0 -> AdditiveExpression()
    * f1 -> ( ( "&lt;&lt;" | "&gt;&gt;" | "&gt;&gt;&gt;" ) AdditiveExpression() )*
    * </PRE>
    */
   public void visit(ShiftExpression n);

   /**
    * <PRE>
    * f0 -> MultiplicativeExpression()
    * f1 -> ( ( "+" | "-" ) MultiplicativeExpression() )*
    * </PRE>
    */
   public void visit(AdditiveExpression n);

   /**
    * <PRE>
    * f0 -> UnaryExpression()
    * f1 -> ( ( "*" | "/" | "%" ) UnaryExpression() )*
    * </PRE>
    */
   public void visit(MultiplicativeExpression n);

   /**
    * <PRE>
    * f0 -> ( ( "++" | "--" | "+" | "-" | "~" | "!" ) )?
    * f1 -> PostfixExpression()
    * </PRE>
    */
   public void visit(UnaryExpression n);

   /**
    * <PRE>
    * f0 -> TypeExpression()
    * f1 -> ( "++" | "--" )?
    * </PRE>
    */
   public void visit(PostfixExpression n);

   /**
    * <PRE>
    * f0 -> AllocationExpression()
    *       | CastExpression()
    *       | PrimaryExpression()
    * </PRE>
    */
   public void visit(TypeExpression n);

   /**
    * <PRE>
    * f0 -> "new"
    * f1 -> PrimaryExpressionWithTrailingFxnCallExpList()
    * f2 -> FunctionCallExpressionList()
    * </PRE>
    */
   public void visit(AllocationExpression n);

   /**
    * <PRE>
    * f0 -> "("
    * f1 -> PrimaryExpressionNotFunction()
    * f2 -> ")"
    * f3 -> PrimaryExpression()
    * </PRE>
    */
   public void visit(CastExpression n);

   /**
    * <PRE>
    * f0 -> PrimaryPrefix()
    * f1 -> ( PrimaryPostfix() )*
    * </PRE>
    */
   public void visit(PrimaryExpression n);

   /**
    * <PRE>
    * f0 -> PrimaryPrefixNotFunction()
    * f1 -> ( PrimaryPostfix() )*
    * </PRE>
    */
   public void visit(PrimaryExpressionNotFunction n);

   /**
    * <PRE>
    * f0 -> PrimaryPrefix()
    * f1 -> ( PrimaryPostfixWithTrailingFxnCallExpList() )*
    * </PRE>
    */
   public void visit(PrimaryExpressionWithTrailingFxnCallExpList n);

   /**
    * <PRE>
    * f0 -> PrimaryPrefixNotFunction()
    *       | FunctionPrimaryPrefix()
    *       | ShorthandFunctionPrimaryPrefix()
    * </PRE>
    */
   public void visit(PrimaryPrefix n);

   /**
    * <PRE>
    * f0 -> ThisPrimaryPrefix()
    *       | SuperPrimaryPrefix()
    *       | CalleePrimaryPrefix()
    *       | ArrayDeclarationPrimaryPrefix()
    *       | IdentifierPrimaryPrefix()
    *       | ParenPrimaryPrefix()
    *       | Literal()
    * </PRE>
    */
   public void visit(PrimaryPrefixNotFunction n);

   /**
    * <PRE>
    * f0 -> "this"
    * </PRE>
    */
   public void visit(ThisPrimaryPrefix n);

   /**
    * <PRE>
    * f0 -> "super"
    * </PRE>
    */
   public void visit(SuperPrimaryPrefix n);

   /**
    * <PRE>

    * f0 -> "callee"
    * </PRE>
    */
   public void visit(CalleePrimaryPrefix n);

   /**
    * <PRE>
    * f0 -> &lt;IDENTIFIER&gt;
    * </PRE>
    */
   public void visit(IdentifierPrimaryPrefix n);

   /**
    * <PRE>
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    * </PRE>
    */
   public void visit(ParenPrimaryPrefix n);

   /**
    * <PRE>

    * f0 -> "function"
    * f1 -> "("
    * f2 -> ( Arglist() )?
    * f3 -> ")"
    * f4 -> ( "extends" PrimaryExpressionWithTrailingFxnCallExpList() FunctionCallExpressionList() )?
    * f5 -> "{"
    * f6 -> Program(true)
    * f7 -> "}"
    * </PRE>
    */
   public void visit(FunctionPrimaryPrefix n);

   /**
    * <PRE>

    * f0 -> "'{"
    * f1 -> Program(true)
    * f2 -> "}"
    * </PRE>
    */
   public void visit(ShorthandFunctionPrimaryPrefix n);

   /**
    * <PRE>
    * f0 -> "["
    * f1 -> ( FunctionCallExpressionListBody() )?
    * f2 -> "]"
    * </PRE>
    */
   public void visit(ArrayDeclarationPrimaryPrefix n);

   /**
    * <PRE>
    * f0 -> FunctionCallPrimaryPostfix()
    *       | ArraySubscriptPrimaryPostfix()
    *       | ThisScopeQualifierPrimaryPostfix()
    *       | PropertyIdentifierPrimaryPostfix()
    * </PRE>
    */
   public void visit(PrimaryPostfix n);

   /**
    * <PRE>
    * f0 -> ArraySubscriptPrimaryPostfix()
    *       | ThisScopeQualifierPrimaryPostfix()
    *       | PropertyIdentifierPrimaryPostfix()
    * </PRE>
    */
   public void visit(PrimaryPostfixWithTrailingFxnCallExpList n);

   /**
    * <PRE>
    * f0 -> FunctionCallExpressionList()
    * </PRE>
    */
   public void visit(FunctionCallPrimaryPostfix n);

   /**
    * <PRE>
    * f0 -> "["
    * f1 -> Expression()
    * f2 -> ( ".." Expression() )?
    * f3 -> "]"
    * </PRE>
    */
   public void visit(ArraySubscriptPrimaryPostfix n);

   /**
    * <PRE>
    * f0 -> "."
    * f1 -> &lt;IDENTIFIER&gt;
    * </PRE>
    */
   public void visit(PropertyIdentifierPrimaryPostfix n);

   /**
    * <PRE>
    * f0 -> "."
    * f1 -> "this"
    * </PRE>
    */
   public void visit(ThisScopeQualifierPrimaryPostfix n);

   /**
    * <PRE>
    * f0 -> &lt;INTEGER_LITERAL&gt;
    *       | &lt;FLOATING_POINT_LITERAL&gt;
    *       | &lt;STRING_LITERAL&gt;
    *       | &lt;REGEXP_LITERAL&gt;
    *       | "true"
    *       | "false"
    *       | "null"
    *       | "undefined"
    * </PRE>
    */
   public void visit(Literal n);

   /**
    * <PRE>

    * f0 -> ( "static" | "const" | "private" | "protected" | "public" )*
    * </PRE>
    */
   public void visit(Permissions n);

}

