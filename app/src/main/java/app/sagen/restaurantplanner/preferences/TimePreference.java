package app.sagen.restaurantplanner.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

import app.sagen.restaurantplanner.R;

public class TimePreference extends DialogPreference {

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
        this.hour = hour;
        this.minute = minute;
        persistString((hour < 10 ? String.format("0%s:%s", hour, minute) : String.format("%s:%s", hour, minute)));
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray array, int index) {
        return array.getString(index);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        String time = (String)defaultValue;
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
