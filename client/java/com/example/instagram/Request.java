package com.example.instagram;

import android.util.Log;

import java.io.File;

public class Request {
    public enum actionEnum {REGISTER, LOGIN, POST, LIKE, SEND_FRIEND_REQUEST, REQUEST_POST,
        NUMBER_OF_POSTS, NUMBER_OF_FR, REQUEST_FR, ACCEPT_FR, DENY_FR};
    public actionEnum action;
    public String arg1, arg2;
    public int index;
    public long target_id;
    public String response;
    public int altResponse;
    public long post_id, number_of_likes;
    public String username, caption, file_format;
    public File imageFile;
    public byte[] bytes;

    public Request(actionEnum action){
        this.action = action;
    }

    public Request(actionEnum action, String arg1){
        this.action = action;
        this.arg1 = arg1;
    }

    public Request(actionEnum action, String arg1, String arg2){
        this.action = action;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public Request(actionEnum action, int index){
        this.action = action;
        this.index = index;
    }

    public Request(actionEnum action, long tg_id){
        this.action = action;
        this.target_id = tg_id;
    }

    public Request(actionEnum action, String arg1, String arg2, byte[] bytes){
        this.action = action;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.bytes = bytes;
    }

    public void print(){
        Log.d("tag_used", this.action.toString()+ ' ' + this.arg1 + ' ' + this.arg2);
    }
}
