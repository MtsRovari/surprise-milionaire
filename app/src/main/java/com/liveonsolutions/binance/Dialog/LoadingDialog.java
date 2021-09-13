package com.liveonsolutions.binance.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.liveonsolutions.binance.R;

public class LoadingDialog extends DialogFragment {

    private LottieAnimationView lottieAnimationView;

    public LoadingDialog() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading, container, false);
        setCancelable(false);

        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        lottieAnimationView = view.findViewById(R.id.animation_lottie);

        return view;
    }
}