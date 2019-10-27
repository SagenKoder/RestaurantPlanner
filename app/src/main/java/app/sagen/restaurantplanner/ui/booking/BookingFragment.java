package app.sagen.restaurantplanner.ui.booking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.Booking;
import app.sagen.restaurantplanner.data.DBHandler;

public class BookingFragment extends ListFragment {

    private static final String TAG = "BookingFragment";
    BookingListAdapter bookingListAdapter;
    DBHandler db;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = new DBHandler(getContext());
        bookingListAdapter = new BookingListAdapter(getActivity());
        setListAdapter(bookingListAdapter);
        setEmptyText("Du har ingen bookinger i listen. Opprett en ny med + knappen!");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.fragment_booking, container, false);
        parent.addView(v, 0);

        FloatingActionButton fab = parent.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: BOOKINGER");
                if(db.getAllRestaurants().size() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(BookingFragment.this.getActivity()).create();
                    alertDialog.setTitle("Ingen restauranter");
                    alertDialog.setMessage("Gå til restaurant panelet for å legge til en restaurant først.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }
                Intent intent = new Intent(getContext(), CreateBookingActivity.class);
                startActivity(intent);
            }
        });
        return parent;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
        db = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Booking booking = bookingListAdapter.getItem(position);

        RestaurantAdapterMenuItemClickListener listener = new RestaurantAdapterMenuItemClickListener(booking, v);
        PopupMenu popup = new PopupMenu(getContext(), v, Gravity.END, 0, R.style.PopupMenuMoreCentralized);
        popup.setOnMenuItemClickListener(listener);
        popup.setOnDismissListener(listener);
        popup.inflate(R.menu.item_context_menu);
        popup.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    private void updateList() {
        bookingListAdapter.clear();
        bookingListAdapter.addAll(db.getAllBookings());
        bookingListAdapter.notifyDataSetChanged();
    }


    /**
     * Handles events from the context menu
     */
    public class RestaurantAdapterMenuItemClickListener implements PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener {
        Booking booking;
        View view;

        public RestaurantAdapterMenuItemClickListener(Booking booking, View view) {
            this.booking = booking;
            this.view = view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            view.setBackgroundColor(Color.WHITE);

            switch (item.getItemId()) {
                case R.id.edit:
                    Log.d(TAG, "onMenuItemClick: CLICKED EDIT");
                    Intent intent = new Intent(getContext(), CreateBookingActivity.class);
                    intent.putExtra("editBookingId", booking.getId());
                    startActivity(intent);
                    return true;
                case R.id.delete:
                    Log.d(TAG, "onMenuItemClick: CLICKED DELETE");
                    db.deleteBooking(booking);
                    updateList();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDismiss(PopupMenu menu) {
            view.setBackgroundColor(Color.WHITE);
        }
    }
}
