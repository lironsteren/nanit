package com.sterenl.nanit.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.TextView;

import com.sterenl.nanit.Constants;
import com.sterenl.nanit.R;
import com.sterenl.nanit.utiles.ImageFileHelper;
import com.sterenl.nanit.utiles.PermissionManager;

import java.io.File;
import java.util.Date;
import java.util.List;

public abstract class BaseCameraFragment extends android.support.v4.app.Fragment implements BirthdayContract.BirthdayView, View.OnClickListener {
    private BottomSheetBehavior bottomSheetBehaviorAddPhoto;
    private BottomSheetDialog bottomSheetDialogAddPhoto;
    private TextView layoutTakePhoto;
    private TextView layoutChooseFromLibrary;
    private BirthdayContract.BirthdayPresenter mPresenter;
    private Uri mMakePhotoUri;
    private String mUserAvatar;
    private File imageFile;

    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_PICTURE = 1;
    private static final String IMAGE_TYPE = "image/*";

    private static final String JPG_EXTENSION = ".jpg";
    private static final String DIR_NAME = "attachments";
    private static final String PROVIDER_EXTENSION = ".provider";
    private PermissionManager mPermissionManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new BirthdayPresenter(this);
        mPermissionManager = new PermissionManager(getActivity());
    }

    void handleCameraPress() {
        bottomSheetBehaviorAddPhoto.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialogAddPhoto.show();
    }
    void initBottomSheet() {
        View bottomSheetAddDocumentView = getActivity().getLayoutInflater().inflate(R.layout.bottomsheet_add_photo, null);
        layoutTakePhoto = bottomSheetAddDocumentView.findViewById(R.id.layout_take_photo);
        layoutChooseFromLibrary = bottomSheetAddDocumentView.findViewById(R.id.layout_choose_from_library);
        layoutTakePhoto.setOnClickListener(this);
        layoutChooseFromLibrary.setOnClickListener(this);
        bottomSheetDialogAddPhoto = new BottomSheetDialog(getActivity());
        bottomSheetDialogAddPhoto.setContentView(bottomSheetAddDocumentView);
        bottomSheetBehaviorAddPhoto = BottomSheetBehavior.from((View) bottomSheetAddDocumentView.getParent());
    }

    void handleTakeAPhoto() {
        if (!mPermissionManager.isUserHasCameraPermission()) {
            mPermissionManager.requestCameraPermission(this);
        } else {
            mPresenter.takePhotoRequested(getContext());

        }
    }


    void handleChooseFromGallery() {
        if (!mPermissionManager.isUserHasStoragePermission()) {
            mPermissionManager.requestStoragePermission(this);
        } else {
            mPresenter.selectPhotoRequested();

        }
    }


    @Override
    public void setPresenter(BirthdayContract.BirthdayPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void hideAddPhotoBottomSheet() {
        bottomSheetBehaviorAddPhoto.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetDialogAddPhoto.hide();
    }

    @Override
    public void startCameraActivity() {
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureImageIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            captureImage();
        }
    }

    @Override
    public void startImageSelection() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setType(IMAGE_TYPE);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(Intent.createChooser(intent, "gallery"), GALLERY_PICTURE);
    }

    private void captureImage() {
        final Date creationDate = new Date();
        final String name = String.valueOf(creationDate.getTime()) + JPG_EXTENSION;
        imageFile = new File(ImageFileHelper.getPrivateExternalCacheDir(getActivity(), DIR_NAME), name);
        mMakePhotoUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + PROVIDER_EXTENSION, imageFile);
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, mMakePhotoUri);
        List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(camera, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getActivity().grantUriPermission(packageName, mMakePhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivityForResult(camera, CAMERA_REQUEST);

    }

    /********************************************************************************
     * callback from image intent gallery or camera
     ********************************************************************************/
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    if (resultCode == Activity.RESULT_OK) {
                        mUserAvatar = mMakePhotoUri.toString();
                        saveAndUpdateImage(mUserAvatar);
                    }

                    break;
                case GALLERY_PICTURE:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri selectedImageUri = imageReturnedIntent.getData();
                        Uri fromFile = Uri.fromFile(new File(ImageFileHelper.getRealPathFromURIForGallery(selectedImageUri, getActivity())));
                        mUserAvatar = fromFile.toString();
                        saveAndUpdateImage(mUserAvatar);
                    }
                    break;
            }
        }
    }

    private void saveAndUpdateImage(String userAvatar) {
        mUserAvatar = userAvatar;
        loadAvatar(mUserAvatar);

    }
    abstract void loadAvatar(String userAvatar);



    /********************************************************************************
     * Request WRITE_EXTERNAL and CAMERA permissions callback
     ********************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case Constants.MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handleTakeAPhoto();
                }
                return;
            }


            case Constants.MY_PERMISSIONS_WRITE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handleChooseFromGallery();
                }
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}
