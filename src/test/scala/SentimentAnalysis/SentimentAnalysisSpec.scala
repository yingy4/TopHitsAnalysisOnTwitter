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

  }
}
