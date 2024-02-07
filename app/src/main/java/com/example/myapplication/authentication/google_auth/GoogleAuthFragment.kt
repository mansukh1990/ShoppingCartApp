package com.example.myapplication.authentication.google_auth

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.example.myapplication.activities.ShoppingActivity
import com.example.myapplication.databinding.FragmentGoogleAuthBinding
import com.example.myapplication.util.Constants.LOADING_ANNOTATION
import com.example.myapplication.util.Resource
import com.example.myapplication.util.extesion.closeFragment
import com.example.myapplication.util.extesion.showToast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.util.DataUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class GoogleAuthFragment : Fragment() {
    private lateinit var binding: FragmentGoogleAuthBinding
    private val googleAuthViewModel by viewModels<GoogleAuthViewModel>()

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_google_auth, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
        configureGoogleSignIn()
    }

    private fun configureGoogleSignIn() {
        val mGoogleSignInClient = GoogleSignIn.getClient(
            requireContext(), googleSignInOptions
        )
        val signIntent = mGoogleSignInClient.signInIntent
        googleLauncher.launch(signIntent)

    }

    private val googleLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            googleAuthViewModel.handleGoogleAuthRequest(task, getString(R.string.errorMessage))

        }

    private fun observeListener() {
        googleAuthViewModel.googleAuthLiveData.observe(viewLifecycleOwner) { userState ->
            when (userState) {
                is Resource.Loading -> {
                    loadingDialog.show()

                }

                is Resource.Success -> {
                    navigateToMainFragment()
                    loadingDialog.hide()
                }

                is Resource.Error -> {
                    loadingDialog.hide()
                    showToast(userState.message!!)
                    closeFragment()
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
}