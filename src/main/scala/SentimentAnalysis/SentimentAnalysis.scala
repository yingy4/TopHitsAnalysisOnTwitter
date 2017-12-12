package SentimentAnalysis

import java.util.Properties
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations

import scala.collection.convert.wrapAll._
import SentimentAnalysis.SentimentType.SentimentType

object SentimentType extends Enumeration{
  type SentimentType = Value
  val Negative,Neutural,Positive = Value
}

object AnalyzeSentiment{

  val props = new Properties()
  props.setProperty("annotators","tokenize,ssplit,parse,sentiment")
  val pipeline = new StanfordCoreNLP(props)

  def calculateSentiment(input:String):SentimentType = Option(input) match{
    case Some(input) if(input != null && input.length()>0) =>getSentimentType(input)
    case _ => throw new IllegalArgumentException("Input is Empty")
  }



  def getSentimentType(input: String):SentimentType = {
    val annotation = pipeline.process(input)
    val sentences = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])

    val majorSentiment= sentences.map(x => (x.toString.length, RNNCoreAnnotations.getPredictedClass(x.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree]))))
        .sortBy(-_._1).head._2

    import SentimentType._
    if(majorSentiment < 2) Negative
    else if(majorSentiment == 2) Neutural
    else Positive
  }

}
