package com.jerryhumor.classifier;

import java.util.ArrayList;
import java.util.List;

public class Article {

    private List<String> wordList;
    private int flag;

    public List<String> getWordList() {
        return wordList;
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    public void setWordList(String[] wordList){
        this.wordList = new ArrayList<String>();
        for (String word : wordList){
            this.wordList.add(word);
        }
    }

    public int getFlag() {
        return flag;
    }

    public Article setFlag(int flag) {
        this.flag = flag;
        return this;
    }
}
