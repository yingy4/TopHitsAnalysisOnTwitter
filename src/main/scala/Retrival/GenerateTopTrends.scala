package Retrival

import java.io.{File, FileWriter}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import SentimentAnalysis.{AnalyzeSentiment, SentimentType}

object GenerateTopTrends{

      def main(args: Array[String]): Unit = {

            System.setProperty("twitter4j.oauth.consumerKey", TwitterClient.ConsumerKey)
            System.setProperty("twitter4j.oauth.consumerSecret", TwitterClient.ConsumerSecret)
            System.setProperty("twitter4j.oauth.accessToken", TwitterClient.AccessToken)
            System.setProperty("twitter4j.oauth.accessTokenSecret", TwitterClient.AccessSecret)

            Logger.getLogger("org").setLevel(Level.ERROR)

            val topicsLabel = Array(" ", "Football", "Movie", "Trump")
            val sc = new SparkContext("local[*]", "Topics")
            //  val ssc = new StreamingContext(sc, Seconds(5))

            val ssc = new StreamingContext(sc, Seconds(10))

            val keywords = args.mkString(" ")
            val tweets = keywords.size match {
                  case 0 => TwitterUtils.createStream(ssc, None, Seq("Trump", "Movie", "Football"))
                  case _ => TwitterUtils.createStream(ssc, None, Seq("Trump", "Movie", "Football")++args.toSeq)
            }
            //val tweets = TwitterUtils.createStream(ssc, None, Seq("Trump", "Movie", "Football"))
            val texts = tweets.filter(status => status.getLang == "en").map(status => status.getText().toLowerCase()).cache()

            //      val lda_vol = TwitterLDAModel.generateLDAModel(sc)
            //      InferenceTopics.normalizedTopicsMatrix(lda_vol._1)
            val topicsMatrix = InferenceTopics.loadTopicsMatrix()
            val volArray = InferenceTopics.loadVocabulary()

            /* write vovabulary into file
            val vol = volArray.filter(!_.startsWith("https")).filter(!TwitterClient.stopWords.contains(_)).filter(_.matches("[A-Za-z]+"))
            val pw = new FileWriter(new File("vocabulary_test.txt"),true)
            for(word <- lda_vol._2){
                  pw.append(word).write(" ")
            }
            pw.flush()
            pw.close()
            println(lda_vol._2.size)
            */
            val sentiments = texts.map(text => (text, AnalyzeSentiment.calculateSentiment(text) match {
                  case SentimentType.Negative => 0
                  case SentimentType.Neutural => 1
                  case SentimentType.Positive => 2
            }))

            val topics = sentiments.map {
                  case (text, score) => (InferenceTopics.inferenceTopic(topicsMatrix, text.trim().split(" ").map(word => volArray.indexOf(word)).filter(index => index < 5 && index >= 0).mkString(" ")), score)
            }
              .map { case (array, score) => {
                    //println(array(0) + " : " + array(1) + " : " + array(2));
                    (array.indexOf(array.max), score)
              }
              }.map { case (index, score) => (topicsLabel(index + 1), (1, score)) }.reduceByKey { case ((count1, score1), (count2, score2)) => (count1 + count2, score1 + score2) }
              .filter(x => !x._1.equals(" "))

            val hashtags = sentiments.flatMap { case (text, score) => text.split(" ").map((_, score)) }.filter(_._1.startsWith("#")).map { case (hashtag, score) => (hashtag, (1, score)) }.reduceByKey { case ((count1, score1), (count2, score2)) => (count1 + count2, score1 + score2) }
            val tops = topics.union(hashtags).transform(_.sortBy(_._2._1, false)).map { case (hashtag, (count, score)) => (hashtag, (score.toDouble / count.toDouble).formatted("%.4f")) }

            val results = tops.print()
            ssc.start()
            ssc.awaitTermination()
            /* test
              val str = "Trump"
              val vector = str.trim().split(" ").map(word => volArray.indexOf(word)).filter(index => index <5 && index >=0).mkString(" ")
              val arr = InferenceTopics.inferenceTopic(topicsMatrix, vector)
              println(arr.indexOf(arr.max))
              for(dou <- arr){
                println(dou)
              }
            */
      }
}package Retrival

