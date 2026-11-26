package com.emergency.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class EmergencyContact(
    val name: String,
    val phone: String
)

object ContactStore {

    private const val PREF_NAME = "EmergencyContacts"
    private const val CONTACTS_KEY = "contacts"

    fun getContacts(context: Context): List<EmergencyContact> {

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val savedData = preferences.getString(
            CONTACTS_KEY,
            "[]"
        ) ?: "[]"

        val contacts = mutableListOf<EmergencyContact>()

        try {
            val array = JSONArray(savedData)

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)

                val name = item.optString("name", "")
                val phone = item.optString("phone", "")

                if (name.isNotBlank() && phone.isNotBlank()) {
                    contacts.add(EmergencyContact(name, phone))
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return contacts
    }

    fun addContact(
        context: Context,
        name: String,
        phone: String
    ) {
        val contacts = getContacts(context)
            .filterNot { it.phone == phone }
            .toMutableList()

        contacts.add(EmergencyContact(name, phone))

        saveContacts(context, contacts)
    }

    fun deleteContact(
        context: Context,
        phone: String
    ) {
        val contacts = getContacts(context)
            .filterNot { it.phone == phone }

        saveContacts(context, contacts)
    }

    private fun saveContacts(
        context: Context,
        contacts: List<EmergencyContact>
    ) {
        val array = JSONArray()

        for (contact in contacts) {
            val item = JSONObject()
            item.put("name", contact.name)
            item.put("phone", contact.phone)
            array.put(item)
        }

        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(CONTACTS_KEY, array.toString())
            .apply()
    }
}