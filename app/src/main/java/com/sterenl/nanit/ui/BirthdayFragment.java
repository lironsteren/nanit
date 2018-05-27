package com.sterenl.nanit.ui;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sterenl.nanit.R;
import com.sterenl.nanit.data.BabyDataProvider;
import com.sterenl.nanit.data.BirthdayClassModel;
import com.sterenl.nanit.databinding.FragmentBirthdayBinding;
import com.sterenl.nanit.utiles.ImageFileHelper;
import com.sterenl.nanit.utiles.SourceDataProvider;

public class BirthdayFragment extends BaseCameraFragment {

    private FragmentBirthdayBinding mBinder;


    private final double PLACE_HOLDER_Y_SCALE = (double) 545 / (double) 1202;
    private final double CAMERA_ICON_Y_SCALE = (double) 600 / (double) 1202;
    private final double CAMERA_ICON_X_SCALE = (double) 250 / (double) 358;
    private ImageView mPlaceHolder;
    private ImageView mCameraIcon;


    public static BirthdayFragment newInstance() {
        return new BirthdayFragment();
    }

    /******************************************************************************************************
     *  life cycle method
     ******************************************************************************************************/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_birthday, container, false);
        View view = mBinder.getRoot();
        initView();
        return view;
    }

    /******************************************************************************************************
     *  inner logic method
     ******************************************************************************************************/
    private void initView() {
        BirthdayClassModel baby = SourceDataProvider.getInstance().getScreenResource(BabyDataProvider.getInstance().getBabyData(getActivity()), getActivity());
        mBinder.imgBabyAge.setImageResource(baby.getAgeImage());
        mBinder.txtBirthdayBabyName.setText(baby.getMainTitle());
        mBinder.txtBirthdayBaby.setText(baby.getSubTitle());
        mBinder.imgBackground.setImageResource(baby.getBackground());
        mPlaceHolder = new ImageView(getActivity());
        if (!TextUtils.isEmpty(BabyDataProvider.getInstance().getBabyData(getActivity()).getAvatar())) {
            ImageFileHelper.loadAvatar(getActivity(), mPlaceHolder, BabyDataProvider.getInstance().getBabyData(getActivity()).getAvatar());
        } else {
            mPlaceHolder.setImageResource(baby.getPlaceholder());
        }
        mCameraIcon = new ImageView(getActivity());
        mCameraIcon.setImageResource(baby.getCameraIcon());

        initBottomSheet();

        ViewTreeObserver vto = mBinder.imgBackground.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                addPlaceHolder(this);
                return true;
            }
        });
        mBinder.btnCloseScreen.setOnClickListener(this);
    }

    // i use minSdk 21 so i can use setZ for lower sdk i can just add all the views programmatically in the right order
    private void addPlaceHolder(ViewTreeObserver.OnPreDrawListener onPreDrawListener) {

        mBinder.imgBackground.getViewTreeObserver().removeOnPreDrawListener(onPreDrawListener);
        int mImageViewWidth = mBinder.imgBackground.getMeasuredWidth();
        int mImageViewHeight = mBinder.imgBackground.getMeasuredHeight();

        RelativeLayout.LayoutParams placeHolderParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        placeHolderParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        placeHolderParam.topMargin = (int) (mImageViewHeight * PLACE_HOLDER_Y_SCALE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPlaceHolder.setZ(-1);
        }
        RelativeLayout.LayoutParams cameraIconParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cameraIconParam.topMargin = (int) (mImageViewHeight * CAMERA_ICON_Y_SCALE);
        cameraIconParam.leftMargin = (int) (mImageViewWidth * CAMERA_ICON_X_SCALE);

        mBinder.mainLayout.addView(mPlaceHolder, placeHolderParam);
        mBinder.mainLayout.addView(mCameraIcon, cameraIconParam);
        mBinder.btnCloseScreen.bringToFront();
        mPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCameraPress();
            }
        });
    }

    /******************************************************************************************************
     *  overrides method
     ******************************************************************************************************/
    @Override
    void loadAvatar(String userAvatar) {
        ImageFileHelper.loadAvatar(getActivity(), mPlaceHolder, userAvatar);
        BabyDataProvider.getInstance().updateUserAvatar(getActivity(), userAvatar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_take_photo:
                handleTakeAPhoto();
                break;
            case R.id.layout_choose_from_library:
                handleChooseFromGallery();
                break;
            case R.id.btn_close_screen:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
        }
    }
}
