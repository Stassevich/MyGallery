package com.example.catalog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class ItemActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentsAdapter: CommentsAdapter
    private var commentsList = mutableListOf<Comment>()
    private var isFavourites : Boolean = false

    //Получить изображения из назвний
    fun getImagesFromPaths(imagePaths: ArrayList<String>): ArrayList<Int> {
        val im = ArrayList<Int>()

        for (path in imagePaths) {
            val resourceId = resources.getIdentifier(path, "drawable", this.packageName)

            if (resourceId != 0) {
                im.add(resourceId)
            } else {
                im.add(R.drawable.lamp)
            }
        }
        return im
    }

    //Добавляем если нет комментария, заменяем если есть
    fun replaceOrAddCommentByLogin(
        commentsList: List<Comment>,
        login: String,
        newComment: Comment
    ): List<Comment> {
        val updatedList = commentsList.map { comment ->
            if (comment.username.equals(login, ignoreCase = true)) {
                newComment
            } else {
                comment
            }
        }

        //Проверяем, был ли новый комментарий добавлен
        return if (updatedList.any { it.username.equals(login, ignoreCase = true) }) {
            updatedList
        } else {
            updatedList + newComment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        //Кнопка возврата на главную страницу
        val buttonBack : ImageButton = findViewById(R.id.person_button_back)
        buttonBack.setOnClickListener{
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
            finish()
        }


        val buttonSend: Button = findViewById(R.id.comments_send_review_button)
        val textToSend: EditText = findViewById(R.id.comments_review_input)
        val ratingComment: RatingBar = findViewById(R.id.comments_rating_bar)
        val title : TextView = findViewById(R.id.item_list_title_page)
        val text : TextView = findViewById(R.id.item_list_text_page)
        val image : ImageView = findViewById(R.id.item_list_image_page)
        val buttonFavourites: Button = findViewById(R.id.item_list_button_page_favourites)
        title.text = intent.getStringExtra("itemTitle")
        text.text = intent.getStringExtra("itemText")
        val images = intent.getSerializableExtra("itemImages") as ArrayList<String>
        val itemId = intent.getIntExtra("itemId",0)

        //Получаем главное изображение
        val resourceId = resources.getIdentifier(images[0], "drawable", this.packageName)
        if (resourceId!=0) {
            image.setImageResource(resourceId)
        }else{
            image.setImageResource(R.drawable.lamp)
        }

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        //Получаем все изображения, относящиеся к данной достоприм.
        val imagesSlider = getImagesFromPaths(images)


        sliderAdapter = SliderAdapter(imagesSlider)
        viewPager.adapter = sliderAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = ""
        }.attach()


        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)

        //Получить комментарии о достоприм.
        FirebaseFirestore.getInstance().collection("Comments")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val item = document.toObject(Comment::class.java)
                    if(item.imgId == itemId)
                        commentsList.add(item)
                }

                //Сортируем в порядке добавления
                val sortedCommentsList = commentsList.sortedBy { it.idInItem }

                //Подготавливаем комментарии к отображению
                commentsList.clear()
                commentsList.addAll(sortedCommentsList)
                commentsAdapter = CommentsAdapter(commentsList)
                commentsRecyclerView.adapter = commentsAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }

        val userRef = FirebaseDatabase.getInstance(Secret.linkTo)
            .getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        //Получаем текущее значение favourites
        userRef.child("favourites").get().addOnSuccessListener { dataSnapshot ->
            var currentFavourites = dataSnapshot.value as? List<String> ?: emptyList()
            if(currentFavourites.contains(itemId.toString())){

                //Если выбранная достоприм. избрана
                isFavourites = true
                buttonFavourites.text = "Убрать из избранного"

            }else{

                //Если выбранная достоприм. не избрана
                isFavourites = false
                buttonFavourites.text = "Добавить в избранное"

            }

            //Добавить комментарий
            buttonSend.setOnClickListener{
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

                //Получаем данные о пользователе
                val database: DatabaseReference = FirebaseDatabase.getInstance(Secret.linkTo).getReference("Users")
                database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val login: String = snapshot.child("username").getValue(String::class.java) ?: "unknown"

                            //Создаем уникальный идентификатор для комментария
                            val commentId = commentsList.size + 1 // или используйте другой способ для id

                            //Создаем объект комментария
                            val newComment = Comment(commentId, itemId, login, ratingComment.rating.toString(), textToSend.text.toString())

                            firestore.collection("Comments")
                                .document("$itemId-$userId") // Используем уникальный ключ
                                .set(newComment)
                                .addOnSuccessListener {
                                    val newCommentsList = replaceOrAddCommentByLogin(commentsList,login,newComment)
                                    //Очизаем поля ввода комментария
                                    textToSend.text.clear()
                                    ratingComment.rating = 0f

                                    //Обновляем список комментариев
                                    commentsList.clear()
                                    commentsList.addAll(newCommentsList)
                                    commentsAdapter = CommentsAdapter(commentsList)
                                    commentsRecyclerView.adapter = commentsAdapter
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Failed to add comment: ${e.message}")
                                }
                        } else {
                            Log.e("Firestore", "User data does not exist.")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firestore", "Database error: ${error.message}")
                    }
                })
            } else {
                Log.e("Firestore", "Current user is null.")
            }


        }


        //Добавление в избранное
        buttonFavourites.setOnClickListener {
            val newFavourite: String = itemId.toString()
            val userRef =
                FirebaseDatabase.getInstance(Secret.linkTo)
                    .getReference("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)

            //Получаем текущее значение favourites
            userRef.child("favourites").get().addOnSuccessListener { dataSnapshot ->

                currentFavourites = dataSnapshot.value as? List<String> ?: emptyList()
                var updatedFavourites: List<String>
                if (currentFavourites.contains(newFavourite)) {

                    //Создаем новый список, добавляя новый элемент
                    updatedFavourites = currentFavourites - newFavourite
                    buttonFavourites.text = "Добавить в избранное"
                    isFavourites = true

                } else {
                    updatedFavourites = currentFavourites + newFavourite
                    buttonFavourites.text = "Убрать из избранного"
                    isFavourites = false
                }

                //Обновляем favourites
                userRef.child("favourites").setValue(updatedFavourites)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {

                            //Данные успешно обновлены
                            Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show()

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
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Ошибка получения данных: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        }

    }
}