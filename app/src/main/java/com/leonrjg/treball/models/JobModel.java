package com.leonrjg.treball.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JobModel {
    private String title;
    private String company;
    private String city;
    private String location;
    private String post_date;
    @SerializedName("content")
    private String description;
    private String link;
    private String image;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public String getPostDate() {
        return post_date;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return location + ", " + city;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static class Cast {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
