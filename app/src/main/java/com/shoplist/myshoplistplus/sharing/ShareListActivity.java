package com.shoplist.myshoplistplus.sharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.shoplist.myshoplistplus.BaseActivity;
import com.shoplist.myshoplistplus.R;

public class ShareListActivity extends BaseActivity {
    private static final String LOG_TAG = ShareListActivity.class.getSimpleName();
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
