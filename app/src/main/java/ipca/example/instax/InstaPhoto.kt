package ipca.example.instax

import com.google.firebase.database.DataSnapshot

//
// Created by lourencogomes on 12/16/20.
//
class InstaPhoto {

    var itemId      : String? = null
    var filePath    : String? = null
    var description : String? = null
    var userId      : String? = null
    var userName    : String? = null

    //var usesLikes  : [UserLike]

    constructor(
        itemId: String?,
        filePath: String?,
        description: String?,
        userId: String?,
        userName: String?
    ) {
        this.itemId = itemId
        this.filePath = filePath
        this.description = description
        this.userId = userId
        this.userName = userName
    }

    constructor(
    ) {
        this.itemId      = ""
        this.filePath    = ""
        this.description = ""
        this.userId      = ""
        this.userName    = ""
    }



   fun  toHashMap () : HashMap<String, Any?> {
       val hashMap = HashMap<String, Any?>()
       hashMap["filePath"] = filePath
       hashMap["description"] = description
       hashMap["userId"] = userId
       hashMap["userName"] = userName
       return  hashMap
   }

    companion object{
        fun fromHash( hashMap: HashMap<String, Any?>) : InstaPhoto {
            val item : InstaPhoto = InstaPhoto()
            item.filePath = hashMap["filePath"].toString()
            item.description = hashMap["description"].toString()
            item.userId = hashMap["userId"].toString()
            item.userName = hashMap["userName"].toString()
            return item
        }

        fun fromSnapshot( s : DataSnapshot) : InstaPhoto {
            val item : InstaPhoto = InstaPhoto()
            s.child("filePath")
            item.filePath = s.child("filePath").toString()
            item.description = s.child("description").toString()
            item.userId = s.child("userId").toString()
            item.userName = s.child("userName").toString()
            return item
        }
    }


}