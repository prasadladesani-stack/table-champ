package com.prasad.tablechamp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.prasad.tablechamp.R
import com.prasad.tablechamp.game.GameViewModel

class HomeFragment : Fragment() {

    private lateinit var vm: GameViewModel

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bestTv = view.findViewById<TextView>(R.id.bestText)
        val gemsTv = view.findViewById<TextView>(R.id.gemsText)
        bestTv.text = "🏆 BEST: ${vm.bestScore.value ?: 0}"
        gemsTv.text = "💎 GEMS: ${vm.gems.value ?: 0}"

        view.findViewById<Button>(R.id.btn_learn).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, LearnFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.btn_practice).setOnClickListener {
            ChooseTableDialog { table ->
                vm.startGame(GameViewModel.Mode.PRACTICE, table, 10)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, GameFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }.show(parentFragmentManager, "chooseTable")
        }

        view.findViewById<Button>(R.id.btn_speed).setOnClickListener {
            ChooseTableDialog { table ->
                vm.startGame(GameViewModel.Mode.SPEED, table, 999)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, GameFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }.show(parentFragmentManager, "chooseTable")
        }

        view.findViewById<Button>(R.id.btn_survival).setOnClickListener {
            ChooseTableDialog { table ->
                vm.startGame(GameViewModel.Mode.SURVIVAL, table, 999)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, GameFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }.show(parentFragmentManager, "chooseTable")
        }

        view.findViewById<Button>(R.id.btn_memory).setOnClickListener {
            ChooseTableDialog { table ->
                vm.startGame(GameViewModel.Mode.MEMORY, table, 10)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, GameFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }.show(parentFragmentManager, "chooseTable")
        }

        view.findViewById<Button>(R.id.btn_boss).setOnClickListener {
            ChooseTableDialog { table ->
                vm.startGame(GameViewModel.Mode.BOSS, table, 20)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, GameFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }.show(parentFragmentManager, "chooseTable")
        }
    }
}
