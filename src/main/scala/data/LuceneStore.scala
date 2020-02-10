package data

import java.nio.file.{Path, Paths}
import com.outr.lucene4s.DirectLucene

class LuceneStore(directory: Option[Path] = None) {

  private val lucene = new DirectLucene(Nil, directory = directory)
  private val yumDocuments: SearchableYumDocument = lucene.create.searchable[SearchableYumDocument]

  def index(yumDocument: YumDocument): YumDocument = {
    yumDocuments.update(yumDocument).index()
    yumDocument
  }

  def searchAll(): List[YumDocument] = {
    val paged = yumDocuments.query().search()
    paged.entries.toList
  }

  def search(queryExpression: String): List[YumDocument] = {
    val results = lucene.query().filter(queryExpression).search().entries.toList
    results.map[YumDocument](kwResult => {
      YumDocument(
        kwResult.update.document.getField("docId").stringValue(),
        kwResult.update.document.getField("content").stringValue()
      )
    })
  }

}

