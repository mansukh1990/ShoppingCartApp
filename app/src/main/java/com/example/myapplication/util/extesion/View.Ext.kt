package com.example.myapplication.util.extesion

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide



fun ImageView.loadGif(gifImage : Int){
Glide.with(this.context).asGif().load(gifImage).into(this)
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}