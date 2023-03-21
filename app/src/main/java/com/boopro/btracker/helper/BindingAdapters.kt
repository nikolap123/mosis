package com.boopro.btracker.helper

import androidx.databinding.BindingAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily

@BindingAdapter("imgCornerRadius")
fun setCornerRadius(shapeableImageView: ShapeableImageView, cornerRadius: Float) {
    shapeableImageView.shapeAppearanceModel = shapeableImageView.shapeAppearanceModel
        .toBuilder()
        .setAllCorners(CornerFamily.ROUNDED, cornerRadius)
        .build();
}

