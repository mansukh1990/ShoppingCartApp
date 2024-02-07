package com.example.myapplication.authentication.phone_auth

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.activities.ShoppingActivity
import com.example.myapplication.databinding.FragmentCheckCodeAuthBinding
import com.example.myapplication.util.Constants.COUNT_DOWN_DELAY
import com.example.myapplication.util.Constants.COUNT_DOWN_INTERVAL
import com.example.myapplication.util.Constants.LOADING_ANNOTATION
import com.example.myapplication.util.Resource
import com.example.myapplication.util.extesion.closeFragment
import com.example.myapplication.util.extesion.handleKeyBoardApparition
import com.example.myapplication.util.extesion.hide
import com.example.myapplication.util.extesion.show
import com.example.myapplication.util.extesion.showToast
import com.example.myapplication.util.extesion.stopKeyBoardListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class CheckCodeAuthFragment : Fragment() {

    private lateinit var binding: FragmentCheckCodeAuthBinding
    private val args by navArgs<CheckCodeAuthFragmentArgs>()
    private val authViewModel by activityViewModels<PhoneAuthViewModel>()
    private var isResendTextViewEnabled = false
    private val verificationModel by lazy { args.verificationModel }

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_check_code_auth, container, false)
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        startMinuteCountDown()
        observeListener()
    }

    private fun observeListener() {
        authViewModel.signInStatusLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    loadingDialog.hide()
                    showToast(it.message!!)
                }

                is Resource.Success -> {
                    navigateToMainFragment()
                }

                is Resource.Loading -> {
                    loadingDialog.show()
                }

                else -> {
                    Unit
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

    fun verifyPhoneNumber() {
        val smsCode = binding.verifyCodeNumberEditText.text.toString()
        if (smsCode.isEmpty()) {
            showToast(getString(R.string.type_verification_code_first))
        } else {
            val credential =
                PhoneAuthProvider.getCredential(verificationModel.verificationId, smsCode)
            authViewModel.signInWithPhoneAuthCredential(credential)
        }
    }

    fun resendCode() {
        if (isResendTextViewEnabled) {
            changeResendTextViewsStyle(true)
            startMinuteCountDown()

        }
        resendVerificationCode()
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(verificationModel.phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(authViewModel.phoneAuthCallBack())
            .setActivity(requireActivity())
            .setForceResendingToken(verificationModel.verificationToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


    }
    override fun onResume() {
        super.onResume()
        handleKeyBoardApparition(binding.verifyCodeNumberFAB)
    }

    override fun onStop() {
        super.onStop()
        stopKeyBoardListener()
    }


    private fun startMinuteCountDown() {
        object : CountDownTimer(COUNT_DOWN_DELAY, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                binding.resendTimerTextView.text =
                    getString(R.string.countDown, (millisUntilFinished / 1000))
            }

            override fun onFinish() {
                changeResendTextViewsStyle(false)
            }

        }.start()
    }

    private fun changeResendTextViewsStyle(showTimer: Boolean) = with(binding) {
        if (showTimer) {
            resendTimerTextView.show()
            isResendTextViewEnabled = false
            resendCodeTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.offWhite
                )
            )
        } else {
            resendTimerTextView.hide()
            isResendTextViewEnabled = true
            resendCodeTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.green
                )
            )
        }


    }

    private fun initViews() {
        binding.backButton.setOnClickListener { closeFragment() }

    }
}