package com.example.whatsappclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.UserViewHolder
import com.example.whatsappclone.modals.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_people.*
import kotlinx.android.synthetic.main.fragment_people.view.*
import java.lang.Exception

class PeopleFragment: Fragment() {

    lateinit var mAdapter:FirestorePagingAdapter<User,UserViewHolder>
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance().collection("users")
            .orderBy("name",Query.Direction.DESCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_people, container, false)

        setupAdapter()
        return view
    }

    private fun setupAdapter() {
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(2)
            .setPageSize(10)
            .setEnablePlaceholders(false)
            .build()

        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(database,config,User::class.java)
            .build()

        mAdapter = object : FirestorePagingAdapter<User,UserViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val view = layoutInflater.inflate(R.layout.item_people,parent,false)
                return UserViewHolder(view)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                holder.bind(user = model)
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                super.onLoadingStateChanged(state)
                when(state){
                    LoadingState.ERROR -> {}
                    LoadingState.LOADING_INITIAL -> {}
                    LoadingState.LOADING_MORE -> {}
                    LoadingState.LOADED -> {}
                    LoadingState.FINISHED -> {}
                }
            }

            override fun onError(e: Exception) {
                super.onError(e)
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        peopleRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            adapter = mAdapter
        }
    }
}