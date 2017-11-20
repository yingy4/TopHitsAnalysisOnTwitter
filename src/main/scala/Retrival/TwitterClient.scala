package Retrival

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.{HttpClientBuilder}
import com.github.nscala_time.time.Imports._
import com.github.nscala_time.time._


object TwitterClient {

  val ConsumerKey = "NDWKwDeqzpkWez69eNoVsggof"
  val ConsumerSecret = "IT0H5fZrKiiTGvgCupItDDzwDObRE4CYqzIcFE4V2moOn46jiI"
  val AccessToken = "926177222704197632-viF5HAgK4gP12nCzjKXuJH4QEuQL2vT"
  val AccessSecret = "GTFboOqloXbizK7IhSjlmWwXpoIBwbRZXHyjUtJ31aV5Q"

  def getFromSearchAPIByKeyword(keyword: String, count: Int = 90): String = {
    val today = DateTime.now
    val ss = for(i <- 1 to 7) yield getFromSearchAPIByKeywordForOneDay(today - i.days, keyword, count)
    ss.mkString("\n")
  }

  def getFromSearchAPIByKeywordForOneDay(date: DateTime, keyword: String, count: Int): String = {
    val consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret)
    consumer.setTokenWithSecret(AccessToken, AccessSecret)
    val url = "https://api.twitter.com/1.1/search/tweets.json?q=" + keyword + "&count=" + count + "&until=" + date.toString(StaticDateTimeFormat.forPattern("yyyy-MM-dd"))
    //print(url)
    val request = new HttpGet(url)
    consumer.sign(request)
    val client = HttpClientBuilder.create().build()
    val response = client.execute(request)
    IOUtils.toString(response.getEntity.getContent())
  }
}
