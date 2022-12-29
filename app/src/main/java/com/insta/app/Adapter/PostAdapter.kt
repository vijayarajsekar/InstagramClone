package com.insta.app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.insta.app.Models.UserPostModel
import com.insta.app.Models.UserSearchModel
import com.insta.app.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val ctxx: Context, private val postList: List<UserPostModel>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profileImage: CircleImageView
        var postImage: ImageView

        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView

        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_search)
            postImage = itemView.findViewById(R.id.post_image_banner)

            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_save_btn)

            userName = itemView.findViewById(R.id.user_name)
            likes = itemView.findViewById(R.id.text_post_likes)
            publisher = itemView.findViewById(R.id.text_post_publisher)
            description = itemView.findViewById(R.id.text_post_description)
            comments = itemView.findViewById(R.id.text_post_comments)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(ctxx).inflate(R.layout.post_item_layout, parent, false)
        return ViewHolder(view);
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post = postList[position]

        Picasso.get().load(post.post_image).into(holder.postImage)
        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.post_user)

        holder.description.text = post.post_desc

    }

    override fun getItemCount(): Int {
        return postList.size
    }

    private fun publisherInfo(
        profileImage: CircleImageView,
        userName: TextView,
        publisher: TextView,
        postUserId: String
    ) {
        val usersRef =
            FirebaseDatabase.getInstance().reference.child("InstaUsers").child(postUserId)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<UserSearchModel>(UserSearchModel::class.java)
                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile)
                        .into(profileImage)

                    userName.text = user.username
                    publisher.text = user.fullname
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}