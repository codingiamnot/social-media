package com.example.instagram;

import android.util.Log;

import java.net.*;
import java.io.*;
import org.json.*;

public class Communication  extends Thread{

    private Socket sock;
    private DataOutputStream out_bytes;
    private PrintWriter out;
    private DataInputStream in_bytes;
    private BufferedReader in;
    private Request request;
    private ScrollPostsActivity parent;
    public boolean isListening = true;
    //put your server ip here
    private final String server_ip = "";

    public Communication(ScrollPostsActivity parent, Request request){
        this.parent = parent;
        this.request = request;
    }
    public Communication(Request request) {
        this.request = request;
    }

    public void run(){

        switch (request.action) {
            case REGISTER:
                request.response = this.register(request.arg1, request.arg2);
                break;
            case LOGIN:
                 request.response = this.login(request.arg1, request.arg2);
                 break;
            case REQUEST_POST:
                request.response = this.request_post(request.index, request);
                break;
            case NUMBER_OF_POSTS:
                request.altResponse = this.number_of_posts();
                break;
            case LIKE:
                request.response = this.like(request.target_id);
                break;
            case POST:
                request.response = this.post(request.arg1, request.arg2, request.bytes);
                break;
            case NUMBER_OF_FR:
                request.altResponse = this.number_of_fr();
                break;
            case REQUEST_FR:
                request.response = this.get_fr(request.index);
                break;
            case ACCEPT_FR:
                request.response = this.accept_fr(request.index);
                break;
            case DENY_FR:
                request.response = this.deny_fr(request.index);
                break;
            case SEND_FRIEND_REQUEST:
                Log.d("communication", "sfr");
                request.response = this.send_friend_request(request.target_id);
                break;
            }
    }

    private void connect(String ip, int port){
        Log.d("communication", "connecting to " + ip + ' ' + Integer.toString(port));
        try {
            this.sock = new Socket(ip, port);
            this.out_bytes = new DataOutputStream(sock.getOutputStream());
            this.out = new PrintWriter(sock.getOutputStream(), true);
            this.in_bytes = new DataInputStream(sock.getInputStream());
            this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        }
        catch (IOException e) {
            Log.d("communication", e.toString());
        }

    }

    private void disconnect(){
        try {
            this.in_bytes.close();
            this.in.close();
            this.out_bytes.close();
            this.out.close();
            this.sock.close();
        }
        catch (IOException e){
            Log.d("communication", e.toString());
        }
    }

    private String parse_json_one_field(String string){
        JSONObject object = new JSONObject();
        try {
            object = new JSONObject(string);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }
        String result = "";
        try {
            result = object.getString("result");
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }
        return result;
    }

    private int parse_json_one_field_to_port(String string){
        JSONObject object = new JSONObject();
        try{
            object = new JSONObject(string);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }
        int port = -1;
        try{
            port = object.getInt("port");
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }
        return port;
    }

    private void parse_json_to_post(Request request, String string){
        JSONObject object = new JSONObject();
        try{
            object = new JSONObject(string);
        }
        catch (JSONException e){
            Log.d("communcation", e.toString());
        }
        try{
            request.post_id = object.getLong("post_id");
            request.number_of_likes = object.getLong("likes");
            request.username = object.getString("op_username");
            request.caption = object.getString("caption");
            request.file_format = object.getString("file_format");
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }

    }

    private int parse_json_to_nr_of_posts(String string){
        try {
            JSONObject jsonObject = new JSONObject(string);
            int nrOfPosts = jsonObject.getInt("numberOfPosts");
            return nrOfPosts;
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
            return 0;
        }
    }

    private int parse_json_to_nr_of_fr(String string){
        try {
            JSONObject jsonObject = new JSONObject(string);
            return jsonObject.getInt("numberOfFR");
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
            return 0;
        }
    }

