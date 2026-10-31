package com.example.offlineemergencymessaging.network

/**
 * Tracks devices that the networking layer has connected to.
 *
 * This is a starter state manager, not yet a Bluetooth/Wi-Fi implementation.
 */
class MeshConnectionManager {

    private val connectedDevices = mutableSetOf<String>()

    @Synchronized
    fun onDeviceConnected(deviceId: String) {
        if (deviceId.isNotBlank()) {
            connectedDevices.add(deviceId)
        }
    }

    @Synchronized
    fun onDeviceDisconnected(deviceId: String) {
        connectedDevices.remove(deviceId)
    }

    @Synchronized
    fun getConnectedDevices(): Set<String> {
        return connectedDevices.toSet()
    }

    @Synchronized
    fun isConnected(deviceId: String): Boolean {
        return deviceId in connectedDevices
    }

    @Synchronized
    fun clearConnections() {
        connectedDevices.clear()
    }
}