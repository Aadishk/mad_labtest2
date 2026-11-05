package com.example.labtest2

data class ContactItem(
    val id: String,
    val name: String,
    val phoneNumber: String,
    var isEmergency: Boolean = false,
) {
    val initial: String
        get() = name.firstOrNull { it.isLetterOrDigit() }?.uppercase() ?: "?"
}