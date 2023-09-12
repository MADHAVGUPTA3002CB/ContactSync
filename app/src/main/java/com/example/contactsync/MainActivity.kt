package com.example.contactsync

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactsync.Adapter.ContactsAdapter
import com.example.contactsync.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    private  var auth =  FirebaseAuth.getInstance()
    private lateinit var adapter : ContactsAdapter
    private lateinit var binding: ActivityMainBinding
    private  var contactlist : ArrayList<ContactData> = ArrayList()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = database.getReference("contacts")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        if(hasActivityPermission()){
//            getContacts()
//        }

        binding.imgprofile.setOnClickListener{
            startActivity(Intent(this , UserInformation::class.java))
        }

        adapter = ContactsAdapter()
        binding.rvmycontacts.adapter = adapter;
        binding.rvmycontacts.layoutManager = LinearLayoutManager(this)
        adapter.setUserList(contactlist)

        binding.ExtractAllFromPhone.setOnClickListener {
            binding.ExportContact.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE


            contactlist.clear()



            getContacts()

            adapter.setUserList(contactlist)
            binding.progressBar.visibility = View.INVISIBLE


        }


        binding.ExtractAllFromFB.setOnClickListener {
            binding.ExportContact.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            contactlist.clear()
            val dbquery = databaseReference.child(auth.currentUser!!.uid)
            dbquery.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){

                        for (contact in snapshot.children){
                            val name = contact.child("name").getValue(String::class.java)
                            val phoneNumber = contact.child("phoneno").getValue(String::class.java)

                            // Create a Contact object and add it to the list
                            if (name != null && phoneNumber != null) {
                                val contactgot = ContactData(name , phoneNumber)
                                contactlist.add(contactgot)
                            }
                        }
                        adapter.setUserList(contactlist)



                    }else{
                        Toast.makeText(this@MainActivity, "No contacts Available At Firebase",
                            Toast.LENGTH_SHORT).show()
                    }

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
            binding.progressBar.visibility = View.INVISIBLE
        }


        binding.ExportContact.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            databaseReference.child(auth.currentUser!!.uid).setValue(contactlist)
                .addOnSuccessListener {
                    Toast.makeText(this@MainActivity, "Updated Your List", Toast.LENGTH_SHORT).show()
                    // Data uploaded successfully
                    // You can add your success handling code here
                }
                .addOnFailureListener { e ->
                    // Handle any errors that occurred during the upload
                    // You can add your error handling code here
                    Log.e("Firebase", "Error uploading data: ${e.message}")
                }

            binding.progressBar.visibility = View.INVISIBLE
        }


    }

    override fun onResume() {
        super.onResume()


        if(hasActivityPermission()){

        }
        else{
            requestActivityPermission()
        }
    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getContacts()
//            } else {
//
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.READ_CONTACTS), 69 //by madhav
//                )
//                //  toast("Permission must be granted in order to display contacts information")
//            }
//        }
//    }

    @SuppressLint("Range")
    private fun getContacts() {


        val builder = StringBuilder()
        val resolver: ContentResolver? = contentResolver;
        val cursor = resolver?.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )

        if (cursor != null) {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNumber = (cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                    )).toInt()



                    if (phoneNumber > 0) {
                        val cursorPhone = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            arrayOf(id),
                            null
                        )
                        var i = 1

                        if (cursorPhone != null) {
                            if (cursorPhone.count > 0) {
                                while (cursorPhone.moveToNext() && i==1) {

                                    i = 2
                                    val phoneNumValue = cursorPhone?.let {
                                        cursorPhone.getString(
                                            it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                        )
                                    }
                                    val contact = ContactData(name , phoneNumValue)

                                    if( !contactlist.contains(contact)){
                                        contactlist.add(contact)

                                    }

                                    builder.append("Contact: ").append(name)
                                        .append(", Phone Number: ").append(
                                            phoneNumValue
                                        ).append("\n\n")
                                    if (phoneNumValue != null) {
                                        Log.e("Name ===>", phoneNumValue)
                                    };
                                }
                            }
                        }
                        cursorPhone?.close()
                    }
                }
            } else {
                //   toast("No contacts available!")
            }
        }
        cursor?.close()



    }


//    private fun hasLocationPermission(): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestLocationPermission() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//            LOCATION_PERMISSION_REQUEST_CODE
//        )
//    }
//
    private fun hasActivityPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestActivityPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS), 69 //by madhav
        )
    }




}

