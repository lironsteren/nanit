package com.sterenl.nanit.utiles;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.sterenl.nanit.Constants;


/**********************************************************************************************************************
 * This class handle request permission in runTime
 * in each activity you need to check if the permission granted implement
 * onRequestPermissionsResult
 *********************************************************************************************************************/

public class PermissionManager {

    private Activity mContext;

    public PermissionManager(Activity context) {
        mContext = context;
    }


    public boolean isUserHasCameraPermission() {
        return ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }


    public void requestCameraPermission(Fragment fragment) {
        fragment.requestPermissions(
                new String[]{Manifest.permission.CAMERA},
                Constants.MY_PERMISSIONS_REQUEST_CAMERA);
    }

    public boolean isUserHasStoragePermission() {
        return ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStoragePermission(Fragment fragment) {
        fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                Constants.MY_PERMISSIONS_WRITE_EXTERNAL);
    }
}
