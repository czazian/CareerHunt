package com.example.careerhunt.interfaces

class UserInterface {
    //RecyclerViewInterface -  Allow for clicking on each item inside recyclerView
    interface RecyclerViewEvent {
        fun onItemClick(position: Int);
    }
}