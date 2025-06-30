package com.example.learningvocabularyapp.model;

public class Vocabulary {
    private int id;
    private String word;
    private String meaning;
    private int projectId;

    public Vocabulary(String english, String vietnamese) {
        this.word = english;
        this.meaning = vietnamese;
    }
    public Vocabulary(int id, String word, String meaning, int projectId) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
        this.projectId = projectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
