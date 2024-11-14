package com.example.quizapp.Models;

public class SubCategoryModel {

    private String catagoryName,key;

    public SubCategoryModel() {

    }

    public SubCategoryModel(String catagoryName) {
        this.catagoryName = catagoryName;
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

}
