package Ingest

import spray.json.DefaultJsonProtocol
import scala.util._

class Tweet {

  case class Tweet(text: String, lang: String, created_at: String, retweet_count: Int, user: User, entities: Entities)

  case class User(id: Int, favourities_count: Int, location: String, name: String)

  case class Entities(hashTags: List[HashTag])

  case class HashTag(text: String)

  case class Response(statuses: List[Tweet], search_metadata: Metadata)

  case class Metadata(count: Int)

  object TweetProtocol extends DefaultJsonProtocol {
    implicit val formatUser = jsonFormat4(User.apply)
    implicit val formatHashTag = jsonFormat1(HashTag.apply)
    implicit val formatMetadata = jsonFormat1(Metadata.apply)
    implicit val formatEntities = jsonFormat1(Entities.apply)
    implicit val formatTweet = jsonFormat6(Tweet.apply)
    implicit val formatResponse = jsonFormat2(Response.apply)
  }
  
  object Response {
    import spray.json._

    trait IngestibleResponse extends Ingestible[Response] {
      override def fromString(w: String): Try[Response] = {
        println("w=" + w.parseJson.prettyPrint)
        import TweetProtocol._
        Try(w.parseJson.convertTo[Response])
      }
    }
    implicit object IngestibleResponse extends IngestibleResponse
  }

  object Tweet {
    import spray.json._

    trait IngestibleTweet extends Ingestible[Tweet] {
      override def fromString(w: String): Try[Tweet] = {
        println("w=" + w.parseJson.prettyPrint)
        import TweetProtocol._
        Try(w.parseJson.convertTo[Tweet])
      }
    }
    implicit object IngestibleTweet extends IngestibleTweet
  }

}
