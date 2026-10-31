package com.emergency.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class ContactsActivity : Activity() {

    private lateinit var contactList: LinearLayout

    private val blue = Color.rgb(35, 75, 145)
    private val dark = Color.rgb(35, 45, 60)
    private val red = Color.rgb(190, 45, 45)
    private val bg = Color.rgb(245, 247, 252)

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MAIN SCREEN
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setBackgroundColor(bg)
        }

        // TITLE
        val title = TextView(this).apply {
            text = "EMERGENCY CONTACTS"
            textSize = 23f
            gravity = Gravity.CENTER
            setTextColor(blue)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(12), 0, dp(8))
        }

        // SUBTITLE
        val subtitle = TextView(this).apply {
            text = "Manage your important contacts"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(dark)
            setPadding(0, dp(5), 0, dp(15))
        }

        // CONTACT LIST
        contactList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // SCROLL VIEW
        // SCROLL VIEW
        val scroll = ScrollView(this).apply {
            isFillViewport = true
            isVerticalScrollBarEnabled = true

            addView(
                contactList,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }

        // ADD BUTTON
        val addButton = Button(this).apply {
            text = "+ ADD CONTACT"
            textSize = 17f
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(blue)
            isEnabled = true

            setOnClickListener {
                showAddContactDialog()
            }
        }

        // BACK BUTTON
        val backButton = Button(this).apply {
            text = "← BACK TO HOME"
            textSize = 16f
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(dark)
            isEnabled = true

            setOnClickListener {
                finish()
            }
        }

        // BOTTOM BUTTONS
        val bottomArea = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, dp(4))
        }

        bottomArea.addView(
            addButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(55)
            ).apply {
                bottomMargin = dp(8)
            }
        )

        bottomArea.addView(
            backButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        root.addView(title)
        root.addView(subtitle)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(bottomArea)

        setContentView(root)

        refreshContacts()
    }

    // ADD CONTACT DIALOG
    private fun showAddContactDialog() {

        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(10), dp(20), dp(10))
        }

        val nameInput = EditText(this).apply {
            hint = "Enter contact name"
            textSize = 17f
            maxLines = 1
            inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS
        }

        val phoneInput = EditText(this).apply {
            hint = "Enter phone number"
            textSize = 17f
            maxLines = 1
            inputType = InputType.TYPE_CLASS_PHONE
        }

        form.addView(nameInput)
        form.addView(phoneInput)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Emergency Contact")
            .setView(form)
            .setPositiveButton("SAVE", null)
            .setNegativeButton("CANCEL", null)
            .create()

        // SHOW DIALOG FIRST
        dialog.show()

        // SAVE BUTTON
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {

                val name = nameInput.text.toString().trim()
                val phone = phoneInput.text.toString().trim()

                if (name.isEmpty()) {
                    nameInput.error = "Enter contact name"
                    nameInput.requestFocus()
                    return@setOnClickListener
                }

                if (phone.count { it.isDigit() } < 7) {
                    phoneInput.error = "Enter valid phone number"
                    phoneInput.requestFocus()
                    return@setOnClickListener
                }

                try {
                    ContactStore.addContact(
                        this@ContactsActivity,
                        name,
                        phone
                    )

                    dialog.dismiss()

                    refreshContacts()

                    Toast.makeText(
                        this@ContactsActivity,
                        "Contact saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@ContactsActivity,
                        "Error saving contact: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // REFRESH CONTACT LIST
    private fun refreshContacts() {

        contactList.removeAllViews()

        val contacts = ContactStore.getContacts(this)

        if (contacts.isEmpty()) {

            val emptyText = TextView(this).apply {
                text = "No contacts saved yet.\n\nTap ADD CONTACT to add one."
                textSize = 17f
                gravity = Gravity.CENTER
                setTextColor(dark)
                setPadding(dp(12), dp(40), dp(12), dp(40))
            }

            contactList.addView(emptyText)
            return
        }

        for (contact in contacts) {

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(15), dp(12), dp(15), dp(12))
                setBackgroundColor(Color.WHITE)
                elevation = dp(3).toFloat()
            }

            val nameText = TextView(this).apply {
                text = contact.name
                textSize = 20f
                setTextColor(blue)
                setTypeface(null, Typeface.BOLD)
            }

            val phoneText = TextView(this).apply {
                text = contact.phone
                textSize = 17f
                setTextColor(dark)
                setPadding(0, dp(5), 0, dp(8))
            }

            val deleteButton = Button(this).apply {
                text = "DELETE CONTACT"
                textSize = 14f
                isAllCaps = false
                setTextColor(Color.WHITE)
                setBackgroundColor(red)

                setOnClickListener {

                    AlertDialog.Builder(this@ContactsActivity)
                        .setTitle("Delete Contact")
                        .setMessage("Delete ${contact.name}?")
                        .setPositiveButton("DELETE") { _, _ ->

                            ContactStore.deleteContact(
                                this@ContactsActivity,
                                contact.phone
                            )

                            refreshContacts()

                            Toast.makeText(
                                this@ContactsActivity,
                                "Contact deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton("CANCEL", null)
                        .show()
                }
            }

            card.addView(nameText)
            card.addView(phoneText)

            card.addView(
                deleteButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(48)
                )
            )

            contactList.addView(
                card,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(12)
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (::contactList.isInitialized) {
            refreshContacts()
        }
    }
}
