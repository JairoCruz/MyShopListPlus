package com.shoplist.myshoplistplus.meals;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shoplist.myshoplistplus.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MealsFragment extends Fragment {




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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meals, container, false);
    }

}
