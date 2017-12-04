package bg.uni.sofia.fmi.mjt.sentiment;

public enum Sentiment {
    NEGATIVE("negative"),
    SOMEWHAT_NEGATIVE("somewhat negative"),
    NEUTRAL("neutral"),
    SOMEWHAT_POSITIVE("somewhat positive"),
    POSITIVE("positive");

    private String meaning;
    Sentiment(String meaning) {
        this.meaning = meaning;
    }

    @Override
    public String toString() {
        return this.meaning;
    }
}
