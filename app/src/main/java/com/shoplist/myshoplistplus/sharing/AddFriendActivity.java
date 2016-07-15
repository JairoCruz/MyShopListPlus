package com.shoplist.myshoplistplus.sharing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shoplist.myshoplistplus.BaseActivity;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;

/**
 * Represents the Add Friend screen and functionality
 */

public class AddFriendActivity extends BaseActivity {

    private EditText mEditTextAddFriendEmail;
    private ListView mListViewAutocomplete;
    private AutocompleteFriendAdapter mFriendsAutocompleteAdapter;
    private DatabaseReference mUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        /**
         * Create Firebase references
         */
        mUsersRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USERS);

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();


        /**
         * Set interactive bits, such as click events/adapters
         */
        mFriendsAutocompleteAdapter = new AutocompleteFriendAdapter(AddFriendActivity.this, User.class,
                R.layout.single_autocomplete_item, mUsersRef.orderByChild(Constans.FIREBASE_PROPERTY_EMAIL), mEncodedEmail);

        mListViewAutocomplete.setAdapter(mFriendsAutocompleteAdapter);



        /**mEditTextAddFriendEmail.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
        });**/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFriendsAutocompleteAdapter.cleanup();
    }

    /**
     * Link layout elements from XML and setup the toolbar
     */
    public void initializeScreen(){
        mListViewAutocomplete = (ListView) findViewById(R.id.list_view_friends_autocomplete);
        mEditTextAddFriendEmail = (EditText) findViewById(R.id.edit_text_add_friend_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
