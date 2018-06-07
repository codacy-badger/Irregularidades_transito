package com.joaoeudes7.multas.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.joaoeudes7.multas.MainActivity
import com.joaoeudes7.multas.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        btn_Sign.setOnClickListener {
            val email = edt_email.text.toString()
            val pass = edt_pass.text.toString()

            if (validateForm(email, pass)) {
                signIn(email, pass)
            }
        }
        btn_Register.setOnClickListener { goToRegister() }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        showMessage(currentUser?.email.toString())
    }

    private fun validateForm(email: String, password: String): Boolean {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Digite o endereço de email!", Toast.LENGTH_SHORT).show()
            return false
        } else if (!isEmailValid(email)) {
            Toast.makeText(applicationContext, "Digite um email válido", Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Digite a senha!", Toast.LENGTH_SHORT).show()
            return false
        } else if (password.length < 6) {
            Toast.makeText(applicationContext, "Senha muito pequena, no mínimmo 6 caracteres!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun signIn(email: String, password: String) {
        showMessage("Authenticating...")

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("id", firebaseAuth.currentUser?.email)
                startActivity(intent)
            } else {
                showMessage("Error: ${task.exception?.message}")
            }
        })
    }

    private fun goToRegister() {
        val register = Intent(this@LoginActivity, RegisterActivity::class.java)
        return startActivity(register)
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}