package com.okellosoftwarez.modelfarm.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.okellosoftwarez.modelfarm.Product_Details;
import com.okellosoftwarez.modelfarm.models.Products;
import com.okellosoftwarez.modelfarm.Products_view;
import com.okellosoftwarez.modelfarm.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class productAdapter extends RecyclerView.Adapter<productAdapter.productViewHolder> {
    private static final String TAG = "productAdapter";
    List<Products> productsList;
    private Context mcontext;

    public productAdapter(Context context, List<Products> list) {

        mcontext = context;
        productsList = list;
    }

    @NonNull
    @Override
    public productViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new View...");
        View view = LayoutInflater.from(mcontext).inflate(R.layout.recycler_layout, parent, false);
        productViewHolder productView = new productViewHolder(view);

        Log.d(TAG, "onCreateViewHolder: view created...");
        return productView;
    }

    @Override
    public void onBindViewHolder(@NonNull productViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: allign to recycler...");
        final Products currentProducts = productsList.get(position);
        holder.recName.setText(currentProducts.getName());
        holder.recLocation.setText("Location : " + currentProducts.getLocation());
        holder.recPrice.setText("Price Per KG : " + currentProducts.getPrice());
        Picasso.get()
                .load(currentProducts.getImage())
                .placeholder(R.drawable.back_image)
                .fit()
                .centerCrop()
                .into(holder.recImage);


        if (Products_view.buttonString.equals("buyer")) {

            holder.clickedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent passIntent = new Intent(mcontext, Product_Details.class);
                    passIntent.putExtra("name", currentProducts.getName());
                    passIntent.putExtra("location", currentProducts.getLocation());
                    passIntent.putExtra("price", currentProducts.getPrice());
                    passIntent.putExtra("capacity", currentProducts.getCapacity());
                    passIntent.putExtra("email", currentProducts.getEmail());
                    passIntent.putExtra("phone", currentProducts.getPhone());
                    passIntent.putExtra("image", currentProducts.getImage());
                    passIntent.putExtra("key", currentProducts.getID());

                    Toast.makeText(mcontext, "Key:" + currentProducts.getID(), Toast.LENGTH_LONG).show();
                    mcontext.startActivity(passIntent);
                    Log.d(TAG, "onClick: detail view...");
                }
            });
        }
        Log.d(TAG, "onBindViewHolder: done binding....");
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: Counting method...");
        if (productsList != null) {
            Log.d(TAG, "getItemCount: Done counting for list...");
            return productsList.size();
        }
        Log.d(TAG, "getItemCount: Done counting for non-list...");
        return 0;
    }

    public class productViewHolder extends RecyclerView.ViewHolder {

        ImageView recImage;
        TextView recName, recLocation, recPrice;
        LinearLayout clickedLayout;

        public productViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "productViewHolder: Initializing recycler items...");
            recImage = itemView.findViewById(R.id.recycleImage);
            recName = itemView.findViewById(R.id.recycleName);
            recLocation = itemView.findViewById(R.id.recycleLocation);
            recPrice = itemView.findViewById(R.id.recyclePrice);
            clickedLayout = itemView.findViewById(R.id.rec);
        }
    }
}
