package com.insta.app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.insta.app.Models.UserSearchModel
import com.insta.app.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserSearchAdapter(
    private var ctx: Context,
    private var usersList: List<UserSearchModel>,
    private var isFragment: Boolean = false
) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserSearchAdapter.ViewHolder {
        val mRootView = LayoutInflater.from(ctx).inflate(R.layout.user_item_layout, parent, false)
        return UserSearchAdapter.ViewHolder(mRootView)
    }

    override fun onBindViewHolder(holder: UserSearchAdapter.ViewHolder, position: Int) {
        val userItem = usersList[position]

        holder.userName.text = userItem.username
        holder.userFullname.text = userItem.fullname
        Picasso.get().load(userItem.image).placeholder(R.drawable.profile).into(holder.userImage)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.findViewById(R.id.user_search_name)
        var userFullname: TextView = itemView.findViewById(R.id.user_search_profile_name)
        var userImage: CircleImageView = itemView.findViewById(R.id.user_search_image)
        var userFollow: Button = itemView.findViewById(R.id.user_search_btn_follow)
    }
}