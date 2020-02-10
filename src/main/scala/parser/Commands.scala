package parser

import data.YumDocument

sealed trait Commands
case class IndexCmd(yumDocument: YumDocument) extends Commands
case class QueryCmd(exp: QueryExpression) extends Commands

