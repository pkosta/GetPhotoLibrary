package com.example.getphotolibrary.addphoto;

/*
 * Created by 849501 on 10/20/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class GetPhotoBottomDialogFragment extends AddPhotoBottomDialogFragment {

    private OnBottomSheetButtonClickListener mButtonCLickListener;

    private OnImageReadyListener mImageReadyListener;

    private int mTargetImageWidth = 192;    // default 192 px

    private int mTargetImageHeight = 192;   // default 192 px

    private AddPhotoHelper mAddPhotoHelper;

    private boolean mShowRemoveButton = false;

    public static GetPhotoBottomDialogFragment newInstance(
            @Nullable OnBottomSheetButtonClickListener bottomSheetButtonClickListener,
            @Nullable OnImageReadyListener imageReadyListener,
            int targetImageWidth,
            int targetImageHeight,
            @NonNull String imageName,
            boolean toShowRemoveButton) {

        GetPhotoBottomDialogFragment getPhotoBottomDialogFragment
                = new GetPhotoBottomDialogFragment();

        getPhotoBottomDialogFragment.mButtonCLickListener = bottomSheetButtonClickListener;
        getPhotoBottomDialogFragment.mImageReadyListener = imageReadyListener;
        getPhotoBottomDialogFragment.mTargetImageWidth = targetImageWidth;
        getPhotoBottomDialogFragment.mTargetImageHeight = targetImageHeight;
        getPhotoBottomDialogFragment.mShowRemoveButton = toShowRemoveButton;

        getPhotoBottomDialogFragment.mAddPhotoHelper = new AddPhotoHelper(
                getPhotoBottomDialogFragment,
                imageName);

        return getPhotoBottomDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        showRemovePhotoButton(mShowRemoveButton);

        return view;

    }

    @Override
    protected void onAddPhotoBDFragmentButtonsClick(@AddPhotoBottomSheetButtons String button) {
        switch (button) {
            case AddPhotoBottomSheetButtons.USE_CAMERA:
                if (mButtonCLickListener != null) {
                    mButtonCLickListener
                            .onBottomSheetButtonClick(AddPhotoBottomSheetButtons.USE_CAMERA);
                }
                mAddPhotoHelper.dispatchTakePictureIntent();
                break;
            case AddPhotoBottomSheetButtons.FROM_GALLERY:
                if (mButtonCLickListener != null) {
                    mButtonCLickListener
                            .onBottomSheetButtonClick(AddPhotoBottomSheetButtons.FROM_GALLERY);
                }
                mAddPhotoHelper.dispatchPickPictureIntent();
                break;
            case AddPhotoBottomSheetButtons.REMOVE_PHOTO:
                if (mButtonCLickListener != null) {
                    mButtonCLickListener
                            .onBottomSheetButtonClick(AddPhotoBottomSheetButtons.REMOVE_PHOTO);
                    dismiss();  // dismiss the bottom sheet dialog fragment
                }
                break;
        }
    }

    public interface OnBottomSheetButtonClickListener {
        void onBottomSheetButtonClick(@AddPhotoBottomSheetButtons String button);
    }

    public interface OnImageReadyListener {
        void onImageReadyWithBitmap(Bitmap finalBitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AddPhotoHelper.REQUEST_IMAGE_CAPTURE == requestCode &&
                resultCode == Activity.RESULT_OK) {

            if (mAddPhotoHelper != null) {
                Bitmap finalBitmap = mAddPhotoHelper
                        .getFinalBitmap(mAddPhotoHelper.getPhotoPath(),
                                mTargetImageWidth,
                                mTargetImageHeight);
                if (mImageReadyListener != null) {
                    mImageReadyListener.onImageReadyWithBitmap(finalBitmap);
                }
            }

        } else if (AddPhotoHelper.PICK_IMAGE == requestCode &&
                resultCode == Activity.RESULT_OK) {

            if (mAddPhotoHelper != null) {

                Uri selectedImage = data.getData();

                if (selectedImage != null) {
                    InputStream imageStream = null;
                    try {
                        imageStream = getContext().getContentResolver().openInputStream(selectedImage);
                        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                        Bitmap finalBitmap = mAddPhotoHelper
                                .getFinalBitmap(yourSelectedImage,
                                        mTargetImageWidth,
                                        mTargetImageHeight);

                        if (mImageReadyListener != null) {
                            mImageReadyListener.onImageReadyWithBitmap(finalBitmap);
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        // close the bottom sheet dialog fragment,
        dismiss();
    }

    // Facility to hide the remove photo option when photo is not there
    private void showRemovePhotoButton(boolean flag) {
        mShowRemoveButton = flag;
        mTvBtnRemovePhoto.setVisibility(flag? View.VISIBLE: View.GONE);
        mViewSeparator.setVisibility(flag? View.VISIBLE: View.GONE);
    }
}
