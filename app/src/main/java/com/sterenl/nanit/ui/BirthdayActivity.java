package com.sterenl.nanit.ui;

import android.os.Bundle;

import com.sterenl.nanit.Constants;
import com.sterenl.nanit.R;

public class BirthdayActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (detailsFragment == null) {
            detailsFragment = DetailsFragment.newInstance();
            handleFragment(detailsFragment,
                    null, R.id.fragment_container, Constants.FRAGMENT_DETAILS, false, Constants.ACTION_ADD_FRAGMENT);
        }
    }
}
