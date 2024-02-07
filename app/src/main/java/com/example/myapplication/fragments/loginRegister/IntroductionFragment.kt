package com.example.myapplication.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.activities.ShoppingActivity
import com.example.myapplication.authentication.phone_auth.PhoneAuthViewModel
import com.example.myapplication.databinding.FragmentIntroductionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {

    private lateinit var binding: FragmentIntroductionBinding
    private val authViewModel by viewModels<PhoneAuthViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonStart.setOnClickListener {
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
        binding.buttonStart1.setOnClickListener {
            val isFirstLogIn = authViewModel.checkIfFirstAppOpened()
            if(!isFirstLogIn){
                checkIfUserLoggedIn()
            }
        }
    }

    private fun checkIfUserLoggedIn() {
        val isLoggedIn = authViewModel.checkIfUserLoggedIn()
        if(isLoggedIn){
            navigateToMainFragment()
        }else{
            findNavController().navigate(R.id.action_introductionFragment_to_authFragment)
        }

    }

    private fun navigateToMainFragment() {
        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}