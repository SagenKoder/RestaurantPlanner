package app.sagen.restaurantplanner.preferences;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragment;
import androidx.preference.PreferenceDialogFragmentCompat;

import app.sagen.restaurantplanner.R;

public class TimePreferenceDialogFragment extends PreferenceDialogFragment {

    private static final String TAG = "TimePreferenceDialogFra";
    
    TimePicker timePicker;

    public static TimePreferenceDialogFragment newInstance(String key) {
        Log.d(TAG, String.format("newInstance: key = %s", key));
        final TimePreferenceDialogFragment fragment = new TimePreferenceDialogFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        timePicker = view.findViewById(R.id.edit);

        DialogPreference preference = getPreference();
        if (preference instanceof TimePreference) {
            TimePreference timePreference = ((TimePreference) preference);
            Log.d(TAG, String.format("onBindDialogView: %s %s", timePreference.getHour(), timePreference.getMinute()));
            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(timePreference.getHour());
            timePicker.setCurrentMinute(timePreference.getMinute());
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();

            DialogPreference preference = getPreference();
            if (preference instanceof TimePreference) {
                TimePreference timePreference = ((TimePreference) preference);
                String hourString = hour < 10 ? "0" + hour : String.valueOf(hour);
                String minuteString = minute < 10 ? "0" + minute : String.valueOf(minute);
                String persistentString = String.format("%s:%s", hourString, minuteString);

                Log.d(TAG, String.format("onDialogClosed: time = %s", persistentString));

                if (timePreference.callChangeListener(persistentString)) {
                    timePreference.setTime(hour, minute);
                }
            }
        }
    }
}
