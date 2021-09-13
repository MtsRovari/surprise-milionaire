package com.liveonsolutions.binance.Adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liveonsolutions.binance.ApiService.MyBoxResponse;
import com.liveonsolutions.binance.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.ItemHolder>{

    private static String TAG = "boxAdapter";

    private List<MyBoxResponse> mBoxs = new ArrayList<>();
    private Context context;
    private OnItemClickListener mListener;

    public StatementAdapter(Context context, List<MyBoxResponse> mBoxList) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_statement, viewGroup, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final MyBoxResponse box =  mBoxs.get(position);

        holder.mItem = box;

        holder.mLabel.setText(box.getId());
        holder.mCreatedAt.setText("Comprado em: " + box.getCreatedAt());

        new CountDownTimer(Long.parseLong(box.getDaysToOpen()), 1000) {

            public void onTick(long millisUntilFinished) {

                String hms = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                holder.mDays.setText("Tempo restante: " + hms);
            }

            public void onFinish() {
                holder.mDays.setText("Pronto para abrir!");
            }

        }.start();

        holder.lnlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.OnBoxInteraction(box);
            }
        });
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View mView;
        public TextView mLabel, mDays, mCreatedAt;
        public LinearLayout lnlItem;
        public MyBoxResponse mItem;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mLabel = itemView.findViewById(R.id.t_label);
            mDays = itemView.findViewById(R.id.t_days);
            mCreatedAt = itemView.findViewById(R.id.t_created);
            lnlItem = itemView.findViewById(R.id.lnl_box);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {}
    }

    public void addAll(List<MyBoxResponse> list) {
        mBoxs.clear();
        mBoxs.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mBoxs.size();
    }

    public interface OnItemClickListener {
        void OnBoxInteraction(MyBoxResponse box);
    }
}
