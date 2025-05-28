package com.example.debttracker.unit

import com.example.debttracker.models.FirestoreUser
import com.example.debttracker.models.Transaction
import com.example.debttracker.models.User
import com.google.firebase.Timestamp
import org.junit.Assert
import org.junit.Test
import java.util.Date

class DataModelsTest {
    @Test
    fun transaction_fromMap_and_toMap_validData() {
        val date = Date(1234567890L)
        val timestamp = Timestamp(date)
        val map = mapOf(
            "amount" to 10.0,
            "date" to timestamp,
            "paidBy" to "user1"
        )
        val transaction = Transaction.Companion.fromMap(map)
        Assert.assertNotNull(transaction)
        Assert.assertEquals(10.0, transaction!!.amount, 0.001)
        Assert.assertEquals(date, transaction.date)
        Assert.assertEquals("user1", transaction.paidBy)

        val mapBack = transaction.toMap()
        Assert.assertEquals(10.0, mapBack["amount"])
        Assert.assertEquals(timestamp, mapBack["date"])
        Assert.assertEquals("user1", mapBack["paidBy"])
    }

    @Test
    fun transaction_fromMap_missingAmount() {
        val map = mapOf(
            "date" to Timestamp(Date()),
            "paidBy" to "user1"
        )
        val transaction = Transaction.Companion.fromMap(map)
        Assert.assertNull(transaction)
    }

    @Test
    fun transaction_fromMap_missingDate() {
        val map = mapOf(
            "amount" to 10.0,
            "paidBy" to "user1"
        )
        val transaction = Transaction.Companion.fromMap(map)
        Assert.assertNull(transaction)
    }

    @Test
    fun transaction_fromMap_missingPaidBy() {
        val map = mapOf(
            "amount" to 10.0,
            "date" to Timestamp(Date())
        )
        val transaction = Transaction.Companion.fromMap(map)
        Assert.assertNull(transaction)
    }

    @Test
    fun transaction_fromMap_invalidAmountType() {
        val map = mapOf(
            "amount" to "not_a_double",
            "date" to Timestamp(Date()),
            "paidBy" to "user1"
        )
        val transaction = Transaction.Companion.fromMap(map)
        Assert.assertNull(transaction)
    }

    @Test
    fun user_equality() {
        val user1 = User("uid1", "test@example.com")
        val user2 = User("uid1", "test@example.com")
        val user3 = User("uid2", "other@example.com")
        val user4 = User("uid1", "different@example.com")

        Assert.assertEquals(user1, user2)
        Assert.assertNotEquals(user1, user3)
        Assert.assertNotEquals(user1, user4)
    }

    @Test
    fun firestoreUser_fromMap_and_toMap_validData() {
        val date = Date(1234567890L)
        val timestamp = Timestamp(date)
        val transactionMap = mapOf("amount" to 50.0, "date" to timestamp, "paidBy" to "friend1")
        val transactionsData = mapOf("friend1" to listOf(transactionMap))

        val map = mapOf(
            "uid" to "u1",
            "email" to "e@e.com",
            "name" to "Test User",
            "friends" to listOf("f1", "f2"),
            "incomingRequests" to listOf("r1"),
            "outgoingRequests" to listOf("r2"),
            "transactions" to transactionsData
        )
        val firestoreUser = FirestoreUser.Companion.fromMap(map)
        Assert.assertNotNull(firestoreUser)
        Assert.assertEquals("u1", firestoreUser!!.uid)
        Assert.assertEquals("e@e.com", firestoreUser.email)
        Assert.assertEquals("Test User", firestoreUser.name)
        Assert.assertEquals(listOf("f1", "f2"), firestoreUser.friends)
        Assert.assertEquals(listOf("r1"), firestoreUser.incomingRequests)
        Assert.assertEquals(listOf("r2"), firestoreUser.outgoingRequests)
        Assert.assertNotNull(firestoreUser.transactions["friend1"])
        Assert.assertEquals(1, firestoreUser.transactions["friend1"]!!.size)
        Assert.assertEquals(50.0, firestoreUser.transactions["friend1"]!![0].amount, 0.001)

        val mapBack = firestoreUser.toMap()
        Assert.assertEquals("u1", mapBack["uid"])
        Assert.assertEquals("e@e.com", mapBack["email"])
        Assert.assertEquals("Test User", mapBack["name"])
        Assert.assertEquals(listOf("f1", "f2"), mapBack["friends"])
        @Suppress("UNCHECKED_CAST")
        val transactionsMapBack = mapBack["transactions"] as? Map<String, List<Map<String, Any?>>>
        Assert.assertNotNull(transactionsMapBack)
        Assert.assertEquals(1, transactionsMapBack!!["friend1"]?.size)
        Assert.assertEquals(50.0, transactionsMapBack["friend1"]!![0]["amount"])
    }

    @Test
    fun firestoreUser_fromMap_missingOptionalFields() {
        val map = mapOf(
            "uid" to "u1",
            "email" to "e@e.com",
            "name" to "Test User"
            // friends, incomingRequests, outgoingRequests, transactions are missing
        )
        val firestoreUser = FirestoreUser.Companion.fromMap(map)
        Assert.assertNotNull(firestoreUser)
        Assert.assertEquals("u1", firestoreUser!!.uid)
        Assert.assertTrue(firestoreUser.friends.isEmpty())
        Assert.assertTrue(firestoreUser.incomingRequests.isEmpty())
        Assert.assertTrue(firestoreUser.outgoingRequests.isEmpty())
        Assert.assertTrue(firestoreUser.transactions.isEmpty())
    }

    @Test
    fun firestoreUser_fromMap_emptyListsForOptionalFields() {
        val map = mapOf(
            "uid" to "u1",
            "email" to "e@e.com",
            "name" to "Test User",
            "friends" to emptyList<String>(),
            "incomingRequests" to emptyList<String>(),
            "outgoingRequests" to emptyList<String>(),
            "transactions" to emptyMap<String, List<Map<String, Any>>>()
        )
        val firestoreUser = FirestoreUser.Companion.fromMap(map)
        Assert.assertNotNull(firestoreUser)
        Assert.assertTrue(firestoreUser!!.friends.isEmpty())
        Assert.assertTrue(firestoreUser.incomingRequests.isEmpty())
        Assert.assertTrue(firestoreUser.outgoingRequests.isEmpty())
        Assert.assertTrue(firestoreUser.transactions.isEmpty())
    }

    @Test
    fun firestoreUser_fromMap_missingUid() {
        val map = mapOf(
            "email" to "e@e.com",
            "name" to "Test User"
        )
        val firestoreUser = FirestoreUser.Companion.fromMap(map)
        Assert.assertNull(firestoreUser)
    }
}