import Retrival.InferenceTopics
import org.apache.spark.SparkContext
import org.apache.spark.ml.feature.{CountVectorizer, Word2Vec}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.clustering.LDA

import scala.math.abs

object TwitterLADModel extends App{
  val sc = new SparkContext("local[*]", "LDA")
  val data = sc.textFile("text-tweets.txt")
  //CountVectorizer cv = new CountVectorizer().setInputCol("")
  val volArray = data.flatMap(_.trim().split(" ")).map((_, 1)).reduceByKey(_+_).map{case (topic, count) => (count, topic)}.sortByKey(false).map(_._2).collect()

  val vectors = data.map(s => Vectors.dense(s.trim().split(" ").map(word => volArray.indexOf(word).toDouble)))
  //println("Size : "+vectors.collect().size)
  //parseData.collect()
  var index = 0
  for(word <- volArray){
    print(word + ": " + index)
    index = index + 1
    println()
  }

  val corpus = vectors.zipWithIndex.map(_.swap).cache()

  val ldaModel = new LDA().setK(2).run(corpus)

  val topics = ldaModel.describeTopics(20)
  for(k <- Range(0,2)) {
    print("Topic " + k + ": ")
    for (w <- Range(0, 20)) {
      print("%d=%.5f ".format(topics(k)._1(w), topics(k)._2(w)))
    }
    println()
  }


  val str = "football"
  val vector = str.trim().split(" ").map(word => volArray.indexOf(word)).mkString(" ")
  println(vector)
  val results = InferenceTopics.inferenceTopic(ldaModel, vector)
  for(result <- results) {
    println(result+" ")
  }
//
}
