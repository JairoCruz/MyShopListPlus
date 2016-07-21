package com.shoplist.myshoplistplus.activeListDetail;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.BaseActivity;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.ShoppingListItem;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.sharing.ShareListActivity;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActiveListDetailsActivity extends BaseActivity {

    private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();
    private ListView mListView;
    private TextView mTextViewPeopleShopping;
    private ShoppingList mShoppingList;
    private DatabaseReference mCurrentListRef;
    private DatabaseReference listItemsRef;
    private DatabaseReference firebaseItemLocation;
    private DatabaseReference mCurrentUserRef;
    private DatabaseReference mSharedWithRef;
    private Toolbar toolbar;
    private String mListId;
    private ActiveListItemAdapter mActiveListItemAdapter;
    private ValueEventListener mCurrentListRefListener;
    private ValueEventListener mCurrentUserRefListener;
    private ValueEventListener mSharedWithListener;
    private Button mButtonShopping;
    private User mCurrentUser;
    /* Stores whether the current user is shopping */
    private boolean mShopping = false;
    /* Stores whether the current user is the owner */
    private boolean mCurrentUserIsOwner = false;
    private HashMap<String, User> mSharedWithUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_list_details);


        /* Get the push ID from the extra passed by ShoppingListFragment */
        Intent intent = this.getIntent();
        mListId = intent.getStringExtra(Constans.KEY_LIST_ID);
        if (mListId == null) {
            finish();
            return;
        }

        /**
         * Create Firebase references
         */
        mCurrentListRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USER_LISTS).child(mEncodedEmail).child(mListId);
        listItemsRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS).child(mListId);
        mCurrentUserRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USERS).child(mEncodedEmail);
        mSharedWithRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_LISTS_SHARED_WITH).child(mListId);

        /**
         *  Link layout elements from XML and setup the toolbar
         */
        initializeScreen();


        /**
         * Setup the adapter
         */
        mActiveListItemAdapter = new ActiveListItemAdapter(this, ShoppingListItem.class, R.layout.single_active_list_item,listItemsRef.orderByChild(Constans.FIREBASE_PROPERTY_BOUGHT_BY), mListId, mEncodedEmail);
        /* Create ActiveListItemAdapter and set to listView */
        mListView.setAdapter(mActiveListItemAdapter);

        /**
         * Add ValueEventListener to Firebase references
         * to control get data and control behavior and visibility of elements
         */

        /* Save the most up-to-date version of current user in mCurrentUser */
        mCurrentUserRefListener = mCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) mCurrentUser = currentUser;
                else finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "Error en la lectura de datos" + databaseError.getMessage());
            }
        });

        final Activity thisActivity = this;


        mCurrentListRefListener = mCurrentListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /**
                 * Saving the most recent version of current shopping list into mShoppinList if present
                 * finish() the activity if the list is null (list was removed or unshared by it's owner
                 * while current user is in the list details activity
                 */
                ShoppingList shoppingList = dataSnapshot.getValue(ShoppingList.class);

                if (shoppingList == null) {
                    // Method from Activity
                    Log.e("error", "pase");
                    finish();
                    /**
                     * Make sure to call return, otherwise the rest of the method will execute,
                     * even after calling finish.
                     */
                    return;
                }
                mShoppingList = shoppingList;

                /**
                 * Pass the shopping list to the adapter if it is not null.
                 * We do this here becase mShoppingList is null when first created.
                 */
                mActiveListItemAdapter.setShoppingList(mShoppingList);

                /* Check if the current user is owner */
                mCurrentUserIsOwner = Utils.checkIfOwner(shoppingList, mEncodedEmail);

                /* Calling invalidateOptionsMenu causes onCreateOptionsMenu to be called */
                invalidateOptionsMenu();

                /* Set title appropriately */
                setTitle(shoppingList.getListName());


                HashMap<String, User> usersShopping = mShoppingList.getUsersShopping();
                if (usersShopping != null && usersShopping.size() != 0 && usersShopping.containsKey(mEncodedEmail)) {
                    mShopping = true;
                    mButtonShopping.setText(getString(R.string.button_stop_shopping));
                    mButtonShopping.setBackgroundColor(ContextCompat.getColor(ActiveListDetailsActivity.this, R.color.dark_grey));
                } else {
                    mButtonShopping.setText(getString(R.string.button_start_shopping));
                    mButtonShopping.setBackgroundColor(ContextCompat.getColor(ActiveListDetailsActivity.this, R.color.colorPrimaryDark));
                    mShopping = false;
                }

                setWhosShoppingText(mShoppingList.getUsersShopping());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Error de lectura" + databaseError.getMessage());
            }
        });


        mSharedWithListener = mSharedWithRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSharedWithUsers = new HashMap<String, User>();
                for (DataSnapshot currentUser : dataSnapshot.getChildren()){
                    mSharedWithUsers.put(currentUser.getKey(), currentUser.getValue(User.class));
                }
                mActiveListItemAdapter.setSharedWithUsers(mSharedWithUsers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "Error de lectura" + databaseError.getMessage());
            }
        });


        /**
         *  Set up Click listener for interaction.
         */

        /* Show edit list item name dialog on ListView item long click event */
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               /* Check that the view is not the empty foter item */
                if (view.getId() != R.id.list_view_footer_empty) {

                    ShoppingListItem shoppingListItem = mActiveListItemAdapter.getItem(position);

                    if (shoppingListItem != null) {
                        /**
                         * If the person is the owner and not shopping and the item is not bought, then
                         * they can edit it.
                         */
                        if (shoppingListItem.getOwner().equals(mEncodedEmail) && !mShopping && !shoppingListItem.isBought()){
                            String itemName = shoppingListItem.getItemName();
                            String itemId = mActiveListItemAdapter.getRef(position).getKey();
                            showEditListItemNameDialog(itemName, itemId);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        /* Perform buy/return action on listView item click event if current user is shopping. */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Check that the view is not the empty footer item */
                if (view.getId() != R.id.list_view_footer_empty) {
                    final ShoppingListItem selectedListItem = mActiveListItemAdapter.getItem(position);
                    String itemId = mActiveListItemAdapter.getRef(position).getKey();

                    if (selectedListItem != null) {

                        /* If current user is shopping */
                        if (mShopping) {

                            /* Create map and fill it in with deep path multi write operations list */
                            HashMap<String, Object> updatedItemBoughtData = new HashMap<String, Object>();
                            /* Buy selected item if it is NOT already bought */
                            if (!selectedListItem.isBought()) {
                                updatedItemBoughtData.put(Constans.FIREBASE_PROPERTY_BOUGHT, true);
                                updatedItemBoughtData.put(Constans.FIREBASE_PROPERTY_BOUGHT_BY, mEncodedEmail);
                            } else {
                                /* Return selected item only if it was bought by current user */
                                if (selectedListItem.getBoughtBy().equals(mEncodedEmail)){
                                    updatedItemBoughtData.put(Constans.FIREBASE_PROPERTY_BOUGHT, false);
                                    updatedItemBoughtData.put(Constans.FIREBASE_PROPERTY_BOUGHT_BY, null);
                                }

                            }

                        /* Do update */
                            firebaseItemLocation = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS).child(mListId).child(itemId);
                            firebaseItemLocation.updateChildren(updatedItemBoughtData, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.d(LOG_TAG, "Error al actualizar datos" + databaseError.getMessage());
                                    }
                                }
                            });

                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_list_details, menu);

        /**
         * Get menu items
         *
         */
        MenuItem remove = menu.findItem(R.id.action_remove_list);
        MenuItem edit = menu.findItem(R.id.action_edit_list_name);
        MenuItem share = menu.findItem(R.id.action_share_list);
        MenuItem archive = menu.findItem(R.id.action_archive);

        /* Only the edit and remove options are implemented */
        remove.setVisible(mCurrentUserIsOwner);
        edit.setVisible(mCurrentUserIsOwner);
        share.setVisible(mCurrentUserIsOwner);
        archive.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* Show edit list dialog when the edit action is selected */
        if (id == R.id.action_edit_list_name) {
            showEditListNameDialog();
            return true;
        }

        /* removeList() when the remove action is selected */
        if (id == R.id.action_remove_list) {
            removeList();
            return true;
        }

        /* Eventually we'll add this */
        if (id == R.id.action_share_list) {
            Intent intent = new Intent(ActiveListDetailsActivity.this, ShareListActivity.class);
            intent.putExtra(Constans.KEY_LIST_ID, mListId);
            /* Starts an active showing the details for the selected list */
            startActivity(intent);
            return true;
        }

        /* archiveList() when the archive action is selected */
        if (id == R.id.action_archive) {
            archiveList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActiveListItemAdapter.cleanup();
        mCurrentListRef.removeEventListener(mCurrentListRefListener);
        mCurrentUserRef.removeEventListener(mCurrentUserRefListener);
        mSharedWithRef.removeEventListener(mSharedWithListener);
    }

    /**
     * Archive current list when user selects "Archive" menu item
     */
    public void archiveList() {

    }

    /**
     * Start AddItemsFromMealActivity to add meal ingredients into the shopping list
     * when the user taps on "add meal" fab
     */
    public void addMeal(View view) {

    }

    /**
     * Remove current shopping list and its items from all nodes
     */
    private void removeList() {
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = RemoveListDialogFragment.newInstance(mShoppingList, mListId, mSharedWithUsers);
        dialog.show(getFragmentManager(), "RemoveListDialogFragment");
    }


    private void initializeScreen() {
        mListView = (ListView) findViewById(R.id.list_view_shopping_list_items);
        mTextViewPeopleShopping = (TextView) findViewById(R.id.text_view_people_shopping);
        mButtonShopping = (Button) findViewById(R.id.button_shopping);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);

        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* Inflate the footer, set root layout to null */
        View foooter = getLayoutInflater().inflate(R.layout.footer_empty, null);
        mListView.addFooterView(foooter);
    }


    /**
     * Show edit list name dialog when user selects "Edit list name" menu item
     */
    private void showEditListNameDialog() {
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = EditListNameDialogFragment.newInstance(mShoppingList, mListId, mEncodedEmail, mSharedWithUsers);
        dialog.show(this.getFragmentManager(), "EditListNameDialogFragment");
    }


    /**
     * Show the add list item dialog when user taps "add list item" fab
     */
    public void showAddListItemDialog(View view) {
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = AddListItemDialogFragment.newInstance(mShoppingList, mListId, mEncodedEmail, mSharedWithUsers);
        dialog.show(getFragmentManager(), "AddListItemDialogFragment");
    }

    public void showEditListItemNameDialog(String itemName, String itemId) {
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = EditListItemNameDialogFragment.newInstance(mShoppingList, itemName, itemId, mListId, mEncodedEmail, mSharedWithUsers);

        dialog.show(this.getFragmentManager(), "EditListItemNameDialogFragment");
    }

    /**
     * This method is called when user taps "Start/Stop Shopping" button
     */
    public void toggleShopping(View view){
        DatabaseReference mFirebaseRef = FirebaseDatabase.getInstance().getReference();
        /**
         * Create map and fill it in with deep path multi write operations list
         */
        HashMap<String, Object> updateUserData = new HashMap<String, Object>();
        String propertyToUpdate = Constans.FIREBASE_PROPERTY_USERS_SHOPPING + "/" + mEncodedEmail;

        /**
         * If current user is already shopping, remove current user from usersShopping map
         */

        if (mShopping){
            /* Add the value to update at the specified property for all lists */
            Utils.updateMapForAllWithValue(mSharedWithUsers, mListId, mShoppingList.getOwner(), updateUserData, propertyToUpdate, null);
            /* Appends the timestamp changes for all lists */
            Utils.updateMapWithTimestampLastChanged(mSharedWithUsers, mListId, mShoppingList.getOwner(), updateUserData);

            /* Do a deep-path update */

            mFirebaseRef.updateChildren(updateUserData, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    /* Updates the reversed timestamp */
                    Utils.updateTimestampReversed(databaseError, LOG_TAG, mListId,mSharedWithUsers, mShoppingList.getOwner());
                }
            });
        }else{
            /**
             * If current user is not shopping, create map to represent User model add to usersShopping map
             */
            HashMap<String, Object> currentUser = (HashMap<String, Object>) new ObjectMapper().convertValue(mCurrentUser, Map.class);

            /* Add the value to update at the specified property for all lists */
            Utils.updateMapForAllWithValue(mSharedWithUsers, mListId, mShoppingList.getOwner(), updateUserData, propertyToUpdate, currentUser);

            /* Appends the timestamp changes for all lists */
            Utils.updateMapWithTimestampLastChanged(mSharedWithUsers, mListId, mShoppingList.getOwner(), updateUserData);

            /* Do a deep-path update */
            mFirebaseRef.updateChildren(updateUserData, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    /* Updates the reversed timestamp */
                    Utils.updateTimestampReversed(databaseError, LOG_TAG, mListId, mSharedWithUsers, mShoppingList.getOwner());
                }
            });
        }
    }

    /**
     * Set appropriate text for Start / Stop shopping button and Who's shopping textView
     * depending on the current user shopping status
     */
    private void setWhosShoppingText(HashMap<String, User> usersShopping){
        if (usersShopping != null){
            ArrayList<String> usersWhoAreNotYou = new ArrayList<>();
            /**
             * If at least one user is shopping
             * Add userName to the list of users shopping if this user is not current user
             */
            for (User user : usersShopping.values()){
                if (user != null && !(user.getEmail().equals(mEncodedEmail))){
                    usersWhoAreNotYou.add(user.getName());
                }
            }

            int numberOfUsersShopping = usersShopping.size();
            String usersShoppingText;

            /**
             * If current user is shopping...
             * If current user is the only person shopping, set text to "You are shopping"
             * If current user and one user are shopping, set text "You and userName are shopping"
             * Else set text "You and N others shopping"
             */
            if (mShopping){
                switch (numberOfUsersShopping){
                    case 1:
                        usersShoppingText = getString(R.string.text_you_are_shopping);
                        break;
                    case 2:
                        usersShoppingText = String.format(getString(R.string.text_you_and_other_are_shopping), usersWhoAreNotYou.get(0));
                        break;
                    default:
                        usersShoppingText = String.format(getString(R.string.text_you_and_number_are_shopping), usersWhoAreNotYou.size());
                }
                /**
                 * If current user is not shopping..
                 * If there is only one person shopping, set text to "userName is shopping"
                 * If there are two users shopping, set text "userName1 and userName2 are shopping"
                 * Else set text "userName and N others shopping"
                 */

            }else{
                switch (numberOfUsersShopping){
                    case 1:
                        usersShoppingText = String.format(getString(R.string.text_other_is_shopping), usersWhoAreNotYou.get(0));
                        break;
                    case 2:
                        usersShoppingText = String.format(getString(R.string.text_other_and_other_are_shopping), usersWhoAreNotYou.get(0), usersWhoAreNotYou.get(1));
                        break;
                    default:
                        usersShoppingText = String.format(getString(R.string.text_other_and_number_are_shopping), usersWhoAreNotYou.get(0), usersWhoAreNotYou.size() - 1);
                }
            }
            mTextViewPeopleShopping.setText(usersShoppingText);
        }else {
            mTextViewPeopleShopping.setText("");
        }
    }
}
