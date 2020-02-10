package parser
import data.YumDocument
import errors.Errors
import org.scalatest._
import org.scalatest.matchers.should.Matchers

class CliParserTest extends FlatSpec with Matchers {

  "CliParser" should "parse 'index 1 soup tomato cream salt'" in {
    val p: Either[Errors, Commands] = CliParser.parseString("index 1 soup tomato cream salt")
    assertResult(Right(IndexCmd(YumDocument("1","soup tomato cream salt"))))(p)
  }
  it should "parse 'index 1243345 soup tomato cream salt 5spices'" in {
    val p: Either[Errors, Commands] = CliParser.parseString("index 1243345 soup tomato cream salt 5spices")
    assertResult(Right(IndexCmd(YumDocument("1243345", "soup tomato cream salt 5spices"))))(p)
  }
  it should "parse 'query salt'" in {
    assertResult(Right(QueryCmd(SearchTerm("salt")))) (CliParser.parseString("query salt"))
  }
  it should "parse 'query salt | pepper" in {
    val p: Either[Errors, Commands] = CliParser.parseString("query salt | pepper")
    assertResult(Right(QueryCmd(OrExp(SearchTerm("salt"), SearchTerm("pepper"))))) (p)
  }
  it should "parse 'query (a|b) & c" in {
    val p: Either[Errors, Commands] = CliParser.parseString("query (a|b) & c")
    assertResult(Right(QueryCmd(AndExp(OrExp(SearchTerm("a"), SearchTerm("b")), SearchTerm("c"))))) (p)
  }
  it should "parse 'query c & (a|b)' " in {
    val p: Either[Errors, Commands] = CliParser.parseString("query c & (a|b)")
    assertResult(Right(QueryCmd(AndExp(SearchTerm("c"), OrExp(SearchTerm("a"), SearchTerm("b")))))) (p)
  }
  it should "parse 'query d | (a|(b & c))' " in {
    val p: Either[Errors, Commands] = CliParser.parseString("query d | (a | (b & c))")
    assertResult(
      Right(
        QueryCmd(
          OrExp(
            SearchTerm("d"),
            OrExp(
              SearchTerm("a"),
              AndExp(SearchTerm("b"), SearchTerm("c"))))
        )
      )
    ) (p)
  }
  it should "return an error message parsing 'query a | b | c'" in {
    val p: Either[Errors, Commands] = CliParser.parseString("query a | b | c")
    println(p.left.get)
    assert(p.isLeft)
  }
  it should "return an error message parsing 'index salt'" in {
    val p: Either[Errors, Commands] = CliParser.parseString("index salt")
    println(p.left.get)
    assert(p.isLeft)

  }

}
