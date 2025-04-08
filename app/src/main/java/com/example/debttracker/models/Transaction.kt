package com.example.debttracker.models

import com.google.firebase.Timestamp
import java.util.Date


data class Transaction (
    val amount: Double,
    val date: Date,
    val paidBy: String
    ) {
    companion object {
        fun fromMap(map: Map<String, Any?>): Transaction? {
            val amount = map["amount"] as? Double ?: return null
            val date = map["date"] as? Timestamp ?: return null
            val paidBy = map["paidBy"] as? String ?: return null
            return Transaction(amount, date.toDate(), paidBy)
        }
    }
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "amount" to amount,
            "date" to Timestamp(date),
            "paidBy" to paidBy
        )
    }

}
