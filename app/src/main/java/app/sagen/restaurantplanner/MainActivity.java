package app.sagen.restaurantplanner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_booking, R.id.navigation_restaurants, R.id.navigation_friends)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: DESTIONATION ID " + navController.getCurrentDestination().getLabel());
                String current = navController.getCurrentDestination().getLabel().toString();
                if (current.equals(getString(R.string.friends_menu_item))) {
                    Log.d(TAG, "onClick: FRIEND");
                } else if (current.equals(getString(R.string.restaurant_menu_item))) {
                    Log.d(TAG, "onClick: RESTAURANT");
                } else if (current.equals(getString(R.string.booking_menu_item))) {
                    Log.d(TAG, "onClick: BOOKING");
                }
            }
        });
    }
}
