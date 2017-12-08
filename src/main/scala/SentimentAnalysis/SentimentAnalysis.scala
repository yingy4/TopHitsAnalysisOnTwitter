package SentimentAnalysis

import java.util.Properties
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations

import SentimentAnalysis.SentimentType.SentimentType

//Define Sentiment Types As Enumeration
object SentimentType extends Enumeration{
  type SentimentType = Value
  val Negative,Neutural,Positive = Value
}

object AnalyzeSentiment{

  val props = new Properties()
  props.setProperty("annotators","tokenize,ssplit,parse,sentiment")
  val pipeline = new StanfordCoreNLP(props)

  def calculateSentiment(input:String):SentimentType =Option(input) match{
    case Some(input) if(input != null && input.length()>0) =>getSentimentType(input)
    case _ => throw new IllegalArgumentException("Input is Empty")
  }

  def getSentimentType(input: String):SentimentType = {
    var longest = 0
    var majorSentiment = 0
    val annotation = pipeline.process(input)
    val sentences = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])

    val it = sentences.iterator()
    while (it.hasNext) {
      val sentence = it.next()
      val tree = sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])
      val sentiment = RNNCoreAnnotations.getPredictedClass(tree)
      val parseInput = sentence.toString()

      if (parseInput.length() > longest) {
        majorSentiment = sentiment
        longest = parseInput.length()
      }
    }

    import SentimentType._
    if(majorSentiment < 2) Negative
    else if(majorSentiment == 2) Neutural
    else Positive
  }

}
