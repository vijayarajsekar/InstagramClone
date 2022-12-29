package com.insta.app.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.insta.app.Adapter.PostAdapter
import com.insta.app.Models.UserPostModel
import com.insta.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var mRootView: FragmentHomeBinding
    private var mPostRecyclerView: RecyclerView? = null
    private var mLinearLayoutManager: LinearLayoutManager? = null

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<UserPostModel>? = null
    private var followingList: MutableList<UserPostModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = FragmentHomeBinding.inflate(inflater, container, false)

        mPostRecyclerView = mRootView.recyclerViewHome

        mLinearLayoutManager = LinearLayoutManager(context)
        mLinearLayoutManager?.reverseLayout = true
        mLinearLayoutManager?.stackFromEnd = true

        mPostRecyclerView?.layoutManager = mLinearLayoutManager


        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<UserPostModel>) }
        mPostRecyclerView?.adapter = postAdapter

        checkFollowings()

        return mRootView.root
    }

    private fun checkFollowings() {
        followingList = ArrayList()
        val mFollowinngRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        mFollowinngRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (followingList as ArrayList<String>).clear()

                    for (data in snapshot.children) {
                        data.key?.let {
                            (followingList as ArrayList<String>).add(it)
                        }
                    }

                    retrivePost()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun retrivePost() {
        val mPostRef = FirebaseDatabase.getInstance().reference.child("Posts")

        mPostRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    postList?.clear()
                    for (data in snapshot.children) {
                        val post = data.getValue(UserPostModel::class.java)

                        for (userId in (followingList as ArrayList<String>)) {
                            if (post!!.post_user.equals(userId) or post!!.post_user.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                                postList!!.add(post)
                            }

                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

}