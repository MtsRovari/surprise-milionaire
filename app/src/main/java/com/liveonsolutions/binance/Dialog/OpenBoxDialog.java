package com.liveonsolutions.binance.Dialog;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.liveonsolutions.binance.R;

public class OpenBoxDialog extends DialogFragment {

    private LottieAnimationView lottieAnimationView;
    private OnDialogInteraction mListener;
    private String boxId;

    public OpenBoxDialog() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_open_box, container, false);
        setCancelable(false);

        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        lottieAnimationView = view.findViewById(R.id.animation_lottie);

        assert getArguments() != null;
        boxId = getArguments().getString("boxId");

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null)
                    mListener.OnAnimationEnd(boxId);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OpenBoxDialog.OnDialogInteraction) {
            mListener = (OpenBoxDialog.OnDialogInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDialogInteraction");
        }
    }

    public interface OnDialogInteraction {
        void OnAnimationEnd(String boxId);
    }
}