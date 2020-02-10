package parser

import data.YumDocument
import errors.{Errors, IndexParseError, ParseError, QueryParseError}

import scala.util.parsing.combinator.RegexParsers

object CliParser extends RegexParsers {

  def parseString(string: String): Either[Errors, Commands] = {
    parseAll(command, string) match {
      case Success(result, _) => Right(result)
      case NoSuccess(msg, next) => (msg, next) match {
        case i if next.source.toString.startsWith("index") => Left(IndexParseError(msg, next.pos.column, next.source.toString))
        case q if next.source.toString.startsWith("query") => Left(QueryParseError(msg, next.pos.column, next.source.toString))
        case _ => Left(ParseError(msg, next.pos.column, next.source.toString))
      }
    }
  }

  def docId: Parser[String] =  """([0-9]+)""".r ^^ { _.toString }
  def content:Parser[String] = """.+$""".r ^^ { _.toString}

  def searchTerm: Parser[SearchTerm] =  """\w+""".r ^^ { term => SearchTerm(term) }
  def operand:Parser[QueryExpression] = searchTerm |  "(" ~> nestedExp <~ ")"

  def nestedExp:Parser[QueryExpression] = operand ~ """[&|\|]""".r ~ operand ^^ {
    case op1 ~ "&" ~ op2 => AndExp(op1, op2)
    case op1 ~ "|" ~ op2 => OrExp(op1, op2)
  }

  def queryExpression:Parser[QueryExpression] = nestedExp | searchTerm

  def indexCommand:Parser[IndexCmd] = "index" ~> docId ~ content ^^ { case d ~ c => IndexCmd(YumDocument(d, c)) }

  def queryCommand:Parser[QueryCmd] = "query" ~> queryExpression ^^ { q => QueryCmd(q) }

  def command:Parser[Commands] = indexCommand | queryCommand
}
