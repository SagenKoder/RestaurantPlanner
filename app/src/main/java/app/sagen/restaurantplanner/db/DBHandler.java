package app.sagen.restaurantplanner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import app.sagen.restaurantplanner.data.Booking;
import app.sagen.restaurantplanner.data.Friend;
import app.sagen.restaurantplanner.data.Restaurant;

public class DBHandler extends SQLiteOpenHelper {

    private static final String TAG = "DBHandler";

    private static String DATABASE_NAME = "RestaurantPlanner";
    private static int DATABASE_VERSION = 21;

    public DBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "onCreate: BEGINNING...");

        String createFriendTable = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s VARCHAR(100), %s CHAR(8));",
                Friend.TABLE_FRIENDS,
                Friend.COLUMN_ID,
                Friend.COLUMN_NAME,
                Friend.COLUMN_PHONE);

        String createRestaurantTable = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s VARCHAR(100), %s VARCHAR(100), %s CHAR(8), %s VARCHAR(100));",
                Restaurant.TABLE_RESTAURANTS,
                Restaurant.COLUMN_ID,
                Restaurant.COLUMN_NAME,
                Restaurant.COLUMN_ADDRESS,
                Restaurant.COLUMN_PHONE,
                Restaurant.COLUMN_TYPE);

        String createBookingTable = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s DATETIME, %s INTEGER, FOREIGN KEY(%s) REFERENCES %s(%s));",
                Booking.TABLE_BOOKING,
                Booking.COLUMN_ID,
                Booking.COLUMN_DATE,
                Booking.COLUMN_RESTAURANT_ID,
                Booking.COLUMN_RESTAURANT_ID,
                Restaurant.TABLE_RESTAURANTS,
                Restaurant.COLUMN_ID);

        String createBookingHasFriendTable = String.format("CREATE TABLE %s (%s INTEGER, %s INTEGER,FOREIGN KEY (%s) REFERENCES %s(%s),FOREIGN KEY (%s) REFERENCES %s(%s),UNIQUE(%s, %s));",
                Booking.TABLE_BOOKING_FRIEND,
                Booking.COLUMN_JOIN_BOOKING_ID,
                Booking.COLUMN_JOIN_FRIEND_ID,
                Booking.COLUMN_JOIN_BOOKING_ID,
                Booking.TABLE_BOOKING,
                Booking.COLUMN_ID,
                Booking.COLUMN_JOIN_FRIEND_ID,
                Friend.TABLE_FRIENDS,
                Friend.COLUMN_ID,
                Booking.COLUMN_JOIN_BOOKING_ID,
                Booking.COLUMN_JOIN_FRIEND_ID);

        Log.d(TAG, "onCreate: SQL " + createFriendTable);
        db.execSQL(createFriendTable);


        Log.d(TAG, "onCreate: SQL " + createRestaurantTable);
        db.execSQL(createRestaurantTable);

        Log.d(TAG, "onCreate: SQL " + createBookingTable);
        db.execSQL(createBookingTable);

        Log.d(TAG, "onCreate: SQL " + createBookingHasFriendTable);
        db.execSQL(createBookingHasFriendTable);

        //seedFriends(db);
        seedRestaurants(db);

        Restaurant restaurant = new Restaurant("MyFancyRestaurant", "Nils Olav Gate 3", "12345678", "Bar");
        createRestaurant(db, restaurant);

        Booking booking = new Booking();
        booking.setDateTime(new Date());
        booking.setRestaurant(restaurant); // same restaurant
        booking.setFriends(seedFriends(db)); // created 5 friends
        createBooking(db, booking);

        Booking booking2 = new Booking();
        booking2.setDateTime(new Date());
        booking2.setRestaurant(restaurant); // same restaurant
        booking2.setFriends(seedFriends(db)); // created 5 friends
        createBooking(db, booking2);
    }

    public List<Friend> seedFriends(SQLiteDatabase db) {
        List<Friend> friends = new ArrayList<>();
        String names =
                "    Mistie Mcaleer\n" +
                        "    Rebeca Renick\n" +
                        "    Yesenia Yardley\n" +
                        "    Eduardo Eisenbarth";
        Random random = new Random();
        for (String name : names.trim().split("\n")) {
            friends.add(new Friend(name.trim(), String.valueOf(random.nextInt(89999999) + 10000000)));
        }

        for (Friend friend : friends) createFriend(db, friend);

        return friends;
    }

    public void seedRestaurants(SQLiteDatabase db) {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(new Restaurant("Greasy Burgers", "Kaj Munksgate 4", "", "BurgerJoint"));
        restaurants.add(new Restaurant("Greasy Burgers", "Kaj Munksgate 4", "", "BurgerJoint"));
        restaurants.add(new Restaurant("Greasy Burgers", "Kaj Munksgate 4", "", "BurgerJoint"));
        restaurants.add(new Restaurant("Greasy Burgers", "Kaj Munksgate 4", "", "BurgerJoint"));
        restaurants.add(new Restaurant("Greasy Burgers", "Kaj Munksgate 4", "", "BurgerJoint"));
        restaurants.add(new Restaurant("Greasy Burgers", "Kaj Munksgate 4", "", "BurgerJoint"));
        restaurants.add(new Restaurant("Greasy Burgers", "Kaj Munksgate 4", "", "BurgerJoint"));
        restaurants.add(new Restaurant("Greasy Burgers", "Kaj Munksgate 4", "", "BurgerJoint"));
        restaurants.add(new Restaurant("Greasy Burgers", "Kaj Munksgate 4", "", "BurgerJoint"));

        Random random = new Random();
        for (Restaurant restaurant : restaurants) {
            restaurant.setPhone(String.valueOf(random.nextInt(89999999) + 10000000));
            createRestaurant(db, restaurant);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropFriends = "DROP TABLE IF EXISTS " + Friend.TABLE_FRIENDS;
        String dropRestaurants = "DROP TABLE IF EXISTS " + Restaurant.TABLE_RESTAURANTS;
        String dropBooking = "DROP TABLE IF EXISTS " + Booking.TABLE_BOOKING;
        String dropBookingFriend = "DROP TABLE IF EXISTS " + Booking.TABLE_BOOKING_FRIEND;

        Log.d(TAG, "onUpgrade: SQL " + dropFriends);
        db.execSQL(dropFriends);
        Log.d(TAG, "onUpgrade: SQL " + dropRestaurants);
        db.execSQL(dropRestaurants);
        Log.d(TAG, "onUpgrade: SQL " + dropBooking);
        db.execSQL(dropBooking);
        Log.d(TAG, "onUpgrade: SQL " + dropBookingFriend);
        db.execSQL(dropBookingFriend);

        onCreate(db);
    }


    /* FRIEND */

    public void createFriend(Friend friend) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            createFriend(db, friend);

            Log.d(TAG, "createFriend: FRIEND " + friend);
        } catch (SQLException e) {
            Log.e(TAG, "createFriend: Could not create friend", e);
        }
    }

    public void createFriend(SQLiteDatabase db, Friend friend) {
        ContentValues values = new ContentValues();
        values.put(Friend.COLUMN_NAME, friend.getName());
        values.put(Friend.COLUMN_PHONE, friend.getPhone());
        long id = db.insert(Friend.TABLE_FRIENDS, null, values);
        friend.setId(id);
        Log.d(TAG, "createFriend: FRIEND " + friend);
    }

    public void updateFriend(Friend friend) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(Friend.COLUMN_NAME, friend.getName());
            values.put(Friend.COLUMN_PHONE, friend.getPhone());

            Log.d(TAG, "updateFriend: FRIEND " + friend);

            db.update(
                    Friend.TABLE_FRIENDS,
                    values,
                    String.format("%s = ?", Friend.COLUMN_ID),
                    new String[]{String.valueOf(friend.getId())});
        } catch (SQLException e) {
            Log.e(TAG, "createFriend: Could not create friend", e);
        }
    }

    public void deleteFriend(Friend friend) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Log.d(TAG, "deleteFriend: FRIEND " + friend);

            // delete from join table
            db.delete(Booking.TABLE_BOOKING_FRIEND,
                    Booking.COLUMN_JOIN_FRIEND_ID + " = ?",
                    new String[]{String.valueOf(friend.getId())});

            // delete from friend table
            db.delete(
                    Friend.TABLE_FRIENDS,
                    Friend.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(friend.getId())});
        }
    }

    public List<Friend> getAllFriends() {
        List<Friend> friends = new ArrayList<>();
        String select = String.format("SELECT * FROM %s ORDER BY %s", Friend.TABLE_FRIENDS, Friend.COLUMN_NAME);

        Log.d(TAG, "getAllFriends: SQL " + select);

        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery(select, null)) {

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    Friend friend = new Friend();
                    friend.setId(cursor.getLong(0));
                    friend.setName(cursor.getString(1));
                    friend.setPhone(cursor.getString(2));
                    friends.add(friend);

                    cursor.moveToNext();

                }
            }
        }

        Collections.sort(friends, new Comparator<Friend>() {
            @Override
            public int compare(Friend o1, Friend o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        Log.d(TAG, "getAllFriends: FRIENDS " + friends);

        return friends;
    }

    public Friend getFriend(long id) {
        String select = String.format("SELECT * FROM %s WHERE %s = %s", Friend.TABLE_FRIENDS, Friend.COLUMN_ID, id);

        Log.d(TAG, "getFriend: SQL " + select);

        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery(select, null)) {

            if (cursor.moveToFirst() && !cursor.isAfterLast()) {
                Friend friend = new Friend();
                friend.setId(cursor.getLong(0));
                friend.setName(cursor.getString(1));
                friend.setPhone(cursor.getString(2));

                return friend;
            }
            return null;
        }
    }

    /* /FRIEND */



    /* RESTAURANT */

    public void createRestaurant(Restaurant restaurant) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            createRestaurant(db, restaurant);
        } catch (SQLException e) {
            Log.e(TAG, "createRestaurant: Could not create restaurant", e);
        }
    }

    public void createRestaurant(SQLiteDatabase db, Restaurant restaurant) {
        ContentValues values = new ContentValues();

        values.put(Restaurant.COLUMN_NAME, restaurant.getName());
        values.put(Restaurant.COLUMN_ADDRESS, restaurant.getAddress());
        values.put(Restaurant.COLUMN_PHONE, restaurant.getPhone());
        values.put(Restaurant.COLUMN_TYPE, restaurant.getType());

        long id = db.insert(Restaurant.TABLE_RESTAURANTS, null, values);
        restaurant.setId(id);
    }

    public void updateRestaurant(Restaurant restaurant) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(Restaurant.COLUMN_NAME, restaurant.getName());
            values.put(Restaurant.COLUMN_ADDRESS, restaurant.getAddress());
            values.put(Restaurant.COLUMN_PHONE, restaurant.getPhone());
            values.put(Restaurant.COLUMN_TYPE, restaurant.getType());

            db.update(
                    Restaurant.TABLE_RESTAURANTS,
                    values,
                    String.format("%s = ?", Restaurant.COLUMN_ID),
                    new String[]{String.valueOf(restaurant.getId())});
        } catch (SQLException e) {
            Log.e(TAG, "updateRestaurant: Could not create restaurant", e);
        }
    }

    public void deleteRestaurant(Restaurant restaurant) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Log.d(TAG, "deleteRestaurant: RESTAURANT " + restaurant);

            // delete from booking table
            db.delete(Booking.TABLE_BOOKING,
                    Booking.COLUMN_RESTAURANT_ID + " = ?",
                    new String[]{String.valueOf(restaurant.getId())});

            // delete from restaurant table
            db.delete(
                    Restaurant.TABLE_RESTAURANTS,
                    Restaurant.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(restaurant.getId())});
        }
    }

    public Restaurant getRestaurant(long id) {
        String select = String.format("SELECT * FROM %s WHERE %s = %s", Restaurant.TABLE_RESTAURANTS, Restaurant.COLUMN_ID, id);

        Log.d(TAG, "getRestaurant: SQL " + select);

        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery(select, null)) {

            if (cursor.moveToFirst() && !cursor.isAfterLast()) {

                Restaurant restaurant = new Restaurant();
                restaurant.setId(cursor.getLong(0));
                restaurant.setName(cursor.getString(1));
                restaurant.setAddress(cursor.getString(2));
                restaurant.setPhone(cursor.getString(3));
                restaurant.setType(cursor.getString(4));

                return restaurant;

            }

            return null;
        }
    }

    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String select = String.format("SELECT * FROM %s ORDER BY %s", Restaurant.TABLE_RESTAURANTS, Restaurant.COLUMN_NAME);

        Log.d(TAG, "getAllRestaurants: SQL " + select);

        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery(select, null)) {

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    Restaurant restaurant = new Restaurant();
                    restaurant.setId(cursor.getLong(0));
                    restaurant.setName(cursor.getString(1));
                    restaurant.setAddress(cursor.getString(2));
                    restaurant.setPhone(cursor.getString(3));
                    restaurant.setType(cursor.getString(4));

                    restaurants.add(restaurant);

                    cursor.moveToNext();

                }
            }
        }

        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        Log.d(TAG, "getAllRestaurants: RESTAURANTS " + restaurants);

        return restaurants;
    }

    /* /RESTAURANT */

    /* BOOKING */
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());

    public void createBooking(Booking booking) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            createBooking(db, booking);
        } catch (SQLException e) {
            Log.e(TAG, "createBooking: ", e);
        }
    }

    public Booking createBooking(SQLiteDatabase db, Booking booking) {
        ContentValues bookingValues = new ContentValues();

        bookingValues.put(Booking.COLUMN_DATE, sdf.format(booking.getDateTime()));
        bookingValues.put(Booking.COLUMN_RESTAURANT_ID, booking.getRestaurant().getId());
        long id = db.insert(Booking.TABLE_BOOKING, null, bookingValues);
        booking.setId(id);

        db.beginTransaction();
        try {

            ContentValues joinValues = new ContentValues();
            for (Friend friend : booking.getFriends()) {
                joinValues.put(Booking.COLUMN_JOIN_BOOKING_ID, booking.getId());
                joinValues.put(Booking.COLUMN_JOIN_FRIEND_ID, friend.getId());

                db.insert(Booking.TABLE_BOOKING_FRIEND, null, joinValues);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        return booking;
    }

    public void updateBookingDateOrRestaurant(Booking booking) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(Booking.COLUMN_DATE, sdf.format(booking.getDateTime()));
            values.put(Booking.COLUMN_RESTAURANT_ID, booking.getRestaurant().getId());

            db.update(
                    Booking.TABLE_BOOKING,
                    values,
                    String.format("%s = ?", Booking.COLUMN_ID),
                    new String[]{String.valueOf(booking.getId())});
        } catch (SQLException e) {
            Log.e(TAG, "updateBookingDateOrRestaurant: ", e);
        }
    }

    public void updateBookingAddFriend(Booking booking, Friend friend) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(Booking.COLUMN_JOIN_BOOKING_ID, booking.getId());
            values.put(Booking.COLUMN_JOIN_FRIEND_ID, friend.getId());

            db.insert(Booking.TABLE_BOOKING_FRIEND, null, values);
        } catch (SQLException e) {
            Log.e(TAG, "updateBookingAddFriend: ", e);
        }
    }

    public void updateBookingRemoveFriend(Booking booking, Friend friend) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(Booking.TABLE_BOOKING_FRIEND,
                    String.format("%s = ? AND %s = ?", Booking.COLUMN_JOIN_BOOKING_ID, Booking.COLUMN_JOIN_FRIEND_ID),
                    new String[]{String.valueOf(booking.getId()), String.valueOf(friend.getId())});
        } catch (SQLException e) {
            Log.e(TAG, "updateBookingRemoveFriend: ", e);
        }
    }

    public void deleteBooking(Booking booking) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Log.d(TAG, "deleteBooking: BOOKING " + booking);

            // delete from join table
            db.delete(Booking.TABLE_BOOKING_FRIEND,
                    Booking.COLUMN_JOIN_BOOKING_ID + " = ?",
                    new String[]{String.valueOf(booking.getId())});

            // delete from booking table
            db.delete(
                    Booking.TABLE_BOOKING,
                    Booking.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(booking.getId())});
        }
    }

    public List<Friend> getFriendBookings(Booking booking) {
        List<Friend> friends = new ArrayList<>();
        String select = String.format("SELECT %s.* FROM %s, %s WHERE %s.%s = %s.%s AND %s.%s = " + booking.getId(),
                Friend.TABLE_FRIENDS,
                Friend.TABLE_FRIENDS,
                Booking.TABLE_BOOKING_FRIEND,
                Booking.TABLE_BOOKING_FRIEND, Booking.COLUMN_JOIN_FRIEND_ID,
                Friend.TABLE_FRIENDS, Friend.COLUMN_ID,
                Booking.TABLE_BOOKING_FRIEND, Booking.COLUMN_JOIN_BOOKING_ID);

        Log.d(TAG, "getFriendBookings: SQL " + select);

        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery(select, null)) {

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    Friend friend = new Friend();
                    friend.setId(cursor.getLong(0));
                    friend.setName(cursor.getString(1));
                    friend.setPhone(cursor.getString(2));

                    friends.add(friend);

                    cursor.moveToNext();
                }
            }
        }

        Collections.sort(friends, new Comparator<Friend>() {
            @Override
            public int compare(Friend o1, Friend o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        Log.d(TAG, "getFriendBookings: FRIENDS " + friends);

        return friends;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String select = String.format("SELECT * FROM %s", Booking.TABLE_BOOKING);

        Log.d(TAG, "getAllBookings: SQL " + select);

        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery(select, null)) {

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    try {
                        Booking booking = new Booking();
                        booking.setId(cursor.getLong(0));
                        booking.setDateTime(sdf.parse(cursor.getString(1)));
                        booking.setRestaurant(getRestaurant(cursor.getLong(2)));
                        booking.setFriends(getFriendBookings(booking));
                        bookings.add(booking);

                    } catch (ParseException e) {
                        Log.e(TAG, "getAllBookings: ", e);
                    }

                    cursor.moveToNext();
                }
            }
        }

        Collections.sort(bookings, new Comparator<Booking>() {
            @Override
            public int compare(Booking o1, Booking o2) {
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        });

        Log.d(TAG, "getAllBookings: BOOKINGS " + bookings);

        return bookings;
    }

    public Booking getBooking(long id) {
        String select = String.format("SELECT * FROM %s WHERE %s = %s", Booking.TABLE_BOOKING, Booking.COLUMN_ID, id);

        Log.d(TAG, "getBooking: SQL " + select);

        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery(select, null)) {

            if (cursor.moveToFirst() && !cursor.isAfterLast()) {

                try {
                    Booking booking = new Booking();
                    booking.setId(cursor.getLong(0));
                    booking.setDateTime(sdf.parse(cursor.getString(1)));
                    booking.setRestaurant(getRestaurant(cursor.getLong(2)));
                    booking.setFriends(getFriendBookings(booking));
                    return booking;

                } catch (ParseException e) {
                    Log.e(TAG, "getBooking: ", e);
                }
            }
        }

        return null;
    }

    /* /BOOKING */
}
