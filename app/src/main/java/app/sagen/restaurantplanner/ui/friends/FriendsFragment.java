package app.sagen.restaurantplanner.ui.friends;

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
import app.sagen.restaurantplanner.data.Friend;
import app.sagen.restaurantplanner.data.DBHandler;

public class FriendsFragment extends ListFragment {

    private static final String TAG = "FriendsFragment";

    FriendsListAdapter friendsListAdapter;
    DBHandler db;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = new DBHandler(getContext());
        friendsListAdapter = new FriendsListAdapter(getActivity());
        setListAdapter(friendsListAdapter);
        setEmptyText("Du har ingen venner i listen. Opprett en ny venn med + knappen!");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.fragment_friends, container, false);
        parent.addView(v, 0);

        FloatingActionButton fab = parent.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: FRIEND");
                Intent intent = new Intent(getContext(), CreateFriendActivity.class);
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
        Friend friend = friendsListAdapter.getItem(position);

        FriendAdapterMenuItemClickListener listener = new FriendAdapterMenuItemClickListener(friend, v);
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
        friendsListAdapter.clear();
        friendsListAdapter.addAll(db.getAllFriends());
        friendsListAdapter.notifyDataSetChanged();
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
                    Intent intent = new Intent(getContext(), CreateFriendActivity.class);
                    intent.putExtra("editFriendId", friend.getId());
                    startActivity(intent);
                    return true;
                case R.id.delete:
                    Log.d(TAG, "onMenuItemClick: CLICKED DELETE");
                    db.deleteFriend(friend);
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
