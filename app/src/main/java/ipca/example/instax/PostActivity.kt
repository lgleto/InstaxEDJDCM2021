package ipca.example.instax

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*

class PostActivity : AppCompatActivity() {

    lateinit var buttonPost: FloatingActionButton
    lateinit var imageViewPostPhoto: ImageView
    lateinit var editTextPostDescription: EditText

    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        buttonPost = findViewById(R.id.buttonPost)
        imageViewPostPhoto = findViewById(R.id.imageViewPostPhoto)
        editTextPostDescription = findViewById(R.id.editTextPostDescription)


        database = Firebase.database.reference.child("allphotos")
        storage = Firebase.storage
        var storageRef = storage.reference

        intent.extras?.let {
            val bitmap = it.get("data") as Bitmap
            imageViewPostPhoto.setImageBitmap(bitmap)
        }


        buttonPost.setOnClickListener {
            val uuid = UUID.randomUUID().toString()
            val photoImagesRef = storageRef.child("images/${uuid}.jpg")
            val uploadTask = photoImagesRef.putBytes(toByteData())
            uploadTask.addOnSuccessListener {
                print(it)
                val insta = InstaPhoto("", photoImagesRef.toString(), editTextPostDescription.text.toString(),"","")
                database.child(uuid).setValue(insta.toHashMap())
                finish()
            }.addOnFailureListener {
                print(it)
            }



        }
    }

    fun toByteData(): ByteArray {
        val bitmap = (imageViewPostPhoto.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return data
    }

}