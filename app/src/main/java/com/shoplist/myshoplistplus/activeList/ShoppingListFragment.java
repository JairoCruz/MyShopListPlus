package com.shoplist.myshoplistplus.activeList;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.activeListDetail.ActiveListDetailsActivity;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;

import java.util.Date;


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

    public static ShoppingListFragment newInstance(String mEncodedEmail){
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
        if(getArguments() != null){
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
         * Add ValueEventListeners to Firebase references
         * to control get data and control behavior and visibility of elements
         */

        mActiveListAdapter = new ActiveListAdapter(getActivity(), ShoppingList.class, R.layout.single_active_list, activeListsRef);

        /**
         * Set the adapter to the mListView
         */
        mListView.setAdapter(mActiveListAdapter);




        /**
         * Set interactive bits, such as click events and adapters
         *
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
     * @param rootView
     */

    private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_list);

    }

}
