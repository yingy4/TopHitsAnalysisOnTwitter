# Trending and Sentiment Analysis on Tweets

We implemented a scala project that can analyze trending topics and calculate sentiment scores with these topics on tweets that we acquired via Twitter Restful API.
Overview:
1. Collect the dataset, 100,000 tweets, via Twitter Search API
2. Train the LDA topic modeling with this dataset to get a set of pre-defined topics
3. Wrote program in Scala that it can concurrently acquire tweets that happened in the past 10 seconds
4. Analyze tweets using Spark techniques and generate essential topics and extract popular hashtags from tweets that are acquired
5. Calculate sentiment score for each major topic/hashtag, based on the attitude(positive, negative, neutral) of each tweet associated with the topic/hashtag
6. Output trending topics and popular hashtags with their sentiment scores



## Methodology
Search & Streaming API                   =>          Acquiring Tweets

JSON Format Transformation               =>          Parsing Twitter Sources

Tweets Text Filter                       =>          Filtering Language, Emoji, Stop Words

Topic Model Training                     =>          Training LDA Model    

Spark Streaming                          =>          Reading Tweets and ranking topics(Mapping & Reducing)

Stanford NLP                             =>          Calculating Sentiment


### USE CASE

Run "GenerateTopTrend.scala"

A user can input keywords or just enter the return button. 
If user input a keyword, such as sports, the program concurrently acquire real-time tweets that happened in the past 10 seconds and generate trending topics as well as popular hashtags
from these tweets. Then, it calculates a sentiment score for each topic/hashtag regarding the keyword, sports.
If the user inputs nothing or enters the return button, the topics/hashtags program generates/extracts are generic trending topics and popular hashtags that are not related to any specific keywords.

### Acceptance Criteria (The screenshot is included in the final ppt）

Acceptance Criteria 1: The LDA model should correctly generate topics from tweets. The expected accuracy rate should be above 60 percent.

Explanation: We manually input 20 tweets that are about Trump. Our LDA model can identify 16 of them are about Trump, which is 80% accuracy rate. Therefore, this Acceptance Criteria is met  

Acceptance Criteria 2: Test Ranking Feature

Explanation: We input a tweet set contains 75% tweets related to Trump and 25% tweets related to Movie. Our LDA model can calculate the number of tweets for each topic and rank them. The result shows Trump is the 1st and Movie is the 2nd. Therefore, the ranking feature works successfully and this acceptance criteria is met  

Acceptance Criteria 3: Sentiment analysis feature can correctly identify the attitude(Postive, Neutral, Negative) of the tweets

Explanation: we wrote thirty tweets in SentimentAnalysisSpec class and we provided correct attitude for each tweet to determine whether it passes the criteria or not.
The result is all 30 tests passed which is 100% accuracy. Thus, this acceptance criteria is met



