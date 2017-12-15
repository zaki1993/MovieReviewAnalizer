package bg.uni.sofia.fmi.mjt;

import bg.uni.sofia.fmi.mjt.sentiment.MovieReviewSentimentAnalyzer;
import bg.uni.sofia.fmi.mjt.sentiment.Sentiment;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MovieReviewSentimentAnalyzerTest {

    private static MovieReviewSentimentAnalyzer analyzer;

    static {
        try {
            analyzer = new MovieReviewSentimentAnalyzer("src/bg/uni/sofia/fmi/mjt/resources/movieReviews.txt",
                                                        "src/bg/uni/sofia/fmi/mjt/resources/stopwords.txt");
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        }
    }

    @Test
    public void getsReviewOfEmptySequence() {
        assertEquals(-1.0, analyzer.getReviewSentiment(""), 0.0);
    }

    @Test
    public void getsReviewOfSequenceWithUnknownWords() {
        assertEquals(-1.0, analyzer.getReviewSentiment("zakiunknown"), 0.0);
    }

    @Test
    public void getsReviewOfSequenceWithOnlyKnownWords() {
        assertEquals(2.8, analyzer.getReviewSentiment("The film proves perfection"), 0.1);
    }

    @Test
    public void getsReviewOfSequenceWithKnownAndUnknownWords() {
        assertEquals(2.8, analyzer.getReviewSentiment("The film proves perfection zdrkp zaki is"), 0.1);
    }

    @Test
    public void getsReviewOfSequenceWithStopWords() {
        assertEquals(2.8, analyzer.getReviewSentiment("The is film yours a proves perfection"), 0.1);
    }

    @Test
    public void getsReviewOfSequenceWithMixedSymbols() {
        assertEquals(2.4, analyzer.getReviewSentiment("The ' a yours proves is .,"), 0.1);
    }

    @Test
    public void returnsTrueIfWordIsStopWord() {
        assertTrue(analyzer.isStopWord("is"));
        assertTrue(analyzer.isStopWord("your"));
    }

    @Test
    public void returnsFalseIfWordIsNotStopWord() {
        assertFalse(analyzer.isStopWord("'"));
        assertFalse(analyzer.isStopWord("Movie"));
    }

    @Test
    public void returnsCorrectSentimentResultIfWordIsNotFound() {
        assertEquals(-1.0, analyzer.getWordSentiment("notfound404"), 0.0);
    }

    @Test
    public void returnsWordSentimentLowerCase() {
        assertEquals(1.8, analyzer.getWordSentiment("movie"), 0.1);
    }

    @Test
    public void returnsWordSentimentUpperCase() {
        assertEquals(1.8, analyzer.getWordSentiment("MOVIE"), 0.1);
    }

    @Test
    public void returnsWordSentimentMixedCase() {
        assertEquals(1.8, analyzer.getWordSentiment("mOvIe"), 0.1);
    }

    @Test
    public void returnsCorrectSentimentNameIfWordIsUnknown() {
        assertEquals("unknown", analyzer.getReviewSentimentAsName("zaki"));
    }

    @Test
    public void returnsCorrectSentimentNameIfWordIsKnown() {
        assertEquals(Sentiment.NEGATIVE.toString(), analyzer.getReviewSentimentAsName("throes"));
        assertEquals(Sentiment.SOMEWHAT_NEGATIVE.toString(), analyzer.getReviewSentimentAsName("dreadful"));
        assertEquals(Sentiment.NEUTRAL.toString(), analyzer.getReviewSentimentAsName("pollution"));
        assertEquals(Sentiment.SOMEWHAT_POSITIVE.toString(), analyzer.getReviewSentimentAsName("chronicle"));
        assertEquals(Sentiment.POSITIVE.toString(), analyzer.getReviewSentimentAsName("spontaneous"));
    }

    @Test
    public void returnsCorrectDictionarySize() {
        // better test is to remove all the stop words and then distinct all the words and count their size
        assertEquals(15079, analyzer.getSentimentDictionarySize());
    }

    @Test
    public void returnsMostNegativeWords() {
        Set<String> frequentWords = Set.of("resume", "turd", "claptrap", "Whiffle", "pics");
        assertEquals(frequentWords, analyzer.getMostNegativeWords(5));
    }

    @Test
    public void returnsMostNegativeWordsAllWords() {
        assertEquals(14588, analyzer.getMostNegativeWords(14588).size());
    }

    @Test
    public void returnsMostFrequentWords() {
        Set<String> frequentWords = Set.of("s", "movie", "t", "film", "n");
        assertEquals(frequentWords, analyzer.getMostFrequentWords(5));
    }

    @Test
    public void returnsMostFrequentWordsAllWords() {
        assertEquals(14588, analyzer.getMostFrequentWords(14588).size());
    }

    @Test
    public void returnsMostPositiveWords() {
        Set<String> frequentWords = Set.of("Breillat", "rejects", "geeked", "unhinged", "rounded");
        assertEquals(frequentWords, analyzer.getMostPositiveWords(5));
    }

    @Test
    public void returnsMostPositiveWordsAllWords() {
        assertEquals(14588, analyzer.getMostPositiveWords(14588).size());
    }
}
