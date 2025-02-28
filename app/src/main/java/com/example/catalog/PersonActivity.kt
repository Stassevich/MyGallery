package com.example.catalog

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PersonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)

        val loginTextView : TextView = findViewById(R.id.person_login_val)
        val emailTextView : TextView = findViewById(R.id.person_email_val)
        val ageTextView : TextView = findViewById(R.id.person_age_val)
        val nameTextView : TextView = findViewById(R.id.person_name_val)
        val addressTextView : TextView = findViewById(R.id.person_address_val)
        val cityTextView : TextView = findViewById(R.id.person_city_val)
        val countryTextView : TextView = findViewById(R.id.person_country_val)
        val professionTextView : TextView = findViewById(R.id.person_profession_val)
        val interestsTextView : TextView = findViewById(R.id.person_interests_val)
        val maritalStatusTextView : TextView = findViewById(R.id.person_marital_status_val)
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val database: DatabaseReference = FirebaseDatabase.getInstance(Secret.linkTo).getReference("Users")

            //Получаем данные пользователя
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        //Извлечение данных
                        val login: String? = snapshot.child("username").getValue(String::class.java)
                        val email: String? = snapshot.child("email").getValue(String::class.java)
                        val age: String? = snapshot.child("age").getValue(String::class.java)
                        val name: String? = snapshot.child("name").getValue(String::class.java)
                        val address: String? = snapshot.child("address").getValue(String::class.java)
                        val city: String? = snapshot.child("city").getValue(String::class.java)
                        val country: String? = snapshot.child("country").getValue(String::class.java)
                        val profession: String? = snapshot.child("profession").getValue(String::class.java)
                        val interests: String? = snapshot.child("interests").getValue(String::class.java)
                        val maritalStatus: String? = snapshot.child("maritalStatus").getValue(String::class.java)

                        //Заполнение полей
                        loginTextView.text = login ?: "Логин не найден"
                        emailTextView.text = email ?: "Email не найден"
                        ageTextView.text = age ?: "Возраст не найден"
                        nameTextView.text = name ?: "Имя не найдено"
                        addressTextView.text = address ?: "Адресс не найден"
                        cityTextView.text = city ?: "Город не найден"
                        countryTextView.text = country ?: "Страна не найдена"
                        professionTextView.text = profession ?: "Профессия не найдена"
                        interestsTextView.text = interests ?: "Интересы не найдены"
                        maritalStatusTextView.text = maritalStatus ?: "Семейное положение не найдено"
                    } else {
                        loginTextView.text ="Логин не найден"
                        emailTextView.text ="Email не найден"
                        ageTextView.text ="Возраст не найден"
                        nameTextView.text ="Имя не найдено"
                        addressTextView.text ="Адресс не найден"
                        cityTextView.text ="Город не найден"
                        countryTextView.text ="Страна не найдена"
                        professionTextView.text ="Профессия не найдена"
                        interestsTextView.text ="Интересы не найдены"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        } else {

            //Пользователь не авторизован
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        //Обработка кнопки изменения значений
        val buttonChange: Button = findViewById(R.id.person_button_change)
        buttonChange.setOnClickListener{
            val intent = Intent(this, EditPersonActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Обработка кнопки выхода из аккаунта
        val buttonExit: Button = findViewById(R.id.person_button_exit)
        buttonExit.setOnClickListener{
            auth.signOut()
            AppViewModel.filteredItems.clear()
            AppViewModel.galleryItems.clear()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Обработка кнопки удаления профиля
        val buttonDelete: Button = findViewById(R.id.person_button_delete)
        buttonDelete.setOnClickListener{

            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                user.delete().addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful) {
                        Toast.makeText(this, "Аккаунт успешно удален", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val deleteException = deleteTask.exception
                        Toast.makeText(this, "Ошибка удаления аккаунта: ${deleteException?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Пользователь не аутентифицирован", Toast.LENGTH_SHORT).show()
            }
        }


        //Функция для отображения диалога изменения пароля
        fun showChangePasswordDialog() {
            val user = auth.currentUser

            if (user != null) {
                //Создаем EditText для ввода нового пароля
                val newPasswordInput = EditText(this)
                newPasswordInput.hint = "Введите новый пароль"

                //Создаем диалог
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Изменить пароль")
                    .setMessage("Введите новый пароль:")
                    .setView(newPasswordInput)
                    .setPositiveButton("Изменить") { dialogInterface: DialogInterface, i: Int ->
                        val newPassword = newPasswordInput.text.toString()


                        user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(this, "Пароль успешно изменен", Toast.LENGTH_SHORT).show()
                            } else {
                                val updateException = updateTask.exception
                                Toast.makeText(this, "Ошибка изменения пароля: ${updateException?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    .setNegativeButton("Отмена") { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                    .create()

                dialog.show()
            } else {
                Toast.makeText(this, "Пользователь не аутентифицирован", Toast.LENGTH_SHORT).show()
            }
        }
        val buttonChangePassword : Button = findViewById(R.id.person_button_change_password)
        buttonChangePassword.setOnClickListener{
            showChangePasswordDialog()
        }
        val buttonBack : ImageButton = findViewById(R.id.person_button_back)
        buttonBack.setOnClickListener{
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}