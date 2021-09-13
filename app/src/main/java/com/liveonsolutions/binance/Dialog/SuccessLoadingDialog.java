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

public class SuccessLoadingDialog extends DialogFragment {

    private LottieAnimationView lottieAnimationView;
    private OnDialogInteraction mListener;

    public SuccessLoadingDialog() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_order_loading, container, false);
        setCancelable(false);

        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        lottieAnimationView = view.findViewById(R.id.animation_lottie);

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null)
                    mListener.OnSuccessAnimationEnd();
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
        if (context instanceof SuccessLoadingDialog.OnDialogInteraction) {
            mListener = (SuccessLoadingDialog.OnDialogInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDialogInteraction");
        }
    }

    public interface OnDialogInteraction {
        void OnSuccessAnimationEnd();
    }
}