    private String parse_json_to_fr(String string){
        try{
            JSONObject jsonObject = new JSONObject(string);
            return jsonObject.getString("sender");
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
            return "error";
        }
    }

    private String register(String username, String password){
        Log.d("communication", "register");
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("action", "register");
            dataJson.put("username", username);
            dataJson.put("password", password);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }
        Log.d("communication", dataJson.toString());
        this.connect(server_ip, 1234);
        this.out.println(dataJson);
        String response;
        try{
            response = this.in.readLine();
            Log.d("communication", response);
        }
        catch (IOException e){
            response = "";
            Log.d("communication", e.toString());
        }
        this.disconnect();
        return this.parse_json_one_field(response);
    }

    private String login(String username, String password){
        Log.d("communication", "login");
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("action", "login");
            dataJson.put("username", username);
            dataJson.put("password", password);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }

        Log.d("communication", dataJson.toString());

        this.connect(server_ip, 1234);
        this.out.println(dataJson);
        String response;
        try{
            response = this.in.readLine();
            Log.d("communication", response);
        }
        catch (IOException e){
            response = "";
            Log.d("communication", e.toString());
        }
        this.disconnect();
        return this.parse_json_one_field(response);
    }

    private String like(long post_id){
        Log.d("communication", "like");
        JSONObject dataJson = new JSONObject();
        try{
            dataJson.put("action", "like");
            dataJson.put("post_id", post_id);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }

        Log.d("communication", dataJson.toString());

        this.connect(server_ip, 1234);
        this.out.println(dataJson);
        String response;
        try{
            response = this.in.readLine();
            Log.d("communication", response);
        }
        catch (IOException e){
            response = "";
            Log.d("communication", e.toString());
        }
        this.disconnect();
        return this.parse_json_one_field(response);
    }

    private String send_friend_request(long target_id){
        Log.d("communication", "send_friend_request");
        JSONObject dataJson = new JSONObject();
        try{
            dataJson.put("action", "send_friend_request");
            dataJson.put("target_id", target_id);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }

        Log.d("communication", dataJson.toString());

        this.connect(server_ip, 1234);
        this.out.println(dataJson);
        String response;
        try{
            response = this.in.readLine();
            Log.d("communication", response);
        }
        catch (IOException e){
            response = "";
            Log.d("communication", e.toString());
        }
        this.disconnect();
        return this.parse_json_one_field(response);
    }

    private String request_post(int index, Request request){
        Log.d("communication", "request post");
        String response, responseJson;
        int port = 0;
        Log.d("communication", "request_post");
        JSONObject dataJson = new JSONObject();
        try{
            dataJson.put("action", "request_post");
            dataJson.put("number", index);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
            return "";
        }
        Log.d("communication", dataJson.toString());

        this.connect(server_ip, 1234);
        this.out.println(dataJson);
        try{
            responseJson = this.in.readLine();
            Log.d("communication", responseJson);
            response = this.parse_json_one_field(responseJson);
        }
        catch (IOException e){
            Log.d("communication", e.toString());
            return "";
        }
        this.disconnect();
        Log.d("communication", response);
        if(!response.equals("ok"))
            return response;

        port = this.parse_json_one_field_to_port(responseJson);
        responseJson = "";
        this.connect(server_ip, port);

        try {
            responseJson = this.in.readLine();
        }
        catch(IOException e){
            Log.d("communication", e.toString());
        }
        Log.d("communication", responseJson);
        this.parse_json_to_post(request, responseJson);

        File file = new File(this.parent.postDir, "post" + index + "." + request.file_format);
        Log.d("communication", file.getPath());
        request.imageFile = file;
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            this.connect(server_ip, port);


            byte[] buf = new byte[1024];
            int len;
            while((len = in_bytes.read(buf)) > 0){
                fileOutputStream.write(buf);
            }
            fileOutputStream.close();
        }
        catch (IOException e){
            Log.d("communication", e.toString());
        }
        this.disconnect();
        return "ok";
    }

    private int number_of_posts(){
        Log.d("communication", "number of posts");
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("action", "number_of_posts");
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }
        this.connect(server_ip, 1234);
        this.out.println(dataJson);
        try{
            String responseJson = this.in.readLine();
            Log.d("communication", responseJson);
            String response = this.parse_json_one_field(responseJson);
            if(response.equals("ok")){
                Log.d("communication", "got here");
                Log.d("communication", Integer.toString(parse_json_to_nr_of_posts(responseJson)));
                return parse_json_to_nr_of_posts(responseJson);
            }
            return 0;
        }
        catch (IOException e){
            Log.d("communication", e.toString());
            return 0;
        }
    }

    private String post(String caption, String fileFormat, byte[] image){
        Log.d("communication", "post");
        JSONObject dataJson = new JSONObject();
        try{
            dataJson.put("action", "post");
            dataJson.put("caption", caption);
            dataJson.put("format", fileFormat);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }
        Log.d("communication", dataJson.toString());

        this.connect(server_ip, 1234);
        this.out.println(dataJson);
        try{
            String responseJson = this.in.readLine();
            Log.d("communication", responseJson);
            this.disconnect();
            String response = this.parse_json_one_field(responseJson);
            if(response.equals("ok")){
                int port = this.parse_json_one_field_to_port(responseJson);
                this.connect(server_ip, port);
                this.out_bytes.write(image);
                this.disconnect();
            }
            return response;
        }
        catch (IOException e){
            Log.d("communication", e.toString());
            return "error";
        }
    }

    private int number_of_fr(){
        JSONObject jsonObject = new JSONObject();
        Log.d("communication", "number_of_fr");
        try{
            jsonObject.put("action", "number_of_friend_requests");
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }
        this.connect(server_ip, 1234);
        this.out.println(jsonObject);
        try{
            String responseJson = this.in.readLine();
            Log.d("communication", responseJson);
            String response = this.parse_json_one_field(responseJson);
            if(response.equals("ok")){
                return parse_json_to_nr_of_fr(responseJson);
            }
            else
                return 0;
        }
        catch (IOException e){
            Log.d("communication", e.toString());
            return 0;
        }
    }

    private String get_fr(int index){
        Log.d("communication", "get fr");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "request_friend_request");
            jsonObject.put("number", index);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }
        this.connect(server_ip, 1234);
        this.out.println(jsonObject);
        try{
            String responseJson = this.in.readLine();
            Log.d("communication", responseJson);
            String response = parse_json_one_field(responseJson);
            if(response.equals("ok")){
                return parse_json_to_fr(responseJson);
            }
            else
                return "error";
        }
        catch (IOException e){
            Log.d("communication", e.toString());
            return "error";
        }
    }

    private String accept_fr(int index){
        Log.d("communication", "accept_fr");
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("action", "accept_friend_request");
            jsonObject.put("fr_index", index);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }

        this.connect(server_ip, 1234);
        this.out.println(jsonObject);
        try{
            String responseJson = this.in.readLine();
            Log.d("communication", responseJson);
            String response = parse_json_one_field(responseJson);
            return response;
        }
        catch (IOException e){
            Log.d("communication", e.toString());
            return "error";
        }
    }

    private String deny_fr(int index){
        JSONObject jsonObject = new JSONObject();
        Log.d("communication", "deny_fr");
        try{
            jsonObject.put("action", "deny_friend_request");
            jsonObject.put("fr_index", index);
        }
        catch (JSONException e){
            Log.d("communication", e.toString());
        }

        this.connect(server_ip, 1234);
        this.out.println(jsonObject);
        try{
            String responseJson = this.in.readLine();
            Log.d("communication", responseJson);
            String response = parse_json_one_field(responseJson);
            return response;
        }
        catch (IOException e){
            Log.d("communication", e.toString());
            return "error";
        }
    }
}
