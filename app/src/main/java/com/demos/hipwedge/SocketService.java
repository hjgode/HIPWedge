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

import java.net.URISyntaxException;

import static com.demos.hipwedge.CONST.CHANNEL_ID;


public class SocketService extends Service {

    private static myTcpClient myClient =null;
    public static boolean isConnected(){
        if(myClient !=null && myClient.isAlive())
            return true;
        else
            return false;
    }
    private static final String NOTIFICATION_CHANNEL_ID_DEFAULT = "App running in background";
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

        Toast.makeText(this, "on created", Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder.build();
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        // Set big text style.
        builder.setStyle(bigTextStyle);
        startForeground(0, notification);
    }

//    and make sure to disconnect the socket when the service is destroyed
    @Override
    public void onDestroy() {
        Log.d(CONST.TAG, "SocketService:onDestroy");
        myClient.stopClient();
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

            Thread myThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    myClient = new myTcpClient(_host, _port);
                }
            });
            myThread.start();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            Notification notification = builder.build();
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            // Set big text style.
            builder.setStyle(bigTextStyle);
            startForeground(1, notification);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

    public void setNotificationMessage(CharSequence message, CharSequence title) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(message);
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.notify(3, builder.build());
    }
}