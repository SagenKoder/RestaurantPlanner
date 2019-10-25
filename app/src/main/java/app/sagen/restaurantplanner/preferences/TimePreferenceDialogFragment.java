package app.sagen.restaurantplanner.preferences;

import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import app.sagen.restaurantplanner.R;

public class TimePreferenceDialogFragment extends PreferenceDialogFragmentCompat {

    TimePicker timePicker;

    public static TimePreferenceDialogFragment newInstance(String key) {
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
            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(timePreference.getHour());
            timePicker.setCurrentMinute(timePreference.getMinute());
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int hours = timePicker.getCurrentHour();
            int minutes = timePicker.getCurrentMinute();

            DialogPreference preference = getPreference();
            if (preference instanceof TimePreference) {
                TimePreference timePreference = ((TimePreference) preference);

                if (timePreference.callChangeListener(hours < 10 ? String.format("0%s:%s", hours, minutes) : String.format("%s:%s", hours, minutes))) {
                    timePreference.setTime(hours, minutes);
                }
            }
        }
    }
}
