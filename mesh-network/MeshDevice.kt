```kotlin
/**
 * Represents a nearby device in the emergency messaging mesh.
 */
data class MeshDevice(
    val endpointId: String,
    val deviceName: String,
    val isConnected: Boolean = false
)

/**
 * Manages discovered and connected devices.
 */
class MeshDeviceManager {

    private val devices = mutableMapOf<String, MeshDevice>()

    fun addDiscoveredDevice(device: MeshDevice) {
        devices[device.endpointId] = device
    }

    fun markConnected(endpointId: String) {
        val device = devices[endpointId] ?: return
        devices[endpointId] = device.copy(isConnected = true)
    }

    fun removeDevice(endpointId: String) {
        devices.remove(endpointId)
    }

    fun getAllDevices(): List<MeshDevice> {
        return devices.values.toList()
    }

    fun getConnectedDevices(): List<MeshDevice> {
        return devices.values.filter { it.isConnected }
    }
}
```