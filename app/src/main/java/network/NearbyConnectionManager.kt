
package com.team58.emergencymessaging.network

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class NearbyConnectionManager(
    context: Context,
    private val listener: Listener? = null
) {

    interface Listener {
        fun onDeviceFound(endpointId: String, deviceName: String)
        fun onDeviceConnected(endpointId: String)
        fun onDeviceDisconnected(endpointId: String)
        fun onMessageReceived(endpointId: String, message: String)
        fun onConnectionFailed(endpointId: String)
    }

    private val client = Nearby.getConnectionsClient(context)
    private val serviceId = "com.team58.emergencymessaging"
    private val strategy = Strategy.P2P_CLUSTER

    private val connectedEndpoints = mutableSetOf<String>()

    private val payloadCallback = object : PayloadCallback() {

        override fun onPayloadReceived(
            endpointId: String,
            payload: Payload
        ) {
            if (payload.type == Payload.Type.BYTES) {
                val message = payload.asBytes()?.toString(Charsets.UTF_8)
                if (message != null) {
                    listener?.onMessageReceived(endpointId, message)
                }
            }
        }

        override fun onPayloadTransferUpdate(
            endpointId: String,
            update: PayloadTransferUpdate
        ) {
        }
    }

    private val connectionCallback =
        object : ConnectionLifecycleCallback() {

            override fun onConnectionInitiated(
                endpointId: String,
                connectionInfo: ConnectionInfo
            ) {
                client.acceptConnection(endpointId, payloadCallback)
            }

            override fun onConnectionResult(
                endpointId: String,
                result: ConnectionResolution
            ) {
                if (result.status.isSuccess) {
                    connectedEndpoints.add(endpointId)
                    listener?.onDeviceConnected(endpointId)
                } else {
                    listener?.onConnectionFailed(endpointId)
                }
            }

            override fun onDisconnected(endpointId: String) {
                connectedEndpoints.remove(endpointId)
                listener?.onDeviceDisconnected(endpointId)
            }
        }

    fun startAdvertising(deviceName: String) {
        client.startAdvertising(
            deviceName,
            serviceId,
            connectionCallback,
            AdvertisingOptions.Builder()
                .setStrategy(strategy)
                .build()
        )
    }

    fun startDiscovery(deviceName: String) {
        client.startDiscovery(
            serviceId,
            object : EndpointDiscoveryCallback() {

                override fun onEndpointFound(
                    endpointId: String,
                    info: DiscoveredEndpointInfo
                ) {
                    listener?.onDeviceFound(
                        endpointId,
                        info.endpointName
                    )

                    client.requestConnection(
                        deviceName,
                        endpointId,
                        connectionCallback
                    )
                }

                override fun onEndpointLost(endpointId: String) {
                }
            },
            DiscoveryOptions.Builder()
                .setStrategy(strategy)
                .build()
        )
    }

    fun sendMessage(endpointId: String, message: String) {
        if (endpointId !in connectedEndpoints) return

        client.sendPayload(
            endpointId,
            Payload.fromBytes(message.toByteArray(Charsets.UTF_8))
        )
    }

    fun getConnectedDevices(): Set<String> {
        return connectedEndpoints.toSet()
    }

    fun stopAllConnections() {
        client.stopAdvertising()
        client.stopDiscovery()
        client.stopAllEndpoints()
        connectedEndpoints.clear()
    }
}
