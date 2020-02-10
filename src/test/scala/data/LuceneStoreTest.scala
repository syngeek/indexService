package data

import java.nio.file.{Files, Path}

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec}
import org.scalatest.matchers.should.Matchers
import parser.{AndExp, OrExp, SearchTerm}

class LuceneStoreTest extends FlatSpec with Matchers with BeforeAndAfterAll  {
  val directory:Path = Files.createTempDirectory("temp")
  val docStore = new LuceneStore(Option(directory))
  val yumDoc1 = YumDocument("1", "soup tomato cream salt")
  val yumDoc2 = YumDocument("2", "cake sugar eggs flour sugar cocoa cream butter")
  val yumDoc3 = YumDocument("1", "bread butter salt")
  val yumDoc4 = YumDocument("3", "soup fish potato salt pepper" )


  "DocStore" should "store and index docs" in {
    docStore.index(yumDoc1)
    docStore.index(yumDoc2)
    docStore.index(yumDoc4)
    val results:List[YumDocument] = docStore.searchAll()
    assert(results.size == 3)
    assert(results.contains(yumDoc1))
    assert(results.contains(yumDoc2))
    assert(results.contains(yumDoc4))
  }
  it should "update an existing doc specified by docId by replacing content" in {
    docStore.index(yumDoc3)
    val results:List[YumDocument] = docStore.searchAll()
    assert(results.size == 3)
    assert(results.contains(yumDoc2))
    assert(results.contains(yumDoc3))
    assert(results.contains(yumDoc4))
    val query:String = SearchTerm("soup").toLuceneQueryString
    val searchResult: List[YumDocument] = docStore.search(query)
    assert(searchResult.size == 1)

  }
  it should "return result for yumDoc1 for query (butter & salt)" in {
    val query:String = AndExp(SearchTerm("butter"), SearchTerm("salt")).toLuceneQueryString
    val result: List[YumDocument] = docStore.search(query)
    assert(result.size == 1)
    assert(result.forall(searchResult => searchResult.docId == "1"))
  }
  it should "return results for yumDoc1 and yumDoc4 for query ((butter | potato) & salt)" in {
    val query:String = AndExp(OrExp(SearchTerm("butter"), SearchTerm("potato")), SearchTerm("salt")).toLuceneQueryString
    val result: List[YumDocument] = docStore.search(query)
    assert(result.size == 2)
    result.forall(sr => sr.docId == yumDoc1.docId.toString || sr.docId == yumDoc4.toString)
  }
  it should "return result for yumDoc2 for query sugar" in {
    val query:String = SearchTerm("sugar").toLuceneQueryString
    val result: List[YumDocument] = docStore.search(query)
    assert(result.size == 1)
    assert(result.forall(searchResult => searchResult.docId == "2"))
  }

}
