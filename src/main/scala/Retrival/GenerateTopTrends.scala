package Retrival

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import SentimentAnalysis.{AnalyzeSentiment, SentimentType}

object GenerateTopTrends extends App{

//  def main(args: Array[String]): Unit = {

 //   if(args.size < 0) println("Error for arguement") else {

      System.setProperty("twitter4j.oauth.consumerKey", TwitterClient.ConsumerKey)
      System.setProperty("twitter4j.oauth.consumerSecret", TwitterClient.ConsumerSecret)
      System.setProperty("twitter4j.oauth.accessToken", TwitterClient.AccessToken)
      System.setProperty("twitter4j.oauth.accessTokenSecret", TwitterClient.AccessSecret)

      Logger.getLogger("org").setLevel(Level.ERROR)

      val topicsLabel = Array("Movie", "Trump", "Football")
      val sc = new SparkContext("local[*]", "Topics")
      //  val ssc = new StreamingContext(sc, Seconds(5))

      val ssc = new StreamingContext(sc, Seconds(10))

//      val tweets = args.size match {
//        case 0 => TwitterUtils.createStream(ssc, None)
//        case _ => TwitterUtils.createStream(ssc, None, Seq(args(0)))
//      }
      val tweets = TwitterUtils.createStream(ssc, None)
      val texts = tweets.filter(status => status.getLang == "en").map(status => status.getText().toLowerCase()).cache()

      val lda_vol = TwitterLDAModel.generateLDAModel(sc)
      val ldaModel = lda_vol._1
      val volArray = lda_vol._2

      //val texts = sc.textFile("tweets.txt-1512614419000")
//      val sentiments = texts.map(text => (text, AnalyzeSentiment.calculateSentiment(text) match {
//        case SentimentType.Negative => 0
//        case SentimentType.Neutural => 1
//        case SentimentType.Positive => 2
//      }))
//
//      val topics = sentiments.map {
//        case (text, score) => (InferenceTopics.inferenceTopic(ldaModel, text.trim().split(" ").map(word => if (volArray.indexOf(word) > 31 || volArray.indexOf(word) < 0) 31 else volArray.indexOf(word)).mkString(" ")), score)
//      }
//        .map { case (array, score) => (array.indexOf(array.max), score) }.map { case (index, score) => (topicsLabel(index), (1, score)) }.reduceByKey { case ((count1, score1), (count2, score2)) => (count1 + count2, score1 + score2) }
//
//      val hashtags = sentiments.flatMap { case (text, score) => text.split(" ").map((_, score)) }.filter(_._1.startsWith("#")).map { case (hashtag, score) => (hashtag, (1, score)) }.reduceByKey { case ((count1, score1), (count2, score2)) => (count1 + count2, score1 + score2) }
//      val tops = topics.union(hashtags).transform(_.sortBy(_._2._1, false)).map { case (hashtag, (count, score)) => (hashtag, (score.toDouble / count.toDouble).formatted("%.4f")) }
//
//      val results = tops.print()
        val str = "Trump is a good president"
        val vector = str.trim().split(" ").map(word => if (volArray.indexOf(word) > 31 || volArray.indexOf(word) < 0) 31 else volArray.indexOf(word)).mkString(" ")
        val arr = InferenceTopics.inferenceTopic(ldaModel, vector)
        for(dou <- arr){
          println(dou)
        }
//      ssc.start()
//      ssc.awaitTermination()
    //}
  //}

}