import java.io.{File, FileWriter}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import SentimentAnalysis.{AnalyzeSentiment, SentimentType}

object GenerateTopTrends{

      def main(args: Array[String]): Unit = {

            System.setProperty("twitter4j.oauth.consumerKey", TwitterClient.ConsumerKey)
            System.setProperty("twitter4j.oauth.consumerSecret", TwitterClient.ConsumerSecret)
            System.setProperty("twitter4j.oauth.accessToken", TwitterClient.AccessToken)
            System.setProperty("twitter4j.oauth.accessTokenSecret", TwitterClient.AccessSecret)

            Logger.getLogger("org").setLevel(Level.ERROR)

            val topicsLabel = Array(" ", "Football", "Movie", "Trump")
            val sc = new SparkContext("local[*]", "Topics")
            //  val ssc = new StreamingContext(sc, Seconds(5))

            val ssc = new StreamingContext(sc, Seconds(10))

            val keywords = args.mkString(" ")
            val tweets = keywords.size match {
                  case 0 => TwitterUtils.createStream(ssc, None, Seq("Trump", "Movie", "Football"))
                  case _ => TwitterUtils.createStream(ssc, None, Seq("Trump", "Movie", "Football")++args.toSeq)
            }
            //val tweets = TwitterUtils.createStream(ssc, None, Seq("Trump", "Movie", "Football"))
            val texts = tweets.filter(status => status.getLang == "en").map(status => status.getText().toLowerCase()).cache()

            //      val lda_vol = TwitterLDAModel.generateLDAModel(sc)
            //      InferenceTopics.normalizedTopicsMatrix(lda_vol._1)
            val topicsMatrix = InferenceTopics.loadTopicsMatrix()
            val volArray = InferenceTopics.loadVocabulary()

            /* write vovabulary into file
            val vol = volArray.filter(!_.startsWith("https")).filter(!TwitterClient.stopWords.contains(_)).filter(_.matches("[A-Za-z]+"))
            val pw = new FileWriter(new File("vocabulary_test.txt"),true)
            for(word <- lda_vol._2){
                  pw.append(word).write(" ")
            }
            pw.flush()
            pw.close()
            println(lda_vol._2.size)
            */
            val sentiments = texts.map(text => (text, AnalyzeSentiment.calculateSentiment(text) match {
                  case SentimentType.Negative => 0
                  case SentimentType.Neutural => 1
                  case SentimentType.Positive => 2
            }))

            val topics = sentiments.map {
                  case (text, score) => (InferenceTopics.inferenceTopic(topicsMatrix, text.trim().split(" ").map(word => volArray.indexOf(word)).filter(index => index < 5 && index >= 0).mkString(" ")), score)
            }
              .map { case (array, score) => {
                    //println(array(0) + " : " + array(1) + " : " + array(2));
                    (array.indexOf(array.max), score)
              }
              }.map { case (index, score) => (topicsLabel(index + 1), (1, score)) }.reduceByKey { case ((count1, score1), (count2, score2)) => (count1 + count2, score1 + score2) }
              .filter(x => !x._1.equals(" "))

            val hashtags = sentiments.flatMap { case (text, score) => text.split(" ").map((_, score)) }.filter(_._1.startsWith("#")).map { case (hashtag, score) => (hashtag, (1, score)) }.reduceByKey { case ((count1, score1), (count2, score2)) => (count1 + count2, score1 + score2) }
            val tops = topics.union(hashtags).transform(_.sortBy(_._2._1, false)).map { case (hashtag, (count, score)) => (hashtag, (score.toDouble / count.toDouble).formatted("%.4f")) }

            val results = tops.print()
            ssc.start()
            ssc.awaitTermination()
            /* test
              val str = "Trump"
              val vector = str.trim().split(" ").map(word => volArray.indexOf(word)).filter(index => index <5 && index >=0).mkString(" ")
              val arr = InferenceTopics.inferenceTopic(topicsMatrix, vector)
              println(arr.indexOf(arr.max))
              for(dou <- arr){
                println(dou)
              }
            */
      }
}package Retrival

