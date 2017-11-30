package Retrival
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.clustering.LDA

object LDAModelSample extends App{
  val sc = new SparkContext("local", "LDA")
  val data = sc.textFile("/Users/hukuan/Downloads/sample_lda_data.txt")
  val parsedData = data.map(s => Vectors.dense(s.trim.split(' ').map(_.toDouble)))
  val corpus = parsedData.zipWithIndex.map(_.swap).cache()

  val ldaModel = new LDA().setK(3).run(corpus)

  val topics = ldaModel.describeTopics(11)
  for(k <- Range(0,3)) {
    print("Topic " + k + ": ")
    for (w <- Range(0, 11)) {
      print("%d=%.5f ".format(topics(k)._1(w), topics(k)._2(w)))
    }
    println()
  }
}
