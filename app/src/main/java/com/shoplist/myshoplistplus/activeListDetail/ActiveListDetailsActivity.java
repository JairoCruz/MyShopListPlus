package com.shoplist.myshoplistplus.activeListDetail;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shoplist.myshoplistplus.BaseActivity;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;

public class ActiveListDetailsActivity extends BaseActivity {

    private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();
    private ListView mListView;
    private ShoppingList mShoppingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_list_details);


        /**
         *  Link layout elements from XML and setup the toolbar
         */
        initializeScreen();

        /* Calling invalidateOptionsMenu causes onCreateOptionsMenu to be called */
        invalidateOptionsMenu();


        /**
         *  Set up Click listener for interaction.
         */

        /* Show edit list item name dialog on ListView item long click event */
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               /* Check that the view is not the empty foter item */
                if (view.getId() != R.id.list_view_footer_empty){
                    showEditListItemNameDialog();
                }
                return true;
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
        remove.setVisible(true);
        edit.setVisible(true);
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

    private void initializeScreen() {
        mListView = (ListView) findViewById(R.id.list_view_shopping_list_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
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
     * Show the add list item dialog when user taps "add list item" fab
     */
    /*public void showAddListItemDialog(View view){
        DialogFragment dialog = AddListItemDialogFragment.newInstance(mShoppingList);
        dialog.show(getFragmentManager(), "AddListItemDialogFragment");
    }*/

    public void showEditListItemNameDialog(){
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = EditListNameDialogFragment.newInstance(mShoppingList);
        dialog.show(this.getFragmentManager(), "EditListNameDialogFragment");
    }
}
