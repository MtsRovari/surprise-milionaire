package com.liveonsolutions.binance.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.liveonsolutions.binance.ApiService.MyBoxResponse;
import com.liveonsolutions.binance.ApiService.OrderDetailsResponse;
import com.liveonsolutions.binance.R;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ItemHolder>{

    private static String TAG = "boxAdapter";

    private List<OrderDetailsResponse.Orders> mBoxs = new ArrayList<>();
    private Context context;

    public OrderDetailsAdapter(Context context, List<OrderDetailsResponse.Orders> mBoxList) {
        this.context = context;
        this.mBoxs = mBoxList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_details, viewGroup, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final OrderDetailsResponse.Orders box =  mBoxs.get(position);

        holder.mItem = box;

        holder.tSymbol.setText(box.getSymbol());

        if (box.getPercent().contains("-")) {
            holder.tPercent.setText(box.getPercent() + "%");
            holder.tPercent.setTextColor(ContextCompat.getColor(context, R.color.graphDownColor));
            holder.lottieAnimationView.setAnimation(R.raw.graph_down);
        } else {
            holder.tPercent.setText(box.getPercent().replace("-", "") + "%");
            holder.tPercent.setTextColor(ContextCompat.getColor(context, R.color.graphUpColor));
            holder.lottieAnimationView.setAnimation(R.raw.graph_up);
        }

        holder.tPurchasePrice.setText(box.getPurchasePrice());
        holder.tSalePrice.setText(box.getSalePrice());

        holder.lnlSymbol.setOnClickListener(v -> {
            if (holder.lnlContent.getVisibility() == View.GONE) {
                holder.lnlContent.setVisibility(View.VISIBLE);
                holder.imgMore.setRotation(-90);
            } else {
                holder.lnlContent.setVisibility(View.GONE);
                holder.imgMore.setRotation(90);
            }
        });
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View mView;
        public TextView tSymbol, tPercent, tPurchasePrice, tSalePrice;
        public LinearLayout lnlSymbol, lnlContent;
        public ImageView imgMore;
        public LottieAnimationView lottieAnimationView;
        public OrderDetailsResponse.Orders mItem;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tSymbol = itemView.findViewById(R.id.t_symbol);
            tPercent = itemView.findViewById(R.id.t_percent);
            tPurchasePrice = itemView.findViewById(R.id.t_purchase_price);
            tSalePrice = itemView.findViewById(R.id.t_sale_price);
            lnlSymbol = itemView.findViewById(R.id.lnl_symbol);
            lnlContent = itemView.findViewById(R.id.lnl_content);
            imgMore = itemView.findViewById(R.id.img_more);
            lottieAnimationView = itemView.findViewById(R.id.animation_lottie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {}
    }

    public void addAll(List<OrderDetailsResponse.Orders> list) {
        mBoxs.clear();
        mBoxs.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mBoxs.size();
    }
}
