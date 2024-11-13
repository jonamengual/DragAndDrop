package com.example.draganddrop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {

    var isCurrentlyDragging by mutableStateOf(false)
    private set
    var items by mutableStateOf(emptyList<PersonUiItem>())
    private set
    var addedPersons= mutableStateListOf<PersonUiItem>()
    private set

    init {
        items=listOf(
            PersonUiItem("MICHAEL","1", Color.Gray),
            PersonUiItem("Franchesco","2", Color.Blue),
            PersonUiItem("JON","3", Color.Blue)
        )
    }

    fun startDragging(){
        isCurrentlyDragging=true
    }
    fun stopDragging(){
        isCurrentlyDragging=false
    }
    fun addPerson(personUiItem: PersonUiItem){
        addedPersons.add(personUiItem)
    }
}