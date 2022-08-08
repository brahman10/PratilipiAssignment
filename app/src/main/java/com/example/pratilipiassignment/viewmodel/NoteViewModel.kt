package com.example.pratilipiassignment.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pratilipiassignment.Notes
import com.example.pratilipiassignment.NotesDatabase
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    var selectedColor = MutableLiveData("#171C26")
    private val context = getApplication<Application>().applicationContext
    val callSuccess = MutableLiveData(false)
    var currentDate: String? = null
    var READ_STORAGE_PERM = 123
    var REQUEST_CODE_IMAGE = 456
    var selectedImagePath = ""
    var webLink = ""
    var noteId = -1
    var noteTitle = ""
    var noteDescription = ""
    var notes = MutableLiveData<List<Notes>>()


    fun deleteNote() {

        viewModelScope.launch {
            context?.let {
                NotesDatabase.getDatabase(it).noteDao().deleteSpecificNote(noteId)
                callSuccess.value = true
            }
        }
    }

    fun saveNote() {

        if (noteTitle.isNullOrEmpty()) {
            Toast.makeText(context, "Note Title is Required", Toast.LENGTH_SHORT).show()
        } else if (noteDescription.isNullOrEmpty()) {

            Toast.makeText(context, "Note Description is Required", Toast.LENGTH_SHORT).show()
        } else {

            viewModelScope.launch {
                val notes = Notes()
                notes.title = noteTitle
                notes.noteText = noteDescription
                notes.dateTime = currentDate
                notes.color = selectedColor.value
                notes.imgPath = selectedImagePath
                notes.webLink = webLink
                context?.let {
                    NotesDatabase.getDatabase(it).noteDao().insertNotes(notes)
                    noteTitle = ""
                    noteDescription = ""
                    callSuccess.value = true
                }
            }
        }

    }

    fun updateNote(){
        viewModelScope.launch {
            context?.let {
                val notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)

                notes.title = noteTitle
                notes.noteText = noteDescription
                notes.dateTime = currentDate
                notes.color = selectedColor.value
                notes.imgPath = selectedImagePath
                notes.webLink = webLink

                NotesDatabase.getDatabase(it).noteDao().updateNote(notes)
                noteTitle = ""
                noteDescription = ""
                callSuccess.value = true
            }
        }
    }

    fun getAllNotes()
    {
        viewModelScope.launch {
            context?.let {
                notes.value = NotesDatabase.getDatabase(it).noteDao().getAllNotes()
            }
        }
    }

}