package com.liveonsolutions.binance.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.liveonsolutions.binance.R;

public class WarningDialog extends DialogFragment {

    private TextView btnCancel, btnConfirm;
    private OnDialogInteraction mListener;
    private String boxId;

    public WarningDialog() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm, container, false);
        setCancelable(false);

        btnCancel = view.findViewById(R.id.btn_cancel);
        btnConfirm = view.findViewById(R.id.btn_confirm);

        assert getArguments() != null;
        boxId = getArguments().getString("boxId");

        btnCancel.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onCancelInteraction();
        });

        btnConfirm.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onConfirmInteraction(boxId);
        });

        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDialogInteraction) {
            mListener = (OnDialogInteraction) context;
        }
    }

    public interface OnDialogInteraction{
        void onCancelInteraction();
        void onConfirmInteraction(String boxId);
    }
}