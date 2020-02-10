import java.nio.file.Files

import data.LuceneStore
import errors.{Errors, IndexParseError, ParseError, QueryParseError}
import parser.{CliParser, Commands, IndexCmd, QueryCmd}

class CliController {

  val dataStore = new LuceneStore(Option(Files.createTempDirectory("tempIndex")))

  def processInput(input:String): String = {
    val cmd: Either[Errors, Commands] = CliParser.parseString(input)
    cmd match {
      case Left(e) => e match {
        case ParseError(msg, index, command) => s"unknown command error ${msg} from command ${command}"
        case QueryParseError(msg, index, command) => s"query error ${msg} at position ${index}"
        case IndexParseError(msg, index, command) =>s"index error ${msg} at position ${index}"
      }
      case Right(c) => c match {
        case IndexCmd(yumDocument) => {
          val result = dataStore.index(yumDocument)
          s"index ok ${result.docId}"
        }
        case QueryCmd(exp) => {
          val results = dataStore.search(exp.toLuceneQueryString)
          s"query results ${results.foldLeft(" ")((a, b) => a + b.docId + " ")}"
        }
      }
    }

  }

}
