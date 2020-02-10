package data

import com.outr.lucene4s._
import com.outr.lucene4s.field.Field
import com.outr.lucene4s.mapper.Searchable
import com.outr.lucene4s.query.SearchTerm

trait SearchableYumDocument extends Searchable[YumDocument] {

  //This is supposed to ensure the same document is selected when updating.
  override def idSearchTerms(doc: YumDocument): List[SearchTerm] = List(exact(docId(doc.docId)))

  def docId:Field[String]
  def content:Field[String]
}

