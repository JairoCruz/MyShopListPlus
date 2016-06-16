package com.shoplist.myshoplistplus.activeListDetail;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.utils.Constans;

/**
 * Created by TSE on 13/06/2016.
 */

/**
 * Lets the user remove active shopping list
 */
public class RemoveListDialogFragment extends DialogFragment {

    String mListId;
    private DatabaseReference listToRemoveRef;
    final static String LOG_TAG = RemoveListDialogFragment.class.getSimpleName();


    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static RemoveListDialogFragment newInstance(ShoppingList shoppingList, String listId){
        RemoveListDialogFragment removeListDialogFragment = new RemoveListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constans.KEY_LIST_ID, listId);
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
        /* Get the location to remove from */
        listToRemoveRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_ACTIVE_LISTS).child(mListId);
        /* Remove the value */
        listToRemoveRef.removeValue();
    }
}
