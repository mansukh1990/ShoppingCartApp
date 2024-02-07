package com.example.myapplication.authentication.phone_auth

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.activities.ShoppingActivity
import com.example.myapplication.data.PhoneVerificationModel
import com.example.myapplication.databinding.FragmentPhoneNumberAuthBinding
import com.example.myapplication.util.Constants.LOADING_ANNOTATION
import com.example.myapplication.util.Resource
import com.example.myapplication.util.extesion.handleKeyBoardApparition
import com.example.myapplication.util.extesion.showToast
import com.example.myapplication.util.extesion.stopKeyBoardListener
import com.example.myapplication.util.state.MainAuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.log

@AndroidEntryPoint
class PhoneNumberAuthFragment : Fragment() {

    private lateinit var binding: FragmentPhoneNumberAuthBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog: Dialog

    private val authViewModel by viewModels<PhoneAuthViewModel>()

    private var verificationId: String? = null
    private var verificationToken: PhoneAuthProvider.ForceResendingToken? = null

    private var verificationTimeOut: Long = 0

    private var validPhoneNumber: String = ""

    private val selectedCountryCode by lazy {
        navArgs<PhoneNumberAuthFragmentArgs>().value.code.toInt()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_phone_number_auth, container, false)
        binding.fragment = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            countryCodePicker.setCountryForPhoneCode(selectedCountryCode)
            backButton.setOnClickListener { closeFragment() }
        }
        observeListener()
    }

    private fun observeListener() {
        authViewModel.phoneMainAuthLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is MainAuthState.SuccessWithCredential -> {
                    authViewModel.signInWithPhoneAuthCredential(it.data)
                    authViewModel.setPhoneAuthLiveData(MainAuthState.Idle)
                    loadingDialog.hide()

                }

                is MainAuthState.SuccessWithCode -> {
                    verificationId = it.verificationId
                    verificationToken = it.verificationToken
                    navigateToCheckPhoneNumberAuthFragment(it.verificationId, it.verificationToken)
                    authViewModel.setPhoneAuthLiveData(MainAuthState.Idle)
                    loadingDialog.hide()

                }

                is MainAuthState.Error -> {
                    loadingDialog.hide()
                    when (it.error) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            showToast(getString(R.string.please_check_internet_connection))
                        }

                        else -> {
                            showToast(getString(R.string.errorMessage))

                        }
                    }

                }

                is MainAuthState.Loading -> {
                    loadingDialog.show()

                }

                is MainAuthState.Idle -> {
                    loadingDialog.hide()

                }

            }
            authViewModel.signInStatusLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Error -> {
                        loadingDialog.hide()
                        showToast(it.message!!)

                    }

                    is Resource.Success -> {
                        loadingDialog.hide()
                        navigateToMainFragment()

                    }

                    is Resource.Loading -> {
                        loadingDialog.show()

                    }

                    else -> Unit
                }
            }
        }
    }

    private fun navigateToMainFragment() {
        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    fun checkPhoneNumber() {
        val phoneNumber = binding.phoneNumberEditText.text.toString().trim()
        val countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus

        when {
            phoneNumber.isEmpty() -> {
                showToast(getString(R.string.please_add_your_phone_number_to_continue))
            }

            else -> {
                if ( phoneNumber.length == 10)
                    validPhoneNumber = "$countryCode$phoneNumber"
                if (verificationTimeOut == 0L || verificationTimeOut < System.currentTimeMillis()) {
                    sendFirstSMSVerification(validPhoneNumber)
                }

            }
        }

    }

    private fun sendFirstSMSVerification(validPhoneNumber: String) {
        authViewModel.setPhoneAuthLiveData(MainAuthState.Loading)
        Log.e("TAG before", "mobile number:${validPhoneNumber} ", )
        signWithPhoneNumber(validPhoneNumber)

    }

    private fun signWithPhoneNumber(validPhoneNumber: String) {
        Log.e("TAG", "mobile number:${validPhoneNumber} ", )
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(validPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(authViewModel.phoneAuthCallBack())
            .build()
        verificationTimeOut = (System.currentTimeMillis() + 60000L)
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun navigateToCheckPhoneNumberAuthFragment(
        verificationId: String,
        verificationToken: PhoneAuthProvider.ForceResendingToken
    ) {
        val verificationModel =
            PhoneVerificationModel(verificationId, verificationToken, validPhoneNumber)
        val action =
            PhoneNumberAuthFragmentDirections.actionPhoneNumberAuthFragmentToCheckCodeAuthFragment(
                verificationModel
            )
        findNavController().navigate(action)
    }

    private fun closeFragment() {
        findNavController().popBackStack()
    }

    override fun onResume() {
        super.onResume()
        handleKeyBoardApparition(binding.authByPhoneNumberFAB)
    }

    override fun onStop() {
        super.onStop()
        stopKeyBoardListener()
    }
}