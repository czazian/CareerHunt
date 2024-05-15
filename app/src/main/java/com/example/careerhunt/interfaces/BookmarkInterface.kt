package com.example.careerhunt.interfaces

interface BookmarkInterface {
    interface RecyclerViewEvent {
        fun onItemClick(position: Int);
    }

    interface ProcessCompletionListener {
        fun onAllProcessesCompleted()
    }
}