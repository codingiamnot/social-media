package com.example.instagram;

import android.util.Log;

import java.io.File;

public class Post {
    private long postId, numberOfLikes;
    private String username, caption;
    private File file;

    public Post(int index, ScrollPostsActivity parent){
        Request request = new Request(Request.actionEnum.REQUEST_POST, index);
        Communication communication = new Communication(parent, request);
        communication.start();
        try{
            communication.join();
        }
        catch (InterruptedException e){
            Log.d("communication", e.toString());
        }
        if(request.response.equals("ok")){
            this.file = request.imageFile;
            this.caption = request.caption;
            this.username = request.username;
            this.numberOfLikes = request.number_of_likes;
            this.postId = request.post_id;
            Log.d("posts_tag", "post_ok");
        }
        else{
            //TODO error notification
        }
    }

    public File getFile() {
        return file;
    }
    public String getCaption (){return caption;}
    public String getUsername(){return username;}
    public long getPostId(){return postId;}
    public long getNumberOfLikes(){return numberOfLikes;}
}
