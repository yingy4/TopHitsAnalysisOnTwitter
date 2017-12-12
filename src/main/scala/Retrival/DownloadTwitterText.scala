package Retrival

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils

import scala.io.Source
import java.io.{BufferedWriter, File, FileWriter, PrintWriter}

import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer
import Ingest.{Ingest, Response}

import scala.io.{Codec, Source}

object DownloadTwitterText extends App{

  def getTweetsByStreamingAPI: Unit = {
    System.setProperty("twitter4j.oauth.consumerKey", TwitterClient.ConsumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", TwitterClient.ConsumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", TwitterClient.AccessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", TwitterClient.AccessSecret)

    Logger.getLogger("org").setLevel(Level.ERROR)
    //create a sparkContext
    val sc = new SparkContext("local[*]", "StreamAPITest")
    val ssc = new StreamingContext(sc, Seconds(1))

    val tweets = TwitterUtils.createStream(ssc, None)

    val statuses = tweets.filter(status => status.getLang == "en").map(status => status.getText().toLowerCase().replaceAll("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]|[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]|[.,!?@\n]"," "))


    statuses.print()
    statuses.saveAsTextFiles("tweets.txt")

    ssc.start()
    ssc.awaitTermination()

  }

  def getTweetsBySearchAPI: Unit = {
    val ingester = new Ingest[Response]()
    implicit val codec = Codec.UTF8
    val source  = Source.fromString(TwitterClient.getFromSearchAPIByKeyword("Movie", 90))
    val rts = for (t <- ingester(source).toSeq) yield t
    val rs = rts.flatMap(_.toOption)
    val tss = rs.map(r => r.statuses)
    val ts = tss.flatten

    val pw = new FileWriter(new File("text-tweets.txt"),true)
    for(tweet <- ts){
      pw.append(tweet.text).write("\n")
    }
    //pw.append(rs.size+"")
    print(ts.size)
    pw.flush()
    pw.close()
  }

  //getTweetsBySearchAPI
  getTweetsByStreamingAPI
}
