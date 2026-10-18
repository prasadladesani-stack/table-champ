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

class FinishFragment : Fragment() {

    companion object {
        fun newInstance() = FinishFragment()
    }

    private lateinit var vm: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        return inflater.inflate(R.layout.fragment_finish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tv_result_score).text = "${vm.score.value ?: 0} correct"
        view.findViewById<TextView>(R.id.tv_result_mode).text = "Mode: ${vm.modeName()}"
        view.findViewById<TextView>(R.id.tv_result_best).text = "Best: ${vm.bestScore.value ?: 0}"
        view.findViewById<TextView>(R.id.tv_result_gems).text = "Gems: ${vm.gems.value ?: 0}"

        view.findViewById<Button>(R.id.btn_restart).setOnClickListener {
            vm.restartSameGame()
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, GameFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.btn_home).setOnClickListener {
            parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment.newInstance())
                .commit()
        }
    }
}
