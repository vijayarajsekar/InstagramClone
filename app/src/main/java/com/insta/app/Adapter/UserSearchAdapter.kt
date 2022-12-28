package com.insta.app.Adapter

import android.content.Context
import android.provider.ContactsContract.Profile
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.insta.app.Fragments.ProfileFragment
import com.insta.app.Models.UserSearchModel
import com.insta.app.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserSearchAdapter(
    private var ctx: Context,
    private var usersList: List<UserSearchModel>,
    private var isFragment: Boolean = false
) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {

    private var mFireBaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

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

        checkFollowingStatus(userItem.uid, holder.userFollow)

        holder.itemView.setOnClickListener{
         val mPref = ctx.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE).edit()
            mPref.putString("profileId", userItem.uid).apply()

            (ctx as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view,ProfileFragment()).commit()

        }

        holder.userFollow.setOnClickListener {
            if (holder.userFollow.text.toString().equals("Follow")) {
                mFireBaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString())
                        .child("Following").child(userItem.uid).setValue(true)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                mFireBaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference.child("Follow")
                                        .child(userItem.uid)
                                        .child("Followers").child(it.toString()).setValue(true)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {


                                            }
                                        }
                                }

                            }
                        }
                }
            } else {
                mFireBaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString())
                        .child("Following").child(userItem.uid).removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                mFireBaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference.child("Follow")
                                        .child(userItem.uid)
                                        .child("Followers").child(it.toString()).removeValue()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }

                            }
                        }
                }
            }
        }
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

    private fun checkFollowingStatus(uid: String, userFollow: Button) {
        val mFollowingRef = mFireBaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString())
                .child("Following")
        }

        mFollowingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()) {
                    userFollow.text = "Following"
                } else {
                    userFollow.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}