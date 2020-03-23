package com.demos.hipwedge;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class myTcpClient extends Thread {


    private String serverIP = "192.168.2.3";
    private int serverPort = 52401;
    private InetAddress serverAddr = null;
    private Socket sock = null;
    private boolean running = false;
    private DataInputStream in;
    private DataOutputStream out;
    private byte[] buffer =new byte[CONST.BUF_SIZE];

    public myTcpClient(String host, int port){
        serverIP=host;
        serverPort=port;
        startClient();
    }

    public void startClient(){
        EventBus.getDefault().register(this);
        this.run();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MessageEvent event) {
        Log.d(CONST.TAG, "TcpClient onMessageEvent: "+event.message);
        if (event.messageDirection== MessageEvent.MessageDirection.TRANSMIT){
            send(event.message);
        }
    };

    public void send(String _msg) {
        if (out != null) {
/*
                Thread sendThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
*/
                        try {
                        out.write(_msg.getBytes("UTF-8"));
                        out.flush();

                        Log.i("Send Method", "Outgoing : " + _msg);
                        } catch (UnsupportedEncodingException ex) {
                            Log.e("Send Method", ex.toString());
                        } catch (IOException ex) {
                            Log.e("Send Method", ex.toString());
                        }
/*                    }

                });
*/
        }
    }

    public void stopClient() {
        Log.v(CONST.TAG,"stopClient method run");
        EventBus.getDefault().unregister(this);
        running = false;
    }

    @Override
    public void run() {
        running = true;
        int readCount=0;
        do {
            try {
                serverAddr = InetAddress.getByName(serverIP);

                Log.i("TCP Client", "C: Connecting...");
                sock = new Socket(serverAddr, serverPort);
                EventBus.getDefault().post(new MessageEvent(MessageEvent.MessageDirection.STATUS, MessageEvent.MessageStatus.CONNECTED));
                try {
                    out = new DataOutputStream(sock.getOutputStream());

                    in = new DataInputStream(sock.getInputStream());
                    Log.i(CONST.TAG, "C: Connected.");
                    while (running) {
                        readCount = in.read(buffer);
                        if(readCount>0) {
                            byte[] buf = Arrays.copyOfRange(buffer,0,readCount-1);
                            Log.i("Incoming data: ", new String(buf, "UTF-8"));
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.MessageDirection.RECEIVE, new String(buf, "UTF-8")));
                        }
                    }
                    Log.d(CONST.TAG, "S: Received Message: '" + new String(buffer, "UTF-8") + "'");
                } catch (Exception e) {
                    Log.e(CONST.TAG, "S: Error", e);
                } finally {
                    out.close();
                    in.close();
                    sock.close();
                    Log.d(CONST.TAG, "Closing socket: " + sock);
                }
            } catch(Exception e){
                EventBus.getDefault().post(new MessageEvent(MessageEvent.MessageDirection.STATUS, MessageEvent.MessageStatus.DISCONNECTED));

                try {
                    Log.d(CONST.TAG, "TcpClient thread exception: "+e.getMessage()+" retrying in 1 second...");
                    Thread.sleep(1000);
                }catch (InterruptedException ex){
                    Log.d(CONST.TAG, "TcpClient thread interrupted: "+ex.getMessage());
                }
                Log.d(CONST.TAG, "C: Error", e);
            }
        } while(running);
        Log.i(CONST.TAG, "TcpClient connect() stopped!");
    }
}