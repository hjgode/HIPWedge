package com.demos.hipwedge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import static com.demos.hipwedge.CONST.CHANNEL_ID;


public class SocketService extends Service {
    SocketManager socketManger = SocketManager.getSocketManger();

    private static Socket mSocket=null;
    public static boolean isConnected(){
        if(mSocket!=null && mSocket.connected())
            return true;
        else
            return false;
    }
    public static final String TAG = SocketService.class.getSimpleName();
    private static final String NOTIFICATION_CHANNEL_ID_DEFAULT = "App running in background";
    String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";
    String _host;
    int _port;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(CONST.TAG, "SocketService:onCreate");
        socketManger.init(this::onSocketEvent,_host,_port);
        Toast.makeText(this, "on created", Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setGroup(GROUP_KEY_WORK_EMAIL);
        Notification notification = builder.build();
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        // Set big text style.
        builder.setStyle(bigTextStyle);
        startForeground(0, notification);
    }

    public void onSocketEvent(boolean connect){
        //your code when the socket connection connect or disconnect
        Log.d(CONST.TAG, "SocketServic:onSocketEvent");
    }

//    and make sure to disconnect the socket when the service is destroyed
    @Override
    public void onDestroy() {
        Log.d(CONST.TAG, "SocketService:onDestroy");
        socketManger.onDestroy();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(CONST.TAG, "SocketService:onStartCommand");
        Toast.makeText(this, "start command", Toast.LENGTH_SHORT).show();
        createNotificationChannel();
        try {
            String h=intent.getStringExtra("HOST");
            int p=intent.getIntExtra("PORT", 52401);
            _host=h; _port=p;
            mSocket = IO.socket(_host+":"+_port);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setGroup(GROUP_KEY_WORK_EMAIL);
            Notification notification = builder.build();
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            // Set big text style.
            builder.setStyle(bigTextStyle);
            startForeground(1, notification);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.on("newMessageReceived", onNewMessage);
        mSocket.connect();
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(CONST.TAG, "SocketService:onNewMessage");
            JSONObject data = (JSONObject) args[0];
            String username;
            String message;
            try {
                username = data.getString("username");
                message = data.getString("message");
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                return;
            }

            Log.d(TAG, "call: new message ");
            setNotificationMessage(message, username);
        }
    };

    public void setNotificationMessage(CharSequence message, CharSequence title) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(message);
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.notify(3, builder.build());
    }
}