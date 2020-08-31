package com.mtjin.nomoneytrip.views

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mtjin.nomoneytrip.R
import com.mtjin.nomoneytrip.data.home.Product
import com.mtjin.nomoneytrip.data.local_page.TourIntroduce
import com.mtjin.nomoneytrip.views.home.HomeHashTagAdapter
import com.mtjin.nomoneytrip.views.home.ProductHashTagAdapter
import com.mtjin.nomoneytrip.views.home.HomeProductAdapter
import com.mtjin.nomoneytrip.views.localpage.LocalPageAdapter

@BindingAdapter("urlImage")
fun ImageView.setUrlImage(url: String) {
    Glide.with(this).load(url)
        .thumbnail(0.1f)
        .error(R.drawable.img_product)
        .into(this)
}

@BindingAdapter("setItems")
fun RecyclerView.setAdapterItems(items: List<Any>?) {
    when (adapter) {
        is LocalPageAdapter -> {
            items?.let {
                with(adapter as LocalPageAdapter) {
                    clear()
                    addItems(it as List<TourIntroduce>)
                }
            }
        }
        is HomeProductAdapter -> {
            items?.let {
                with(adapter as HomeProductAdapter) {
                    clear()
                    addItems(it as List<Product>)
                }
            }
        }
        is ProductHashTagAdapter -> {
            items?.let {
                with(adapter as ProductHashTagAdapter) {
                    clear()
                    addItems(it as List<String>)
                }
            }
        }
        is HomeHashTagAdapter -> {
            items?.let {
                with(adapter as HomeHashTagAdapter) {
                    clear()
                    addItems(it as List<String>)
                }
            }
        }
    }
}