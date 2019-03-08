package project.com.maktab.onlinemarket.network.base;

import java.util.List;

import project.com.maktab.onlinemarket.model.customer.Customer;
import project.com.maktab.onlinemarket.model.order.Order;
import project.com.maktab.onlinemarket.network.webservices.add_customer.CustomerResponse;
import project.com.maktab.onlinemarket.model.attributes.Attributes;
import project.com.maktab.onlinemarket.model.attributes.AttributesTerms;
import project.com.maktab.onlinemarket.model.category.Category;
import project.com.maktab.onlinemarket.model.product.Product;
import project.com.maktab.onlinemarket.network.webservices.add_order.OrderResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @GET("products/?")
    Call<List<Product>> getAllProducts(@Query("orderby") String orderType);


    @GET("products/{id}/?")
    Call<Product> getProduct(@Path("id") String productId);


    @GET("products/categories/?per_page=100")
    Call<List<Category>> getAllCategories();

    @GET("products/categories/?per_page=100")
    Call<List<Category>> getSubCategories(@Query("parent") String parentId);


    @GET("products/?featured=true")
    Call<List<Product>> getFeaturedProducts();

    @GET("products/?featured=true")
    Call<List<Product>> getAllProductWithPageFeatured(@Query("page") String pageNumber, @Query("orderby") String orderBy, @Query("order") String order,
                                              @Query("attribute_term") String... attributes);



    @GET("products/?")
    Call<List<Product>> getProductsSubCategoires(@Query("page") String pageNumber, @Query("category") String categoryId
            , @Query("orderby") String orderBy, @Query("order") String order, @Query("attribute_term") String... attributes);

    @GET("products/?")
    Call<List<Product>> searchProducts(@Query("page") String pageNumber, @Query("search") String productName
            , @Query("orderby") String orderBy, @Query("order") String order, @Query("attribute_term") String... attributes);

/*
    @GET("products?")
    Call<List<Product>> getReleatedProducts(@Query("include") String... releateds);*/
    @GET("products?")
    Call<List<Product>> getReleatedProducts(@Query("include") String... realated);


    @GET("products/?")
    Call<List<Product>> getAllProductWithPage(@Query("page") String pageNumber, @Query("orderby") String orderBy, @Query("order") String order,
                                              @Query("attribute_term") String... attributes);


    @GET("products/attributes/?")
    Call<List<Attributes>> getProductAttributes(@Query("id") String productId);


    @GET("products/attributes/{id}/terms")
    Call<List<AttributesTerms>> getAttributesTerms(@Path("id") String attrId);


    @POST("customers")
    Call<CustomerResponse> addCustomer(@Body Customer customer);

    @GET("customers/{id}")
    Call<Customer> getCustomer(@Path("id") String customerId);

    @POST("orders")
    Call<OrderResponse> addOrder(@Body Order order);

    @GET("customers")
    Call<List<Customer>> getAllCustomers();



}
