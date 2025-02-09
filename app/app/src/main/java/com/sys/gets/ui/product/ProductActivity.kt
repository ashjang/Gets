package com.sys.gets.ui.product

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.chip.Chip
import com.sys.gets.R
import com.sys.gets.data.*
import com.sys.gets.databinding.ActivityProductBinding
import com.sys.gets.network.Network

const val PRODUCT_ACTIVITY_TAG = "p_act"

class ProductActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ID = "ID"
    }

    private lateinit var binding: ActivityProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra(EXTRA_ID, 0)
        var isChecked = false

        if (id != 0) {
            val productRequest = JsonObjectRequest(
                Request.Method.GET,
                "${Network.PRODUCT_URL}/$id",
                null,
                { response ->
                    if (response.getBoolean("result")) {

                        binding.apply {
                            category.text = Category.values()
                                .filter { response.getInt("type") == it.code }
                                .map { getString(it.resID) }.first()

                            name.text = response.getString("name")
                            brand.text = response.getString("brand")
                            price.text = Format.currency(response.getInt("price"))
                            season.text = Season.values()
                                .filter { (response.getInt("season") and it.code) != 0 }
                                .joinToString { getString(it.resID) }
                            gender.text = Gender.values()
                                .filter { (response.getInt("gender") and it.code) != 0 }
                                .joinToString { getString(it.resID) }
                            size.text = response.getString("size").replace(",", ", ")
                            color.text = Color.values()
                                .filter { (response.getInt("color") and it.code) != 0 }
                                .joinToString { getString(it.resID) }
                            material.text = Material.values()
                                .filter { (response.getInt("fiber") and it.code) != 0 }
                                .joinToString { getString(it.resID) }
                            age.text =
                                Age.values().filter { (response.getInt("age") and it.code) != 0 }
                                    .joinToString { getString(it.resID) }
                            Style.values().filter { (response.getInt("style") and it.code) != 0 }
                                .forEach {
                                    style.addView(Chip(this@ProductActivity).apply { setText(it.resID) })
                                }
                        }

                        val imageRequest = ImageRequest(
                            "${Network.PRODUCT_IMAGE_URL}/${response.getString("image1ID")}",
                            { bitmap ->
                                bitmap?.run {
                                    binding.productImage.setImageBitmap(bitmap)
                                }
                            },
                            0,
                            0,
                            ImageView.ScaleType.FIT_CENTER,
                            Bitmap.Config.RGB_565,
                            null
                        )

                        imageRequest.tag = PRODUCT_ACTIVITY_TAG
                        Network.getInstance(this@ProductActivity)
                            .addToRequestQueue(imageRequest)

                        binding.favoriteButton.setOnClickListener {
                            if (!isChecked) { // 체크 안되어있는 경우
                                Network.addSimpleRequest(
                                    this,
                                    PRODUCT_ACTIVITY_TAG,
                                    Network.PRODUCT_FAVORITE_URL,
                                    id
                                ) {
                                    isChecked = true
                                    binding.favoriteIcon.setColorFilter(
                                        ContextCompat.getColor(
                                            this,
                                            R.color.favoriteRed
                                        ), android.graphics.PorterDuff.Mode.SRC_IN
                                    )
                                }
                            } else { // 체크 되어있는 경우
                                Network.addSimpleRequest(
                                    this,
                                    PRODUCT_ACTIVITY_TAG,
                                    Network.PRODUCT_UNFAVORITE_URL,
                                    id
                                ) {
                                    isChecked = false
                                    binding.favoriteIcon.setColorFilter(
                                        ContextCompat.getColor(
                                            this,
                                            R.color.favoriteGrey
                                        ), android.graphics.PorterDuff.Mode.SRC_IN
                                    )
                                }
                            }
                            Log.e("TAG", "$isChecked ${binding.favoriteIcon.backgroundTintList}")
                        }

                        Network.addSimpleRequest(
                            this,
                            PRODUCT_ACTIVITY_TAG,
                            Network.PRODUCT_CHECK_FAVORITE_URL,
                            id
                        ) {
                            isChecked = true
                            binding.favoriteIcon.setColorFilter(
                                ContextCompat.getColor(
                                    this,
                                    R.color.favoriteRed
                                ), android.graphics.PorterDuff.Mode.SRC_IN
                            )
                            Log.e("TAG", "checked item")
                        }
                    }

                },
                null
            )
            productRequest.tag = PRODUCT_ACTIVITY_TAG
            Network.getInstance(this).addToRequestQueue(productRequest)
        }
    }

    override fun onStop() {
        super.onStop()
        Network.getInstance(this).requestQueue.apply {
            cancelAll(PRODUCT_ACTIVITY_TAG)
        }
    }
}