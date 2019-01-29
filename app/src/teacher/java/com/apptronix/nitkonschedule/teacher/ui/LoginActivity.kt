package com.apptronix.nitkonschedule.teacher.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.model.User
import com.apptronix.nitkonschedule.teacher.service.AuthService
import com.apptronix.nitkonschedule.teacher.service.AuthService.ACTION_GMAIL_LOGIN
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    lateinit var _emailText: EditText
    lateinit var _passwordText: EditText
    lateinit var textDummyHintEmail: TextView
    lateinit var textDummyHintPassword: TextView
    lateinit var _btnLogin: AppCompatButton
    internal var RC_SIGN_IN = 1
    internal var progressDialog: ProgressDialog? = null
    internal var mGoogleApiClient: GoogleApiClient? = null
    private var user: User ? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LoginEvent) {

        Timber.i(event.message)

        when (event.message) {
            "LoginSuccessful" -> {

                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
            "LoginFailed" -> {

                Toast.makeText(this, R.string.server_authentication_failed, Toast.LENGTH_LONG).show()

            }
            "ServerUnreachable" -> {

                Toast.makeText(this, R.string.server_unreachable_msg, Toast.LENGTH_LONG).show()

            }
            else -> {

                Toast.makeText(this, event.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }


    override fun onResume() {
        super.onResume()

        if (user!!.refreshToken!=null) {
            Timber.i("Access Token %s", user!!.accessToken)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        _emailText = findViewById(R.id.input_email)
        _passwordText = findViewById(R.id.input_password)
        textDummyHintEmail = findViewById(R.id.text_dummy_hint_username)
        textDummyHintPassword = findViewById(R.id.text_dummy_hint_password)
        _btnLogin = findViewById(R.id.btn_login);


        // Username
        _emailText.setOnFocusChangeListener({ v, hasFocus ->
            if (hasFocus) {
                Handler().postDelayed({
                    // Show white background behind floating label
                    textDummyHintEmail.setVisibility(View.VISIBLE)
                }, 100)
            } else {
                // Required to show/hide white background behind floating label during focus change
                if (_emailText.getText().length > 0)
                    textDummyHintEmail.setVisibility(View.VISIBLE)
                else
                    textDummyHintEmail.setVisibility(View.INVISIBLE)
            }
        })

        // Password
        _passwordText.setOnFocusChangeListener({ v, hasFocus ->
            if (hasFocus) {
                Handler().postDelayed({
                    // Show white background behind floating label
                    textDummyHintPassword.setVisibility(View.VISIBLE)
                }, 100)
            } else {
                // Required to show/hide white background behind floating label during focus change
                if (_passwordText.getText().length > 0)
                    textDummyHintPassword.setVisibility(View.VISIBLE)
                else
                    textDummyHintPassword.setVisibility(View.INVISIBLE)
            }
        })
        btn_login.setOnClickListener(this)

        user=User(this)
        if (user!!.refreshToken!=null) {
            Timber.i("Access Token %s", user!!.accessToken)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        sign_in_button!!.setOnClickListener(this)
    }

    fun login() {

        if (!validate()) {
            onLoginFailed()
            return
        }


        Timber.i("Login clcikd ")
        val email = _emailText.getText().toString()
        val password = _passwordText.getText().toString()

        val intent = Intent(this, AuthService::class.java)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        intent.action = AuthService.ACTION_LOGIN
        startService(intent)

    }

    override fun onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true)
    }

    fun onLoginSuccess() {
        btn_login.isEnabled = true
        finish()
    }

    fun onLoginFailed() {
        Toast.makeText(baseContext, R.string.g_login_failed, Toast.LENGTH_LONG).show()

        btn_login.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val email = input_email.text.toString()
        val password = input_password.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.error = getString(R.string.enter_valid_email)
            valid = false
        } else {
            input_email.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            input_password.error = getString(R.string.password_error)
            valid = false
        } else {
            input_password.error = null
        }

        return valid
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

        Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_LONG).show()

    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.sign_in_button -> {
                signIn()
            }
            R.id.btn_login -> {
                login()
            }
        }
    }

    private fun signIn() {

        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Timber.i("handleSignInResult: %s", result.isSuccess)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount

            val toastText = "Hi " + acct!!.displayName!!
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()

            user!!.makeUser(this, acct)

            val loginIntent = Intent(this, AuthService::class.java)
            loginIntent.action = ACTION_GMAIL_LOGIN
            loginIntent.putExtra("idToken", acct.idToken)
            startService(loginIntent)


        } else {
            Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show()
        }
    }

    class LoginEvent(var message: String)

}