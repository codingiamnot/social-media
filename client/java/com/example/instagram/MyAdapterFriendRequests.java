package com.example.instagram;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class MyAdapterFriendRequests extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private FriendRequestsActivity context;
    private int numberOfFR = -1;
    private final String debugTag = "friend requests";

    public MyAdapterFriendRequests(FriendRequestsActivity context){
        this.context = context;
    }

    public static class MyViewHolderFriendRequests extends RecyclerView.ViewHolder{
        public TextView sender;
        public Button accept, deny;
        public MyViewHolderFriendRequests(@NonNull View itemView){
            super(itemView);
            sender = itemView.findViewById(R.id.sender);
            accept = itemView.findViewById(R.id.accept);
            deny = itemView.findViewById(R.id.deny);
        }
    }

    public MyAdapterFriendRequests.MyViewHolderFriendRequests onCreateViewHolder
            (@NonNull ViewGroup viewGroup, int viewType){
        Log.d(debugTag, "create");
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.friend_request, viewGroup, false);
        return new MyAdapterFriendRequests.MyViewHolderFriendRequests(view);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolderTemp, final int position){
        final FriendRequest friendRequest = new FriendRequest(position + 1);
        MyViewHolderFriendRequests viewHolder = (MyViewHolderFriendRequests) viewHolderTemp;
        viewHolder.sender.setText(friendRequest.getSender());
        viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request request = new Request(Request.actionEnum.ACCEPT_FR, position+1);
                Communication communication = new Communication(request);
                communication.start();
            }
        });
        viewHolder.deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request request = new Request(Request.actionEnum.DENY_FR, position+1);
                Communication communication = new Communication(request);
                communication.start();
            }
        });
    }

    public int getItemCount(){
        if(numberOfFR == -1){
            Request request = new Request(Request.actionEnum.NUMBER_OF_FR);
            Communication communication = new Communication(request);
            communication.start();
            try {
                communication.join();
            }
            catch (InterruptedException e){
                Log.d(debugTag, e.toString());
            }
            Log.d(debugTag, Integer.toString(request.altResponse));
            numberOfFR = request.altResponse;
            return request.altResponse;
        }
        else
            return numberOfFR;
    }
}
