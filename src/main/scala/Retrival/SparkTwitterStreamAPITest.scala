package Retrival

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j._

object SparkTwitterStreamAPITest extends App{

  System.setProperty("twitter4j.oauth.consumerKey", TwitterClient.ConsumerKey)
  System.setProperty("twitter4j.oauth.consumerSecret", TwitterClient.ConsumerSecret)
  System.setProperty("twitter4j.oauth.accessToken", TwitterClient.AccessToken)
  System.setProperty("twitter4j.oauth.accessTokenSecret", TwitterClient.AccessSecret)

  Logger.getLogger("org").setLevel(Level.ERROR)

  //create a sparkContext
  val sc = new SparkContext("local[*]", "StreamAPITest")
  val ssc = new StreamingContext(sc, Seconds(1))

  val tweets = TwitterUtils.createStream(ssc, None)

  val statuses = tweets.map(status => status.getText())
  val hashtags = statuses.flatMap(status => status.split(" ")).filter(word => word.startsWith("#"))
  hashtags.print()

  ssc.start()
  ssc.awaitTermination()
}
