package com.example.debttracker.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for creating ViewModels with dependencies
 */
class ViewModelFactory(
    private val loginViewModel: LoginViewModel,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddDebtViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddDebtViewModel(loginViewModel, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
