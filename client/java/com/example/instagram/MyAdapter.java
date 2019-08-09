package com.example.instagram;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int numberOfPosts = -1;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView username, likes, caption;
        public ImageView image;
        public Button likeButton;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.op_username);
            likes = itemView.findViewById(R.id.number_of_likes);
            caption = itemView.findViewById(R.id.caption);
            image = itemView.findViewById(R.id.image);
            likeButton = itemView.findViewById(R.id.like_button);
        }
    }

    public MyAdapter(Context context){
        this.context = context;
    }

    @NonNull
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType){
        Log.d("posts_tag", "create");
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.post_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolderTemp, final int position){
        Log.d("posts_tag", "bind");
        final Post post = new Post(position+1,(ScrollPostsActivity) this.context);
        final MyViewHolder viewHolder = (MyViewHolder) viewHolderTemp;
        viewHolder.username.setText( post.getUsername() );
        viewHolder.caption.setText( post.getCaption() );
        viewHolder.likes.setText( Long.toString( post.getNumberOfLikes() ) );
        viewHolder.image.setImageURI(Uri.fromFile( post.getFile() ));
        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Request request = new Request(Request.actionEnum.LIKE, post.getPostId());
                Communication communication = new Communication(request);
                communication.start();
                try{
                    communication.join();
                }
                catch (InterruptedException e){
                    Log.d("communication", e.toString());
                }
                if(request.response.equals("ok")){
                    viewHolder.likes.setText(Long.toString( post.getNumberOfLikes() + 1 ));
                }
                else{
                    //TODO show error
                }
            }
        });
    }

    public int getItemCount(){
        Log.d("posts_tag", "get item count");
        if(this.numberOfPosts != -1){
            return this.numberOfPosts;
        }
        Request request = new Request(Request.actionEnum.NUMBER_OF_POSTS);
        Communication communication = new Communication(request);
        communication.start();
        try {
            communication.join();
            Log.d("posts_tag", request.altResponse + " posts");
            this.numberOfPosts = request.altResponse;
            return request.altResponse;
        }
        catch (InterruptedException e){
            Log.d("posts_tag", e.toString());
            return 0;
        }
    }

}
