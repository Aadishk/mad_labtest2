package com.example.labtest2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.View
.import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private val masterContactsList = mutableListOf<ContactItem>()
    private val filteredContactsList = mutableListOf<ContactItem>()
    private val emergencyContactsList = mutableListOf<ContactItem>()

    private lateinit var viewEmergency: View
    private lateinit var viewContacts: View

    private lateinit var etSearchContact: EditText
    private lateinit var tvEmergencyHeader: TextView
    private lateinit var tvEmptyEmergency: TextView
    private lateinit var lvEmergencyContacts: ListView

    private lateinit var tvAllContactsHeader: TextView
    private lateinit var tvEmptyAllContacts: TextView
    private lateinit var lvAllContacts: ListView
    private lateinit var layoutPermission: LinearLayout
    private lateinit var btnGrantPermission: MaterialButton

    private lateinit var tabEmergency: LinearLayout
    private lateinit var ivTabEmergencyIcon: ImageView
    private lateinit var tvTabEmergencyLabel: TextView

    private lateinit var tabContacts: LinearLayout
    private lateinit var ivTabContactsIcon: ImageView
    private lateinit var tvTabContactsLabel: TextView

    private lateinit var contactAdapter: ContactAdapter
    private lateinit var emergencyAdapter: EmergencyContactAdapter

    private var pendingCallContact: ContactItem? = null

    private val requestContactsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                loadPhoneContacts()
            } else {
                Toast.makeText(this, "Permission denied to read contacts.", Toast.LENGTH_SHORT).show()
                loadFallbackContacts()
            }
        }

    private val requestCallPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val target = pendingCallContact
            if (isGranted && target != null) {
                makePhoneCall(target)
            } else {
                target?.let {
                    Toast.makeText(
                        this,
                        "${getString(R.string.call_permission_denied)} (${it.name})",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
            pendingCallContact = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupAdapters()
        setupSearch()
        setupBottomNavigation()
        checkAndLoadContacts()
    }

    private fun initViews() {
        viewEmergency = findViewById(R.id.viewEmergency)
        viewContacts = findViewById(R.id.viewContacts)

        etSearchContact = findViewById(R.id.etSearchContact)
        tvEmergencyHeader = findViewById(R.id.tvEmergencyHeader)
        tvEmptyEmergency = findViewById(R.id.tvEmptyEmergency)
        lvEmergencyContacts = findViewById(R.id.lvEmergencyContacts)

        tvAllContactsHeader = findViewById(R.id.tvAllContactsHeader)
        tvEmptyAllContacts = findViewById(R.id.tvEmptyAllContacts)
        lvAllContacts = findViewById(R.id.lvAllContacts)
        layoutPermission = findViewById(R.id.layoutPermission)
        btnGrantPermission = findViewById(R.id.btnGrantPermission)

        tabEmergency = findViewById(R.id.tabEmergency)
        ivTabEmergencyIcon = findViewById(R.id.ivTabEmergencyIcon)
        tvTabEmergencyLabel = findViewById(R.id.tvTabEmergencyLabel)

        tabContacts = findViewById(R.id.tabContacts)
        ivTabContactsIcon = findViewById(R.id.ivTabContactsIcon)
        tvTabContactsLabel = findViewById(R.id.tvTabContactsLabel)

        btnGrantPermission.setOnClickListener {
            requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun setupAdapters() {
        contactAdapter = ContactAdapter(this, filteredContactsList) { contact ->
            onEmergencyContactToggled(contact)
        }
        lvAllContacts.adapter = contactAdapter

        emergencyAdapter = EmergencyContactAdapter(
            this,
            emergencyContactsList,
            onCallClicked = { contact ->
                handleCallClick(contact)
            },
            onEmergencyToggle = { contact ->
                onEmergencyContactToggled(contact)
            },
        )
        lvEmergencyContacts.adapter = emergencyAdapter
    }

    private fun setupSearch() {
        etSearchContact.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterContacts(s?.toString().orEmpty())
                }

                override fun afterTextChanged(s: Editable?) {}
            },
        )
    }

    private fun setupBottomNavigation() {
        tabEmergency.setOnClickListener {
            selectTab(isEmergencyTab = true)
        }
        tabContacts.setOnClickListener {
            selectTab(isEmergencyTab = false)
        }
        selectTab(isEmergencyTab = true)
    }

    private fun selectTab(isEmergencyTab: Boolean) {
        if (isEmergencyTab) {
            viewEmergency.visibility = View.VISIBLE
            viewContacts.visibility = View.GONE

            tabEmergency.setBackgroundResource(R.drawable.bg_tab_selected)
            tabContacts.setBackgroundResource(android.R.color.transparent)

            ivTabEmergencyIcon.setColorFilter(ContextCompat.getColor(this, R.color.ios_red))
            tvTabEmergencyLabel.setTextColor(ContextCompat.getColor(this, R.color.ios_red))
            tvTabEmergencyLabel.setTypeface(null, Typeface.BOLD)

            ivTabContactsIcon.setColorFilter(ContextCompat.getColor(this, R.color.ios_text_secondary))
            tvTabContactsLabel.setTextColor(ContextCompat.getColor(this, R.color.ios_text_secondary))
            tvTabContactsLabel.setTypeface(null, Typeface.NORMAL)
        } else {
            viewEmergency.visibility = View.GONE
            viewContacts.visibility = View.VISIBLE

            tabContacts.setBackgroundResource(R.drawable.bg_tab_selected)
            tabEmergency.setBackgroundResource(android.R.color.transparent)

            ivTabContactsIcon.setColorFilter(ContextCompat.getColor(this, R.color.ios_blue))
            tvTabContactsLabel.setTextColor(ContextCompat.getColor(this, R.color.ios_blue))
            tvTabContactsLabel.setTypeface(null, Typeface.BOLD)

            ivTabEmergencyIcon.setColorFilter(ContextCompat.getColor(this, R.color.ios_text_secondary))
            tvTabEmergencyLabel.setTextColor(ContextCompat.getColor(this, R.color.ios_text_secondary))
            tvTabEmergencyLabel.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun checkAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadPhoneContacts()
        } else {
            layoutPermission.visibility = View.VISIBLE
            requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun loadPhoneContacts() {
        layoutPermission.visibility = View.GONE
        masterContactsList.clear()

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
            ),
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC",
        )

        val seenNumbers = mutableSetOf<String>()

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val id = if (idIndex >= 0) it.getString(idIndex) else ""
                val name = if (nameIndex >= 0) it.getString(nameIndex) ?: "Unknown" else "Unknown"
                val rawNumber = if (numberIndex >= 0) it.getString(numberIndex) ?: "" else ""
                val cleanNumber = rawNumber.replace("\\s+".toRegex(), "")

                if (cleanNumber.isNotEmpty() && !seenNumbers.contains(cleanNumber)) {
                    seenNumbers.add(cleanNumber)
                    masterContactsList.add(
                        ContactItem(
                            id = id,
                            name = name,
                            phoneNumber = rawNumber,
                            isEmergency = false,
                        )
                    )
                }
            }
        }

        if (masterContactsList.isEmpty()) {
            loadFallbackContacts()
        } else {
            masterContactsList[0].isEmergency = true
            filterContacts(etSearchContact.text.toString())
        }
    }

    private fun loadFallbackContacts() {
        layoutPermission.visibility = View.GONE
        masterContactsList.clear()
        masterContactsList.addAll(
            listOf(
                ContactItem("1", "Embassy SOS Hotline", "+1-800-555-0199", isEmergency = true),
                ContactItem("2", "Travel Insurance Helpline", "+1-800-555-0144", isEmergency = true),
                ContactItem("3", "Alex River (Tour Guide)", "+1-555-0178", isEmergency = false),
                ContactItem("4", "Grand Hotel Front Desk", "+1-555-0123", isEmergency = false),
                ContactItem("5", "Sarah Connor (Travel Companion)", "+1-555-0191", isEmergency = false),
            )
        )
        filterContacts(etSearchContact.text.toString())
    }

    private fun filterContacts(query: String) {
        filteredContactsList.clear()
        val cleanQuery = query.trim().lowercase()

        if (cleanQuery.isEmpty()) {
            filteredContactsList.addAll(masterContactsList)
        } else {
            filteredContactsList.addAll(
                masterContactsList.filter {
                    it.name.lowercase().contains(cleanQuery) ||
                            it.phoneNumber.lowercase().contains(cleanQuery)
                }
            )
        }

        tvEmptyAllContacts.visibility = if (filteredContactsList.isEmpty()) View.VISIBLE else View.GONE
        contactAdapter.updateContacts(filteredContactsList)
        updateEmergencyContactsUI()
    }

    private fun onEmergencyContactToggled(contact: ContactItem) {
        val masterMatch = masterContactsList.find { it.id == contact.id || it.phoneNumber == contact.phoneNumber }
        masterMatch?.isEmergency = contact.isEmergency

        contactAdapter.notifyDataSetChanged()
        updateEmergencyContactsUI()
    }

    private fun updateEmergencyContactsUI() {
        emergencyContactsList.clear()
        emergencyContactsList.addAll(masterContactsList.filter { it.isEmergency })

        val count = emergencyContactsList.size
        tvEmergencyHeader.text = getString(R.string.emergency_section_title_count, count)

        if (emergencyContactsList.isEmpty()) {
            tvEmptyEmergency.visibility = View.VISIBLE
            lvEmergencyContacts.visibility = View.GONE
        } else {
            tvEmptyEmergency.visibility = View.GONE
            lvEmergencyContacts.visibility = View.VISIBLE
            emergencyAdapter.updateContacts(emergencyContactsList)
        }

        tvAllContactsHeader.text = getString(R.string.all_contacts_section_count, masterContactsList.size)
    }

    private fun handleCallClick(contact: ContactItem) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            makePhoneCall(contact)
        } else {
            pendingCallContact = contact
            Toast.makeText(
                this,
                "${getString(R.string.call_permission_denied)} (${contact.name})",
                Toast.LENGTH_SHORT,
            ).show()
            requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    private fun makePhoneCall(contact: ContactItem) {
        try {
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = "tel:${contact.phoneNumber}".toUri()
            }
            startActivity(callIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not launch call: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}