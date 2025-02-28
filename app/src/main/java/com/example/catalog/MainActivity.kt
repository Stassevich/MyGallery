package com.example.catalog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val linkToReg: TextView = findViewById(R.id.to_reg_page_text)
        val authButton : Button = findViewById(R.id.auth_button_in)
        val loginText : TextView = findViewById(R.id.auth_login);
        val passwordText : TextView = findViewById(R.id.auth_password);

        authButton.setOnClickListener{


            //Проверка не авторизован ли пользователь
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            if (currentUser != null) {
                auth.signOut()
                AppViewModel.filteredItems.clear()
                AppViewModel.galleryItems.clear()
            }

            //Вход пользователя
            auth.signInWithEmailAndPassword(loginText.text.toString(), passwordText.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Вход выполнен успешно
                        val intent = Intent(this, GalleryActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {

                        // Вход не удался
                        Toast.makeText(this, "Пользователь не вошёл: ${task.exception?.message}", Toast.LENGTH_LONG).show()

                    }
                }
        }

        //Переход на страницу регистрации
        linkToReg.setOnClickListener{
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}