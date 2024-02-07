package com.example.myapplication.data

import android.os.Parcelable
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.parcelize.Parcelize

data class User(
    val firstname : String,
    val lastName : String,
    val email : String,
    val imagePath : String = ""

){
    constructor():this("","" ,"","")
}

@Parcelize
data class PhoneVerificationModel (
    val verificationId: String,
    val verificationToken: PhoneAuthProvider.ForceResendingToken,
    val phoneNumber: String
): Parcelable
