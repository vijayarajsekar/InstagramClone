package com.insta.app.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.insta.app.Adapter.UserSearchAdapter
import com.insta.app.Models.UserSearchModel
import com.insta.app.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    lateinit var mRootView: FragmentSearchBinding

    private var mUserRecyclerView: RecyclerView? = null
    private var mUserSearchAdapter: UserSearchAdapter? = null
    private var mUserLisItems: MutableList<UserSearchModel>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mRootView = FragmentSearchBinding.inflate(inflater, container, false)

        mUserRecyclerView = mRootView.recyclerViewSearch
        mUserRecyclerView?.setHasFixedSize(true)
        mUserRecyclerView?.layoutManager = LinearLayoutManager(context)

        mUserLisItems = ArrayList()
        mUserSearchAdapter = context?.let {
            UserSearchAdapter(it, mUserLisItems as ArrayList<UserSearchModel>, true)
        }

        mUserRecyclerView?.adapter = mUserSearchAdapter

        mRootView.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (mRootView.searchEditText.text.toString() == "") {

                } else {
                    mUserRecyclerView?.visibility = View.VISIBLE
                    getUsers()
                    searchUser(p0.toString().lowercase())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        return mRootView.root
    }

    private fun getUsers() {
        val mUsersList = FirebaseDatabase.getInstance().getReference().child("InstaUsers")
        mUsersList.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (mRootView.searchEditText.text.toString() == "") {

                } else {
                    mUserLisItems?.clear()

                    for (users in snapshot.children) {
                        val userItemsFdb = users.getValue(UserSearchModel::class.java)
                        if (userItemsFdb != null) {
                            mUserLisItems?.add(userItemsFdb)
                        }
                    }

                    mUserSearchAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun searchUser(inputUsername: String) {
        val mSearchUser = FirebaseDatabase.getInstance().getReference().child("InstaUsers")
            .orderByChild("fullname").startAt(inputUsername).endAt(inputUsername + "\uf8ff")

        mSearchUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUserLisItems?.clear()

                if (mRootView?.searchEditText?.text.toString() == "") {

                } else {
                    for (users in snapshot.children) {
                        val userItemsFdb = users.getValue(UserSearchModel::class.java)
                        if (userItemsFdb != null) {
                            mUserLisItems?.add(userItemsFdb)
                        }
                    }

                    mUserSearchAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}