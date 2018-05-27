package com.sterenl.nanit.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sterenl.nanit.Constants;

/**
 *  This class handle all the save and restore baby data
 */
public class BabyDataProvider {
    private static BabyDataProvider mInstance;


    public static BabyDataProvider getInstance() {
        if (mInstance == null) {
            mInstance = new BabyDataProvider();
        }
        return mInstance;
    }


    public void SaveBabyData(BabyModel baby, Context context) {
        SharedPreferences.Editor edit = context
                .getSharedPreferences(Constants.BABY_PREF_TYPE, Context.MODE_PRIVATE).edit();
        Gson gson = Converters.registerDateTime(new GsonBuilder()).create();
        String json = gson.toJson(baby);
        edit.putString(Constants.BABY_DATA, json).commit();
    }

    public BabyModel getBabyData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.BABY_PREF_TYPE, Context.MODE_PRIVATE);
        if (prefs.contains(Constants.BABY_DATA)) {
            Gson gson = Converters.registerDateTime(new GsonBuilder()).create();
            String json = prefs.getString(Constants.BABY_DATA, null);
            return gson.fromJson(json, BabyModel.class);
        }
        return null;
    }
    public void updateUserAvatar(Context context, String avatar){
        SharedPreferences prefs = context.getSharedPreferences(Constants.BABY_PREF_TYPE, Context.MODE_PRIVATE);
        Gson gson = Converters.registerDateTime(new GsonBuilder()).create();
        String json = prefs.getString(Constants.BABY_DATA, null);
        BabyModel baby =  gson.fromJson(json, BabyModel.class);
        baby.setAvatar(avatar);
        SharedPreferences.Editor edit = context
                .getSharedPreferences(Constants.BABY_PREF_TYPE, Context.MODE_PRIVATE).edit();
        String updateBaby = gson.toJson(baby);
        edit.putString(Constants.BABY_DATA, updateBaby).commit();
    }

}
