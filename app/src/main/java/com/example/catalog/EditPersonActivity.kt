package com.example.catalog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditPersonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_person)

        val loginEdit : EditText = findViewById(R.id.person_login_edit)
        val ageEdit : EditText = findViewById(R.id.person_age_edit)
        val nameEdit : EditText = findViewById(R.id.person_name_edit)
        val addressEdit : EditText = findViewById(R.id.person_address_edit)
        val cityEdit : EditText = findViewById(R.id.person_city_edit)
        val countryEdit : EditText = findViewById(R.id.person_country_edit)
        val professionEdit : EditText = findViewById(R.id.person_profession_edit)
        val interestsEdit : EditText = findViewById(R.id.person_interests_edit)
        val maritalStatusEdit : EditText = findViewById(R.id.person_marital_status_edit)


        //Проверка на авторизованного пользователя, если нет то переход на авторизацию
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {

            val userId = currentUser.uid
            val database: DatabaseReference = FirebaseDatabase.getInstance(Secret.linkTo).getReference("Users")
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        //Получаем данные из БД
                        val login: String? = snapshot.child("username").getValue(String::class.java)
                        val age: String? = snapshot.child("age").getValue(String::class.java)
                        val name: String? = snapshot.child("name").getValue(String::class.java)
                        val address: String? = snapshot.child("address").getValue(String::class.java)
                        val city: String? = snapshot.child("city").getValue(String::class.java)
                        val country: String? = snapshot.child("country").getValue(String::class.java)
                        val profession: String? = snapshot.child("profession").getValue(String::class.java)
                        val interests: String? = snapshot.child("interests").getValue(String::class.java)
                        val maritalStatus: String? = snapshot.child("maritalStatus").getValue(String::class.java)

                        //Устанавливаем текст в TextView
                        loginEdit.setText(login ?: "Логин не найден")
                        ageEdit.setText(age ?: "Логин не найден")
                        loginEdit.setText (login ?: "Логин не найден")
                        ageEdit.setText (age ?: "Возраст не найден")
                        nameEdit.setText (name ?: "Имя не найдено")
                        addressEdit.setText (address ?: "Адресс не найден")
                        cityEdit.setText (city ?: "Город не найден")
                        countryEdit.setText (country ?: "Страна не найдена")
                        professionEdit.setText (profession ?: "Профессия не найдена")
                        interestsEdit.setText (interests ?: "Интересы не найдены")
                        maritalStatusEdit.setText (maritalStatus ?: "Семейное положение не найдено")

                    } else {

                        //Если нет ифнормации о пользователе в БД
                        loginEdit.setText("Логин не найден")
                        ageEdit.setText("Логин не найден")
                        loginEdit.setText ("Логин не найден")
                        ageEdit.setText ("Возраст не найден")
                        nameEdit.setText ("Имя не найдено")
                        addressEdit.setText ("Адресс не найден")
                        cityEdit.setText ("Город не найден")
                        countryEdit.setText ("Страна не найдена")
                        professionEdit.setText ("Профессия не найдена")
                        interestsEdit.setText ("Интересы не найдены")
                        maritalStatusEdit.setText ("Семейное положение не найдено")

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        } else {
        }

        //Обработка кнопок
        val buttonChange: Button = findViewById(R.id.person_button_change_apply)
        buttonChange.setOnClickListener{

            //Получаем текст из EditText
            val usernameText: String = loginEdit.text.toString().ifEmpty { "Не указано" }
            val ageText: String = ageEdit.text.toString().ifEmpty { "Не указано" }
            val nameText: String = nameEdit.text.toString().ifEmpty { "Не указано" }
            val addressText: String = addressEdit.text.toString().ifEmpty { "Не указано" }
            val cityText: String = cityEdit.text.toString().ifEmpty { "Не указано" }
            val countryText: String = countryEdit.text.toString().ifEmpty { "Не указано" }
            val professionText: String = professionEdit.text.toString().ifEmpty { "Не указано" }
            val interestsText: String = interestsEdit.text.toString().ifEmpty { "Не указано" }
            val maritalStatusText: String = maritalStatusEdit.text.toString().ifEmpty { "Не указано" }

            //Создаем HashMap для хранения данных пользователя
            val userMap: HashMap<String, Any> = HashMap()
            userMap["username"] = usernameText
            userMap["age"] = ageText
            userMap["name"] = nameText
            userMap["address"] = addressText
            userMap["city"] = cityText
            userMap["country"] = countryText
            userMap["profession"] = professionText
            userMap["interests"] = interestsText
            userMap["maritalStatus"] = maritalStatusText

            //Обновляем информацию в БД
            FirebaseDatabase.getInstance(Secret.linkTo)
                .getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .updateChildren(userMap)
                .addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {

                        //Данные успешно обновлены
                        Toast.makeText(this, "Данные успешно обновлены", Toast.LENGTH_SHORT).show()

                    } else {

                        //Обработка ошибки
                        val dbException = dbTask.exception
                        Toast.makeText(
                            this,
                            "Ошибка обновления в базе данных: ${dbException?.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

            val intent = Intent(this, PersonActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Отмена изменений
        val buttonCansel: Button = findViewById(R.id.person_button_change_cansel)
        buttonCansel.setOnClickListener{
            val intent = Intent(this, PersonActivity::class.java)
            startActivity(intent)
            finish()
        }
     }
}