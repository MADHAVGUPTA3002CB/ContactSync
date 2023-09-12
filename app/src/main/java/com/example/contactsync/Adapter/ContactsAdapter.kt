package com.example.contactsync.Adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsync.ContactData
import com.example.contactsync.databinding.ItemContactBinding


class ContactsAdapter: RecyclerView.Adapter<ContactViewHolder>() {


    var allcontactlist = listOf<ContactData>()
    lateinit var binding : ItemContactBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allcontactlist.size
    }

    @SuppressLint("CommitPrefEdits")
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentuser = allcontactlist[position]

        holder.Name.text = currentuser.name

        holder.Phone.text = currentuser.phoneno.toString()




    }

    @SuppressLint("NotifyDataSetChanged")
    fun setUserList(list: List<ContactData>) {


        val diffResult = DiffUtil.calculateDiff(MyDiffCallback(allcontactlist, list))
        allcontactlist = list
        notifyDataSetChanged()
        diffResult.dispatchUpdatesTo(this)


    }

}

class ContactViewHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {

    val Name: TextView = binding.Name

    val Phone: TextView = binding.Contact

}

class MyDiffCallback(
    private val oldList : List<ContactData>,
    private val newList : List<ContactData>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

