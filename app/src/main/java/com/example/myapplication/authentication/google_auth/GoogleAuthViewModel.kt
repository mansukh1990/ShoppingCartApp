package com.example.myapplication.authentication.google_auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AuthenticationRepository
import com.example.myapplication.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    private val _googleAuthLiveData = MutableLiveData<Resource<Unit?>>()
    val googleAuthLiveData get() = _googleAuthLiveData

    private fun firebaseAuthWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            _googleAuthLiveData.postValue(authenticationRepository.signInWithCredential(credential))
        }
    }

    fun handleGoogleAuthRequest(task: Task<GoogleSignInAccount>, errorMsg: String) {
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuthWithGoogle(credential)
        } catch (e: ApiException) {
            Log.e("TAG", "handleGoogleAuthRequest: $e")
            _googleAuthLiveData.value = Resource.Error(errorMsg)
        }
    }

}