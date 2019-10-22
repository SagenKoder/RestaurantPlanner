package app.sagen.restaurantplanner.ui.restaurants;

import java.util.List;

import app.sagen.restaurantplanner.data.Friend;

public interface RestaurantListAdapterListener {
    void onDeleteRestaurant(Friend friend, RestaurantListAdapter friendsListAdapter);

    void onEditRestaurant(Friend friend, RestaurantListAdapter friendsListAdapter);

    List<Friend> getAllRestaurant(RestaurantListAdapter friendsListAdapter);
}
