package com.alsharany.firebaseapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_sign_in.*


class SignInFragment : Fragment() {
    lateinit var emailAddress: EditText
    lateinit var Password: EditText
    lateinit var signUpTextView: TextView
    lateinit var login: Button
    lateinit var phoneSignIn: Button
    lateinit var googleSignIn: Button
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth
    private var goToAnotherFragment: homePageFragment.GoToAnotherFragment?=null




    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToAnotherFragment=context as homePageFragment.GoToAnotherFragment
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        emailAddress = view.findViewById<EditText>(R.id.email)
        Password = view.findViewById<EditText>(R.id.password)
        login = view.findViewById<Button>(R.id.signup)
        phoneSignIn = view.findViewById(R.id.phone_btn)
        googleSignIn = view.findViewById(R.id.google_btn)
        signUpTextView = view.findViewById(R.id.signUpTextView)
        return view
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        googleSignIn.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
            googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            signInByGoogleAccount()
        }
        signUpTextView.setOnClickListener {
            goToAnotherFragment?.OnTransferToFragment(SignUpFragment.newInstance())

        }
        login.setOnClickListener {
            if (!(emailAddress.text.isNullOrBlank() || emailAddress.text.isNullOrEmpty())) {
                if (!(Password.text.isNullOrBlank() || Password.text.isNullOrEmpty()) && Password.length() > 6) {
                    signin()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "passward must be not empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    Password.apply {
                        hint = "invalid passward"
                        setHintTextColor(Color.RED)
                    }

                }

            } else {
                Toast.makeText(requireContext(), "email must be not empty", Toast.LENGTH_SHORT)
                    .show()
                emailAddress.apply {
                    hint = "invalid email"
                    setHintTextColor(Color.RED)

                }


            }
        }
    }
    private fun signin() {
        auth.signInWithEmailAndPassword(emailAddress.text.toString(), Password.text.toString())
            .addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "login successfully", Toast.LENGTH_SHORT)
                        .show()
                    LoginPref.setStoredQuery(requireContext(),"login successful")
                    val intent = Intent(requireContext(), MainActivity()::class.java)
                    startActivity(intent)
                } else
                    Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT)
                        .show()
            }
    }

    private fun signInByGoogleAccount() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("signinActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("signinActivity", "Google sign in failed", e)
                    // ...
                }
            } else
                Log.w("signinActivity", "Google sign in failed", exception)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("signinActivity", "signInWithCredential:success")
                    LoginPref.setStoredQuery(requireContext(),"login successful")
                    val intent = Intent(requireContext(), MainActivity()::class.java)
                    startActivity(intent)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("signinActivity", "signInWithCredential:failure", task.exception)

                    // updateUI(null)
                }

                // ...
            }
    }

    companion object {
        const val RC_SIGN_IN = 120

        @JvmStatic
        fun newInstance() = SignInFragment()
    }
}