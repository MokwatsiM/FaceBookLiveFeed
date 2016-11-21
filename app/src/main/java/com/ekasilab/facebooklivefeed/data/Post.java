package com.ekasilab.facebooklivefeed.data;



/**
 * Created by ekasilab on 17/11/2016.
 */

public class Post {

    private String postID,user,textPost,imagePost;

    public Post() {

    }

    public Post(String postID, String user, String textPost, String imagePost) {
        this.postID = postID;
        this.user = user;
        this.textPost = textPost;
        this.imagePost = imagePost;
    }

    public String getPostID() {
        return postID;
    }

    public String getUser() {
        return user;
    }

    public String getTextPost() {
        return textPost;
    }

    public String getImagePost() {
        return imagePost;
    }
}
