package com.example.debttracker.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for creating ViewModels with custom dependencies
 */
class ViewModelFactory(
    private val loginViewModel: LoginViewModel? = null,
    private val context: Context? = null
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // If LoginViewModel is requested and we have context, pass context for currency conversion
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(context = context) as T
            }
            // If AddDebtViewModel is requested, pass dependencies
            modelClass.isAssignableFrom(AddDebtViewModel::class.java) && loginViewModel != null -> {
                AddDebtViewModel(loginViewModel, context) as T
            }
            // Add more ViewModels here as needed
            else -> throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
        }
    }
}