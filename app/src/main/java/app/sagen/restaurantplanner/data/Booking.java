package app.sagen.restaurantplanner.data;

import java.util.Date;
import java.util.List;

public class Booking {

    public static final String TABLE_BOOKING = "booking";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "booking_date";
    public static final String COLUMN_RESTAURANT_ID = "restaurant_id";

    public static final String TABLE_BOOKING_FRIEND = "booking_has_friend";
    public static final String COLUMN_JOIN_BOOKING_ID = "booking_id";
    public static final String COLUMN_JOIN_FRIEND_ID = "friend_id";

    long id;
    Date dateTime;
    Restaurant restaurant;
    List<Friend> friends;

    public Booking() {
    }

    public Booking(Date dateTime, Restaurant restaurant, List<Friend> friends) {
        this.dateTime = dateTime;
        this.restaurant = restaurant;
        this.friends = friends;
    }

    public Booking(long id, Date dateTime, Restaurant restaurant, List<Friend> friends) {
        this.id = id;
        this.dateTime = dateTime;
        this.restaurant = restaurant;
        this.friends = friends;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }
}
