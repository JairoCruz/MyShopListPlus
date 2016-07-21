package com.shoplist.myshoplistplus.activeListDetail;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;

import java.util.HashMap;

/**
 * Created by TSE on 13/06/2016.
 */

/**
 * Lets the user remove active shopping list
 */
public class RemoveListDialogFragment extends DialogFragment {

    String mListId;
    String mListOwner;
    HashMap mSharedWith;
    final static String LOG_TAG = RemoveListDialogFragment.class.getSimpleName();


    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static RemoveListDialogFragment newInstance(ShoppingList shoppingList, String listId, HashMap<String, User> sharedWithUsers){
        RemoveListDialogFragment removeListDialogFragment = new RemoveListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constans.KEY_LIST_ID, listId);
        bundle.putString(Constans.KEY_LIST_OWNER, shoppingList.getOwner());
        bundle.putSerializable(Constans.KEY_SHARED_WITH_USERS, sharedWithUsers);
        removeListDialogFragment.setArguments(bundle);
        return removeListDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recupero el valor del bundle pasado a traves de newInstanceHelper
        mListId = getArguments().getString(Constans.KEY_LIST_ID);
        mListOwner = getArguments().getString(Constans.KEY_LIST_OWNER);
        mSharedWith = (HashMap) getArguments().getSerializable(Constans.KEY_SHARED_WITH_USERS);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog)
                .setTitle(getActivity().getResources().getString(R.string.action_remove_list))
                .setMessage(getString(R.string.dialog_message_are_you_sure_remove_list))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeList();
                        /* Dismiss the dialog */
                        dialog.dismiss();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* Dismiss the dialog */
                        dialog.dismiss();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert);
        return  builder.create();
    }

    private void removeList(){
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

        /**
         * Create map and fill it in with deep path multi write operations list
         */
        HashMap<String, Object> removeListData = new HashMap<String, Object>();

       /* Remove the ShoppingList from both user lists and active lists */
        Utils.updateMapForAllWithValue(mSharedWith,mListId, mListOwner, removeListData, "", null);

        /* Remove the associdated list items */
        removeListData.put("/" + Constans.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS + "/" + mListId, null);
        /* Remove the lists shared with */
        removeListData.put("/" + Constans.FIREBASE_LOCATION_LISTS_SHARED_WITH + "/" + mListId, null);

        Log.e("eliminar", removeListData.toString());

        /* do a deep-path update */
        firebaseRef.updateChildren(removeListData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.e("error", "error en update data" + databaseError.getMessage());
                }
            }
        });
    }
}
