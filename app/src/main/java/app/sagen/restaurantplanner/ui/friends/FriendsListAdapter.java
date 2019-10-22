package app.sagen.restaurantplanner.ui.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.Friend;

public class FriendsListAdapter extends ArrayAdapter<Friend> {

    public FriendsListAdapter(@NonNull Context context, List<Friend> friends) {
        super(context, -1, friends);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Objects.requireNonNull(inflater);
            view = inflater.inflate(R.layout.fragment_friend_item, parent, false);
        }

        final Friend friend = getItem(position);

        if (friend != null) {
            TextView name = view.findViewById(R.id.friend_name);
            name.setText(friend.getName());

            TextView phone = view.findViewById(R.id.friend_number);
            phone.setText(friend.getPhone());
        }

        return view;
    }
}
