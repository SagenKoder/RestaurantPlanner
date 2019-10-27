package app.sagen.restaurantplanner.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.Booking;
import app.sagen.restaurantplanner.data.DBHandler;

public class NotificationService extends Service {

    private static final String TAG = "NotificationService";

    DBHandler dbHandler;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public NotificationService() {
        super();
        dbHandler = new DBHandler(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "I NotificationService", Toast.LENGTH_LONG).show();

        Log.i(TAG, "onStartCommand: ");

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sendsms = defaultSharedPreferences.getBoolean("SmsEnable", true);
        String smsMessage = defaultSharedPreferences.getString("SmsMessage", getString(R.string.settings_sms_message_default));

        List<Booking> bookingList = dbHandler.getAllBookingsNext24H();

        Log.i(TAG, String.format("onStartCommand: sendsms=%s, smsMessage=%s, bookingListLength=%s, bookingList=%s", sendsms, smsMessage, bookingList.size(), bookingList));

        for (Booking booking : bookingList) {
            Log.i(TAG, "onStartCommand: Checking out booking " + booking);

            /* Send notification */
            Intent i = new Intent(this, Result.class);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Log.i(TAG, "onStartCommand: Trying to send notification!");

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(booking.getRestaurant().getName())
                    .setContentText(booking.getRestaurant().getAddress())
                    .setSmallIcon(R.drawable.ic_restaurant_black_24dp)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pIntent).build();

            notificationManager.notify(0, notification);

//            Notification notifikasjon = new NotificationCompat.Builder(this)
//                    .setContentTitle("Planlagt restaurant idag")
//                    .setContentText(String.format("Restaurant: %s\nKlokken: %s\nAddresse: %s\nAntall invitert: %s",
//                            booking.getRestaurant().getName(),
//                            sdf.format(booking.getDateTime()),
//                            booking.getRestaurant().getAddress(),
//                            booking.getFriends().size()))
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentIntent(pIntent).build();
//            notifikasjon.flags |= Notification.FLAG_AUTO_CANCEL;

            /* Send sms */

//            if(sendsms) {
//                Log.i(TAG, "onStartCommand: sender sms");
//                SmsManager smsManager = SmsManager.getDefault();
//
//                for (Friend friend : booking.getFriends()) {
//                    Log.i(TAG, "onStartCommand: sender sms til venn " + friend);
//                    smsManager.sendTextMessage(friend.getPhone(), null, smsMessage
//                            .replace("%friend", friend.getName())
//                            .replace("%restaurant", booking.getRestaurant().getName())
//                            .replace("%time", sdf.format(booking.getDateTime()))
//                            .replace("%address", booking.getRestaurant().getAddress())
//                            .replace("%phone", booking.getRestaurant().getPhone()), null, null);
//                }
//            }
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
