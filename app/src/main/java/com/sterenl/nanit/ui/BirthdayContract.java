package com.sterenl.nanit.ui;

import android.content.Context;

public class BirthdayContract {
    public interface BirthdayPresenter {
        void takePhotoRequested(Context context);

        void selectPhotoRequested();
    }

    public interface BirthdayView {
        void setPresenter(BirthdayPresenter presenter);

        void hideAddPhotoBottomSheet();

        void startCameraActivity();

        void startImageSelection();
    }
}
