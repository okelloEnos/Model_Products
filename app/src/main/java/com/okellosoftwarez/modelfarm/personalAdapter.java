package com.okellosoftwarez.modelfarm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class personalAdapter extends RecyclerView.Adapter<personalAdapter.personalViewHolder> {
    private static final String TAG = "personalAdapter";
    List<Products> personalProductsList;
    private Context personalContext;
    private onItemClickListener clickListener;

    public personalAdapter(Context context, List<Products> list) {

        personalContext = context;
        personalProductsList = list;
    }

    @NonNull
    @Override
    public personalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new View...");
        View view = LayoutInflater.from(personalContext).inflate(R.layout.recycler_layout, parent, false);
        personalViewHolder productView = new personalViewHolder(view);

        Log.d(TAG, "onCreateViewHolder: view created...");
        return productView;
    }

    @Override
    public void onBindViewHolder(@NonNull personalViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: allign to recycler...");
        final Products currentProducts = personalProductsList.get(position);
        holder.recName.setText(currentProducts.getName());
        holder.recLocation.setText("Location : " + currentProducts.getLocation());
        holder.recPrice.setText("Price Per KG : " + currentProducts.getPrice());
        Picasso.with(personalContext)
                .load(currentProducts.getImage())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.recImage);


//        holder.clickedLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(personalContext, "Editing Coming Soon...", Toast.LENGTH_SHORT).show();
//            }
//        });
//            if (Products_view.buttonString.equals("buyer")) {
//
//                holder.clickedLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent passIntent = new Intent(mcontext, Product_Details.class);
//                        passIntent.putExtra("name", currentProducts.getName());
//                        passIntent.putExtra("location", currentProducts.getLocation());
//                        passIntent.putExtra("price", currentProducts.getPrice());
//                        passIntent.putExtra("capacity", currentProducts.getCapacity());
//                        passIntent.putExtra("email", currentProducts.getEmail());
//                        passIntent.putExtra("phone", currentProducts.getPhone());
//                        passIntent.putExtra("image", currentProducts.getImage());
//                        mcontext.startActivity(passIntent);
//                        Log.d(TAG, "onClick: detail view...");
//                    }
//                });
//            }
        Log.d(TAG, "onBindViewHolder: done binding....");
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: Counting method...");
        if (personalProductsList != null) {
            Log.d(TAG, "getItemCount: Done counting for list...");
            return personalProductsList.size();
        }
        Log.d(TAG, "getItemCount: Done counting for non-list...");
        return 0;
    }

    public class personalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        ImageView recImage;
        TextView recName, recLocation, recPrice;
        LinearLayout clickedLayout;

        public personalViewHolder(@NonNull View itemView)  {
            super(itemView);
            Log.d(TAG, "productViewHolder: Initializing recycler items...");
            recImage = itemView.findViewById(R.id.recycleImage);
            recName = itemView.findViewById(R.id.recycleName);
            recLocation = itemView.findViewById(R.id.recycleLocation);
            recPrice = itemView.findViewById(R.id.recyclePrice);
            clickedLayout = itemView.findViewById(R.id.rec);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {

            if (clickListener != null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    clickListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Do Whatever");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (clickListener != null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){

                    switch (item.getItemId()){
                        case 1 :
                            clickListener.onWhateverClick(position);
                            return true;

                        case 2 :
                            clickListener.onDeleteItem(position);
                            return true;
                    }
                }
            }

            return false;
        }
    }

    public interface onItemClickListener{

        void onItemClick(int position);

        void  onDeleteItem(int position);

        void onWhateverClick(int position);

    }

    public void setOnItemClickListener(onItemClickListener listener){
        clickListener = listener;
    }
}