package com.shoplist.myshoplistplus.activeList;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.utils.Constans;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {

    private String mEncodeEmail;
    // private ActiveListAdapter mActiveListAdapter;
    private ListView mListView;
    private ActiveListAdapter mActiveListAdapter;
    private TextView mTextViewListName;



    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /*
    * Create fragment and pass bundle vith data as it's arguments
    * Right now there are not arguments...but eventually there will be
    * */

    public static ShoppingListFragment newInstance(String encodeEmail){
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();
        args.putString(Constans.KEY_ENCODED_EMAIL,encodeEmail);
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
            mEncodeEmail = getArguments().getString(Constans.KEY_ENCODED_EMAIL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * Create the adapter with selected sort order
         */
        // mActiveListAdapter = new ActiveListAdapter(getActivity(), ShoppingList.class, R.layout.single_active_list, orderedActivieUserListsRef, mEncodeEmail);

        /**
         *  Set the adapter to the mListView
         * */
      //  mListView.setAdapter(mActiveListAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
       // mActiveListAdapter.cleanup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);*/

        /*
        * Initialize UI elements
        * */
        View rootView = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        initializeScreen(rootView);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("listName");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String listName = dataSnapshot.getValue(String.class);
                Log.d("error", "Value is:" + listName);
                mTextViewListName.setText(listName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("error1","Failed to read value", databaseError.toException());
            }
        });

        return rootView;
    }

    private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_list);
        mTextViewListName = (TextView) rootView.findViewById(R.id.text_view_list_name);
        mTextViewListName.setText("hola");
    }

}
