package bg.uni.sofia.fmi.mjt.sentiment;

import java.util.Collection;

public interface SentimentAnalyzer {
    /**
     * @param review
     *            the text of the review
     * @return the review sentiment as a floating-point number in the interval [0.0,
     *         4.0] if known, and -1.0 if unknown
     */
    double getReviewSentiment(String review);

    /**
     * @param review
     *            the text of the review
     * @return the review sentiment as a name: "negative", "somewhat negative",
     *         "neutral", "somewhat positive", "positive"
     */
    String getReviewSentimentAsName(String review);

    /**
     * @param word is a word
     * @return the review sentiment of the word as a floating-point number in the
     *         interval [0.0, 4.0] if known, and -1.0 if unknown
     */
    double getWordSentiment(String word);

    /**
     * Returns a collection of the n most frequent words found in the reviews
     */
    Collection<String> getMostFrequentWords(int n);

    /**
     * Returns a collection of the n most positive words in the reviews
     */
    Collection<String> getMostPositiveWords(int n);

    /**
     * Returns a collection of the n most negative words in the reviews
     */
    Collection<String> getMostNegativeWords(int n);

    /**
     * Returns the total number of words with known sentiment score
     */
    int getSentimentDictionarySize();

    /**
     * Returns whether a word is a stopword
     */
    boolean isStopWord(String word);
}

