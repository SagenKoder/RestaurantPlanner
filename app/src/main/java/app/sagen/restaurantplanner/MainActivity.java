package app.sagen.restaurantplanner;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import app.sagen.restaurantplanner.preferences.SettingsActivity;
import app.sagen.restaurantplanner.service.NotificationBroadcastReciever;
import app.sagen.restaurantplanner.service.NotificationService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private IntentFilter intentFilter;
    private NotificationBroadcastReciever notificationBroadcastReciever;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, String.format("onSharedPreferenceChanged: KEY=%s", key));
            if(key.equalsIgnoreCase("SmsTimeOfDay") || key.equalsIgnoreCase("NotificationSerice")) {
                if(sharedPreferences.getBoolean("NotificationSerice", true)) {
                    startService();
                } else {
                    stopService();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_booking, R.id.navigation_restaurants, R.id.navigation_friends)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 1);
        }

        intentFilter = new IntentFilter("app.sagen.restaurantplanner.startService");
        notificationBroadcastReciever = new NotificationBroadcastReciever();

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        startService();
    }

    @Override
    protected void onResume() {
        registerReceiver(notificationBroadcastReciever, intentFilter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(notificationBroadcastReciever);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.show_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



    public void startService() {
        Log.d(TAG, "startService: ");
        Intent intent = new Intent("app.sagen.restaurantplanner.startService");
        sendBroadcast(intent);
    }

    public void stopService() {
        Log.d(TAG, "stopService: ");
        Intent i = new Intent(this, NotificationService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarm != null) {
            alarm.cancel(pintent);
        }
    }

}
