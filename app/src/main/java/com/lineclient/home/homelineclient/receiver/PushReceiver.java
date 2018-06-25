package com.lineclient.home.homelineclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lineclient.home.homelineclient.activity.HomeActivity;

//import cn.jpush.android.api.JPushInterface;

public class PushReceiver extends BroadcastReceiver{

    public interface PushReceive{
        void onMessage(String msg);
        void onRegistrationId(String id);
        void onNotifactionId(int id);
    }

    private PushReceive pushReceive;

    public void setPushReceive(PushReceive pushReceive) {
        this.pushReceive = pushReceive;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      /*  try {
            Bundle bundle = intent.getExtras();
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                if(pushReceive!=null){
                    pushReceive.onRegistrationId(bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID));
                }
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                if(pushReceive!=null){
                    pushReceive.onMessage(bundle.getString(JPushInterface.EXTRA_MESSAGE));
                }
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                if(pushReceive!=null){
                    pushReceive.onNotifactionId(bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
                }
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Intent i = new Intent(context,HomeActivity.class);
                i.putExtras(bundle);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                context.startActivity(i);
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                //boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            }
        } catch (Exception e){

        }*/
    }

}
