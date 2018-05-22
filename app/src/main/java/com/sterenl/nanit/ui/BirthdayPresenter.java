package com.sterenl.nanit.ui;

import android.content.Context;

import java.io.File;

public class BirthdayPresenter implements BirthdayContract.BirthdayPresenter {

    private final BirthdayContract.BirthdayView mBirthdayView;


    public BirthdayPresenter(BirthdayContract.BirthdayView birthdayView) {
        mBirthdayView = birthdayView;
        mBirthdayView.setPresenter(this);
    }

    @Override
    public void takePhotoRequested(Context context) {
        mBirthdayView.hideAddPhotoBottomSheet();
        mBirthdayView.startCameraActivity();
    }

    @Override
    public void selectPhotoRequested() {
        mBirthdayView.hideAddPhotoBottomSheet();
        mBirthdayView.startImageSelection();
    }

}
