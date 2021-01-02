package com.alsharany.firebaseapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_sign_in.password

lateinit var auth:FirebaseAuth
class SignUpFragment : Fragment() {
    lateinit var emailAderss: EditText
    lateinit var userName: EditText
    lateinit var Passward: EditText
    lateinit var PasswardConfoirm: EditText
    lateinit var signUpBTN: Button
    lateinit var signInTextView: TextView
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var phoneSignIn: Button
    lateinit var googleSignIn: Button
    lateinit var googleSignInClient: GoogleSignInClient
    private var goToAnotherFragment: homePageFragment.GoToAnotherFragment?=null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToAnotherFragment=context as homePageFragment.GoToAnotherFragment
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth  = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

         val view=inflater.inflate(R.layout.fragment_sign_up, container, false)

        emailAderss= view.findViewById(R.id.email)
        userName= view.findViewById(R.id.email)
        Passward =view.findViewById(R.id.password)
        PasswardConfoirm=view.findViewById(R.id.confirm_password)
        signUpBTN=view.findViewById(R.id.signup)
        signInTextView=view.findViewById(R.id.have_account)
        phoneSignIn = view.findViewById(R.id.phone_Method)
        googleSignIn = view.findViewById(R.id.google_account_Method)
        return view
    }

    override fun onStart() {
        super.onStart()
        signUpBTN.setOnClickListener {
            if( !(userName.text.isNullOrBlank() ||userName.text.isNullOrEmpty())&& userName.length() > 3){
                if( !(emailAderss.text.isNullOrBlank() ||emailAderss.text.isNullOrEmpty()))

                {
                    if( !(Passward.text.isNullOrBlank() ||Passward.text.isNullOrEmpty())&& Passward.length()>6){
                        createUser()
                    }else{
                        Toast.makeText(requireContext(), "passward must be not empty", Toast.LENGTH_SHORT).show()
                        Passward.apply {
                            hint="invalid passward"
                            setHintTextColor(Color.RED)
                        }

                    }

                }else
                {
                    Toast.makeText(requireContext(), "email must be not empty", Toast.LENGTH_SHORT).show()
                    emailAderss.apply {
                        hint = "invalid email"
                        setHintTextColor(Color.RED)

                    }
                }
            }else{
                Toast.makeText(requireContext(), "userName must be not empty and contain 3 character at least ", Toast.LENGTH_SHORT).show()
                Passward.apply {
                    hint="invalid user name"
                    setHintTextColor(Color.RED)
                }
            }



        }
       signInTextView.setOnClickListener {
           goToAnotherFragment?.OnTransferToFragment(SignInFragment.newInstance())

       }

        googleSignIn.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
            googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            signInByGoogleAccount()
        }
    }
    private fun createUser() {
       auth.createUserWithEmailAndPassword(emailAderss.text.toString(),password.text.toString())
            .addOnCompleteListener(requireActivity()){
                if(it.isSuccessful){
                    val intent = Intent(requireContext(), MainActivity()::class.java)
                    LoginPref.setStoredQuery(requireContext(),"login successful")
                    startActivity(intent)
                    Toast.makeText(requireContext(),"create user successfully", Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT).show()
            }
    }
    private fun signInByGoogleAccount() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, SignInFragment.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SignInFragment.RC_SIGN_IN) {
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

        @JvmStatic
        fun newInstance() =
            SignUpFragment().apply {
            }
    }
}