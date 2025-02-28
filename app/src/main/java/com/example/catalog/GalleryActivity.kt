package com.example.catalog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class GalleryActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private var selectedFilterText: String = ""
    private var newText: String = ""

    //Отображение нового списка достопримечательностей
    fun updateData( itemsList: RecyclerView, items: ArrayList<Item>){
        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = GalleryAdapter(items, this)
    }

    //Поиск по названию и фильтрация
    fun searchAndFilter(searchText: String, itemsList: RecyclerView,items: ArrayList<Item>, selectedOption: String){
        val filteredItems = ArrayList(items.filter { item ->
            item.title.contains(searchText ?: "", ignoreCase = true)
        })

        filterList(itemsList, filteredItems, selectedFilterText)
    }

    //Фильтрация списка достопримечательностей
    private fun filterList(itemsList: RecyclerView,items: ArrayList<Item>, selectedOption: String) {
        val filteredItems = when (selectedOption) {
            "Природа" -> items.filter { it.type == "Nature" }
            "Город" -> items.filter { it.type == "City" }
            "Музей" -> items.filter { it.type == "Museum" }
            "Архитектура" -> items.filter { it.type == "Architecture" }
            else -> items
        }
        updateData(itemsList, ArrayList(filteredItems))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val itemsList: RecyclerView = findViewById(R.id.gallery_list)
        val items = AppViewModel.galleryItems

        //Отображение данных, которые хранились в памяти
        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = GalleryAdapter(AppViewModel.galleryItems, this)

        //Получение новых данных
        FirebaseFirestore.getInstance().collection("Items")
            .get()
            .addOnSuccessListener { documents ->
                items.clear()
                for (document in documents) {
                    val item = document.toObject(Item::class.java)
                    items.add(item)
                }
                AppViewModel.galleryItems = items
                updateData(itemsList, items)
            }
            .addOnFailureListener { exception ->
                 Log.w("Firestore", "Error getting documents: ", exception)
            }

        //Настройка Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val searchView: SearchView = findViewById(R.id.gallery_searchView)

        //Настройка Spinner
        val spinner: Spinner = findViewById(R.id.gallery_spinner)
        val options = arrayOf("Все", "Природа", "Город","Музей", "Архитектура")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        spinner.adapter = spinnerAdapter

        //Нажатие на пункт в списке фильтрацции
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedFilterText = options[position]
                searchAndFilter(newText.toString(), itemsList,items,selectedFilterText)

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        //Установка слушателя для элементов меню
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_person -> {
                    startActivity(Intent(this, PersonActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_favourites -> {
                    startActivity(Intent(this, FavouritesActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            //Обработка ввода данных в строку поиска
            override fun onQueryTextSubmit(newEnterText: String?): Boolean {
                return true
            }

            //Обработка ввода данных в строку поиска
            override fun onQueryTextChange(newEnterText: String?): Boolean {
                newText = newEnterText.toString()
                searchAndFilter(newText, itemsList,items,selectedFilterText)
                return true
            }
        })


        //Настройка кнопки меню
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

}