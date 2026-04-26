package com.example.mycrudapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycrudapp.daos.UserDao
import com.example.mycrudapp.entities.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val dao: UserDao): ViewModel() {

    // val users by dao.getAllUsers().collectAsState(initial = emptyList())

    val users = dao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun registerUser(
        username: String,
        pass: String,
        email: String,
        phnNumber: String,
        onResult: (Boolean,String) -> Unit
    ) {
        viewModelScope.launch {
            val existingUser = dao.getUsersByUsername(username)
            if (existingUser != null)
            {
                onResult(false, "Username already Exists!")
            }
            else
            {
                dao.insertUser(
                    Users(
                        username = username,
                        password = pass,
                        email = email,
                        phnNumber = phnNumber
                    )
                )
                onResult(true, "User Created Successfully!")
            }
        }
    }

    fun loginUser(
        username: String,
        pass: String,
        onResult: (Boolean,String) -> Unit
    ){
        viewModelScope.launch {
            val user = dao.getUsersByUsername(username)
            if (user != null && user.password == pass)
            {
                onResult(true, "Login Successfull")
            }
            else
            {
                onResult(false, "Invalid Credentials")
            }
        }
    }

    fun updateUser(user: Users){
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateUsers(user)
        }
    }

    fun deleteUser(user: Users) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteUsers(user)
        }
    }
}