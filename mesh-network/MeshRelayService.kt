package com.example.offlineemergencymessaging.network

/**
 * Prepares messages for relaying through the mesh.
 *
 * Actual device-to-device transmission will be added using
 * Google Nearby Connections API.
 */
class MeshRelayService(
    private val router: MeshRouter = MeshRouter()
) {

    /**
     * Returns a forwarded copy if the message is eligible.
     * Returns null if it is a duplicate or has reached the hop limit.
     */
    fun prepareMessageForRelay(message: MeshMessage): MeshMessage? {
        if (!router.shouldRelay(message)) {
            return null
        }

        return router.prepareRelay(message)
    }
}