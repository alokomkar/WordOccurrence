package com.alokomkar.wordoccurence;

/**
 * Created by Alok on 12/04/17.
 */

public class WordModel implements Comparable<WordModel> {

    private String word;
    private Integer wordCount;

    public WordModel(String word, Integer wordCount) {
        this.word = word;
        this.wordCount = wordCount;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }


    @Override
    public int compareTo(WordModel wordModel) {
        return this.wordCount.compareTo(wordModel.wordCount);
    }

    @Override
    public String toString() {
        return word + " : " + wordCount;
    }
}
