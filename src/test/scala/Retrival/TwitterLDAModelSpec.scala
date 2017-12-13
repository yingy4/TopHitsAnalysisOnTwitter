package Rtrivial

import Retrival.{InferenceTopics, TwitterLDAModel}
import org.scalatest.{FlatSpec, Matchers}

import scala.io.{Codec, Source}
import scala.util._
import Retrival.TwitterLDAModel._
import Retrival.InferenceTopics._
import org.apache.spark.SparkContext

class TwitterLDAModelSpec extends FlatSpec with Matchers{

  behavior of "create lda model"

  it should "match the size" in {
    val vocabulary = InferenceTopics.loadVocabulary()
    val topicsModel = InferenceTopics.loadTopicsMatrix()
    vocabulary.length shouldBe 4848
    topicsModel.length shouldBe 3
  }

  it should "classify tweets into Trump" in {
    val vocabulary = InferenceTopics.loadVocabulary()
    val topicsMatrix = InferenceTopics.loadTopicsMatrix()
    val str = "Trump is a good president"
    val vector = str.trim().split(" ").map(word => vocabulary.indexOf(word)).filter(index => index <5 && index >=0).mkString(" ")
    val arr = InferenceTopics.inferenceTopic(topicsMatrix, vector)
    arr.indexOf(arr.max) shouldBe 2
  }

  it should "classify tweets into Movie" in {
    val vocabulary = InferenceTopics.loadVocabulary()
    val topicsMatrix = InferenceTopics.loadTopicsMatrix()
    val str = "Spider man is a good movie"
    val vector = str.trim().split(" ").map(word => vocabulary.indexOf(word)).filter(index => index <5 && index >=0).mkString(" ")
    val arr = InferenceTopics.inferenceTopic(topicsMatrix, vector)
    arr.indexOf(arr.max) shouldBe 1
  }

  it should "classify tweets into Football" in {
    val vocabulary = InferenceTopics.loadVocabulary()
    val topicsMatrix = InferenceTopics.loadTopicsMatrix()
    val str = "I love playing football"
    val vector = str.trim().split(" ").map(word => vocabulary.indexOf(word)).filter(index => index <5 && index >=0).mkString(" ")
    val arr = InferenceTopics.inferenceTopic(topicsMatrix, vector)
    arr.indexOf(arr.max) shouldBe 0
  }

  behavior of "test for topic model accurancy"

  it should "accurancy better than 60% for 20 tweets" in {
    val vocabulary = InferenceTopics.loadVocabulary()
    val topicsMatrix = InferenceTopics.loadTopicsMatrix()
    val source = Source.fromFile("testdata/topicTest.txt")
    var count = 0
    for (tweet <- source.getLines()){
      val vector = tweet.trim().split(" ").map(word => vocabulary.indexOf(word)).filter(index => index <5 && index >=0).mkString(" ")
      val arr = InferenceTopics.inferenceTopic(topicsMatrix, vector)
      if (arr.indexOf(arr.max) == 2 ) {
        count = count + 1
      }
    }

    (count.toDouble / 20 >= 0.6 ) shouldBe true
    println("topic model accurancy is "+count.toDouble / 20 + " which is better than 0.6")
  }

  behavior of "test for topic ranking"

  it should "accurancy better than 60% for 28 tweets" in {
    val vocabulary = InferenceTopics.loadVocabulary()
    val topicsMatrix = InferenceTopics.loadTopicsMatrix()
    val sc = new SparkContext("local[*]", "Topics")
    val data = sc.textFile("testdata/rankTest.txt")

    val rankedTopics = data.map(tweet => InferenceTopics.inferenceTopic(topicsMatrix, tweet.trim().split(" ").map(word => vocabulary.indexOf(word)).filter(index => index <5 && index >=0).mkString(" ")))
        .map(arr => arr.indexOf(arr.max)).filter(_ >= 0).map((_, 1)).reduceByKey(_+_).sortBy(_._2, false).map(_._1).collect()

    rankedTopics(0) shouldBe 2
    rankedTopics(1) shouldBe 1
    println("For tweets which contain 75% about Trump and 25% about Movie, Trump's trending is higher than Movie")
  }


}
