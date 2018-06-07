package com.joaoeudes7.multas.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.joaoeudes7.multas.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()

        btn_Register.setOnClickListener {
            val email = edt_email.text.toString()
            val pass = edt_pass.text.toString()

            if (validateForm(email, pass)) {
                register(email, pass)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.signOut()
    }

    private fun register(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showMessage("Conta Criada com Sucesso!")
            } else {
                showMessage("Algo de errado aconteceu!")
            }
        }
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

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}
