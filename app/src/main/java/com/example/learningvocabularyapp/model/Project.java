package com.example.learningvocabularyapp.model;

public class Project {
    private int id;
    private String projectName;
    private String learningLanguage;
    private String projectImage;
    private String correctImage;
    private String wrongImage;


    public Project(){}
    public Project(String projectName, int id, String learningLanguage, String correctImage, String wrongImage, String projectImage) {
        this.projectName = projectName;
        this.id = id;
        this.learningLanguage = learningLanguage;
        this.correctImage = correctImage;
        this.wrongImage = wrongImage;
        this.projectImage = projectImage;
    }
    public int getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getLearningLanguage() {
        return learningLanguage;
    }

    public String getProjectImage() {
        return projectImage;
    }

    public String getCorrectImage() {
        return correctImage;
    }

    public String getWrongImage() {
        return wrongImage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectImage(String projectImage) {
        this.projectImage = projectImage;
    }

    public void setLearningLanguage(String learningLanguage) {
        this.learningLanguage = learningLanguage;
    }

    public void setCorrectImage(String correctImage) {
        this.correctImage = correctImage;
    }

    public void setWrongImage(String wrongImage) {
        this.wrongImage = wrongImage;
    }

}
