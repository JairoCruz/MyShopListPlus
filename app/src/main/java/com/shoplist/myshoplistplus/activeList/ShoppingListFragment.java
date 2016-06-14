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
    // private ActiveListAdapter mActiveListAdapter;
    private ListView mListView;
    private ActiveListAdapter mActiveListAdapter;
    private TextView mTextViewListName;
    private TextView mTextViewOwner;
    private TextView mTextViewEditTime;



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

        // Obtengo una referencia a mi objeto en firebase.
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_ACTIVE_LIST);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Utilizo el methodo de dataSnapshot.exists para saber si existe la referencia a la cual
                // intento acceder
                if (dataSnapshot.exists()) {
                    /*for (DataSnapshot child : dataSnapshot.getChildren()){
                        Log.e("error", String.valueOf(child));
                    }*/
                    ShoppingList shoppingList = dataSnapshot.getValue(ShoppingList.class);
                    // Log.d("error", "Value is:" + listName);
                    mTextViewListName.setText(shoppingList.getListName());
                    mTextViewOwner.setText(shoppingList.getOwner());
                    // Si existe un valor para la fecha
                    if (shoppingList.getTimestampLastChanged() != null){
                        // Almaceno el dato de la fecha en una variable tipo long ya que asi es como se almacena en FireBase
                        long dateLastChanged = (long) shoppingList.getTimestampLastChanged().get(Constans.FIREBASE_PROPERTY_TIMESTAMP);
                        //Log.e("Error", Utils.SIMPLE_DATE_FORMAT.format(new Date(dateLastChanged)));
                        // Luego llamo un metodo para formatear la fecha y sea legible
                        mTextViewEditTime.setText(Utils.SIMPLE_DATE_FORMAT.format(new Date(dateLastChanged)));
                    }

                }else{
                    mTextViewListName.setText("Vacio");
                    mTextViewOwner.setText("Vacio");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("error1","Failed to read value", databaseError.toException());

            }
        });


        /**
         * Set interactive bits, such as click events and adapters
         *
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        // Agrego un OnClickListener a el TextView que tiene el nombre de la lista
        mTextViewListName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Starts an active showing the details for the selected list */
                Intent intent = new Intent(getActivity(), ActiveListDetailsActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_list);
        mTextViewListName = (TextView) rootView.findViewById(R.id.text_view_list_name);
        //mTextViewListName.setText("hola");
        mTextViewOwner = (TextView) rootView.findViewById(R.id.text_view_created_by_user);
        mTextViewEditTime = (TextView) rootView.findViewById(R.id.text_view_edit_time);
    }

}
