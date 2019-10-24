package app.sagen.restaurantplanner.ui.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.Booking;
import app.sagen.restaurantplanner.data.Restaurant;
import app.sagen.restaurantplanner.db.DBHandler;

public class CreateBookingActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(this);

        setContentView(R.layout.activity_create_booking);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setDimAmount(0.60f);

        final long bookingId;
        if (getIntent().hasExtra("editBookingId")) {
            bookingId = getIntent().getLongExtra("editBookingId", -1);
        } else {
            bookingId = -1;
        }

        List<Restaurant> restaurantList = dbHandler.getAllRestaurants();

        final Booking booking = dbHandler.getBooking(bookingId);
        final Spinner restaurantInput = findViewById(R.id.create_booking_restaurant);
        final Button selectDate = findViewById(R.id.create_booking_date);
        final TextView selectDateLabel = findViewById(R.id.create_booking_date_label);
        updateDatelabel(selectDateLabel);

        int id = -1;
        for(int i = 0; i < restaurantList.size(); i++) {
            if(restaurantList.get(i).getId() == booking.getRestaurant().getId()) {
                id = i;
                break;
            }
        }

        restaurantInput.setAdapter(new ArrayAdapter<Restaurant>(this, android.R.layout.simple_list_item_multiple_choice, restaurantList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    Objects.requireNonNull(inflater);
                    view = inflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);
                }
                Restaurant restaurant = getItem(position);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(restaurant.getName());

                return view;
            }
        });

        restaurantInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                closeContextMenu();
                closeOptionsMenu();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(id >= 0) {
            restaurantInput.setSelection(id);
        }

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                updateDatelabel(selectDateLabel);
            }
        };

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDatelabel(selectDateLabel);

                new TimePickerDialog(CreateBookingActivity.this, time,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true).show();
            }
        };

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateBookingActivity.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        final EditText nameInput = findViewById(R.id.create_restaurant_name);
//        final EditText phoneInput = findViewById(R.id.create_restaurant_phone);
//        final EditText addressInput = findViewById(R.id.create_restaurant_address);
//        final EditText typeInput = findViewById(R.id.create_restaurant_type);
//        final TextView title = findViewById(R.id.create_restaurant_title);
//
//        final Restaurant restaurant = dbHandler.getRestaurant(restaurantId);
//        if (restaurantId > 0) {
//            if (restaurant == null) {
//                finish(); // just exit...
//                return;
//            }
//            title.setText("Rediger en restaurant");
//            nameInput.setText(restaurant.getName());
//            phoneInput.setText(restaurant.getPhone());
//            typeInput.setText(restaurant.getType());
//            addressInput.setText(restaurant.getAddress());
//        }
//
//        Button button = findViewById(R.id.create_restaurant_submit);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (restaurant != null) {
//                    restaurant.setName(nameInput.getText().toString());
//                    restaurant.setPhone(phoneInput.getText().toString());
//                    restaurant.setAddress(addressInput.getText().toString());
//                    restaurant.setType(phoneInput.getText().toString());
//                    dbHandler.updateRestaurant(restaurant);
//                    Toast.makeText(getApplicationContext(), "Endring lagret!", Toast.LENGTH_SHORT).show();
//                } else {
//                    dbHandler.createRestaurant(new Restaurant(
//                            nameInput.getText().toString(),
//                            addressInput.getText().toString(),
//                            phoneInput.getText().toString(),
//                            typeInput.getText().toString()));
//                    Toast.makeText(getApplicationContext(), "Du la til en ny restaurant!", Toast.LENGTH_SHORT).show();
//                }
//                finish();
//            }
//        });
    }

    private void updateDatelabel(TextView textView) {
        textView.setText(sdf.format(calendar.getTime()));
    }
}