import java.io.{File, FileWriter}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import SentimentAnalysis.{AnalyzeSentiment, SentimentType}

object GenerateTopTrends{

      def main(args: Array[String]): Unit = {

            System.setProperty("twitter4j.oauth.consumerKey", TwitterClient.ConsumerKey)
            System.setProperty("twitter4j.oauth.consumerSecret", TwitterClient.ConsumerSecret)
            System.setProperty("twitter4j.oauth.accessToken", TwitterClient.AccessToken)
            System.setProperty("twitter4j.oauth.accessTokenSecret", TwitterClient.AccessSecret)

            Logger.getLogger("org").setLevel(Level.ERROR)

            val topicsLabel = Array(" ", "Football", "Movie", "Trump")
            val sc = new SparkContext("local[*]", "Topics")
            //  val ssc = new StreamingContext(sc, Seconds(5))

            val ssc = new StreamingContext(sc, Seconds(10))

            val keywords = args.mkString(" ")
            val tweets = keywords.size match {
                  case 0 => TwitterUtils.createStream(ssc, None, Seq("Trump", "Movie", "Football"))
                  case _ => TwitterUtils.createStream(ssc, None, Seq("Trump", "Movie", "Football")++args.toSeq)
            }
            //val tweets = TwitterUtils.createStream(ssc, None, Seq("Trump", "Movie", "Football"))
            val texts = tweets.filter(status => status.getLang == "en").map(status => status.getText().toLowerCase()).cache()

            //      val lda_vol = TwitterLDAModel.generateLDAModel(sc)
            //      InferenceTopics.normalizedTopicsMatrix(lda_vol._1)
            val topicsMatrix = InferenceTopics.loadTopicsMatrix()
            val volArray = InferenceTopics.loadVocabulary()

            /* write vovabulary into file
            val vol = volArray.filter(!_.startsWith("https")).filter(!TwitterClient.stopWords.contains(_)).filter(_.matches("[A-Za-z]+"))
            val pw = new FileWriter(new File("vocabulary_test.txt"),true)
            for(word <- lda_vol._2){
                  pw.append(word).write(" ")
            }
            pw.flush()
            pw.close()
            println(lda_vol._2.size)
            */
            val sentiments = texts.map(text => (text, AnalyzeSentiment.calculateSentiment(text) match {
                  case SentimentType.Negative => 0
                  case SentimentType.Neutural => 1
                  case SentimentType.Positive => 2
            }))

            val topics = sentiments.map {
                  case (text, score) => (InferenceTopics.inferenceTopic(topicsMatrix, text.trim().split(" ").map(word => volArray.indexOf(word)).filter(index => index < 5 && index >= 0).mkString(" ")), score)
            }
              .map { case (array, score) => {
                    //println(array(0) + " : " + array(1) + " : " + array(2));
                    (array.indexOf(array.max), score)
              }
              }.map { case (index, score) => (topicsLabel(index + 1), (1, score)) }.reduceByKey { case ((count1, score1), (count2, score2)) => (count1 + count2, score1 + score2) }
              .filter(x => !x._1.equals(" "))

            val hashtags = sentiments.flatMap { case (text, score) => text.split(" ").map((_, score)) }.filter(_._1.startsWith("#")).map { case (hashtag, score) => (hashtag, (1, score)) }.reduceByKey { case ((count1, score1), (count2, score2)) => (count1 + count2, score1 + score2) }
            val tops = topics.union(hashtags).transform(_.sortBy(_._2._1, false)).map { case (hashtag, (count, score)) => (hashtag, (score.toDouble / count.toDouble).formatted("%.4f")) }

            val results = tops.print()
            ssc.start()
            ssc.awaitTermination()
            /* test
              val str = "Trump"
              val vector = str.trim().split(" ").map(word => volArray.indexOf(word)).filter(index => index <5 && index >=0).mkString(" ")
              val arr = InferenceTopics.inferenceTopic(topicsMatrix, vector)
              println(arr.indexOf(arr.max))
              for(dou <- arr){
                println(dou)
              }
            */
      }
}
