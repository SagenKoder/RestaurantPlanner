package app.sagen.restaurantplanner.ui.booking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.Booking;

public class BookingListAdapter extends ArrayAdapter<Booking> {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public BookingListAdapter(@NonNull Context context) {
        super(context, -1);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Objects.requireNonNull(inflater);
            view = inflater.inflate(R.layout.fragment_booking_item, parent, false);
        }

        final Booking booking = getItem(position);

        if (booking != null) {
            TextView restaurantName = view.findViewById(R.id.booking_restaurant);
            TextView dateTime = view.findViewById(R.id.booking_date);
            TextView friends = view.findViewById(R.id.booking_friends);

            restaurantName.setText(String.format("%s (%s)", booking.getRestaurant().getName(), booking.getRestaurant().getType()));
            dateTime.setText(sdf.format(booking.getDateTime()));
            friends.setText(String.valueOf(booking.getFriends().size()));
        }

        return view;
    }
}
