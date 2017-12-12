package Ingest

import org.scalatest.{FlatSpec, Matchers}

import scala.io.{Codec, Source}
import scala.util._
import Ingest._


class TweetSpec extends FlatSpec with Matchers{


  behavior of "Tweet convert for tweet.json"

  it should "match the size" in {
    val ingester = new Ingest[Tweet]()
    implicit val codec = Codec.UTF8
    val source = Source.fromFile("testdata/tweet.json")
    val ts = for (t <- ingester(source).toSeq) yield t
    ts.size shouldBe 1
    source.close()
  }

  it should "match pattern" in {
    val ingester = new Ingest[Tweet]()
    implicit val codec = Codec.UTF8
    val source = Source.fromFile("testdata/tweet.json")
    val ts = for (t <- ingester(source).toSeq) yield t
    ts should matchPattern { case Stream(Success(_)) => }
    source.close()
  }

  it should "match content" in {
    val ingester = new Ingest[Tweet]()
    implicit val codec = Codec.UTF8
    val source = Source.fromFile("testdata/tweet.json")
    val ts = for (t <- ingester(source).toSeq) yield t
    val tweet:Tweet = ts.headOption.getOrElse(fail()) match {
      case Success(x) => x
      case Failure(e) => throw new Exception("err:"+e)
    }
    tweet.lang shouldBe "en"
    tweet.text shouldBe "\"@SinAbunz_TM: @realDonaldTrump TRUMP VICTORY IN NOVEMBER! #MAGA #TrumpPence16\""
    source.close()
  }

}
