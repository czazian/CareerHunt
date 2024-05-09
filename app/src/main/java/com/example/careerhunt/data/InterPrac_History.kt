package com.example.careerhunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "InterPrac_History",
    primaryKeys = ["personalID", "faqAnsID"],
    foreignKeys = [
        ForeignKey(
            entity = Personal::class,
            parentColumns = ["personalID"],
            childColumns = ["personalID"],
        ),
        ForeignKey(
            entity = Answer_FAQ::class,
            parentColumns = ["faqAnsID"],
            childColumns = ["faqAnsID"],
        )
    ]
)
data class InterPrac_History(
    @ColumnInfo(name = "personalID")
    var personalID: String,
    @ColumnInfo(name = "faqAnsID")
    var faqAnsID: String,
)
