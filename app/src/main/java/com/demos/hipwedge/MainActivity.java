package com.demos.hipwedge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    Context _coContext=this;
    TextView txtData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startSocketService(_coContext, "192.168.0.40", 52401);
        txtData=findViewById(R.id.txtData);
    }

    @Override
    protected void onResume()
    {
        startSocketService(_coContext, "192.168.0.40", 52401);
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        if(event.messageDirection== MessageEvent.MessageDirection.RECEIVE){
            txtData.setText(event.message);
            txtData.setBackgroundColor(Color.WHITE);
        }else if(event.messageDirection== MessageEvent.MessageDirection.STATUS){
            if(event.messageStatus== MessageEvent.MessageStatus.CONNECTED)
                txtData.setBackgroundColor(Color.GREEN);
            else
                txtData.setBackgroundColor(Color.RED);
        }
    }
    void startSocketService(Context context, String host, int port) {
        if(SocketService.isConnected())
            return;

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        host=SP.getString("host_ip", "192.168.0.40");
        port= Integer.parseInt(SP.getString("host_port", "52401"));

        Intent intent = new Intent(context, SocketService.class);
        intent.putExtra("HOST", host);
        intent.putExtra("PORT", port);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            context.startForegroundService(intent);
        } else
        {
            context.startService(intent);
        }
    }
}
