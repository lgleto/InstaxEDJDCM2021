package ipca.example.instax

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1343

    lateinit var  listView : ListView

    var adapter : PhotosAdapapter? = null

    var photos : MutableList<InstaPhoto> = arrayListOf()

    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = Firebase.database
        val myRef = database.getReference("allphotos")
        storage = Firebase.storage
        var storageRef = storage.reference

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            //myRef.setValue("Hello, World!")
            dispatchTakePictureIntent()
        }

        listView = findViewById(R.id.listView)
        adapter = PhotosAdapapter()
        listView.adapter = adapter

        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                photos.clear()
                for(s in snapshot.children) {
                    val instaPhoto = InstaPhoto.fromSnapshot(s)
                    photos.add(instaPhoto)
                }
                adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }

    inner class PhotosAdapapter : BaseAdapter()  {
        override fun getCount(): Int {
            return photos.size
        }

        override fun getItem(p0: Int): Any {
            return photos[p0]
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val rootView = layoutInflater.inflate(R.layout.row_instax, p2,false)

            val textViewDescription= rootView.findViewById<TextView>(R.id.textViewDescription)
            val imageViewPhoto= rootView.findViewById<ImageView>(R.id.imageViewPhoto)
            textViewDescription.text = photos[p0].description
            val gsReference = storage.getReferenceFromUrl(photos[p0].filePath?:"")
            gsReference.downloadUrl.addOnSuccessListener {
                var downloadUrl : String =  it.toString()?:""
                Glide.with(this@MainActivity)
                        .load(downloadUrl)
                        .into(imageViewPhoto)
            }

            return rootView
        }

    }




    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            data?.extras?.let {
                val imageBitmap = it.get("data") as Bitmap
                //imageView.setImageBitmap(imageBitmap)
                Handler().postDelayed({

                    val intent = Intent(this@MainActivity, PostActivity::class.java)
                    intent.putExtra("data", imageBitmap)
                    startActivity(intent)
                }, 1000)

            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}