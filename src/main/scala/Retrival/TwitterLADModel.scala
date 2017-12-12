package Retrival

import Retrival.InferenceTopics
import org.apache.spark.SparkContext
import org.apache.spark.ml.feature.{CountVectorizer, Word2Vec}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.clustering.{LDA, LDAModel}

import scala.math.abs

object TwitterLDAModel {

  def generateLDAModel(sc: SparkContext): Tuple2[LDAModel, Array[String]]= {
//    val sc = new SparkContext("local[*]", "LDA")
    val data = sc.textFile("text-tweets.txt")
    //CountVectorizer cv = new CountVectorizer().setInputCol("")
    val volArrayOriginal = data.flatMap(_.trim().split(" ")).map((_, 1)).reduceByKey(_ + _).map { case (topic, count) => (count, topic) }.sortByKey(false).map(_._2).collect()
    val volArray = volArrayOriginal.filter(!_.startsWith("https")).filter(word => !TwitterClient.stopWords.contains(word.toLowerCase)).filter(_.toLowerCase().matches("[A-Za-z]+"))
    //val vectors = data.map(s => Vectors.dense(s.trim().split(" ").map(word => if (volArray.indexOf(word).toDouble > 31 || volArray.indexOf(word).toDouble < 0) 31 else volArray.indexOf(word).toDouble)))
    val vectors = data.map(s => Vectors.dense(s.trim().split(" ").map(word => volArray.indexOf(word).toDouble).filter(index => index <32 && index >=0)))
    //println("Size : "+vectors.collect().size)
    //parseData.collect()
    var index = 0
    for (word <- volArray) {
      print(word + ": " + index)
      index = index + 1
      println()
    }

    val corpus = vectors.zipWithIndex.map(_.swap).cache()

    val ldaModel = new LDA().setK(3).run(corpus)
    Tuple2(ldaModel,volArray)
  }

}
