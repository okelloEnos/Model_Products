package com.okellosoftwarez.modelfarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.cartAdapterViewHolder> {
    private List<orderModel> orderModelList;
    private Context ordersContext;

    public cartAdapter(Context ordersContext, List<orderModel> orderModelList) {
        this.ordersContext = ordersContext;
        this.orderModelList = orderModelList;
    }

    @NonNull
    @Override
    public cartAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View orderView = LayoutInflater.from(ordersContext).inflate(R.layout.cartrecycler, parent, false);
        cartAdapterViewHolder cartView = new cartAdapterViewHolder(orderView);

        return cartView;
    }

    @Override
    public void onBindViewHolder(@NonNull cartAdapterViewHolder holder, int position) {
        orderModel currentOrder = orderModelList.get(position);
        holder.orderName.setText(currentOrder.getPrdOrderedName());
        holder.orderCapacity.setText(currentOrder.getPrdOrderedCapacity() + " KG");
        holder.orderedPrice.setText(currentOrder.getPrdOrderedTotal());
        Picasso.with(ordersContext)
                .load(currentOrder.getPrdOrderImage())
                .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                .fit()
                .centerCrop()
                .into(holder.orderImage);

    }

    @Override
    public int getItemCount() {
        if (orderModelList != null){
            return orderModelList.size();
        }
        return 0;
    }

    public class cartAdapterViewHolder extends RecyclerView.ViewHolder{
        ImageView orderImage;
        TextView orderName, orderCapacity, orderedPrice;

        public cartAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            orderImage = itemView.findViewById(R.id.cartImage);
            orderName = itemView.findViewById(R.id.selectedPrdName);
            orderCapacity = itemView.findViewById(R.id.selectedCapacity);
            orderedPrice = itemView.findViewById(R.id.totalSelected);
        }
    }
}
