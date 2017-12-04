package bg.uni.sofia.fmi.mjt.sentiment.comparator;

import java.util.Comparator;

public class MostFrequentWordsComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
}
