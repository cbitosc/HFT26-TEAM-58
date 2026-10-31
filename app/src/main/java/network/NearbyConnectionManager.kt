
package com.team58.emergencymessaging.network

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Strategy

class NearbyConnectionManager(context: Context) {

    private val connectionsClient: ConnectionsClient =
        Nearby.getConnectionsClient(context)

    private val serviceId =
        "com.team58.emergencymessaging"

    private val strategy = Strategy.P2P_CLUSTER

    fun startAdvertising(deviceName: String) {
        connectionsClient.startAdvertising(
            deviceName,
            serviceId,
            connectionCallback,
            com.google.android.gms.nearby.connection.AdvertisingOptions.Builder()
                .setStrategy(strategy)
                .build()
        )
    }

    fun startDiscovery(deviceName: String) {
        connectionsClient.startDiscovery(
            serviceId,
            object : com.google.android.gms.nearby.connection.EndpointDiscoveryCallback() {
                override fun onEndpointFound(
                    endpointId: String,
                    info: com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
                ) {
                    connectionsClient.requestConnection(
                        deviceName,
                        endpointId,
                        connectionCallback
                    )
                }

                override fun onEndpointLost(endpointId: String) {}
            },
            com.google.android.gms.nearby.connection.DiscoveryOptions.Builder()
                .setStrategy(strategy)
                .build()
        )
    }

    private val connectionCallback =
        object : ConnectionLifecycleCallback() {

            override fun onConnectionInitiated(
                endpointId: String,
                connectionInfo: ConnectionInfo
            ) {
                connectionsClient.acceptConnection(endpointId, payloadCallback)
            }

            override fun onConnectionResult(
                endpointId: String,
                result: ConnectionResolution
            ) {}

            override fun onDisconnected(endpointId: String) {}
        }

    private val payloadCallback =
        object : com.google.android.gms.nearby.connection.PayloadCallback() {

            override fun onPayloadReceived(
                endpointId: String,
                payload: com.google.android.gms.nearby.connection.Payload
            ) {}

            override fun onPayloadTransferUpdate(
                endpointId: String,
                update: com.google.android.gms.nearby.connection.PayloadTransferUpdate
            ) {}
        }

    fun stopAllConnections() {
        connectionsClient.stopAdvertising()
        connectionsClient.stopDiscovery()
        connectionsClient.stopAllEndpoints()
    }
}
