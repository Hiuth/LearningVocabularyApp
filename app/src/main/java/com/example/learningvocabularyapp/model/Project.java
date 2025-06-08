package com.example.learningvocabularyapp.model;

public class Project {
    private int id;
    private String projectName;
    private String learningLanguage;
    private byte [] projectImage;
    private byte[] correctImage;
    private byte[] wrongImage;


    public Project(){}
    public Project(String projectName, String learningLanguage, byte[] correctImage, byte[] wrongImage, byte[] projectImage) {
        this.projectName = projectName;
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

    public byte[] getProjectImage() {
        return projectImage;
    }

    public byte[] getCorrectImage() {
        return correctImage;
    }

    public byte[] getWrongImage() {
        return wrongImage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectImage(byte[] projectImage) {
        this.projectImage = projectImage;
    }

    public void setLearningLanguage(String learningLanguage) {
        this.learningLanguage = learningLanguage;
    }

    public void setCorrectImage(byte[]  correctImage) {
        this.correctImage = correctImage;
    }

    public void setWrongImage(byte[]  wrongImage) {
        this.wrongImage = wrongImage;
    }

}
