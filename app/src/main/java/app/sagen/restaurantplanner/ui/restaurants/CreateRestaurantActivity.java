package app.sagen.restaurantplanner.ui.restaurants;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.Restaurant;
import app.sagen.restaurantplanner.db.DBHandler;

public class CreateRestaurantActivity extends AppCompatActivity {

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(this);

        setContentView(R.layout.activity_create_restaurant);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setDimAmount(0.60f);

        final long restaurantId;
        if (getIntent().hasExtra("editRestaurantId")) {
            restaurantId = getIntent().getLongExtra("editRestaurantId", -1);
        } else {
            restaurantId = -1;
        }

        final EditText nameInput = findViewById(R.id.create_restaurant_name);
        final EditText phoneInput = findViewById(R.id.create_restaurant_phone);
        final EditText addressInput = findViewById(R.id.create_restaurant_address);
        final EditText typeInput = findViewById(R.id.create_restaurant_type);
        final TextView title = findViewById(R.id.create_restaurant_title);

        final Restaurant restaurant = dbHandler.getRestaurant(restaurantId);
        if (restaurantId > 0) {
            if (restaurant == null) {
                finish(); // just exit...
                return;
            }
            title.setText("Rediger en restaurant");
            nameInput.setText(restaurant.getName());
            phoneInput.setText(restaurant.getPhone());
            typeInput.setText(restaurant.getType());
            addressInput.setText(restaurant.getAddress());
        }

        Button button = findViewById(R.id.create_restaurant_submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurant != null) {
                    restaurant.setName(nameInput.getText().toString());
                    restaurant.setPhone(phoneInput.getText().toString());
                    restaurant.setAddress(addressInput.getText().toString());
                    restaurant.setType(phoneInput.getText().toString());
                    dbHandler.updateRestaurant(restaurant);
                    Toast.makeText(getApplicationContext(), "Endring lagret!", Toast.LENGTH_SHORT).show();
                } else {
                    dbHandler.createRestaurant(new Restaurant(
                            nameInput.getText().toString(),
                            addressInput.getText().toString(),
                            phoneInput.getText().toString(),
                            typeInput.getText().toString()));
                    Toast.makeText(getApplicationContext(), "Du la til en ny restaurant!", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}
