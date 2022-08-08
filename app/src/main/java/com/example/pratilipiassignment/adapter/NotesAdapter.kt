package com.example.pratilipiassignment.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pratilipiassignment.Notes
import com.example.pratilipiassignment.R
import com.example.pratilipiassignment.databinding.ItemRvNotesBinding
import kotlin.collections.ArrayList

class NotesAdapter() :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    var listener:OnItemClickListener? = null
    var arrList = ArrayList<Notes>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            ItemRvNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    fun setData(arrNotesList: List<Notes>){
        arrList = arrNotesList as ArrayList<Notes>
    }

    fun setOnClickListener(listener1: OnItemClickListener){
        listener = listener1
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {

        holder.binding.tvTitle.text = arrList[position].title
        holder.binding.tvDesc.text = arrList[position].noteText
        holder.binding.tvDateTime.text = arrList[position].dateTime

        holder.binding.cardView.setCardBackgroundColor(Color.parseColor("#171C26"))

        if (arrList[position].imgPath != null){
            holder.binding.imgNote.setImageBitmap(BitmapFactory.decodeFile(arrList[position].imgPath))
            holder.binding.imgNote.visibility = View.VISIBLE
        }else{
            holder.binding.imgNote.visibility = View.GONE
        }

        if (arrList[position].webLink != ""){
            holder.binding.tvWebLink.text = arrList[position].webLink
            holder.binding.tvWebLink.visibility = View.VISIBLE
        }else{
            holder.binding.tvWebLink.visibility = View.GONE
        }

        holder.binding.cardView.setOnClickListener {
            listener!!.onClicked(arrList[position].id!!)
        }

    }

    class NotesViewHolder(val binding:ItemRvNotesBinding) : RecyclerView.ViewHolder(binding.root){

    }


    interface OnItemClickListener{
        fun onClicked(noteId:Int)
    }

}