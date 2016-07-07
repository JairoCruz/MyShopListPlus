package com.shoplist.myshoplistplus.activeList;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.activeListDetail.ActiveListDetailsActivity;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.utils.Constans;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {

    private String mEncodeEmail;
    private ActiveListAdapter mActiveListAdapter;
    private DatabaseReference activeListsRef;
    private ListView mListView;


    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /*
    * Create fragment and pass bundle vith data as it's arguments
    * Right now there are not arguments...but eventually there will be
    * */

    public static ShoppingListFragment newInstance(String encodedEmail) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();
        args.putString(Constans.KEY_ENCODED_EMAIL, encodedEmail);
        fragment.setArguments(args);
        return fragment;
    }

    /*
    * Initialize instance variables with data from bundle
    * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEncodeEmail = getArguments().getString(Constans.KEY_ENCODED_EMAIL);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        /*
        * Initialize UI elements
        * */
        View rootView = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        initializeScreen(rootView);


        /**
         * Set interactive bits, such as click events and adapters
         * Add ItemClick Listener to ListView
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Por medio de el listener obtengo la posicion del elemento utilizando el metodo getItem y se lo paso a mi Variable del tipo ShoppingList */
                ShoppingList selectedList = mActiveListAdapter.getItem(position);
                /* Me aseguro que la variable no este vacia */
                if (selectedList != null){
                    /* Si no esta vacia Lanzo una nueva actividad en este caso Detail Activity */
                    Intent intent = new Intent(getActivity(), ActiveListDetailsActivity.class);
                    /**
                     * Get the list ID usign the adapter's get ref method to get the Firebase
                     * ref and then grab the key.
                     */
                    String listId = mActiveListAdapter.getRef(position).getKey();
                    intent.putExtra(Constans.KEY_LIST_ID, listId);
                    /* Starts an active showing the details for the selected list */
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    /**
     * Updates the order of mListView onResume to handle sortOrderChanges properly
     */
    @Override
    public void onResume() {
        super.onResume();
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPref.getString(Constans.KEY_PREF_SORT_ORDER_LISTS, Constans.ORDER_BY_KEY);
        Log.e("OderBy", sortOrder);

        Query orderedActiveUserListsRef;
        DatabaseReference activeListsRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_ACTIVE_LISTS);
        /**
         * Sort active lists by "date created"
         * if it's been selected in the SettingsActivity
         */
        if (sortOrder.equals(Constans.ORDER_BY_KEY)){
            orderedActiveUserListsRef = activeListsRef.orderByKey();
        }else{
            /**
             * Sort active by lists by name or datelastChanged. Otherwise
             * depending o what's been selected in SettingsActivity
             */
            orderedActiveUserListsRef = activeListsRef.orderByChild(sortOrder);
        }

        /**
         * Create the adapter with selected sort order
         */
        mActiveListAdapter = new ActiveListAdapter(getActivity(), ShoppingList.class, R.layout.single_active_list, orderedActiveUserListsRef, mEncodeEmail);

        /**
         * Set the adapter to the mListView
         */
        mListView.setAdapter(mActiveListAdapter);
    }

    /**
     * Cleanup the adapter when activity is destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mActiveListAdapter.cleanup();
    }

    /**
     * Link list view from XML
     *
     * @param rootView
     */

    private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_list);

    }

}
