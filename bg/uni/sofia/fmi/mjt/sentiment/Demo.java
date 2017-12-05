package bg.uni.sofia.fmi.mjt.sentiment;

import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws IOException {
        MovieReviewSentimentAnalyzer analyzer = new MovieReviewSentimentAnalyzer("src/bg/uni/sofia/fmi/mjt/resources/movieReviews.txt",
                                                                                 "src/bg/uni/sofia/fmi/mjt/resources/stopwords.txt");
        System.out.println(analyzer.getMostNegativeWords(8900));
        System.out.println(analyzer.getMostFrequentWords(10));
        System.out.println(analyzer.getReviewSentiment("nice"));
        System.out.println(analyzer.getReviewSentimentAsName("nice"));
        System.out.println(analyzer.isStopWord("yoUrs"));

    }
}
