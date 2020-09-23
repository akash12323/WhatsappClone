package com.example.whatsappclone.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.*
import com.example.whatsappclone.modals.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_people.*
import java.lang.Exception

// IN THIS WE WILL DISPLAY ALL THE USERS THAT ARE PRESENT ON THE DATABASE IN FIREBASE FIRESTORE

private const val DELETED_VIEW_TYPE = 1
private const val NORMAL_VIEW_TYPE = 2

class PeopleFragment: Fragment() {

    lateinit var mAdapter:FirestorePagingAdapter<User,RecyclerView.ViewHolder>

    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance().collection("users")
            .orderBy("name",Query.Direction.ASCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_people, container, false)

        setupAdapter()

        return view
    }

    private fun setupAdapter() {
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(2)             //To declare initial number of pages
            .setPageSize(10)                    //To declare listSize after initial loading
            .setEnablePlaceholders(false)
            .build()

        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(database,config,User::class.java)
            .build()

        mAdapter = object : FirestorePagingAdapter<User,RecyclerView.ViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return when(viewType){
                    NORMAL_VIEW_TYPE->{
                        UserViewHolder(layoutInflater.inflate(R.layout.item_people,parent,false))
                    }
                    else-> EmptyViewHolder(
                        layoutInflater.inflate(R.layout.empty_view, parent, false)
                    )
                }
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: User) {
                if(holder is UserViewHolder){
                    holder.bind(user = model){ name: String, photo: String, id: String ->
                        val intent = Intent(requireContext(),ChatActivity::class.java)
                        intent.putExtra(UID,id.toString())
                        intent.putExtra(NAME,name.toString())
                        intent.putExtra(IMAGE,photo.toString())
                        startActivity(intent)
                    }
                }
                else{

                }
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

            override fun getItemViewType(position: Int): Int {
                val item = getItem(position)?.toObject(User::class.java)
                return if(auth.uid == item!!.uid){
                    DELETED_VIEW_TYPE
                }
                else{
                    NORMAL_VIEW_TYPE
                }
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