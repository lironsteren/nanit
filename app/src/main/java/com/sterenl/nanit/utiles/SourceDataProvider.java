package com.sterenl.nanit.utiles;

import android.content.Context;
import android.content.res.TypedArray;

import com.sterenl.nanit.R;
import com.sterenl.nanit.data.BabyModel;
import com.sterenl.nanit.data.BirthdayClassModel;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class SourceDataProvider {

    private static SourceDataProvider mInstance;
    private static BirthdayClassModel mBirthdayClass;
    private int mBabyAgeInMonths;
    private int mBabyAgeInYears;

    private final int TWELVE = 12;
    private final int ONE = 1;
    private final int ZERO = 0;

    private final String RESOURCE_TYPE = "drawable";
    private final String RESOURCE_NAME_PREFIX = "n";

    public static SourceDataProvider getInstance() {
        if (mInstance == null) {
            mInstance = new SourceDataProvider();
            mBirthdayClass = new BirthdayClassModel();
        }
        return mInstance;
    }


    public BirthdayClassModel getScreenResource(BabyModel baby, Context context) {
        calculateTimePass(baby.getBod());
        updateSubTitle(context);
        updateImageResource(context);
        updateMainTitle(context, baby.getName());
        updateBackgroundImages(context);
        return mBirthdayClass;
    }

    // This method base that both array are the same size
    private void updateBackgroundImages(Context context) {
        TypedArray backgroundArray = context.getResources().obtainTypedArray(R.array.background_images);
        TypedArray placeHolderArray = context.getResources().obtainTypedArray(R.array.placeholder_images);
        TypedArray cameraArray = context.getResources().obtainTypedArray(R.array.camera_images);
        int choice = (int) (Math.random() * backgroundArray.length());
        mBirthdayClass.setBackground(backgroundArray.getResourceId(choice, R.drawable.android_elephant_popup));
        mBirthdayClass.setPlaceholder(placeHolderArray.getResourceId(choice, R.drawable.default_place_holder_yellow));
        mBirthdayClass.setCameraIcon(cameraArray.getResourceId(choice, R.drawable.camera_icon_yellow));

    }

    private void updateMainTitle(Context context, String name) {
        mBirthdayClass.setMainTitle(context.getString(R.string.baby_screen_main_headline, name));
    }

    private void updateImageResource(Context context) {
        int resourceId;
        if (mBabyAgeInMonths < TWELVE && mBabyAgeInYears == ZERO) {
            resourceId = context.getResources().getIdentifier(RESOURCE_NAME_PREFIX + mBabyAgeInMonths, RESOURCE_TYPE, context.getPackageName());
        } else {
            resourceId = context.getResources().getIdentifier(RESOURCE_NAME_PREFIX + mBabyAgeInYears, RESOURCE_TYPE, context.getPackageName());

        }
        mBirthdayClass.setAgeImage(resourceId);
    }

    private void calculateTimePass(DateTime bod) {
        DateTime dateTime = new DateTime();
        mBabyAgeInMonths = new Period(bod, dateTime).getMonths();
        mBabyAgeInYears = new Period(bod, dateTime).getYears();
    }


    private void updateSubTitle(Context context) {
        if (mBabyAgeInYears == ZERO) {
            if (mBabyAgeInMonths <= ONE) {
                mBirthdayClass.setSubTitle(context.getString(R.string.month_old));
            } else if (mBabyAgeInMonths < TWELVE) {
                mBirthdayClass.setSubTitle(context.getString(R.string.months_old));
            }
        } else {
            if (mBabyAgeInYears <= ONE) {
                mBirthdayClass.setSubTitle(context.getString(R.string.year_old));
            } else {
                mBirthdayClass.setSubTitle(context.getString(R.string.years_old));
            }
        }
    }
}
