package app.sagen.restaurantplanner.ui.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.Booking;
import app.sagen.restaurantplanner.data.Friend;
import app.sagen.restaurantplanner.data.Restaurant;
import app.sagen.restaurantplanner.data.DBHandler;
import app.sagen.restaurantplanner.ui.booking.menus.FriendsSelector;
import app.sagen.restaurantplanner.ui.booking.menus.RestaurantSelector;

public class CreateBookingActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());

    Booking booking;
    List<Friend> addFriends = new ArrayList<>();
    List<Friend> removeFriends = new ArrayList<>();

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

        final boolean edit;
        if (getIntent().hasExtra("editBookingId")) {
            edit = true;
            long id = getIntent().getLongExtra("editBookingId", -1);
            Booking booking = dbHandler.getBooking(id);
            this.booking = booking;
        } else {
            edit = false;
            this.booking = new Booking();
        }

        List<Restaurant> restaurantList = dbHandler.getAllRestaurants();
        if(booking.getRestaurant() == null) booking.setRestaurant(restaurantList.get(0));

        List<Friend> friendList = dbHandler.getAllFriends();
        if(booking.getFriends() == null) booking.setFriends(new ArrayList<Friend>());

        final TextView restaurantInputLabel = findViewById(R.id.create_booking_restaurant_label);
        restaurantInputLabel.setText(booking.getRestaurant().getName());

        final Button restaurantInput = findViewById(R.id.create_booking_restaurant);
        final Button selectDate = findViewById(R.id.create_booking_date);
        final TextView selectDateLabel = findViewById(R.id.create_booking_date_label);
        final Button friendsButton = findViewById(R.id.create_booking_friend);
        final TextView friendLabel = findViewById(R.id.create_booking_friend_label);
        friendLabel.setText(String.format("%s venner valgt", booking.getFriends().size()));
        final Button submitButton = findViewById(R.id.create_booking_submit);

        updateDatelabel(selectDateLabel);

        // todo: https://stackoverflow.com/questions/20017329/android-select-items-in-a-multi-select-listview-inside-alertdialog

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                updateDatelabel(selectDateLabel);
                booking.setDateTime(calendar.getTime());
                dbHandler.updateBookingDateOrRestaurant(booking);
            }
        };

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDatelabel(selectDateLabel);
                booking.setDateTime(calendar.getTime());
                dbHandler.updateBookingDateOrRestaurant(booking);

                new TimePickerDialog(CreateBookingActivity.this, time,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true).show();
            }
        };

        // open date selector
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateBookingActivity.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // create restaurantselector
        final RestaurantSelector restaurantSelector = new RestaurantSelector(restaurantList, -1, new RestaurantSelector.RestaurantSelectorCallback() {
            @Override
            public void onFinish(Restaurant restaurant) {
                booking.setRestaurant(restaurant);
                dbHandler.updateBookingDateOrRestaurant(booking);
                restaurantInputLabel.setText(booking.getRestaurant().getName());
            }
        });

        // open restaurant selector
        restaurantInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantSelector.show(fragmentManager, "restaurantSelector");
            }
        });

        final FriendsSelector friendsSelector = new FriendsSelector(friendList, booking.getFriends(), new FriendsSelector.FriendSelectorCallback() {
            @Override
            public void onFinish(List<Friend> addFriends, List<Friend> removeFriends) {
                booking.getFriends().removeAll(removeFriends);
                booking.getFriends().addAll(addFriends);

                CreateBookingActivity.this.removeFriends = removeFriends;
                CreateBookingActivity.this.addFriends = addFriends;

                friendLabel.setText(String.format("%s venner valgt", booking.getFriends().size()));
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsSelector.show(fragmentManager, "friendsSelector");
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit) {
                    dbHandler.updateBookingDateOrRestaurant(booking);
                    for(Friend remove : removeFriends) dbHandler.updateBookingRemoveFriend(booking, remove);
                    for(Friend add : addFriends) dbHandler.updateBookingAddFriend(booking, add);
                } else {
                    dbHandler.createBooking(booking);
                    for(Friend remove : removeFriends) dbHandler.updateBookingRemoveFriend(booking, remove);
                    for(Friend add : addFriends) dbHandler.updateBookingAddFriend(booking, add);
                }
                finish();
            }
        });
    }

    private void updateDatelabel(TextView textView) {
        textView.setText(sdf.format(calendar.getTime()));
    }
}
