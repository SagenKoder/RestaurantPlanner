package app.sagen.restaurantplanner.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class RestaurantPlannerProvider extends ContentProvider {

    // Designet etter dette svaret: https://stackoverflow.com/questions/3814005/best-practices-for-exposing-multiple-tables-using-content-providers-in-android

    public static final String CONTENT_AUTHORITY = "app.sagen.restaurantplanner";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FRIEND = "friend";
    public static final String PATH_RESTAURANT = "restaurant";
    public static final String PATH_BOOKING = "booking";

    public static final int FRIEND_GET_ALL = 100;
    public static final int FRIEND_GET_ONE = 101;
    public static final int RESTAURANT_GET_ALL = 200;
    public static final int RESTAURANT_GET_ONE = 201;
    public static final int BOOKING_GET_ALL = 300;
    public static final int BOOKING_GET_ONE = 301;
    // todo: public static final int BOOKING_GET_ONE_FRIEND = 302;
    // todo: public static final int BOOKING_GET_ONE_RESTAURANTS = 303;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        String content = CONTENT_AUTHORITY;

        URI_MATCHER.addURI(content, PATH_FRIEND, FRIEND_GET_ALL);
        URI_MATCHER.addURI(content, PATH_FRIEND + "/#", FRIEND_GET_ONE);
        URI_MATCHER.addURI(content, PATH_RESTAURANT, RESTAURANT_GET_ALL);
        URI_MATCHER.addURI(content, PATH_RESTAURANT + "/#", RESTAURANT_GET_ONE);
        URI_MATCHER.addURI(content, PATH_BOOKING, BOOKING_GET_ALL);
        URI_MATCHER.addURI(content, PATH_BOOKING + "/#", BOOKING_GET_ONE);
        // todo: URI_MATCHER.addURI(content, PATH_BOOKING + "/#/" + PATH_FRIEND, BOOKING_GET_ONE_FRIEND);
        // todo: URI_MATCHER.addURI(content, PATH_BOOKING + "/#/" + PATH_RESTAURANT, BOOKING_GET_ONE_RESTAURANTS);
    }

    DBHandler dbHandler;

    @Override
    public boolean onCreate() {
        dbHandler = new DBHandler(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case FRIEND_GET_ALL:
                return FriendEntry.CONTENT_TYPE;
            // todo: case BOOKING_GET_ONE_FRIEND:
            case FRIEND_GET_ONE:
                return FriendEntry.CONTENT_ITEM_TYPE;
            // todo: case BOOKING_GET_ONE_RESTAURANTS:
            case RESTAURANT_GET_ALL:
                return RestaurantEntry.CONTENT_TYPE;
            case RESTAURANT_GET_ONE:
                return RestaurantEntry.CONTENT_ITEM_TYPE;
            case BOOKING_GET_ALL:
                return BookingEntry.CONTENT_TYPE;
            case BOOKING_GET_ONE:
                return BookingEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHandler.getDb();
        Cursor cursor;
        switch (URI_MATCHER.match(uri)) {

            case FRIEND_GET_ALL:
                cursor = db.query(
                        FriendEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FRIEND_GET_ONE:
                long id = ContentUris.parseId(uri);
                cursor = db.query(
                        FriendEntry.TABLE_NAME,
                        projection,
                        FriendEntry.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case RESTAURANT_GET_ALL:
                cursor = db.query(
                        RestaurantEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case RESTAURANT_GET_ONE:
                id = ContentUris.parseId(uri);
                cursor = db.query(
                        RestaurantEntry.TABLE_NAME,
                        projection,
                        RestaurantEntry.COLUMN_ID + " =  ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case BOOKING_GET_ALL:
                cursor = db.query(
                        BookingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            case BOOKING_GET_ONE:
                id = ContentUris.parseId(uri);
                cursor = db.query(
                        BookingEntry.TABLE_NAME,
                        projection,
                        BookingEntry.TABLE_NAME + " = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Could not find the URI " + uri);
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHandler.getDb();
        long id;
        Uri insertedUri;

        switch (URI_MATCHER.match(uri)) {
            case FRIEND_GET_ALL:
                id = db.insert(FriendEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    insertedUri = FriendEntry.buildFriendUri(id);
                } else {
                    throw new UnsupportedOperationException("Could not insert row into " + uri);
                }
                break;
            case RESTAURANT_GET_ALL:
                id = db.insert(RestaurantEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    insertedUri = RestaurantEntry.buildRestaurantUri(id);
                } else {
                    throw new UnsupportedOperationException("Could not insert row into " + uri);
                }
                break;
            case BOOKING_GET_ALL:
                id = db.insert(BookingEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    insertedUri = BookingEntry.buildBookingUri(id);
                } else {
                    throw new UnsupportedOperationException("Could not insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("You can not insert data into the table " + uri);
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return insertedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHandler.getDb();
        int rows;

        switch (URI_MATCHER.match(uri)) {
            case FRIEND_GET_ALL:
                rows = db.delete(FriendEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RESTAURANT_GET_ALL:
                rows = db.delete(RestaurantEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKING_GET_ALL:
                rows = db.delete(BookingEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        if (selection == null || rows != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHandler.getDb();
        int rows;

        switch (URI_MATCHER.match(uri)) {
            case FRIEND_GET_ALL:
                rows = db.update(FriendEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case RESTAURANT_GET_ALL:
                rows = db.update(RestaurantEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BOOKING_GET_ALL:
                rows = db.update(BookingEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        if (rows >= 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    public static final class FriendEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FRIEND).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_FRIEND;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_FRIEND;

        public static final String TABLE_NAME = Friend.TABLE_FRIENDS;
        public static final String COLUMN_ID = Friend.COLUMN_ID;
        public static final String COLUMN_NAME = Friend.COLUMN_NAME;
        public static final String COLUMN_PHONE = Friend.COLUMN_PHONE;

        public static Uri buildFriendUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class RestaurantEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESTAURANT).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_RESTAURANT;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_RESTAURANT;

        public static final String TABLE_NAME = Restaurant.TABLE_RESTAURANTS;
        public static final String COLUMN_ID = Restaurant.COLUMN_ID;
        public static final String COLUMN_NAME = Restaurant.COLUMN_NAME;
        public static final String COLUMN_PHONE = Restaurant.COLUMN_PHONE;
        public static final String COLUMN_ADDRESS = Restaurant.COLUMN_ADDRESS;
        public static final String COLUMN_TYPE = Restaurant.COLUMN_TYPE;

        public static Uri buildRestaurantUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class BookingEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKING).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_BOOKING;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_BOOKING;

        public static final String TABLE_NAME = Booking.TABLE_BOOKING;
        public static final String COLUMN_ID = Booking.COLUMN_ID;
        public static final String COLUMN_DATE = Booking.COLUMN_DATE;
        public static final String COLUMN_RESTAURANT_ID = Booking.COLUMN_RESTAURANT_ID;

        public static Uri buildBookingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
