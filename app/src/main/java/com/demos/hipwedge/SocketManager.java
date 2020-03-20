package com.demos.hipwedge;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import org.json.JSONObject;


import java.net.URISyntaxException;

public class SocketManager {


    private  static SocketManager socketManger;

    Socket socket;
    Callback<Boolean> onConnect;

    public void init(Callback<Boolean> onConnect, String sHost, int port){
        this.onConnect = onConnect;
        connectToSocket(sHost,port);
        listenToPublicEvents();
    }

    private void connectToSocket(String sHost, int port){
        try{
            IO.Options opts = new IO.Options();
            //optional parameter for authentication
            //opts.query = "token=" + YOUR_TOKEN;
            opts.forceNew = true;
            opts.reconnection = true;
            opts.reconnectionDelay = 1000;
            socket = IO.socket(sHost+":"+port, opts);
            socket.connect();

        }
        catch(URISyntaxException e){

            throw new RuntimeException(e);
        }
    }

    private void listenToPublicEvents(){
        socket.on(Socket.EVENT_CONNECT, args -> {
            if(onConnect!=null)
                onConnect.onResult(true);
        } );

        socket.on(Socket.EVENT_DISCONNECT, args ->{
            if(onConnect!=null)
                onConnect.onResult(false);
        });
    }


    public void emit(String event, JSONObject data, Ack ack){
        socket.emit(event, new JSONObject[]{data}, ack);
    }


    public void on(String event, Emitter.Listener em){
        socket.on(event, em);
    }


    public static SocketManager getSocketManger() {
        if(socketManger == null){
            socketManger = new SocketManager();
        }
        return socketManger;
    }


    public boolean isConnected(){
        return socket!=null && socket.connected();
    }

    public void onDestroy() {
        onConnect = null;
        socket.disconnect();
    }

    public interface Callback<T> {
        void onResult(T t);
    }

}