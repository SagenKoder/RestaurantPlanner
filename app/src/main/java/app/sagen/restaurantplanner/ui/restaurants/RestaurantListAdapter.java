package app.sagen.restaurantplanner.ui.restaurants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Objects;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.Restaurant;

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

    public RestaurantListAdapter(@NonNull Context context) {
        super(context, -1);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Objects.requireNonNull(inflater);
            view = inflater.inflate(R.layout.fragment_restaurant_item, parent, false);
        }

        final Restaurant restaurant = getItem(position);

        if (restaurant != null) {
            TextView name = view.findViewById(R.id.restaurant_name);
            TextView type = view.findViewById(R.id.restaurant_type);
            TextView address = view.findViewById(R.id.restaurant_address);
            TextView phone = view.findViewById(R.id.restaurant_phone);

            name.setText(restaurant.getName());
            type.setText(restaurant.getType());
            address.setText(restaurant.getAddress());
            phone.setText(restaurant.getPhone());
        }

        return view;
    }
}
