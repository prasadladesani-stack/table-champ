package com.prasad.tablechamp.ui

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.prasad.tablechamp.R
import com.prasad.tablechamp.game.GameViewModel

class GameFragment : Fragment() {

    companion object {
        fun newInstance() = GameFragment()
    }

    private lateinit var vm: GameViewModel
    private val optionButtons = Array<Button?>(4) { null }
    private var mediaCorrect: MediaPlayer? = null
    private var mediaWrong: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val questionTv = view.findViewById<TextView>(R.id.tv_question)
        val scoreTv = view.findViewById<TextView>(R.id.tv_score)
        val streakTv = view.findViewById<TextView>(R.id.tv_streak)
        val livesTv = view.findViewById<TextView>(R.id.tv_lives)
        val timeTv = view.findViewById<TextView>(R.id.tv_time)
        val progress = view.findViewById<ProgressBar>(R.id.progress_time)

        optionButtons[0] = view.findViewById(R.id.opt1)
        optionButtons[1] = view.findViewById(R.id.opt2)
        optionButtons[2] = view.findViewById(R.id.opt3)
        optionButtons[3] = view.findViewById(R.id.opt4)

        view.findViewById<Button>(R.id.btn_exit).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val correctId = resources.getIdentifier("correct", "raw", requireContext().packageName)
        val wrongId = resources.getIdentifier("wrong", "raw", requireContext().packageName)
        if (correctId != 0) mediaCorrect = MediaPlayer.create(requireContext(), correctId)
        if (wrongId != 0) mediaWrong = MediaPlayer.create(requireContext(), wrongId)

        vm.currentQuestion.observe(viewLifecycleOwner) { q ->
            questionTv.text = q.questionText
            for (i in 0 until 4) {
                optionButtons[i]?.isEnabled = true
                optionButtons[i]?.text = q.options.getOrNull(i)?.toString() ?: ""
            }
        }

        vm.score.observe(viewLifecycleOwner) { s ->
            scoreTv.text = "Score: $s"
        }

        vm.streak.observe(viewLifecycleOwner) { st ->
            streakTv.text = "Streak: $st"
        }

        vm.lives.observe(viewLifecycleOwner) { l ->
            livesTv.text = "Lives: $l"
        }

        vm.timeLeft.observe(viewLifecycleOwner) { t ->
            if (t == null) {
                timeTv.visibility = View.GONE
                progress.visibility = View.GONE
            } else {
                timeTv.visibility = View.VISIBLE
                progress.visibility = View.VISIBLE
                timeTv.text = "Time: ${t}s"
                val percent = ((t.toFloat() / vm.speedTotalSeconds) * 100f).toInt().coerceIn(0, 100)
                progress.progress = percent
            }
        }

        for (i in 0 until 4) {
            optionButtons[i]?.setOnClickListener { btn ->
                for (b in optionButtons) b?.isEnabled = false
                val selected = (btn as Button).text.toString().toIntOrNull() ?: -1
                val correct = vm.currentQuestion.value?.answer ?: -1
                val anim = if (selected == correct) R.anim.correct_anim else R.anim.wrong_anim
                btn.startAnimation(AnimationUtils.loadAnimation(requireContext(), anim))
                if (selected == correct) {
                    mediaCorrect?.start()
                } else {
                    mediaWrong?.start()
                    vibrateShort()
                }
                vm.pickAnswer(selected)
                btn.postDelayed({
                    if (vm.isFinished()) {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.container, FinishFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        vm.nextQuestion()
                    }
                }, 700)
            }
        }

        vm.finished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, FinishFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
        }

        vm.startIfNeeded()
    }

    private fun vibrateShort() {
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        try {
            vibrator.vibrate(60)
        } catch (_: Exception) {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaCorrect?.release()
        mediaWrong?.release()
        mediaCorrect = null
        mediaWrong = null
    }
}
