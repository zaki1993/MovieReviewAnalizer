package bg.uni.sofia.fmi.mjt.sentiment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {

    private static final String SPECIAL_CHARACTERS_REGEX = "\\.|,|-|`|'|;|\\?|!";
    private static final String DOUBLE_WHITE_SPACE_REGEX = "\\s+";
    private static final String EMPTY_WORD = "";
    private static final String WHITE_SPACE = " ";
    private static final Map<String, String> filters = Map.ofEntries(Map.entry(SPECIAL_CHARACTERS_REGEX, EMPTY_WORD),
                                                                     Map.entry(DOUBLE_WHITE_SPACE_REGEX, WHITE_SPACE));
    private Set<String> stopWordsSet;
    private Map<String, Map.Entry<Integer, Double>> reviewWords;

    public MovieReviewSentimentAnalyzer(String reviewsFileName, String stopWordsFileName) throws IOException {

        initStopWords(stopWordsFileName);
        initReviewsWords(reviewsFileName);
    }

    /**
     * Initializes stop words pool
     * and retrieves all the stop words from given file
     * @param stopwordsFileName
     * @throws IOException
     */
    private void initStopWords(String stopwordsFileName) throws IOException {

        this.stopWordsSet = new HashSet<>();
        readFile(stopwordsFileName, this::addStopWord);
    }

    /**
     * Initializes review words pool
     * and retireves all the review words from given file
     * @param reviewsFileName
     * @throws IOException
     */
    private void initReviewsWords(String reviewsFileName) throws IOException {
        this.reviewWords = new HashMap<>();
        readFile(reviewsFileName, this::parseReviewWord);
    }

    /**
     * Adds a stop word to the pool of stop words
     * @param word
     */
    private void addStopWord(String word) {
        this.stopWordsSet.add(word);
    }

    /**
     * Receives string with rating and comment
     * and depending if the words in the comment are good or bad
     * inserts them into the review words pool
     * @param sentance
     */
    private void parseReviewWord(String sentance) {

        int rating = Integer.valueOf(sentance.substring(0, 1));

        String filteredWords = removeStopWords(sentance.substring(1));
        filteredWords = filterString(filteredWords, filters);
        System.out.println(rating + " " + filteredWords);
    }

    private String removeStopWords(String words) {
        String result = words;
        for (String stopWord : stopWordsSet) {
            result = result.replace(stopWord, EMPTY_WORD);
        }
        return result;
    }

    private String filterString(String word, Map<String, String> filters) {
        String result = word;
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            result = filterString(result, filter.getKey(), filter.getValue());
        }
        result = result.replaceAll("\\b[\\w']{1,2}\\b", "");
        result = result.replaceAll("\\s{2,}", " ");
        return result.trim();
    }

    /**
     * Filter string with regex
     * @param word
     * @param regex
     * @param replacement
     * @return
     */
    private String filterString(String word, String regex, String replacement) {

        return word.replaceAll(regex, replacement);
    }

    /**
     * This method reads file by given name
     * and trims every line of the file and tokenize it by given method
     * @param fileName
     * @param methodHandler is used like tokenizer
     * @throws IOException
     */
    private void readFile(String fileName, Consumer<String> methodHandler) throws IOException {

        Stream<String> stopWordsStream = Files.lines(Paths.get(fileName));
        stopWordsStream.map(String::trim).forEach(methodHandler);
    }


    @Override
    public double getReviewSentiment(String review) {
        return 0;
    }

    @Override
    public String getReviewSentimentAsName(String review) {
        return null;
    }

    @Override
    public double getWordSentiment(String word) {
        return 0;
    }

    @Override
    public Collection<String> getMostFrequentWords(int n) {
        return null;
    }

    @Override
    public Collection<String> getMostPositiveWords(int n) {
        return null;
    }

    @Override
    public Collection<String> getMostNegativeWords(int n) {
        return null;
    }

    @Override
    public int getSentimentDictionarySize() {
        return 0;
    }

    @Override
    public boolean isStopWord(String word) {
        return false;
    }
}
