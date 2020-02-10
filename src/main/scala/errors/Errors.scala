package errors

sealed trait Errors {
  val msg:String
}
case class ParseError(msg:String, index:Int, command:String) extends Errors
case class QueryParseError(msg:String, index:Int, command:String) extends Errors
case class IndexParseError(msg:String, index:Int, command:String) extends Errors
