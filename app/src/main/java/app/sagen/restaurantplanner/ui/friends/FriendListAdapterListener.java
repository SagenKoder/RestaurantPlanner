package app.sagen.restaurantplanner.ui.friends;

import java.util.List;

import app.sagen.restaurantplanner.data.Friend;

public interface FriendListAdapterListener {
    void onDeleteFriend(Friend friend, FriendsListAdapter friendsListAdapter);

    void onEditFriend(Friend friend, FriendsListAdapter friendsListAdapter);

    List<Friend> getAllFriends(FriendsListAdapter friendsListAdapter);
}