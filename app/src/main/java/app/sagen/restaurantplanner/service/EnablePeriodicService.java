package app.sagen.restaurantplanner.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class EnablePeriodicService extends Service {

    private static final String TAG = "EnablePeriodicService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "I EnablePeriodicService", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStartCommand: START");

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // only run if enabled
        boolean runService = defaultSharedPreferences.getBoolean("NotificationSerice", true);
        Log.d(TAG, String.format("onStartCommand: runService=%s", runService));
        if (!runService) return super.onStartCommand(intent, flags, startId);

        // time of day
        String smsTimeOfDay = defaultSharedPreferences.getString("SmsTimeOfDay", "08:00");
        int hour, minute;
        try {
            String[] split = smsTimeOfDay.split(":");
            hour = Integer.parseInt(split[0]);
            minute = Integer.parseInt(split[1]);
        } catch (Exception e) {
            hour = 8;
            minute = 0;
            Log.e(TAG, "onStartCommand: could not parse time of day", e);
        }
        Log.d(TAG, String.format("onStartCommand: time=%s minute=%s", hour, minute));

        // create intent
        Intent i = new Intent(this, NotificationService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, i, 0);

        // start alarm
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarm != null) {
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pintent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
