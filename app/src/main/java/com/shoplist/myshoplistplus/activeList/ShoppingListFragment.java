package com.shoplist.myshoplistplus.activeList;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    public static ShoppingListFragment newInstance(String mEncodedEmail) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();
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
         * Create Firebase references
         */
        activeListsRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_ACTIVE_LISTS);

        /**
         * Create the adapter, giving it the activity, model class, layout for each row in the
         * list and finally, a reference to the Firebase location with the list data.
         *
         */

        mActiveListAdapter = new ActiveListAdapter(getActivity(), ShoppingList.class, R.layout.single_active_list, activeListsRef);

        /**
         * Set the adapter to the mListView
         */
        mListView.setAdapter(mActiveListAdapter);


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
