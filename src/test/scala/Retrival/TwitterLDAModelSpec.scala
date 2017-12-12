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

}

