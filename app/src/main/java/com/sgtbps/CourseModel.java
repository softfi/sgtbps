package com.sgtbps;


  public class CourseModel {

    private String title;
     private int image;

    public CourseModel(String course_name, int imgid) {
        this.title = course_name;
        this.image = imgid;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}


