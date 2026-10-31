
package com.team58.emergencymessaging

import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team58.emergencymessaging.network.NearbyConnectionManager
import com.team58.emergencymessaging.network.NearbyPermissionManager
import com.team58.emergencymessaging.ui.theme.EmergencyMessagingTheme

class MainActivity : ComponentActivity() {

    private val status = mutableStateOf("Ready")
    private val messageInput = mutableStateOf("")
    private val connectedEndpoints = mutableStateListOf<String>()
    private val foundDevices = mutableStateListOf<String>()
    private val messages = mutableStateListOf<String>()

    private var connectionManager: NearbyConnectionManager? = null
    private var pendingAction: (() -> Unit)? = null

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            if (results.values.all { it }) {
                status.value = "Permissions granted"
                pendingAction?.invoke()
            } else {
                status.value = "Please allow nearby permissions"
            }
            pendingAction = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmergencyMessagingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(16.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Offline Emergency Messaging",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text("Status: ${status.value}")

                        Button(
                            onClick = {
                                runWithPermissions {
                                    getConnectionManager()
                                        .startAdvertising(Build.MODEL)

                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Advertise This Phone")
                        }

                        Button(
                            onClick = {
                                runWithPermissions {
                                    getConnectionManager()
                                        .startDiscovery(Build.MODEL)
                                    status.value = "Searching for nearby phones"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Discover Nearby Phones")
                        }

                        Text("Found devices")
                        foundDevices.forEach { Text("Device: $it") }

                        Text("Connected devices")
                        connectedEndpoints.forEach {
                            Text("Connected: $it")
                        }

                        OutlinedTextField(
                            value = messageInput.value,
                            onValueChange = { messageInput.value = it },
                            label = { Text("Enter a test message") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { sendTestMessage() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Send Message")
                        }

                        Button(
                            onClick = {
                                connectionManager?.stopAllConnections()
                                connectedEndpoints.clear()
                                status.value = "Connections stopped"
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Stop Connections")
                        }

                        Text("Message history")
                        messages.forEach { Text(it) }
                    }
                }
            }
        }
    }

    private fun runWithPermissions(action: () -> Unit) {
        if (NearbyPermissionManager.hasAllPermissions(this)) {
            action()
        } else {
            pendingAction = action
            permissionLauncher.launch(
                NearbyPermissionManager.getRequiredPermissions()
            )
        }
    }

    private fun getConnectionManager(): NearbyConnectionManager {
        if (connectionManager == null) {
            connectionManager = NearbyConnectionManager(
                this,
                object : NearbyConnectionManager.Listener {

                    override fun onDeviceFound(
                        endpointId: String,
                        deviceName: String
                    ) {
                        if (!foundDevices.contains(deviceName)) {
                            foundDevices.add(deviceName)
                        }
                        status.value = "Found $deviceName"
                    }

                    override fun onDeviceConnected(endpointId: String) {
                        if (!connectedEndpoints.contains(endpointId)) {
                            connectedEndpoints.add(endpointId)
                        }
                        status.value = "Phone connected!"
                    }

                    override fun onDeviceDisconnected(endpointId: String) {
                        connectedEndpoints.remove(endpointId)
                        status.value = "Phone disconnected"
                    }

                    override fun onMessageReceived(
                        endpointId: String,
                        message: String
                    ) {
                        messages.add("Received: $message")
                    }

                    override fun onConnectionFailed(endpointId: String) {
                        status.value = "Connection failed"
                    }
                }
            )
        }
        return connectionManager!!
    }

    private fun sendTestMessage() {
        val message = messageInput.value.trim()

        if (message.isEmpty()) {
            status.value = "Enter a message first"
            return
        }

        if (connectedEndpoints.isEmpty()) {
            status.value = "No connected phones"
            return
        }

        connectedEndpoints.forEach { endpointId ->
            connectionManager?.sendMessage(endpointId, message)
        }

        messages.add("Me: $message")
        messageInput.value = ""
        status.value = "Message sent"
    }

    override fun onDestroy() {
        connectionManager?.stopAllConnections()
        super.onDestroy()
    }
}
