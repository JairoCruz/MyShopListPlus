package com.shoplist.myshoplistplus.activeListDetail;


import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.BaseActivity;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.ShoppingListItem;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;

import java.util.HashMap;

public class ActiveListDetailsActivity extends BaseActivity {

    private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();
    private ListView mListView;
    private ShoppingList mShoppingList;
    private DatabaseReference mActiveListRef;
    private DatabaseReference listItemsRef;
    private DatabaseReference firebaseItemLocation;
    private Toolbar toolbar;
    private String mListId;
    private ActiveListItemAdapter mActiveListItemAdapter;
    private ValueEventListener mActiveListRefListener;
    /* Stores whether the current user is the owner */
    private boolean mCurrentUserIsOwner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_list_details);


        /* Get the push ID from the extra passed by ShoppingListFragment */
        Intent intent = this.getIntent();
        mListId = intent.getStringExtra(Constans.KEY_LIST_ID);
        if (mListId == null){
            finish();
            return;
        }

        /**
         * Create Firebase references
         */
        mActiveListRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_ACTIVE_LISTS).child(mListId);
        listItemsRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS).child(mListId);


        /**
         *  Link layout elements from XML and setup the toolbar
         */
        initializeScreen();


        /**
         * Setup the adapter
         */
        mActiveListItemAdapter = new ActiveListItemAdapter(this, ShoppingListItem.class, R.layout.single_active_list_item, listItemsRef, mListId, mEncodedEmail);
        /* Create ActiveListItemAdapter and set to listView */
        mListView.setAdapter(mActiveListItemAdapter);



        mActiveListRefListener = mActiveListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /**
                 * Saving the most recent version of current shopping list into mShoppinList if present
                 * finish() the activity if the list is null (list was removed or unshared by it's owner
                 * while current user is in the list details activity
                 */
                ShoppingList shoppingList = dataSnapshot.getValue(ShoppingList.class);

                if (shoppingList == null){
                    // Method from Activity
                    Log.e("error","pase");
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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Error de lectura" + databaseError.getMessage());
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
                if (view.getId() != R.id.list_view_footer_empty){

                    ShoppingListItem shoppingListItem = mActiveListItemAdapter.getItem(position);

                    if (shoppingListItem != null){
                        String itemName = shoppingListItem.getItemName();
                        String itemId = mActiveListItemAdapter.getRef(position).getKey();

                        showEditListItemNameDialog(itemName, itemId);
                        return true;
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
                if (view.getId() != R.id.list_view_footer_empty){
                    final ShoppingListItem selectedListItem = mActiveListItemAdapter.getItem(position);
                    String itemId = mActiveListItemAdapter.getRef(position).getKey();

                    if (selectedListItem != null){
                        /* Create map and fill it in with deep path multi write operations list */
                        HashMap<String, Object> updatedItemBoughtData = new HashMap<String, Object>();
                        /* Buy selected item if it is NOT already bought */
                        if (!selectedListItem.isBought()){
                            updatedItemBoughtData.put(Constans.FIREBASE_PROPERTY_BOUGHT, true);
                            updatedItemBoughtData.put(Constans.FIREBASE_PROPERTY_BOUGHT_BY, mEncodedEmail);
                        }else{
                            updatedItemBoughtData.put(Constans.FIREBASE_PROPERTY_BOUGHT, false);
                            updatedItemBoughtData.put(Constans.FIREBASE_PROPERTY_BOUGHT_BY, null);
                        }
                        firebaseItemLocation = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS).child(mListId).child(itemId);
                        firebaseItemLocation.updateChildren(updatedItemBoughtData, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null){
                                    Log.d(LOG_TAG, "Error al actualizar datos" + databaseError.getMessage());
                                }
                            }
                        });
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
        share.setVisible(false);
        archive.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* Show edit list dialog when the edit action is selected */
        if (id == R.id.action_edit_list_name){
            showEditListNameDialog();
            return true;
        }

        /* removeList() when the remove action is selected */
        if (id == R.id.action_remove_list){
            removeList();
            return true;
        }

        /* Eventually we'll add this */
        if (id == R.id.action_share_list){
            return true;
        }

        /* archiveList() when the archive action is selected */
        if(id == R.id.action_archive){
            archiveList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActiveListItemAdapter.cleanup();
        mActiveListRef.removeEventListener(mActiveListRefListener);
    }

    /**
     * Archive current list when user selects "Archive" menu item
     */
    public void archiveList(){

    }

    /**
     * Start AddItemsFromMealActivity to add meal ingredients into the shopping list
     * when the user taps on "add meal" fab
     */
    public void addMeal(View view){

    }

    /**
     * Remove current shopping list and its items from all nodes
     */
    private void removeList() {
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = RemoveListDialogFragment.newInstance(mShoppingList, mListId);
        dialog.show(getFragmentManager(), "RemoveListDialogFragment");
    }



    private void initializeScreen() {
        mListView = (ListView) findViewById(R.id.list_view_shopping_list_items);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);

        /* Add back button to the action bar */
        if (getSupportActionBar() != null){
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
        DialogFragment dialog = EditListNameDialogFragment.newInstance(mShoppingList, mListId, mEncodedEmail);
        dialog.show(this.getFragmentManager(), "EditListNameDialogFragment");
    }


    /**
     * Show the add list item dialog when user taps "add list item" fab
     */
    public void showAddListItemDialog(View view){
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = AddListItemDialogFragment.newInstance(mShoppingList, mListId, mEncodedEmail);
        dialog.show(getFragmentManager(), "AddListItemDialogFragment");
    }

    public void showEditListItemNameDialog(String itemName, String itemId){
        /* Create an instance of the dialog fragment and show it */
       DialogFragment dialog = EditListItemNameDialogFragment.newInstance(mShoppingList, itemName, itemId, mListId, mEncodedEmail);

        dialog.show(this.getFragmentManager(),"EditListItemNameDialogFragment");
    }
}
