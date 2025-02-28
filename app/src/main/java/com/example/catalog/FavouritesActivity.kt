package com.example.catalog

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class FavouritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        //Получаем ссылку на пользователя
        val userRef = FirebaseDatabase.getInstance(Secret.linkTo)
            .getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        val itemsList: RecyclerView = findViewById(R.id.favourites_list)

        //Отображение данных, которые хранятся в памяти
        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = GalleryAdapter(AppViewModel.filteredItems, this)

        //Получаем текущее значение favourites
        userRef.child("favourites").get().addOnSuccessListener { dataSnapshot ->

            val currentFavourites:List<String> = dataSnapshot.value as? List<String> ?: emptyList()

            val items = arrayListOf<Item>()
                        FirebaseFirestore.getInstance().collection("Items")
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val item = document.toObject(Item::class.java)

                                    items.add(item)

                                }

                                //Выбераем только избранные достоприм.
                                val filteredItems = ArrayList(items.filter { item ->
                                    currentFavourites.contains(item.id.toString())
                                })

                                //Обновляем список избранных
                                AppViewModel.filteredItems = filteredItems
                                itemsList.layoutManager = LinearLayoutManager(this)
                                itemsList.adapter = GalleryAdapter(filteredItems, this)

                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Ошибка получения данных: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }

        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Ошибка получения данных: ${exception.message}", Toast.LENGTH_SHORT).show()
        }

        //Возрат на главную страницу
        val buttonBack : ImageButton = findViewById(R.id.favourites_button_back)
        buttonBack.setOnClickListener{
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}