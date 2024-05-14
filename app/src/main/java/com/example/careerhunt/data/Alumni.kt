package com.example.careerhunt.data

import com.google.firebase.database.Exclude

data class Alumni(@Exclude var id: String = "",
                  var title : String = "",
                  var content : String = "",
                  var date : String = "",
                  var personal_id : String = "",
                  var personal_liked : ArrayList<String> = arrayListOf()
)
