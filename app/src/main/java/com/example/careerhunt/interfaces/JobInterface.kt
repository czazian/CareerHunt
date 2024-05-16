package com.example.careerhunt.interfaces

class JobInterface {
    //RecyclerViewInterface -  Allow for clicking on each item inside recyclerView
    interface RecyclerViewEvent {
        fun onItemClick(position: Int, recyclerViewSource: String);
    }

    interface ProcessCompletionListener {
        fun onAllProcessesCompleted()
    }
}