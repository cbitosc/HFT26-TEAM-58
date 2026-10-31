package com.emergency.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import android.content.res.ColorStateList
import android.widget.ProgressBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessagesActivity : Activity() {

    private var selectedContact: EmergencyContact? = null

    private lateinit var selectedContactText: TextView
    private lateinit var messageInput: EditText
    private lateinit var aiResultText: TextView

    private val blue = Color.rgb(35, 75, 145)
    private val dark = Color.rgb(35, 45, 60)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 8, 12, 8)
            setBackgroundColor(Color.rgb(245, 247, 252))
        }

        val title = TextView(this).apply {
            text = "EMERGENCY MESSAGES"
            textSize = 22f
            setTextColor(blue)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(4, 10, 4, 12)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(2, 2, 2, 8)
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            isVerticalScrollBarEnabled = true
            addView(content)
        }

        val contactLabel = TextView(this).apply {
            text = "Choose emergency contact"
            textSize = 16f
            setTextColor(dark)
            setPadding(4, 8, 4, 8)
        }

        val chooseButton = createButton(
            "CHOOSE CONTACT",
            blue
        ) {
            chooseContact()
        }

        selectedContactText = TextView(this).apply {
            text = "No contact selected"
            textSize = 15f
            setTextColor(dark)
            setPadding(10, 10, 10, 10)
        }

        val messageLabel = TextView(this).apply {
            text = "Type your emergency message"
            textSize = 16f
            setTextColor(dark)
            setPadding(4, 12, 4, 6)
        }

        messageInput = EditText(this).apply {
            hint = "Enter your message here"
            textSize = 16f
            minLines = 4
            gravity = Gravity.TOP
            inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setPadding(12, 12, 12, 12)
        }

        aiResultText = TextView(this).apply {
            text = "AI results will appear here."
            textSize = 15f
            setTextColor(dark)
            setPadding(12, 14, 12, 14)
            setBackgroundColor(Color.WHITE)
        }

        content.addView(contactLabel)
        content.addView(chooseButton)
        content.addView(selectedContactText)
        content.addView(messageLabel)
        content.addView(
            messageInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        )

        // AI buttons

        content.addView(
            createButton("SUMMARIZE MESSAGE", blue) {
                summarizeMessage()
            }
        )

        content.addView(
            createButton("CHECK URGENCY", blue) {
                classifyMessage()
            }
        )

        content.addView(
            createButton("TRANSLATE MESSAGE", blue) {
                showTranslationDialog()
            }
        )

        val resultLabel = TextView(this).apply {
            text = "AI RESULT"
            textSize = 15f
            setTextColor(blue)
            setTypeface(null, Typeface.BOLD)
            setPadding(4, 12, 4, 6)
        }

        content.addView(resultLabel)
        content.addView(aiResultText)

        // Fixed bottom buttons

        val bottomArea = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 6, 0, 2)
            setBackgroundColor(Color.rgb(245, 247, 252))
        }

        val sendButton = createButton(
            "SEND MESSAGE",
            blue
        ) {
            openSms()
        }

        val backButton = createButton(
            "BACK TO HOME",
            dark
        ) {
            finish()
        }

        bottomArea.addView(sendButton)
        bottomArea.addView(backButton)

        root.addView(title)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(
            bottomArea,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(root)
    }

    // Common button design for all five buttons

    private fun createButton(
        label: String,
        buttonColor: Int,
        action: () -> Unit
    ): Button {

        return Button(this).apply {

            text = label
            textSize = 15f

            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)

            gravity = Gravity.CENTER
            isAllCaps = false
            isSingleLine = true

            minHeight = 52
            minimumHeight = 52

            backgroundTintList =
                ColorStateList.valueOf(buttonColor)

            setPadding(8, 6, 8, 6)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                54
            ).apply {
                topMargin = 4
                bottomMargin = 4
            }

            setOnClickListener {
                action()
            }
        }
    }

    private fun getMessage(): String? {

        val message = messageInput.text.toString().trim()

        if (message.isEmpty()) {
            Toast.makeText(
                this,
                "Please type an emergency message first",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }

        return message
    }

    // SUMMARIZATION

    private fun summarizeMessage() {

        val message = getMessage() ?: return

        aiResultText.text = "Generating summary..."

        RetrofitClient.api.summarize(
            MessageRequest(message)
        ).enqueue(object : Callback<SummaryResponse> {

            override fun onResponse(
                call: Call<SummaryResponse>,
                response: Response<SummaryResponse>
            ) {
                if (response.isSuccessful) {

                    val result = response.body()

                    aiResultText.text =
                        if (result?.success == true) {
                            "SUMMARY:\n${result.response}"
                        } else {
                            "Could not generate summary."
                        }

                } else {
                    aiResultText.text =
                        "Summary failed. HTTP ${response.code()}"
                }
            }

            override fun onFailure(
                call: Call<SummaryResponse>,
                error: Throwable
            ) {
                aiResultText.text =
                    "Connection failed: ${error.message}"
            }
        })
    }

    // URGENCY CLASSIFICATION

    private fun classifyMessage() {

        val message = getMessage() ?: return

        aiResultText.text = "Checking emergency urgency..."

        RetrofitClient.api.classify(
            MessageRequest(message)
        ).enqueue(object : Callback<ClassificationResponse> {

            override fun onResponse(
                call: Call<ClassificationResponse>,
                response: Response<ClassificationResponse>
            ) {

                if (response.isSuccessful) {

                    val result = response.body()

                    aiResultText.text =
                        if (result?.success == true) {
                            "URGENCY: ${result.classification}\n\n" +
                                    (result.reason ?: "")
                        } else {
                            "Could not classify urgency."
                        }

                } else {
                    aiResultText.text =
                        "Classification failed. HTTP ${response.code()}"
                }
            }

            override fun onFailure(
                call: Call<ClassificationResponse>,
                error: Throwable
            ) {
                aiResultText.text =
                    "Connection failed: ${error.message}"
            }
        })
    }

    // TRANSLATION

    private fun showTranslationDialog() {

        val message = getMessage() ?: return

        val languageInput = EditText(this).apply {
            hint = "Example: Hindi, Telugu, Tamil"
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(30, 20, 30, 20)
        }

        AlertDialog.Builder(this)
            .setTitle("Translate emergency message")
            .setMessage("Enter the target language")
            .setView(languageInput)
            .setPositiveButton("TRANSLATE") { _, _ ->

                val language =
                    languageInput.text.toString().trim()

                if (language.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Please enter a language",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    translateMessage(message, language)
                }
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun translateMessage(
        message: String,
        language: String
    ) {

        aiResultText.text = "Translating message..."

        RetrofitClient.api.translate(
            TranslationRequest(message, language)
        ).enqueue(object : Callback<TranslationResponse> {

            override fun onResponse(
                call: Call<TranslationResponse>,
                response: Response<TranslationResponse>
            ) {

                if (response.isSuccessful) {

                    val result = response.body()

                    aiResultText.text =
                        if (result?.success == true) {
                            "TRANSLATION ($language):\n\n" +
                                    result.translated_message
                        } else {
                            "Could not translate message."
                        }

                } else {
                    aiResultText.text =
                        "Translation failed. HTTP ${response.code()}"
                }
            }

            override fun onFailure(
                call: Call<TranslationResponse>,
                error: Throwable
            ) {
                aiResultText.text =
                    "Connection failed: ${error.message}"
            }
        })
    }

    // CONTACT SELECTION

    private fun chooseContact() {

        val contacts = ContactStore.getContacts(this)

        if (contacts.isEmpty()) {

            Toast.makeText(
                this,
                "Please add a contact first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val names = contacts.map {
            "${it.name} - ${it.phone}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Contact")
            .setItems(names) { _, position ->

                selectedContact = contacts[position]

                selectedContactText.text =
                    "Selected: ${selectedContact!!.name}\n" +
                            selectedContact!!.phone
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    // SEND MESSAGE

    private fun openSms() {

        val contact = selectedContact

        if (contact == null) {

            Toast.makeText(
                this,
                "Please choose a contact",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val message = messageInput.text.toString().trim()

        if (message.isEmpty()) {

            Toast.makeText(
                this,
                "Please type a message",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val intent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("smsto:${contact.phone}")
        )

        intent.putExtra("sms_body", message)

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
}