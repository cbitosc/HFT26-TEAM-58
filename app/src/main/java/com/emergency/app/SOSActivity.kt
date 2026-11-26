package com.emergency.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class SOSActivity : Activity() {

    private var selectedContact: EmergencyContact? = null
    private lateinit var contactText: TextView

    private val blue = Color.rgb(35, 75, 145)
    private val red = Color.rgb(190, 45, 45)
    private val dark = Color.rgb(35, 45, 60)

    private val sosMessage =
        "SOS! I need emergency help. Please contact me immediately."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 15, 16, 12)
            setBackgroundColor(Color.rgb(245, 247, 252))
        }

        val title = TextView(this).apply {
            text = "SOS EMERGENCY"
            textSize = 25f
            setTextColor(red)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(4, 12, 4, 18)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scroll = ScrollView(this)
        scroll.addView(content)

        val instruction = TextView(this).apply {
            text = "Choose the person you want to contact in an emergency."
            textSize = 17f
            setTextColor(dark)
            setPadding(4, 8, 4, 18)
        }

        val chooseButton = Button(this).apply {
            text = "CHOOSE SOS CONTACT"
            textSize = 17f
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(blue)
            setOnClickListener {
                chooseContact()
            }
        }

        contactText = TextView(this).apply {
            text = "No contact selected"
            textSize = 17f
            setTextColor(dark)
            setPadding(12, 16, 12, 16)
        }

        val messageTitle = TextView(this).apply {
            text = "Emergency message"
            textSize = 18f
            setTextColor(dark)
            setTypeface(null, Typeface.BOLD)
            setPadding(4, 22, 4, 10)
        }

        val messagePreview = TextView(this).apply {
            text = sosMessage
            textSize = 17f
            setTextColor(dark)
            setPadding(15, 18, 15, 18)
            setBackgroundColor(Color.WHITE)
        }

        content.addView(instruction)
        content.addView(chooseButton)
        content.addView(contactText)
        content.addView(messageTitle)
        content.addView(messagePreview)

        val sosButton = Button(this).apply {
            text = "PREPARE SOS MESSAGE"
            textSize = 17f
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(red)
            setOnClickListener {
                prepareSOS()
            }
        }

        val backButton = Button(this).apply {
            text = "← BACK TO HOME"
            textSize = 17f
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(dark)
            setOnClickListener {
                finish()
            }
        }

        val bottomArea = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        bottomArea.addView(
            sosButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                55
            ).apply {
                bottomMargin = 7
            }
        )

        bottomArea.addView(
            backButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                55
            )
        )

        root.addView(title)

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
    }

    private fun chooseContact() {

        val contacts = ContactStore.getContacts(this)

        if (contacts.isEmpty()) {
            Toast.makeText(
                this,
                "Please add an emergency contact first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val names = contacts.map {
            "${it.name} - ${it.phone}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Choose SOS Contact")
            .setItems(names) { _, position ->

                selectedContact = contacts[position]

                contactText.text =
                    "Selected: ${selectedContact!!.name}\n${selectedContact!!.phone}"
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun prepareSOS() {

        val contact = selectedContact

        if (contact == null) {
            Toast.makeText(
                this,
                "Please choose an emergency contact",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Prepare SOS Message")
            .setMessage(
                "Open SMS with the emergency message for ${contact.name}?"
            )
            .setPositiveButton("CONTINUE") { _, _ ->

                val intent = Intent(
                    Intent.ACTION_SENDTO,
                    Uri.parse("smsto:${contact.phone}")
                )

                intent.putExtra("sms_body", sosMessage)

                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "No SMS application found",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }
}