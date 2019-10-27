package app.sagen.restaurantplanner.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationBroadcastReciever extends BroadcastReceiver {

    private static final String TAG = "NotificationBroadcastRe";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "I NotificationBroadcastReciever", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onReceive: Recieved notification");

        Intent i = new Intent(context, EnablePeriodicService.class);
        context.startService(i);
    }
}
