package bg.uni.sofia.fmi.mjt.sentiment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {


    private static final String SINGLE_LETTER_REGEX_V1   = "\\b[\\w']{1,2}\\b";
    private static final String SINGLE_LETTER_REGEX_V2   = "\\s{2,}";
    private static final String SPECIAL_CHARACTERS_REGEX = "\\.|,|-|`|'|;|\\?|!";
    private static final String DOUBLE_WHITE_SPACE_REGEX = "\\s+";
    private static final String EMPTY_WORD = "";
    private static final String WHITE_SPACE = " ";

    private Set<String> stopWordsSet;
    private Map<String, Map.Entry<Integer, Double>> reviewWords;

    public MovieReviewSentimentAnalyzer(String reviewsFileName, String stopWordsFileName) throws IOException {

        initStopWords(stopWordsFileName);
        initReviewsWords(reviewsFileName);
    }

    /**
     * Initializes stop words pool
     * and retrieves all the stop words from given file
     * @param stopWordsFileName
     * @throws IOException
     */
    private void initStopWords(String stopWordsFileName) throws IOException {

        this.stopWordsSet = new HashSet<>();
        readFile(stopWordsFileName, this::addStopWord);
    }

    /**
     * Initializes review words pool
     * and retrieves all the review words from given file
     * @param reviewsFileName is filename of file with train data
     * @throws IOException
     */
    private void initReviewsWords(String reviewsFileName) throws IOException {
        this.reviewWords = new HashMap<>();
        readFile(reviewsFileName, this::parseReviewWord);
        System.out.println(reviewWords);
    }

    /**
     * Adds a stop word to the pool of stop words
     * @param word is stop word
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
        filteredWords = filterString(filteredWords);
        Arrays.stream(filteredWords.split(" ")).forEach(word -> calculateSentimentalScore(word, rating));
    }

    /**
     * Calculates the sentimental score of given word
     * @param word
     * @param rating
     */
    private void calculateSentimentalScore(String word, int rating) {

        if (word.isEmpty()) {
            return;
        }
        if (reviewWords.containsKey(word)) {
            Map.Entry<Integer, Double> wordStatus = reviewWords.get(word);
            int occurrences = wordStatus.getKey();
            double newRating = (occurrences * wordStatus.getValue() + rating) / (occurrences + 1);
            reviewWords.put(word, Map.entry(occurrences + 1, newRating));
        } else {
            reviewWords.put(word, Map.entry(1, (double) rating));
        }
    }

    /**
     * This method removes all the stop words from given sequence of words
     * @param words is sequence of words
     * @return sequence of filtered words
     */
    private String removeStopWords(String words) {
        String result = words;
        for (String stopWord : stopWordsSet) {
            result = result.replace(stopWord, EMPTY_WORD);
        }
        return result;
    }

    /**
     * Use regexs in particular order to filter the entry data
     * @param word
     * @return
     */
    private String filterString(String word) {

        String result = word;
        result = result.replaceAll(SPECIAL_CHARACTERS_REGEX,EMPTY_WORD);
        result = result.replaceAll(DOUBLE_WHITE_SPACE_REGEX, WHITE_SPACE);
        result = result.replaceAll(SINGLE_LETTER_REGEX_V1, EMPTY_WORD);
        result = result.replaceAll(SINGLE_LETTER_REGEX_V2, WHITE_SPACE);
        return result.trim();
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
