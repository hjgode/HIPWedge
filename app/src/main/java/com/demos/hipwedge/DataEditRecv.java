package com.demos.hipwedge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.greenrobot.eventbus.EventBus;

import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.demos.hipwedge.CONST.TAG;

public class DataEditRecv extends BroadcastReceiver {
    static Context _context;

    @Override
    public void onReceive(Context context, Intent intent) {
        _context=context;
        boolean bUseSocket=true, bDataPassthru=false;
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        if(!SP.getBoolean("use_socket", true))
            bUseSocket=true;
        if(!SP.getBoolean("data_passthru", false))
            bDataPassthru=false;

        //startSocketService(context, "192.168.0.40", 52401);
        String input = intent.getStringExtra("data");//Read the scan result from the Intent
        Log.d(TAG, "Package" + intent.getPackage() + ", " + intent.toString());
//        this.myContext = context;//you can retrieve context from onReceive argument
//        this.myIntent = new Intent(BROADCAST_ACTION);
        //logText.append(input);
        doLog(input, context);

        /*
        codeId b (java.lang.String)
        dataBytes [B@c9a8a48 ([B)
        data 10110 (java.lang.String)
        timestamp 2016-09-17T09:05:27.619+2:00 (java.lang.String)
        aimId ]A0 (java.lang.String)
        version 1 (java.lang.Integer)
        charset ISO-8859-1 (java.lang.String)
        */

        String codeId = intent.getStringExtra("codeId");

        String codeType = codeIDs.getNameCodeID(codeId);
        Log.i(TAG, "codeType=" + codeType);
        Log.i(TAG, codeIDs.getNameCodeID(codeId));

        String aimID = intent.getStringExtra("aimId");
        String timeStamp = intent.getStringExtra("timestamp");
        String charSet = intent.getStringExtra("charset");
        String myTimeStamp = "---";
        Date myDate = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            myDate = sdf.parse(timeStamp);
            Log.d(TAG, "Date: " + myDate.toString());
            sdf.applyPattern("yyyy-MM-dd hh:mm");
            myTimeStamp = (sdf.format(myDate));

            Log.d(CONST.TAG, "DataEditRcv post Message: "+input);
            if(bUseSocket)
                EventBus.getDefault().post(new MessageEvent(MessageEvent.MessageDirection.TRANSMIT, input));

        } catch (RuntimeException ex) {
            Log.e(TAG, "SimpleDateFormat: " + ex.toString());
        } catch (ParseException ex) {
            Log.e(TAG, "SimpleDateFormat: " + ex.toString());
        }
        //return edited data as bundle
        Bundle bundle = new Bundle();
        //Return the Modified scan result string
        bundle.putString("data", input);
        if(bDataPassthru)
            setResultExtras(bundle);
        else
            setResultExtras(null);
    }

    void doLog(String s, Context context) {
        Log.d(TAG, s);
        //doUpdate(s, context);
    }

}
