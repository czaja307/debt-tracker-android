package com.example.debttracker.models

data class FirestoreUser (
    val uid: String,
    val email: String,
    val name: String,
    val friends: List<String> = emptyList(),
    val incomingRequests: List<String> = emptyList(),
    val outgoingRequests: List<String> = emptyList(),
    val transactions: Map<String, List<Transaction>> = emptyMap()
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): FirestoreUser? {
            val uid = map["uid"] as? String ?: return null
            val email = map["email"] as? String ?: return null
            val name = map["name"] as? String ?: return null
            val friends = (map["friends"] as? List<String>) ?: emptyList()
            val incomingRequests = (map["incomingRequests"] as? List<String>) ?: emptyList()
            val outgoingRequests = (map["outgoingRequests"] as? List<String>) ?: emptyList()
            val transactions = map["transactions"] as? Map<String, List<Map<String, Any>>>

            val rawTransactions = map["transactions"] as? Map<String, List<Map<String, Any>>>
            val transactionsConverted: Map<String, List<Transaction>> = rawTransactions?.mapValues { entry ->
                entry.value.mapNotNull { Transaction.fromMap(it) }
            } ?: emptyMap()
            return FirestoreUser(uid, email, name, friends, incomingRequests, outgoingRequests, transactionsConverted)

        }
    }

    fun toMap(): Map<String, Any?> {
        val transactionsMap = transactions.mapValues { entry ->
            entry.value.map { transaction: Transaction -> transaction.toMap() }
        }
        return mapOf(
            "uid" to uid,
            "email" to email,
            "name" to name,
            "friends" to friends,
            "incomingRequests" to incomingRequests,
            "outgoingRequests" to outgoingRequests,
            "transactions" to transactionsMap
        )
    }

}