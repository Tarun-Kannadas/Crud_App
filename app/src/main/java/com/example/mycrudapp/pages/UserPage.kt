package com.example.mycrudapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mycrudapp.entities.Users
import com.example.mycrudapp.viewModels.UserViewModel

@Composable
fun UserPage(viewModel: UserViewModel, navController: NavHostController) {
    val usersList by viewModel.users.collectAsState()

    var hasLoadedData by remember { mutableStateOf(false) }

    LaunchedEffect(usersList) {
        if (usersList.isNotEmpty()) {
            hasLoadedData = true
        }

        if (hasLoadedData && usersList.isEmpty()) {
            navController.navigate("login") {
                popUpTo(0)
            }
        }
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var userToEdit by remember { mutableStateOf<Users?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<Users?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("User Database", style = MaterialTheme.typography.headlineMedium)
            IconButton(
                onClick = {
                    navController.navigate("login"){
                        popUpTo(0)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(usersList) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = user.username, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Phone: ${user.phnNumber}", style = MaterialTheme.typography.bodyMedium)
                        }

                        Row {
                            // EDIT BUTTON
                            IconButton(onClick = {
                                userToEdit = user
                                showEditDialog = true
                            }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit User", tint = MaterialTheme.colorScheme.primary)
                            }

                            // DELETE BUTTON - Now triggers the dialog instead of instantly deleting
                            IconButton(onClick = {
                                userToDelete = user
                                showDeleteDialog = true
                            }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && userToEdit != null) {
        EditUserDialog(
            user = userToEdit!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedUsername, updatedEmail, updatedPhone ->
                userToEdit?.let { currentUser ->
                    val updatedUser = currentUser.copy(
                        username = updatedUsername,
                        email = updatedEmail,
                        phnNumber = updatedPhone
                    )
                    viewModel.updateUser(updatedUser)
                    showEditDialog = false
                }
            }
        )
    }

    if (showDeleteDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete ${userToDelete?.username}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        userToDelete?.let { viewModel.deleteUser(it) }
                        showDeleteDialog = false // Close dialog after deleting
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Makes the button red!
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EditUserDialog(
    user: Users,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var editUsername by remember { mutableStateOf(user.username) }
    var editEmail by remember { mutableStateOf(user.email) }
    var editPhone by remember { mutableStateOf(user.phnNumber) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit User") },
        text = {
            Column {
                OutlinedTextField(value = editUsername, onValueChange = { editUsername = it }, label = { Text("Username") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = editEmail, onValueChange = { editEmail = it }, label = { Text("Email") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = editPhone, onValueChange = { editPhone = it }, label = { Text("Phone") })
            }
        },
        confirmButton = {
            Button(onClick = { onSave(editUsername, editEmail, editPhone) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}