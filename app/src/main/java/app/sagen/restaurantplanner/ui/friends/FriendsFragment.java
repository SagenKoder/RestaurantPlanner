package app.sagen.restaurantplanner.ui.friends;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.Friend;
import app.sagen.restaurantplanner.db.DBHandler;

public class FriendsFragment extends ListFragment {

    private static final String TAG = "FriendsFragment";
    
    FriendsListAdapter friendsListAdapter;
    DBHandler db;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = new DBHandler(getContext());
        friendsListAdapter = new FriendsListAdapter(getActivity(), db.getAllFriends());
        setListAdapter(friendsListAdapter);
        setEmptyText("Du har ingen venner i listen. Opprett en ny venn med + knappen!");
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
        Friend friend = friendsListAdapter.getItem(position);

        FriendAdapterMenuItemClickListener listener = new FriendAdapterMenuItemClickListener(friend, v);
        PopupMenu popup = new PopupMenu(getContext(), v, Gravity.END, 0, R.style.PopupMenuMoreCentralized);
        popup.setOnMenuItemClickListener(listener);
        popup.setOnDismissListener(listener);
        popup.inflate(R.menu.friend_item_menu);
        popup.show();
    }

    /**
     * Handles events from the context menu
     */
    public class FriendAdapterMenuItemClickListener implements PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener {
        Friend friend;
        View view;

        public FriendAdapterMenuItemClickListener(Friend friend, View view) {
            this.friend = friend;
            this.view = view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            view.setBackgroundColor(Color.WHITE);

            switch (item.getItemId()) {
                case R.id.edit:
                    Log.d(TAG, "onMenuItemClick: CLICKED EDIT");
                    // todo
                    return true;
                case R.id.delete:
                    Log.d(TAG, "onMenuItemClick: CLICKED DELETE");
                    db.deleteFriend(friend);
                    friendsListAdapter.clear();
                    friendsListAdapter.addAll(db.getAllFriends());
                    friendsListAdapter.notifyDataSetChanged();
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
