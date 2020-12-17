package com.okellosoftwarez.modelfarm.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.okellosoftwarez.modelfarm.R;
import com.okellosoftwarez.modelfarm.orderModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.cartAdapterViewHolder> {
    private List<orderModel> orderModelList;
    private Context ordersContext;
    private onCartClickListener cartClickListener;

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
        Picasso.get()
                .load(currentOrder.getPrdOrderImage())
                .placeholder(R.drawable.back_image)
                .fit()
                .centerCrop()
                .into(holder.orderImage);

    }

    @Override
    public int getItemCount() {
        if (orderModelList != null) {
            return orderModelList.size();
        }
        return 0;
    }

    public class cartAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        ImageView orderImage;
        TextView orderName, orderCapacity, orderedPrice;

        public cartAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            orderImage = itemView.findViewById(R.id.cartImage);
            orderName = itemView.findViewById(R.id.selectedPrdName);
            orderCapacity = itemView.findViewById(R.id.selectedCapacity);
            orderedPrice = itemView.findViewById(R.id.totalSelected);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {

            if (cartClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    cartClickListener.cartItemClick(position);
                }
            }

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Select Action...");
            MenuItem editItem = menu.add(Menu.NONE, 1, 1, "Change Your Order");
            MenuItem deleteItem = menu.add(Menu.NONE, 2, 2, "Delete Product Order");

            editItem.setOnMenuItemClickListener(this);
            deleteItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (cartClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {

                        case 1:
                            cartClickListener.editCartItem(position);
                            return true;

                        case 2:
                            cartClickListener.deleteCartItem(position);
                            return true;
                    }
                }
            }

            return false;
        }
    }

    public interface onCartClickListener {
        void cartItemClick(int position);

        void deleteCartItem(int position);

        void editCartItem(int position);
    }

    public void setOnCartClickListener(onCartClickListener listener) {
        cartClickListener = listener;
    }
}
