package com.example.labtest2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class ContactAdapter(
    private val context: Context,
    private var contacts: List<ContactItem>,
    private val onEmergencyToggle: (ContactItem) -> Unit,
) : BaseAdapter() {

    fun updateContacts(newContacts: List<ContactItem>) {
        this.contacts = newContacts
        notifyDataSetChanged()
    }

    override fun getCount(): Int = contacts.size

    override fun getItem(position: Int): ContactItem = contacts[position]

    override fun getItemId(position: Int): Long = position.toLong()

    private class ViewHolder(view: View) {
        val tvAvatarInitial: TextView = view.findViewById(R.id.tvAvatarInitial)
        val tvContactName: TextView = view.findViewById(R.id.tvContactName)
        val tvContactPhone: TextView = view.findViewById(R.id.tvContactPhone)
        val cbEmergency: CheckBox = view.findViewById(R.id.cbEmergency)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val contact = getItem(position)

        holder.tvAvatarInitial.text = contact.initial
        holder.tvContactName.text = contact.name
        holder.tvContactPhone.text = contact.phoneNumber

        holder.cbEmergency.setOnCheckedChangeListener(null)
        holder.cbEmergency.isChecked = contact.isEmergency

        holder.cbEmergency.setOnCheckedChangeListener { _, isChecked ->
            contact.isEmergency = isChecked
            onEmergencyToggle(contact)
        }

        view.setOnClickListener {
            val newState = !contact.isEmergency
            contact.isEmergency = newState
            holder.cbEmergency.isChecked = newState
            onEmergencyToggle(contact)
        }

        return view
    }
}