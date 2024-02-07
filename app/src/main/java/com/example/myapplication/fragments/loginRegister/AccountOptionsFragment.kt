package com.example.myapplication.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.activities.ShoppingActivity
import com.example.myapplication.authentication.phone_auth.PhoneAuthViewModel
import com.example.myapplication.databinding.FragmentAccountOptionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountOptionsFragment : Fragment(R.layout.fragment_account_options) {

    private lateinit var binding: FragmentAccountOptionsBinding
    private val authViewModel by viewModels<PhoneAuthViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountOptionsBinding.inflate(layoutInflater)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonLoginAccountOptions.setOnClickListener {
                val isFirstLogIn = authViewModel.checkIfFirstAppOpened()
                if(!isFirstLogIn){
                    checkIfUserLoggedIn()
                }
               // findNavController().navigate(R.id.action_accountOptionsFragment_to_loginFragment)
            }
            buttonRegisterAccountOptions.setOnClickListener {
                findNavController().navigate(R.id.action_accountOptionsFragment_to_registerFragment)
            }
        }
    }

    private fun checkIfUserLoggedIn() {
        val isLoggedIn = authViewModel.checkIfUserLoggedIn()
        if(isLoggedIn){
            navigateToMainFragment()
        }else{
            findNavController().navigate(R.id.action_accountOptionsFragment_to_loginFragment)
        }
    }

    private fun navigateToMainFragment() {
        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}