package com.shoplist.myshoplistplus.activeListDetail;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.ShoppingListItem;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;

import java.util.HashMap;

/**
 * Created by Marhinita on 18/6/2016.
 */

public class ActiveListItemAdapter extends FirebaseListAdapter<ShoppingListItem> {

    private ShoppingList mShoppingList;
    private String mListId;
    private String mEncodedEmail;

    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public ActiveListItemAdapter(Activity activity, Class<ShoppingListItem> modelClass, int modelLayout, Query ref, String listId, String encodedEmail ) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mListId = listId;
        this.mEncodedEmail = encodedEmail;
    }

    /**
     * Public method that is used to pass shoppingList object when it is loaded in ValueEventListener
     */
    public void setShoppingList(ShoppingList shoppingList){
        this.mShoppingList = shoppingList;
        this.notifyDataSetChanged();
    }



    /**
     *
     * @param v
     * @param model
     * @param position
     */
    @Override
    protected void populateView(View v, final ShoppingListItem model, int position) {
        ImageButton buttonRemoveItem = (ImageButton) v.findViewById(R.id.button_remove_item);
        TextView textViewItemName = (TextView) v.findViewById(R.id.text_view_active_list_item_name);
        final TextView textViewBoughtByUser = (TextView) v.findViewById(R.id.text_view_bought_by_user);
        TextView textViewBoughtBy = (TextView) v.findViewById(R.id.text_view_bought_by);

        String owner = model.getOwner();

        textViewItemName.setText(model.getItemName());

        setItemAppearanceBaseOnBoughtStatus(owner, textViewBoughtByUser, textViewBoughtBy, buttonRemoveItem, textViewItemName, model);


        /* Gets the id of the item to remove */
        final String itemToRemoveId = this.getRef(position).getKey();

        /**
         * Set the on click listener for "Remove list item" button
         */
        buttonRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity, R.style.CustomTheme_Dialog)
                        .setTitle(mActivity.getString(R.string.remove_item_option))
                        .setMessage(mActivity.getString(R.string.dialog_message_are_you_sure_remove_list_item))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(itemToRemoveId);
                                /* Dismiss the dialog */
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }


    private void setItemAppearanceBaseOnBoughtStatus(String owner, final TextView textViewBoughtByUser, TextView textViewBoughtBy, ImageButton buttonRemoveItem,
                                                     TextView textViewItemName, ShoppingListItem item){
        /**
         * If selected item is bought
         * Set "Bought by" text to "You" if current user is owner of the list
         * Set "Bought by" text to userName if current user is NOT owner of the list
         * Set the remove item button invisible if current user is NOT list or item owner
         */
        if (item.isBought() && item.getBoughtBy() != null){
            textViewBoughtBy.setVisibility(View.VISIBLE);
            textViewBoughtByUser.setVisibility(View.VISIBLE);
            buttonRemoveItem.setVisibility(View.INVISIBLE);
            /* Add a strike-through */
            textViewItemName.setPaintFlags(textViewItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (item.getBoughtBy().equals(mEncodedEmail)){
                textViewBoughtByUser.setText(mActivity.getString(R.string.text_you));
            }else{
                DatabaseReference boughtByUserRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USERS).child(item.getBoughtBy());
                /* Get the item's owner's name; use a SingleValueEvent listener for memory efficiency */
                boughtByUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null){
                            textViewBoughtByUser.setText(user.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(mActivity.getClass().getSimpleName(), "Error de lectura fallido" + databaseError.getMessage());
                    }
                });
            }
        }else {
            /**
             * If selected item is NOT bought
             * Set "Bought by" text to be empty and invisible
             * Set the remove item button visible if current user is owner of the list or selected item
             */

            /* Remove the strike-through */
            textViewItemName.setPaintFlags(textViewItemName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            textViewBoughtBy.setVisibility(View.INVISIBLE);
            textViewBoughtByUser.setVisibility(View.INVISIBLE);
            textViewBoughtByUser.setText("");
            buttonRemoveItem.setVisibility(View.VISIBLE);
        }
    }

    private void removeItem(String itemId){

        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

        /* Make a map for the removal */
        HashMap<String, Object> updatedRemoveItemMap = new HashMap<String, Object>();

        /* Remove the item by passing null */
        updatedRemoveItemMap.put("/" + Constans.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS + "/" + mListId + "/" + itemId, null);

        /* Make the timestamp for last changed */
        HashMap<String, Object> changedTimestampMap = new HashMap<>();
        changedTimestampMap.put(Constans.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        /* Add the updated timestamp */
        updatedRemoveItemMap.put("/" + Constans.FIREBASE_LOCATION_ACTIVE_LISTS + "/" + mListId + "/" + Constans.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, changedTimestampMap);

        /* Do the update */
        firebaseRef.updateChildren(updatedRemoveItemMap);
    }
}
