package com.example.whatsappclone

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.modals.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_people.view.*

class UserViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

    fun bind(user:User, onClick:(name:String,photo:String,id:String)->Unit) = with(itemView){
        countTv.visibility = View.GONE
        timeTv.visibility = View.GONE

        titleTv.text = user.name
        subTitleTv.text = user.status
        Picasso.get().load(user.thumbImage).placeholder(R.drawable.addimage)
            .error(R.drawable.addimage)
            .into(userImgView)

        setOnClickListener {
            onClick.invoke(user.name , user.thumbImage , user.uid)
        }
    }

}