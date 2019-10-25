package app.sagen.restaurantplanner.ui.booking.menus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import app.sagen.restaurantplanner.data.Booking;
import app.sagen.restaurantplanner.data.Friend;
import app.sagen.restaurantplanner.data.Restaurant;
import app.sagen.restaurantplanner.db.DBHandler;

public class FriendsSelector extends DialogFragment {

    private static final String TAG = "FriendsSelector";
    
    public interface FriendSelectorCallback {
        void onFinish(List<Friend> addFriends, List<Friend> removeFriends);
    }

    private Friend[] friends;
    private String[] friendNames;
    private boolean[] selectedItems;
    private FriendSelectorCallback callback;

    private List<Friend> removeFriends = new ArrayList<>();
    private List<Friend> addFriends = new ArrayList<>();

    public FriendsSelector(List<Friend> friends, List<Friend> selectedFriends, FriendSelectorCallback callback) {
        this.friends = friends.toArray(new Friend[0]);
        this.callback = callback;

        friendNames = new String[friends.size()];
        for(int i = 0; i < friendNames.length; i++){
            Friend friend = this.friends[i];
            friendNames[i] = friend.getName();
        }

        this.selectedItems = new boolean[friendNames.length];
        for(int i = 0; i < friendNames.length; i++){
            Friend friend = this.friends[i];
            selectedItems[i] = selectedFriends.contains(friend);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Velg en restaurant");
        builder.setMultiChoiceItems(friendNames, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                isChecked = !isChecked; // update to next value
                Log.e(TAG, "onClick: item=" + item + " checked=" + isChecked + " selecteditem=" + selectedItems[item]);
                if(selectedItems[item] && !isChecked) {
                    Log.e(TAG, "onClick: Add");
                    addFriends.add(friends[item]);
                    removeFriends.remove(friends[item]);
                    selectedItems[item] = true;
                } else if(!selectedItems[item] && isChecked) {
                    Log.e(TAG, "onClick: Remove");
                    addFriends.remove(friends[item]);
                    removeFriends.add(friends[item]);
                    selectedItems[item] = false;
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Log.e(TAG, "onCancel: CANCEL2");
        callback.onFinish(addFriends, removeFriends);
        super.onCancel(dialog);
    }
}
