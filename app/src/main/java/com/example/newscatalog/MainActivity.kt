package com.example.newscatalog

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var myAdapter: AdapterList
    private lateinit var itemList: MutableList<ItemList>
    private lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inisialisasi Firebase
        // Initialize Firebase
        FirebaseApp.initializeApp(  this)
        db = FirebaseFirestore.getInstance()



        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatAddNews);
        progressDialog = ProgressDialog(this@MainActivity).apply {
            setTitle("Loading....")
        }

        // Setup RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(  this)
        itemList = ArrayList()
        myAdapter = AdapterList(itemList)
        recyclerView.adapter = myAdapter

        floatingActionButton.setOnClickListener {
            val toAddPage = Intent(this@MainActivity, NewsAdd::class.java)
            startActivity(toAddPage)

        }



        myAdapter.setOnItemClickListener(object : AdapterList.OnItemClickListener {
            override fun onItemClick(item: ItemList) {
                val intent = Intent(this@MainActivity, NewsDetail::class.java).apply {
                    putExtra("id", item.id)
                    putExtra("title", item.judul)
                    putExtra("desc", item.subJudul)
                    putExtra("imageUrl", item.imageUrl)
                }
                startActivity(intent)
            }
        })

    }



    private fun getData(){
        progressDialog.show()
        db.collection("news")
            .get()
            .addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                itemList.clear()
                for (document in task.result) {
                    val item = ItemList(
                            document.id,
                            document.getString("title") ?: "",
                            document.getString("desc") ?: "",
                            document.getString("imageUrl") ?: ""
                        )
                    itemList.add(item)
                    Log.d("data", "${document.id} => ${document.data}")
                }
                myAdapter.notifyDataSetChanged()
            } else {
                Log.w("data", "Error getting documents.", task.exception)
            }
                progressDialog.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        getData()
    }



//        recyclerView.layoutManager = GridLayoutManager(this, 2)
//        recyclerView.setHasFixedSize(true)
//        val itemList = listOf(
//            ItemList("Ferrari", "Deskripsi 1", "https://img4.icarcdn.com/865311/prev-desktop_harga-mobil-ferrari-ini-di-indonesia-rp-16-miliar-siapa-mau-beli_cover_2020_ferrari-488-pista-di-indonesia-dijual-rp-16-miliar-2020-2.jpg"),
//            ItemList("", "Deskripsi 2", "https://cdn.motor1.com/images/mgl/JYbWM/s3/lamborghini-huracan-evo-feature.jpg"),
//            ItemList("judul 3", "Deskripsi 3", "https://cdn0-production-images-kly.akamaized.net/NkwD9ZoKdO4Cvk8LEEsUnuSYbfQ=/750x0/smart/filters:quality(75):strip_icc():format(jpeg)/kly-media-production/medias/3981549/original/039518500_1648785174-IMG_20220401_104450.jpg")
//
//        )
//        val adapter = AdapterList(itemList)
//        recyclerView.adapter = adapter
//    }
}