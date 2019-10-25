package app.sagen.restaurantplanner.ui.booking.menus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import app.sagen.restaurantplanner.data.Restaurant;

public class RestaurantSelector extends DialogFragment {

    public interface RestaurantSelectorCallback {
        void onFinish(Restaurant restaurant);
    }

    Restaurant[] restaurants;
    String[] restaurantNames;
    int activeItem;
    RestaurantSelectorCallback callback;

    public RestaurantSelector(List<Restaurant> restaurants, int activeItem, RestaurantSelectorCallback callback) {
        this.restaurants = restaurants.toArray(new Restaurant[0]);
        this.activeItem = activeItem;
        this.callback = callback;

        restaurantNames = new String[restaurants.size()];
        for(int i = 0; i < restaurantNames.length; i++){
            Restaurant restaurant = this.restaurants[i];
            restaurantNames[i] = String.format("%s (%s)", restaurant.getName(), restaurant.getType());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Velg en restaurant");
        builder.setSingleChoiceItems(restaurantNames, activeItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onFinish(restaurants[which]);
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
