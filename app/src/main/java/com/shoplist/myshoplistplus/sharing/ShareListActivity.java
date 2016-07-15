package com.shoplist.myshoplistplus.sharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shoplist.myshoplistplus.BaseActivity;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;

public class ShareListActivity extends BaseActivity {
    private static final String LOG_TAG = ShareListActivity.class.getSimpleName();
    private ListView mListView;
    private FriendAdapter mFriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();

        /**
         * Create Firebase references
         */
        DatabaseReference currentUserFriendsRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USER_FRIENDS).child(mEncodedEmail);

        /**
         * Set interactive bits, such as click events/adapters
         */
        mFriendAdapter = new FriendAdapter(ShareListActivity.this, User.class, R.layout.single_user_item, currentUserFriendsRef);

        /* Set adapter for the listView */
        mListView.setAdapter(mFriendAdapter);
    }

    /**
     * Cleanup the adapter when activity is destroyed
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* Set adapter for the listView */
        mFriendAdapter.cleanup();
    }

    /**
     * Link layout elements from XML and setup the toolbar
     */
    public void initializeScreen(){
        mListView = (ListView) findViewById(R.id.list_view_friends_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        /* Add back button to the action bar */
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * Launch AddFriendActivity to find and add user to current user's friends list
     * when the button AddFriend is pressed
     */
    public void onAddFriendPressed(View view){
        Intent intent = new Intent(ShareListActivity.this, AddFriendActivity.class);
        startActivity(intent);
    }
}
