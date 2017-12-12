package Retrival
import java.io.{File, FileWriter}

import scala.math._
import org.apache.spark.mllib.clustering.{LDA, LDAModel}

import scala.io.Source


object InferenceTopics {
  def inferenceTopic(topicsMatrix: Array[Array[Double]], doc: String, maxIter: Int = 50): Array[Double] = {

    //val topicsMatrix = normalizedTopicsMatrix(ldaModel)
    //println(topicsMatrix.length + ": " + topicsMatrix(0).length)
    val topicnum = topicsMatrix.length
    def hasConverged(a: Array[Double], b: Array[Double]) = {
      (a zip b).map{ case (x, y) => abs(x-y)}.max < 1e-2
    }

    val tokens = doc.split(" ").filter(!_.isEmpty).map(_.toInt)
    val prior = Array.fill(topicnum)(1.0/topicnum)
    val posterior = new Array[Double](topicnum)
    val rangeT = 0 until topicnum
    var converged = false
    var n = 0
    while(!converged && n < maxIter){
      rangeT.foreach{t => posterior(t) = 0f}
      tokens.foreach{id =>
        val z = rangeT.map{t => prior(t) * topicsMatrix(t)(id)}
        val s = z.sum
        rangeT.foreach(t => posterior(t) += z(t)/s)
      }
      val s = posterior.sum
      rangeT.foreach(t => posterior(t) /= s)
      converged = hasConverged(prior, posterior)
      rangeT.foreach(t => prior(t) = posterior(t))
      n += 1
    }
    //println(s"iterated ${n} steps")
    posterior
  }


  def normalizedTopicsMatrix(ldaModel: LDAModel): Unit = {
    val topics = ldaModel.describeTopics(32)
    //println("Topics : "+topics(0)._1.length)
    val topicsMatrix = new Array[Array[Double]](ldaModel.k)
    //println(ldaModel.k)
    val rangeT = 0 until ldaModel.k
    rangeT.foreach{t => topicsMatrix(t) = (topics(t)._1 zip topics(t)._2).sortBy(_._1).map(_._2)}

    val pw = new FileWriter(new File("topicMatrix.txt"),true)
    for(topic <- topicsMatrix){
      for(word <- topic){
        pw.append(word.toString).write(" ")
      }
      pw.write("\n")
    }
    pw.flush()
    pw.close()
  }


  def loadTopicsMatrix(): Array[Array[Double]] = {
    val ldamodel = for(line <- Source.fromFile("topicMatrix.txt").getLines()) yield line.trim.split(" ").map(_.toDouble)
    ldamodel.toArray
  }

  def loadVocabulary(): Array[String] = {
    val vocabulary = for(line <- Source.fromFile("vocabulary.txt").getLines()) yield line.trim.split(" ")
    vocabulary.next()
  }

}
