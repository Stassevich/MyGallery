package com.example.catalog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        val linkToReg: TextView = findViewById(R.id.to_auth_page_text)
        val regButton : Button = findViewById(R.id.reg_button_request)
        val loginText : TextView = findViewById(R.id.reg_login)
        val passwordText : TextView = findViewById(R.id.reg_password)
        val emailText : TextView = findViewById(R.id.reg_email)

        //Нажатин на кнопку регистрации
        regButton.setOnClickListener {

            if (loginText.text.length != 0 && emailText.text.length != 0 && passwordText.text.length != 0) {

                //Проверка не авторизован ли пользователь
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    auth.signOut()
                    AppViewModel.filteredItems.clear()
                    AppViewModel.galleryItems.clear()
                }

                //Создание пользователя
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    emailText.text.toString(),
                    passwordText.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        //Заданание стартовых значений для таблицы пользователя
                        val userMap: HashMap<String, Any> = HashMap()
                        userMap["username"] = loginText.text.toString()
                        userMap["email"] = emailText.text.toString()
                        userMap["age"] = "-"
                        userMap["favourites"] = ArrayList<String>()
                        userMap["name"] = "-"
                        userMap["address"] = "-"
                        userMap["city"] = "-"
                        userMap["country"] = "-"
                        userMap["profession"] = "-"
                        userMap["interests"] = "-"
                        userMap["maritalStatus"] = "-"


                        //Добавление информации о пользователе в БД
                        FirebaseDatabase.getInstance(Secret.linkTo)
                            .getReference("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(userMap)
                            .addOnCompleteListener { dbTask ->
                                if (!dbTask.isSuccessful) {
                                    //Обработка ошибки
                                    val dbException = dbTask.exception
                                    Toast.makeText(
                                        this,
                                        "Ошибка записи в базу данных: ${dbException?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                                val intent = Intent(this, GalleryActivity::class.java)
                                startActivity(intent)
                                finish()

                            }
                    }

                }
            }
        }

        //Переход на страницу авторизации
        linkToReg.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}