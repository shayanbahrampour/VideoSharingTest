package com.example.test

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.ACTION_VIDEO_CAPTURE
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {


    val database = FirebaseDatabase.getInstance().getReference()
    var storage = FirebaseStorage.getInstance()
    private var videoList: ArrayList<String>?=null
    var ITEM_NUMBER : Int=0
    private val CAMERA_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        videoList=ArrayList<String>()


        database.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var size: Int = videoList!!.size
                if (size > 0) {
                    for (i in 0 until size) {
                        videoList!!.removeAt(0)
                    }
                }
                for (ds in dataSnapshot.children)
                {
                    videoList!!.add(ds.value.toString())
                }
                videoList!!.reverse()


                ShowVideo(ITEM_NUMBER)
                LandigPageRecyclerView.setOnTouchListener( object : OnSwipeTouchListener(this@MainActivity){
                    override fun onSwipeLeft()
                    {
                        //CHECK IF ITS THE END OF THE LIST OR NOT
                        if (ITEM_NUMBER.equals(videoList!!.size-1))
                        {
                            Toast.makeText(this@MainActivity,"Videos Ended",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            ITEM_NUMBER +=1
                            ShowVideo(ITEM_NUMBER)
                        }
                    }


                });

            }
        })



        camera_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val permission = ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CAMERA
                )

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        101
                    )
                } else {
                    var intent = Intent(ACTION_VIDEO_CAPTURE)
                    startActivityForResult(intent, CAMERA_REQUEST_CODE)

                }

            }
        })


    }



    fun ShowVideo(i : Int)
    {
        var dialog = ProgressDialog.show(
            this@MainActivity, "",
            "Downloading. Please wait...", true
        )
        var ref =storage.reference.child(videoList!![i])
        ref.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
            override fun onSuccess(uri: Uri?) {
                LandigPageRecyclerView.setVideoURI(uri)
                LandigPageRecyclerView.start()
                LandigPageRecyclerView.setOnPreparedListener {
                    dialog.dismiss()
                }


            }


        })

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode ==CAMERA_REQUEST_CODE && resultCode == RESULT_OK)
        {

            var dialog = ProgressDialog.show(
                this@MainActivity, "",
                "Uploading. Please wait...", true
            )

            val vid = data!!.data
            var keyPath : String=database.push().key.toString()


            val storageRef = storage.reference
            var name = "name_"+keyPath+".mp4"
            val mountainsRef = storageRef.child(name)
            val mountainImagesRef = storageRef.child("images/mountains.mp4")
            mountainsRef.name == mountainImagesRef.name
            mountainsRef.path == mountainImagesRef.path

            var usad = vid?.let {
                mountainsRef.putFile(it)
            }


            usad?.addOnFailureListener {

            }?.addOnSuccessListener {
                WriteToDB(name)
                dialog.dismiss()
                Toast.makeText(this,"Video Uploaded Succesfully",Toast.LENGTH_SHORT).show()
                ITEM_NUMBER=0
            }


        }

    }


    private fun WriteToDB(name : String) {
        var name_path : String=database.push().key.toString()
        database.child(name_path).setValue(name)
    }


}


