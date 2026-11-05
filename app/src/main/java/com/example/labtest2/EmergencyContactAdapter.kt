package com.example.labtest2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView

class EmergencyContactAdapter(
    private val context: Context,
    private var emergencyContacts: List<ContactItem>,
    private val onCallClicked: (ContactItem) -> Unit,
    private val onEmergencyToggle: (ContactItem) -> Unit,
) : BaseAdapter() {

    fun updateContacts(newContacts: List<ContactItem>) {
        this.emergencyContacts = newContacts
        notifyDataSetChanged()
    }

    override fun getCount(): Int = emergencyContacts.size

    override fun getItem(position: Int): ContactItem = emergencyContacts[position]

    override fun getItemId(position: Int): Long = position.toLong()

    private class ViewHolder(view: View) {
        val tvEmAvatarInitial: TextView = view.findViewById(R.id.tvEmAvatarInitial)
        val tvEmContactName: TextView = view.findViewById(R.id.tvEmContactName)
        val tvEmContactPhone: TextView = view.findViewById(R.id.tvEmContactPhone)
        val btnCallEmergency: ImageButton = view.findViewById(R.id.btnCallEmergency)
        val cbEmRemove: CheckBox = view.findViewById(R.id.cbEmRemove)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_emergency_contact, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val contact = getItem(position)

        holder.tvEmAvatarInitial.text = contact.initial
        holder.tvEmContactName.text = contact.name
        holder.tvEmContactPhone.text = contact.phoneNumber

        holder.cbEmRemove.setOnCheckedChangeListener(null)
        holder.cbEmRemove.isChecked = contact.isEmergency

        holder.cbEmRemove.setOnCheckedChangeListener { _, isChecked ->
            contact.isEmergency = isChecked
            onEmergencyToggle(contact)
        }

        holder.btnCallEmergency.setOnClickListener {
            onCallClicked(contact)
        }

        return view
    }
}