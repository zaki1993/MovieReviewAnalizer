package bg.uni.sofia.fmi.mjt.sentiment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {

    private static final String VALID_WORD_REGEX = "[a-zA-Z0-9]*";
    private Set<String> stopWordsSet;
    private CaseInSensitiveMap reviewWords;

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
        this.reviewWords = new CaseInSensitiveMap();
        readFile(reviewsFileName, this::parseReviewWord);
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
     * @param sequence
     */
    private void parseReviewWord(String sequence) {

        int rating = Integer.valueOf(sequence.substring(0, 1));

        Pattern pattern = Pattern.compile(VALID_WORD_REGEX);
        Matcher matcher = pattern.matcher(sequence.substring(1));
        while (matcher.find()) {
            String word = matcher.group();
            if (!word.trim().isEmpty() && !isStopWord(word)) {
                calculateSentimentalScore(word, rating);
            }
        }
    }

    /**
     * Calculates the sentimental score of given word
     * @param word
     * @param rating
     */
    private void calculateSentimentalScore(String word, int rating) {

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

        Pattern pattern = Pattern.compile(VALID_WORD_REGEX);
        Matcher matcher = pattern.matcher(review);
        int counter = 0;
        double result = 0.0;
        while (matcher.find()) {
            String word = matcher.group();
            if (!word.trim().isEmpty() && !isStopWord(word) && reviewWords.containsKey(word)) {
                counter++;
                result += reviewWords.get(word).getValue();
            }
        }
        return counter == 0 ? -1.0 : result / counter;
    }

    @Override
    public String getReviewSentimentAsName(String review) {

        double reviewSentiment = getReviewSentiment(review);
        String result;
        if (reviewSentiment >= 0.0 && reviewSentiment < 1.0) {
            result = Sentiment.NEGATIVE.toString();
        } else if (reviewSentiment >= 1.0 && reviewSentiment < 2.0) {
            result = Sentiment.SOMEWHAT_NEGATIVE.toString();
        } else if (reviewSentiment == 2.0) {
            result = Sentiment.NEUTRAL.toString();
        } else if (reviewSentiment > 2.0 && reviewSentiment <= 3.0) {
            result = Sentiment.SOMEWHAT_POSITIVE.toString();
        } else if (reviewSentiment > 3.0 && reviewSentiment <= 4.0){
            result = Sentiment.POSITIVE.toString();
        } else {
            result = "unknown";
        }
        return result;
    }

    /**
     * This method takes sequence and parses it like it is
     * It does not remove separators
     * @param word
     * @return sentimental score for word
     */
    @Override
    public double getWordSentiment(String word) {

        double result;
        if (reviewWords.containsKey(word)) {
            result = reviewWords.get(word).getValue();
        } else {
            result = -1.0;
        }
        return result;
    }

    /**
     * Sort words by occurrences
     * @param n
     * @return collection of the first N most occurred words
     */
    @Override
    public Collection<String> getMostFrequentWords(int n) {

        // provide argument type, because of the weakness in the compiler's type inferencing mechanism
        // when not using method reference
        return reviewWords.entrySet()
                          .stream()
                          .sorted(Comparator.comparingInt((Map.Entry<String, Map.Entry<Integer, Double>> reviewEntry) -> reviewEntry.getValue().getKey())
                          .reversed())
                          .limit(n)
                          .map(Map.Entry::getKey)
                          .collect(Collectors.toSet());
    }

    /**
     * Sort words by rating in decreasing order
     * @param n
     * @return collection of first N most rated words
     */
    @Override
    public Collection<String> getMostPositiveWords(int n) {

        // provide argument type, because of the weakness in the compiler's type inferencing mechanism
        // when not using method reference
        return reviewWords.entrySet()
                          .stream()
                          .sorted(Comparator.comparingDouble((Map.Entry<String, Map.Entry<Integer, Double>> reviewEntry) -> reviewEntry.getValue().getValue())
                          .reversed())
                          .limit(n)
                          .map(Map.Entry::getKey)
                          .collect(Collectors.toSet());
    }

    /**
     * Sort words by rating in increasing order
     * @param n
     * @return collection of first N most lowly rated words
     */
    @Override
    public Collection<String> getMostNegativeWords(int n) {

        return reviewWords.entrySet()
                          .stream()
                          .sorted(Comparator.comparingDouble(reviewEntry -> reviewEntry.getValue().getValue()))
                          .limit(n)
                          .map(Map.Entry::getKey)
                          .collect(Collectors.toSet());
    }

    /**
     * @return the size of the reviewed words
     */
    @Override
    public int getSentimentDictionarySize() {
        return this.reviewWords.size();
    }

    /**
     * @param word
     * @return true of word is stop word, false otherwise
     */
    @Override
    public boolean isStopWord(String word) {
        return findStopWord(word.trim());
    }

    /**
     * Compares with equalsIgnoreCase if the word is stop word
     * @param word
     * @return
     */
    private boolean findStopWord(String word) {
        return stopWordsSet.stream().anyMatch(word::equalsIgnoreCase);
    }
}
