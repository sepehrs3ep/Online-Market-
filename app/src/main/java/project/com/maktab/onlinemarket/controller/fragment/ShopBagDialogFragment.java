package project.com.maktab.onlinemarket.controller.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import project.com.maktab.onlinemarket.R;
import project.com.maktab.onlinemarket.controller.activity.ProductInfoActivity;
import project.com.maktab.onlinemarket.model.product.Product;
import project.com.maktab.onlinemarket.model.product.ProductLab;
import project.com.maktab.onlinemarket.network.Api;
import project.com.maktab.onlinemarket.network.RetrofitClientInstance;
import project.com.maktab.onlinemarket.utils.GenerateSnackBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopBagDialogFragment extends DialogFragment {
    @BindView(R.id.shop_final_sum_text_view)
     TextView mShopFinalSumTextView;
    @BindView(R.id.submit_shop_bag_btn)
    Button mSubmitShopBagBtn;
    @BindView(R.id.shop_bag_recycler_view)
     RecyclerView mShopBagRecyclerView;

    private ProgressDialog mProgressDialog;

    private ShopBagAdapter mAdapter;

    public static ShopBagDialogFragment newInstance() {

        Bundle args = new Bundle();

        ShopBagDialogFragment fragment = new ShopBagDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ShopBagDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RecyclerView.LayoutParams.MATCH_PARENT;
        params.height = RecyclerView.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_shop_bag_dialog, container, false);
        ButterKnife.bind(this,view);
        mShopBagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        updateUI();



        return view;
    }
    private void updateUI(){
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_product));
        mProgressDialog.show();
        List<String> productIdies = ProductLab.getInstance().getShoppingBag();

        RetrofitClientInstance.getRetrofitInstance().create(Api.class).getReleatedProducts(productIdies.toString())
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if(!response.isSuccessful()){
                            Toast.makeText(getActivity(), R.string.problem_response, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<Product> products = response.body();
                        if(products==null||products.size()<=0)
                            new GenerateSnackBar(getActivity(),R.string.no_item).getSnackbar().show();
                        if(mAdapter==null){
                            mAdapter = new ShopBagAdapter(products);
                            mShopBagRecyclerView.setAdapter(mAdapter);
                        }else {
                            mAdapter.setProductList(products);
                            mAdapter.notifyDataSetChanged();
                        }
                        calculateProductsPrice(products);
                        mProgressDialog.cancel();
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        Toast.makeText(getActivity(), R.string.problem_response, Toast.LENGTH_SHORT).show();
                    }
                });


    }
    private void calculateProductsPrice(List<Product> list){
        long finalValue = 0 ;
        for(Product product:list){
            long price = Long.valueOf(product.getPrice());
            finalValue += price;
        }

        mShopFinalSumTextView.setText(getString(R.string.price_format,finalValue + " "));
    }
     class ShopBagViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_shop_bag_image)
        ImageView mProductImage;
        @BindView(R.id.product_shop_bag_name_text_view)
         TextView mProductName;
        @BindView(R.id.full_price_text_view)
         TextView mProductFullPrice;
        @BindView(R.id.final_price_text_view)
         TextView mProductFinalPrice;
        @BindView(R.id.delete_product_bag_btn)
         Button mDeleteProductBtn;

        private Product mProduct;


        public ShopBagViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mDeleteProductBtn.setOnClickListener(v -> {
                ProductLab.getInstance().deleteFromBag(mProduct.getId());
                updateUI();
            });
            mProductImage.setOnClickListener(v -> {
                Intent intent = ProductInfoActivity.newIntent(getActivity(),mProduct.getId());
                startActivity(intent);
            });

        }
        public void bind(Product product){
            mProduct = product;
            Picasso.get().load(product.getImages().get(0).getPath()).into(mProductImage);
            mProductName.setText(product.getName());
            mProductFullPrice.setText(product.getPrice());
            mProductFinalPrice.setText(product.getRegularPrice());
        }
    }
    private class  ShopBagAdapter extends RecyclerView.Adapter<ShopBagViewHolder>{
        private List<Product> mProductList;

        public ShopBagAdapter(List<Product> productList) {
            mProductList = productList;
        }

        public void setProductList(List<Product> productList) {
            mProductList = productList;
        }

        @NonNull
        @Override
        public ShopBagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.shop_bag_list_item,parent,false);

            return new ShopBagViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShopBagViewHolder holder, int position) {
            Product product = mProductList.get(position);
            holder.bind(product);
        }

        @Override
        public int getItemCount() {
            return mProductList.size();
        }
    }

}
