package com.example.quizapp.Models;

public class CategoryModel {

    private String catagoryName,catagoryImage,key;

    public CategoryModel() {

    }

    public CategoryModel(String catagoryName, String catagoryImage) {
        this.catagoryName = catagoryName;
        this.catagoryImage = catagoryImage;
    }

    public String getCatagoryName() {
        return catagoryName;
    }

    public void setCatagoryName(String catagoryName) {
        this.catagoryName = catagoryName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCatagoryImage() {
        return catagoryImage;
    }

    public void setCatagoryImage(String catagoryImage) {
        this.catagoryImage = catagoryImage;
    }
}
