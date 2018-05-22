package com.sterenl.nanit.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.sterenl.nanit.R;
import com.sterenl.nanit.data.BabyDataProvider;
import com.sterenl.nanit.data.BabyModel;
import com.sterenl.nanit.databinding.FragmentDetailsBinding;
import com.sterenl.nanit.utiles.DateDialogUtils;
import com.sterenl.nanit.utiles.ImageFileHelper;
import com.sterenl.nanit.utiles.SchedulerProvider;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class DetailsFragment extends BaseCameraFragment implements View.OnClickListener {

    private FragmentDetailsBinding mBinder;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormat.mediumDate();
    private DateTime mDob;
    int mImageViewWidth;
    int mXCoordinate;
    int mYCoordinate;
    private String mUserAvatar;
    private Disposable mTextDisposable;


    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);
        View view = mBinder.getRoot();
        initView();
        setOnClickListener();
        return view;
    }

    private void initView() {
        updateViewFromStorage();
        initBottomSheet();
        ViewTreeObserver vto = mBinder.imgAvatar.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                addCameraIcon(this);
                return true;
            }
        });
    }

    private void updateViewFromStorage() {
        BabyModel baby = BabyDataProvider.getInstance().getBabyData(getActivity());
        if (baby!=null){
            mBinder.txtFirstName.setText(baby.getName());
            mBinder.btnOpenBirthDate.setText(dateTimeFormatter.print(baby.getBod()));
            // because this is not mandatory fields need extra check
            if (!TextUtils.isEmpty(baby.getAvatar())){
                ImageFileHelper.loadAvatar(getActivity(),mBinder.imgAvatar, baby.getAvatar());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTextDisposable!=null &&!mTextDisposable.isDisposed()){
            mTextDisposable.dispose();
        }
    }

    /**
     * add camera icon into to the top right avatar icon
     * i measure the placeholder point via invision to get point so i can calculate the scale.
     */
    private void addCameraIcon(ViewTreeObserver.OnPreDrawListener listener) {
        mBinder.imgAvatar.getViewTreeObserver().removeOnPreDrawListener(listener);
        mImageViewWidth = mBinder.imgAvatar.getMeasuredWidth();
        mXCoordinate = mBinder.imgAvatar.getLeft();
        mYCoordinate = mBinder.imgAvatar.getTop();
        ImageView cameraIcon = new ImageView(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        double scale = (double) 307 / (double) 376;
        params.leftMargin = (int) (mXCoordinate + (mImageViewWidth * (scale)));
        params.topMargin = mYCoordinate;
        cameraIcon.setImageResource(R.drawable.camera_icon_blue);
        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCameraPress();
            }
        });
        mBinder.mainLayout.addView(cameraIcon, params);
    }


    private void setOnClickListener() {
        mBinder.bodLayout.setOnClickListener(this);
        mBinder.btnShowBirthdayScreen.setOnClickListener(this);
        setEditTextListener();
    }

    private void setEditTextListener() {
        mTextDisposable = Observable.mergeArray(RxTextView.textChanges(mBinder.txtFirstName), RxTextView.textChanges(mBinder.btnOpenBirthDate)).debounce(100, TimeUnit.MILLISECONDS, SchedulerProvider.getInstance().ui()).subscribeWith(new DisposableObserver<CharSequence>() {
            @Override
            public void onNext(CharSequence charSequence) {
                if (mBinder.btnOpenBirthDate.getText().length() > 0 && mBinder.txtFirstName.getText().length() > 0) {
                    mBinder.btnShowBirthdayScreen.setEnabled(true);
                } else {
                    mBinder.btnShowBirthdayScreen.setEnabled(false);
                }
            }
            @Override
            public void onError(Throwable e) { }
            @Override
            public void onComplete() {
            }
        });
    }

    private void moveToBirthdayScreen() {
        BabyModel babyDetails = new BabyModel();
        babyDetails.setBod(mDob);
        babyDetails.setName(mBinder.txtFirstName.getText().toString());
        babyDetails.setAvatar(mUserAvatar);
        BabyDataProvider.getInstance().SaveBabyData(babyDetails, getActivity());
    }

    private void openCalender() {
        DateDialogUtils.showDateDialog(getActivity(), null, DateDialogUtils.DateDialogRange.PAST, new DateDialogUtils.OnDateSelectedListener() {
            @Override
            public void onDateSelected(DateTime date) {
                mDob = date;
                mBinder.btnOpenBirthDate.setText(dateTimeFormatter.print(date));
            }
        });
    }

    @Override
    void loadAvatar(String userAvatar) {
        mUserAvatar = userAvatar;
        ImageFileHelper.loadAvatar(getActivity(), mBinder.imgAvatar, mUserAvatar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bod_layout:
                openCalender();
                break;
            case R.id.btn_show_birthday_screen:
                moveToBirthdayScreen();
                break;
            case R.id.layout_take_photo:
                handleTakeAPhoto();
                break;
            case R.id.layout_choose_from_library:
                handleChooseFromGallery();
                break;
        }
    }


}
