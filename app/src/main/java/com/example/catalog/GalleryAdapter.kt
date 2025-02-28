package com.example.catalog

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GalleryAdapter(var items: List<Item>, var activity: Activity) : RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.item_in_list_image_page)
        val title: TextView = view.findViewById(R.id.item_in_list_title_page)
        val desc: TextView = view.findViewById(R.id.item_in_list_text_page)
        val button: Button = view.findViewById(R.id.item_in_list_button_page)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_in_gallery, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = items[position].title
        holder.desc.text = items[position].decs


        //Загружаем изображение
        val resourceId = activity.resources.getIdentifier(items[position].image[0], "drawable", activity.packageName)
        if (resourceId!=0) {
            holder.image.setImageResource(resourceId)
        }else{
            val resourceId1 = activity.resources.getIdentifier("lamp.jpg", "drawable", activity.packageName)
            holder.image.setImageResource(resourceId1)
        }

        holder.button.setOnClickListener{
            val intent = Intent(activity,  ItemActivity::class.java)

            //Передаём данные о выбранной достопримечательности
            intent.putExtra("itemId", items[position].id)
            intent.putExtra("itemTitle", items[position].title)
            intent.putExtra("itemText", items[position].text)
            intent.putExtra("itemImages", ArrayList(items[position].image))


            activity.startActivity(intent)
            activity.finish()

        }

    }
}