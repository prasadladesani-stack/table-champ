package com.prasad.tablechamp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.prasad.tablechamp.R
import com.prasad.tablechamp.game.GameViewModel

class LearnFragment : Fragment() {
    private lateinit var vm: GameViewModel

    companion object {
        fun newInstance() = LearnFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        return inflater.inflate(R.layout.fragment_learn, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val table = vm.selectedTable ?: 2
        val header = view.findViewById<TextView>(R.id.learn_header)
        val learnContainer = view.findViewById<LinearLayout>(R.id.learn_container)
        header.text = "Table of $table"
        learnContainer.removeAllViews()

        for (i in 1..20) {
            val item = TextView(requireContext())
            item.text = "$table × $i = ${table * i}"
            item.setPadding(16, 12, 16, 12)
            item.setBackgroundResource(R.drawable.rounded_card)
            item.setTextColor(resources.getColor(R.color.white, null))
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 6, 0, 6)
            learnContainer.addView(item, lp)
        }

        view.findViewById<TextView>(R.id.learn_practice).setOnClickListener {
            vm.startGame(GameViewModel.Mode.PRACTICE, table, 10)
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, GameFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }
}
