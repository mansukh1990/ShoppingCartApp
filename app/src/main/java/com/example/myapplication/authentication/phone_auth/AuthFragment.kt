package com.example.myapplication.authentication.phone_auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private lateinit var binding: FragmentAuthBinding
    private var selectedCodeCountry = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false)
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignInWithGoogle.setOnClickListener {
            navigateToGoogleAuthFragment()

        }

        if (selectedCodeCountry >= 0) {
            binding.countryCodePicker.setCountryForPhoneCode(selectedCodeCountry.toInt())
        }

    }


    fun navigateToPhoneAuthFragment() {
        selectedCodeCountry = binding.countryCodePicker.selectedCountryCodeAsInt.toLong()
        val action =
            AuthFragmentDirections.actionAuthFragmentToPhoneNumberAuthFragment(selectedCodeCountry)
        findNavController().navigate(action)

    }

    fun navigateToGoogleAuthFragment() {
       val action = AuthFragmentDirections.actionAuthFragmentToGoogleAuthFragment4()
        findNavController().navigate(action)
    }

}