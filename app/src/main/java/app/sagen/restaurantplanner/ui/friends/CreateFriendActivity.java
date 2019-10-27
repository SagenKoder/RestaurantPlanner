package app.sagen.restaurantplanner.ui.friends;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import app.sagen.restaurantplanner.R;
import app.sagen.restaurantplanner.data.DBHandler;
import app.sagen.restaurantplanner.data.Friend;

public class CreateFriendActivity extends AppCompatActivity {

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(this);

        setContentView(R.layout.activity_create_friend);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setDimAmount(0.60f);

        final long friendId;
        if (getIntent().hasExtra("editFriendId")) {
            friendId = getIntent().getLongExtra("editFriendId", -1);
        } else {
            friendId = -1;
        }

        final EditText nameInput = findViewById(R.id.create_friend_name);
        final EditText phoneInput = findViewById(R.id.create_friend_phone);
        final TextView title = findViewById(R.id.create_friend_title);

        final Friend friend = dbHandler.getFriend(friendId);
        if (friendId > 0) {
            if (friend == null) {
                finish(); // just exit...
                return;
            }
            title.setText(getString(R.string.edit_friend));
            nameInput.setText(friend.getName());
            phoneInput.setText(friend.getPhone());
        }

        Button button = findViewById(R.id.create_friend_submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friend != null) {
                    friend.setName(nameInput.getText().toString());
                    friend.setPhone(phoneInput.getText().toString());
                    dbHandler.updateFriend(friend);
                    Toast.makeText(getApplicationContext(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
                } else {
                    dbHandler.createFriend(new Friend(nameInput.getText().toString(), phoneInput.getText().toString()));
                    Toast.makeText(getApplicationContext(), getString(R.string.added_new_friend), Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}
