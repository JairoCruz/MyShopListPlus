package com.shoplist.myshoplistplus.activeList;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.utils.Constans;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {

    private String mEncodeEmail;
    // private ActiveListAdapter mActiveListAdapter;
    private ListView mListView;



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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);*/

        /*
        * Initialize UI elements
        * */
        View rootView = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        initializeScreen(rootView);

        return rootView;
    }

    private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_list);
    }

}
