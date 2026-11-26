package com.emergency.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private val blue = Color.rgb(35, 75, 145)
    private val red = Color.rgb(190, 45, 45)
    private val dark = Color.rgb(35, 45, 60)

    // Corrected variable name
    private val screenBackgroundColor = Color.rgb(245, 247, 252)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 25, 18, 18)

            // Correct background assignment
            setBackgroundColor(screenBackgroundColor)
        }

        // MAIN TITLE
        val title = TextView(this).apply {
            text = "EMERGENCY CONNECT"
            textSize = 25f
            setTextColor(blue)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(5, 25, 5, 10)
        }

        // SUBTITLE
        val subtitle = TextView(this).apply {
            text = "Stay Connected. Stay Safe."
            textSize = 17f
            setTextColor(dark)
            gravity = Gravity.CENTER
            setPadding(5, 5, 5, 30)
        }

        // HOME SCREEN OPTIONS
        val options = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            addView(options)
        }

        // CONTACTS BUTTON
        addOption(
            options,
            "CONTACTS",
            "Add and manage emergency contacts",
            blue
        ) {
            startActivity(
                Intent(this, ContactsActivity::class.java)
            )
        }

        // MESSAGES BUTTON
        addOption(
            options,
            "MESSAGES",
            "Choose a contact and prepare a message",
            blue
        ) {
            startActivity(
                Intent(this, MessagesActivity::class.java)
            )
        }

        // SOS BUTTON
        addOption(
            options,
            "SOS EMERGENCY",
            "Prepare an emergency SOS message",
            red
        ) {
            startActivity(
                Intent(this, SOSActivity::class.java)
            )
        }

        // ADD COMPONENTS TO SCREEN
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

        setContentView(root)
    }

    // FUNCTION TO CREATE HOME SCREEN OPTIONS
    private fun addOption(
        parent: LinearLayout,
        heading: String,
        description: String,
        color: Int,
        action: () -> Unit
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(Color.WHITE)
            elevation = 6f
            isClickable = true
            isFocusable = true
            setOnClickListener {
                action()
            }
        }

        val headingText = TextView(this).apply {
            text = heading
            textSize = 21f
            setTextColor(color)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(8, 12, 8, 12)
        }

        val detail = TextView(this).apply {
            text = description
            textSize = 16f
            setTextColor(dark)
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 12)
        }

        val actionText = TextView(this).apply {
            text = "TAP TO OPEN  →"
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(12, 16, 12, 16)
            setBackgroundColor(color)
        }

        card.addView(headingText)
        card.addView(detail)
        card.addView(
            actionText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        parent.addView(
            card,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 22
            }
        )
    }
}