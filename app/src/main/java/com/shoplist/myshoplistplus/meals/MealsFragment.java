package com.shoplist.myshoplistplus.meals;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shoplist.myshoplistplus.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MealsFragment extends Fragment {
    private ListView mListView;

    /**
     * Este es el cascaron de este fragment el cual implementa un listview. este listview se le agrego un OnItemClickListener
     */

    public MealsFragment() {
        // Required empty public constructor
    }

    public static MealsFragment newInstance(){
        MealsFragment fragment = new MealsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View rootView = inflater.inflate(R.layout.fragment_meals,container,false);

        /* Link layout elments from XML and setup the toolbar */
        initializeScreen(rootView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return rootView;
    }

    public void initializeScreen(View rootView){
        mListView = (ListView) rootView.findViewById(R.id.list_view_meals_list);
        View footer = getActivity().getLayoutInflater().inflate(R.layout.footer_empty,null);
        mListView.addFooterView(footer);
    }

}
