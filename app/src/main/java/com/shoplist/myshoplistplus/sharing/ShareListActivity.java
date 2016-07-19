package com.shoplist.myshoplistplus.sharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.BaseActivity;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;

import java.util.HashMap;

public class ShareListActivity extends BaseActivity {
    private static final String LOG_TAG = ShareListActivity.class.getSimpleName();
    private ListView mListView;
    private FriendAdapter mFriendAdapter;
    private ShoppingList mShoppingList;
    private String mListId;
    private DatabaseReference mActiveListRef, mSharedWithRef;
    private ValueEventListener mActiveListRefListener, mSharedWithListener;
    private HashMap<String, User> mSharedWithUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);


        /* Get the push ID from the extra passed by ActiveListDetailsActivity */
        Intent intent = this.getIntent();
        mListId = intent.getStringExtra(Constans.KEY_LIST_ID);
        if (mListId == null){
            /* No point in continuing without a valid ID */
            finish();
            return;
        }

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();

        /**
         * Create Firebase references
         */
        DatabaseReference currentUserFriendsRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USER_FRIENDS).child(mEncodedEmail);
        mActiveListRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USER_LISTS).child(mEncodedEmail).child(mListId);
        mSharedWithRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_LISTS_SHARED_WITH).child(mListId);


        /**
         * Add ValueEventListeners to Firebase references
         * to control get data and control behavior and visibility of elements
         */
        mActiveListRefListener = mActiveListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ShoppingList shoppingList = dataSnapshot.getValue(ShoppingList.class);
                /**
                 * Saving the most recent version of current shopping list into mShoppingList
                 * and pass ii to setShoppingList() if present
                 * finish() the activity otherwise
                 */
                if (shoppingList != null){
                    mShoppingList = shoppingList;
                    mFriendAdapter.setShoppingList(mShoppingList);
                }else {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "Error en lectura de datos" + databaseError.getMessage());
            }
        });

        mSharedWithListener = mSharedWithRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSharedWithUsers = new HashMap<String, User>();
                for (DataSnapshot currentUser : dataSnapshot.getChildren()){
                    mSharedWithUsers.put(currentUser.getKey(), currentUser.getValue(User.class));
                }
                mFriendAdapter.setShareWithUsers(mSharedWithUsers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "Error de lectura" + databaseError.getMessage());
            }
        });

        /**
         * Set interactive bits, such as click events/adapters
         */
        mFriendAdapter = new FriendAdapter(ShareListActivity.this, User.class, R.layout.single_user_item, currentUserFriendsRef, mListId);

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
        mActiveListRef.removeEventListener(mActiveListRefListener);
        mSharedWithRef.removeEventListener(mSharedWithListener);
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
