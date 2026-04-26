package com.example.mycrudapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.mycrudapp.databases.AppDatabase
import com.example.mycrudapp.pages.LoginPage
import com.example.mycrudapp.pages.SignupPage
import com.example.mycrudapp.pages.UserPage
import com.example.mycrudapp.viewModels.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation()
{
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)

    val userViewModel: UserViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                UserViewModel(database.UserDao())
            }
        }
    )

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginPage(navController, userViewModel)
        }
        composable("signup") {
            SignupPage(navController, userViewModel)
        }
        composable("user_crud") {
            // We are now passing the navController into the screen!
            UserPage(viewModel = userViewModel, navController = navController)
        }
    }
}