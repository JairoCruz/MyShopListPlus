package com.shoplist.myshoplistplus.activeListDetail;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingListItem;

/**
 * Created by Marhinita on 18/6/2016.
 */

public class ActiveListItemAdapter extends FirebaseListAdapter<ShoppingListItem> {

    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public ActiveListItemAdapter(Activity activity, Class<ShoppingListItem> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
    }



    /**
     *
     * @param v
     * @param model
     * @param position
     */
    @Override
    protected void populateView(View v, ShoppingListItem model, int position) {
        ImageButton buttonRemoveItem = (ImageButton) v.findViewById(R.id.button_remove_item);
        TextView textViewMealItemName = (TextView) v.findViewById(R.id.text_view_active_list_item_name);

        textViewMealItemName.setText(model.getItemName());

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
                                removeItem();
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

    private void removeItem(){

    }
}
