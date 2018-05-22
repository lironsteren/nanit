package com.sterenl.nanit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.sterenl.nanit.Constants;

public class BaseActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    public void handleFragment(Fragment fragment, Bundle bundle, int id, String tag, boolean addToBackStack, String action) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        if (action.equals(Constants.ACTION_ADD_FRAGMENT)) {
            fragmentTransaction.add(id, fragment, tag);

        } else {
            fragmentTransaction.replace(id, fragment, tag);
        }
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

}
