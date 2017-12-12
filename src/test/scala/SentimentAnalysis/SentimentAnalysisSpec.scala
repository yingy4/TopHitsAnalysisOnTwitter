package SentimentAnalysis

import org.scalatest.{FunSpec, Matchers}

class SentimentAnalysisSpec extends FunSpec with Matchers{

  describe("sentiment analysis") {
    it("should return POSITIVE when input has positive emotion") {
      val input = "Boston is a great city."
      val sentiment = SentimentAnalysis.AnalyzeSentiment.calculateSentiment(input)
      sentiment should be(SentimentType.Positive)
    }
    it("should return NEGATIVE when input has negative emotion") {
      val input = "This book is terrible"
      val sentiment = SentimentAnalysis.AnalyzeSentiment.calculateSentiment(input)
      sentiment should be(SentimentType.Negative)
    }
    it("should return NEUTRAL when input has netural emotion") {
      val input = "I went to New York City yesterday"
      val sentiment = SentimentAnalysis.AnalyzeSentiment.calculateSentiment(input)
      sentiment should be(SentimentType.Neutural)
    }

    it("should return POSITIVE when input has both postive and negative sentiment,but the major sentiment is Positive") {
      val input = "I love this movie it is a good movie and worth to spend time watching it. But the ending is boring."
      val sentiment = SentimentAnalysis.AnalyzeSentiment.calculateSentiment(input)
      sentiment should be(SentimentType.Positive)
    }

    it("should return NEGATIVE when input has both postive and negative sentiment,but the major sentiment is Negative") {
      val input = "The begining is good.I really don't like the story which is so terrible and it was a waste of time watching it."
      val sentiment = SentimentAnalysis.AnalyzeSentiment.calculateSentiment(input)
      sentiment should be(SentimentType.Negative)
    }

    it("should return NEUTRAL when this input has netural emotion") {
      val input = "Julian and I ate an lunch yesterday at state station. We both studied history in college."
      val sentiment = SentimentAnalysis.AnalyzeSentiment.calculateSentiment(input)
      sentiment should be(SentimentType.Neutural)
    }

    it("should return POSITIVE when this input has positive emotion") {
      val input = "This trip is wonderful."
      val sentiment = SentimentAnalysis.AnalyzeSentiment.calculateSentiment(input)
      sentiment should be(SentimentType.Positive)
    }

    it("should return NEGATIVE when this input has negative emotion") {
      val input = "I am very upset now for my flight got delayed again."
      val sentiment = SentimentAnalysis.AnalyzeSentiment.calculateSentiment(input)
      sentiment should be(SentimentType.Negative)
    }
    it("should return NEUTRAL when this input has a netural emotion") {
      val input = "I saw a cat today"
      val sentiment = SentimentAnalysis.AnalyzeSentiment.calculateSentiment(input)
      sentiment should be(SentimentType.Neutural)
    }

  }

}
