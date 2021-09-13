package com.liveonsolutions.binance.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liveonsolutions.binance.ApiService.BoxResponse;
import com.liveonsolutions.binance.R;

import java.util.ArrayList;
import java.util.List;

public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.ItemHolder>{

    private static String TAG = "boxAdapter";

    private List<BoxResponse> mBoxs = new ArrayList<>();
    private Context context;
    private OnItemClickListener mListener;
    
    public BoxAdapter(Context context, List<BoxResponse> mBoxList) {
        this.context = context;
        this.mBoxs = mBoxList;

        try {
            this.mListener = ((OnItemClickListener) context);
        }catch (ClassCastException e){
            throw new ClassCastException("Activity must implement OnMyAccountsClickListener.");
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_box, viewGroup, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final BoxResponse box =  mBoxs.get(position);

        holder.mItem = box;

        holder.mLabel.setText(box.getLabel());
        holder.mDays.setText("Vencimento: " + box.getDays() + " dias");

        holder.lnlBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.OnBoxInteraction(box);
            }
        });
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View mView;
        public TextView mLabel, mDays;
        public RelativeLayout lnlBuy;
        public BoxResponse mItem;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mLabel = itemView.findViewById(R.id.t_label);
            mDays = itemView.findViewById(R.id.t_days);
            lnlBuy = itemView.findViewById(R.id.lnl_buy);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {}
    }

    public void addAll(List<BoxResponse> list) {
        mBoxs.clear();
        mBoxs.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mBoxs.size();
    }

    public interface OnItemClickListener {
        void OnBoxInteraction(BoxResponse box);
    }
}
