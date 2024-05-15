package com.example.careerhunt.dataAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.careerhunt.R

class ImageAdapter(private var imageList: ArrayList<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>()  {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        //Get details

        private val imageItem:AppCompatImageView = itemView.findViewById(R.id.carousel_image_view)

        fun bind(imageUrl:String){
            imageItem.load(imageUrl) {
                transformations(RoundedCornersTransformation(8f))
            }
        }
        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.image_list_view, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

}