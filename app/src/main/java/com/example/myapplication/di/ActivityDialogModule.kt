package com.example.myapplication.di

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ImageView
import com.example.myapplication.R
import com.example.myapplication.util.Constants.LOADING_ANNOTATION
import com.example.myapplication.util.extesion.loadGif
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Named

@Module
@InstallIn(ActivityComponent::class)
class ActivityDialogModule {

    @ActivityScoped
    @Provides
    @Named(LOADING_ANNOTATION)
    fun provideLoadingDialog(@ActivityContext context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loading_dialog_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imageviewLoading = dialog.findViewById<ImageView>(R.id.loadingImageView)
        imageviewLoading.loadGif(R.drawable.carrot_loader)
        dialog.create()
        return dialog

    }
}