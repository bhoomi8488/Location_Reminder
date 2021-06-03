package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_authentication)

        observeAuthenticationState()
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
    }


    private fun observeAuthenticationState() {
        //get current user:
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            // if already logged in, redirect to Reminders list screen
            startActivity(Intent(this, RemindersActivity::class.java))
            finish()
        } else {
            // only show the UI if the user really needs to sign in
            // this is to avoid the ugly ui issue where the signin buttons
            // appear briefly
            val binding = DataBindingUtil.setContentView<ActivityAuthenticationBinding>(this,R.layout.activity_authentication)
            setContentView(binding.root) // inflate layout
            //launchSignInFlow() // set OnClickListener's
        }

        // Handle the case where the user were previously not signed in
        // Having a livedata means that the user would be taken to the next screen automatically as soon as they are signed in
        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            if (authenticationState.equals(AuthenticationViewModel.AuthenticationState.AUTHENTICATED)) {
                val intent = Intent(this, RemindersActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                btnLogin.setOnClickListener {
                    launchSignInFlow()
                }
            }
        })
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in user.
                val intent = Intent(this, RemindersActivity::class.java)
                startActivity(intent)
                finish()
                Log.i(
                    TAG,
                    "Successfully signed in user " +
                            "${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                if (response == null) {
                    Log.i(TAG, "User cancelled Login flow")
                } else {
                    Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
                }
            }
        }
    }

    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
        const val TAG = "AuthenticationActivity"
    }

}
