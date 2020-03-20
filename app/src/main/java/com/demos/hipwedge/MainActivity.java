package com.demos.hipwedge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    Context _coContext=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startSocketService(_coContext, "192.168.0.40", 52401);
    }

    @Override
    protected void onResume()
    {
        startSocketService(_coContext, "192.168.0.40", 52401);
        super.onResume();
    }

    void startSocketService(Context context, String host, int port) {
        if(SocketService.isConnected())
            return;
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
