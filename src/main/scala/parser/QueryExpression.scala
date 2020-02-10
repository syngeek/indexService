package parser

sealed trait QueryExpression {
  def toLuceneQueryString: String
}
case class SearchTerm(term:String) extends QueryExpression {
  override def toLuceneQueryString: String = term
}
case class AndExp(op1:QueryExpression, op2:QueryExpression) extends QueryExpression {
  override def toLuceneQueryString: String = s"(${op1.toLuceneQueryString} AND ${op2.toLuceneQueryString})"
}
case class OrExp(op1:QueryExpression, op2:QueryExpression) extends QueryExpression {
  override def toLuceneQueryString: String = s"(${op1.toLuceneQueryString} OR ${op2.toLuceneQueryString})"
}
case class Exp(op:QueryExpression) extends QueryExpression {
  override def toLuceneQueryString: String = op.toLuceneQueryString
}
