package com.example.fitnessapp.utils

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun SdkUnavailableDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("SDK Unavailable") },
        text = { Text("Health Connect SDK is not available on this device.") },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK") }
        }
    )
}

@Composable
fun InstallHealthConnectDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Health Connect Required") },
        text = { Text("Please install or update Health Connect to use this feature.") },
        confirmButton = {
            Button(onClick = {
                onDismiss()
                try {
                    val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
                        setPackage("com.android.vending")
                    }
                    context.startActivity(playStoreIntent)
                } catch (e: Exception) {
                    Log.i("InstallDialog", "Could not open Play Store: $e")
                    Toast.makeText(context, "Could not open Play Store.", Toast.LENGTH_SHORT).show()
                }
            }) { Text("Install/Update") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
