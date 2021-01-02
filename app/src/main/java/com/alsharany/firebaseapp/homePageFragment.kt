package com.alsharany.firebaseapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class homePageFragment : Fragment(),InputDialogFragmen.AddNews {
    interface GoToAnotherFragment{
        fun OnTransferToFragment(fr:Fragment)
    }
    companion object {
        fun newInstance() = homePageFragment()
    }

   // private lateinit var viewModel: HomePageViewModel
    lateinit var db: FirebaseFirestore
    lateinit var fireStoreList: RecyclerView
    var adapter: FirestoreRecyclerAdapter<NewsModel, NewsViewHolder>? = null
    private var goToAnotherFragment:GoToAnotherFragment?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
      goToAnotherFragment=context as GoToAnotherFragment
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        setHasOptionsMenu(true)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.home_page_fragment, container, false)
        fireStoreList=view.findViewById(R.id.display_recyclerView)

        fireStoreList.layoutManager= LinearLayoutManager(requireContext())

        fireStoreList.setHasFixedSize(true)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        fireStoreList.adapter=adapter
    }
    fun loadData(){
        val query=db.collection("news")

        val options: FirestoreRecyclerOptions<NewsModel> = FirestoreRecyclerOptions
            .Builder<NewsModel>()
            .setQuery(query,object : SnapshotParser<NewsModel> {
                override fun parseSnapshot(snapshot: DocumentSnapshot): NewsModel {
                    return snapshot.toObject(NewsModel::class.java)!!
                }

            })
            .setLifecycleOwner(this)
            .build()

        adapter = object:FirestoreRecyclerAdapter<NewsModel,NewsViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):NewsViewHolder {
                val view=LayoutInflater.from(parent.context)
                    .inflate(R.layout.single_item,parent,false)

                return NewsViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: NewsViewHolder,
                position: Int,
                model: NewsModel
            ) {

                holder.title.text=model.newsTitle
                holder.details.text=model.newsDetails
            }


        }



    }
    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
    class NewsViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var details: TextView
        init {
            title=itemView.findViewById(R.id.title_tv) as TextView
            details=itemView.findViewById(R.id.details_tv) as TextView
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_news,menu)
        return super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item!= null){
            if(item.itemId==R.id.add_news){
                if( LoginPref.getStoredQuery(requireContext()).isNotEmpty()){
                    InputDialogFragmen.newInstance().apply {
                        setTargetFragment(this@homePageFragment,0)
                        show(this@homePageFragment.parentFragmentManager,"this")
                    }
                    true
                }else{
                    goToAnotherFragment?.OnTransferToFragment(SignInFragment.newInstance())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAddNews(news: MutableMap<String, Any>) {
        addNews(news)
    }
    fun addNews(news:MutableMap<String,Any>){
        val db =FirebaseFirestore.getInstance()
        db.collection("news")
            .add(news)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    "onsuccess",
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            }
            .addOnFailureListener { e ->
                Log.w(
                    "onFailer",
                    "Error adding document",
                    e
                )
            }

    }


}