package app.sagen.restaurantplanner.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

import app.sagen.restaurantplanner.R;

public class TimePreference extends DialogPreference {

    private static final String TAG = "TimePreference";

    private int hour, minute;

    public TimePreference(Context context) {
        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setTime(int hour, int minute) {
        Log.d(TAG, String.format("setTime: %s %s", hour, minute));
        this.hour = hour;
        this.minute = minute;
        String hourString = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minuteString = minute < 10 ? "0" + minute : String.valueOf(minute);
        String persistentString = String.format("%s:%s", hourString, minuteString);
        Log.d(TAG, "setTime: persistentstring=" + persistentString);
        persistString(persistentString);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray array, int index) {
        String defaultValue = array.getString(index);
        Log.d(TAG, "onGetDefaultValue: DefaultValue=" + defaultValue);
        return defaultValue;
    }



    @Override
    protected void onSetInitialValue(boolean restore, @Nullable Object defaultValue) {
        String time;
        if(restore) {
            time = getPersistedString((String)defaultValue);
        } else {
            time = (String) defaultValue;
        }
        try {
            String[] split = time.trim().split(":");
            int hour = Math.min(Math.max(Integer.parseInt(split[0].trim()), 0), 23);
            int minute = Math.min(Math.max(Integer.parseInt(split[1].trim()), 0), 59);
            setTime(hour, minute);
        } catch (Exception e) {
            setTime(8, 0);
        }
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.preferences_timedialog;
    }

}
