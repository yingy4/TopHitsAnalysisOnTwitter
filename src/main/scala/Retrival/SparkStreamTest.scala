import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreamTest extends App {

  val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")

  // 创建一个StreamingContext，每1秒钟处理一次计算程序
  val ssc = new StreamingContext(conf, Seconds(1))

  // 使用StreamingContext创建DStream，DStream表示TCP源中的流数据. lines这个DStream表示接收到的服务器数据，每一行都是文本
  val lines = ssc.socketTextStream("localhost", 9999)

  // 使用flatMap将每一行中的文本转换成每个单词，并产生一个新的DStream。
  val words = lines.flatMap(_.split(" "))

  // 使用map方法将每个单词转换成tuple
  val pairs = words.map(word => (word, 1))

  // 使用reduceByKey计算出每个单词的出现次数
  val wordCounts = pairs.reduceByKey(_ + _)

  wordCounts.print()

  ssc.start() // 开始计算
  ssc.awaitTermination() // 等待计算结束
}
