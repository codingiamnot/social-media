package com.example.instagram;

import android.util.Log;

public class FriendRequest {
    private String sender;
    private int position;

    public FriendRequest(int position){
        this.position = position;
        Request request = new Request(Request.actionEnum.REQUEST_FR, position);
        Communication communication = new Communication(request);
        communication.start();
        try{
            communication.join();
        }
        catch (InterruptedException e){
            Log.d("friend requests", e.toString());
        }
        sender = request.response;
    }

    public String getSender(){
        return sender;
    }
}
