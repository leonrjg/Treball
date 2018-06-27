package com.leonrjg.treball.models;

public class CategoryModel {
    public String id;
    public String name;
    public Integer count;

    public String getInfoItem() { return name + " ("+count+")"; }
}