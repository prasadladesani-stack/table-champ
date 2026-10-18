package com.prasad.tablechamp.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.prasad.tablechamp.R

class ChooseTableDialog(private val onSelected: (Int) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_choose_table, null)
        val container = view.findViewById<LinearLayout>(R.id.table_container)

        for (n in 1..20) {
            val button = Button(requireContext())
            button.text = n.toString()
            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            lp.setMargins(6, 6, 6, 6)
            button.layoutParams = lp
            button.setOnClickListener {
                onSelected(n)
                dismiss()
            }

            if ((n - 1) % 4 == 0) {
                val row = LinearLayout(requireContext())
                row.orientation = LinearLayout.HORIZONTAL
                row.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                container.addView(row)
                row.addView(button)
            } else {
                val lastRow = container.getChildAt(container.childCount - 1) as LinearLayout
                lastRow.addView(button)
            }
        }

        builder.setView(view)
        return builder.create()
    }
}
