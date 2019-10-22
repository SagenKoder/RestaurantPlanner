package app.sagen.restaurantplanner.ui.restaurants;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.Restaurant;
import app.sagen.restaurantplanner.db.DBHandler;

public class RestaurantsFragment extends ListFragment {

    private static final String TAG = "RestaurantsFragment";
    
    RestaurantListAdapter restaurantListAdapter;
    DBHandler db;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = new DBHandler(getContext());
        restaurantListAdapter = new RestaurantListAdapter(getActivity(), db.getAllRestaurants());
        setListAdapter(restaurantListAdapter);
        setEmptyText("Du har ingen restauranter i listen. Opprett en ny venn med + knappen!");
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
        Restaurant restaurant = restaurantListAdapter.getItem(position);

        RestaurantAdapterMenuItemClickListener listener = new RestaurantAdapterMenuItemClickListener(restaurant, v);
        PopupMenu popup = new PopupMenu(getContext(), v, Gravity.END, 0, R.style.PopupMenuMoreCentralized);
        popup.setOnMenuItemClickListener(listener);
        popup.setOnDismissListener(listener);
        popup.inflate(R.menu.friend_item_menu);
        popup.show();
    }

    /**
     * Handles events from the context menu
     */
    public class RestaurantAdapterMenuItemClickListener implements PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener {
        Restaurant restaurant;
        View view;

        public RestaurantAdapterMenuItemClickListener(Restaurant restaurant, View view) {
            this.restaurant = restaurant;
            this.view = view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            view.setBackgroundColor(Color.WHITE);

            switch (item.getItemId()) {
                case R.id.edit:
                    Log.d(TAG, "onMenuItemClick: CLICKED EDIT");
                    onButtonShowPopupWindowClick(getView());
                    return true;
                case R.id.delete:
                    Log.d(TAG, "onMenuItemClick: CLICKED DELETE");
                    db.deleteRestaurant(restaurant);
                    restaurantListAdapter.clear();
                    restaurantListAdapter.addAll(db.getAllRestaurants());
                    restaurantListAdapter.notifyDataSetChanged();
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

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        getContext();
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.layout_create_friend, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
    
}
