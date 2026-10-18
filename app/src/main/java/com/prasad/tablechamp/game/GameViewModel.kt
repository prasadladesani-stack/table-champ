package com.prasad.tablechamp.game

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    enum class Mode { PRACTICE, SPEED, SURVIVAL, MEMORY, BOSS }

    data class Question(
        val a: Int,
        val b: Int,
        val answer: Int,
        val questionText: String,
        val options: List<Int>
    )

    private val prefs: SharedPreferences =
        application.getSharedPreferences("progress", Context.MODE_PRIVATE)

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> = _currentQuestion

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _streak = MutableLiveData(0)
    val streak: LiveData<Int> = _streak

    private val _lives = MutableLiveData(3)
    val lives: LiveData<Int> = _lives

    private val _timeLeft = MutableLiveData<Int?>(null)
    val timeLeft: LiveData<Int?> = _timeLeft

    private val _finished = MutableLiveData(false)
    val finished: LiveData<Boolean> = _finished

    val bestScore = MutableLiveData(prefs.getInt("best", 0))
    val gems = MutableLiveData(prefs.getInt("gems", 0))

    var selectedTable: Int? = null
    private var totalQuestions = 10
    private var questionIndex = 0
    private var currentAnswer = 0
    private var memoryValue = 0
    private var mode = Mode.PRACTICE
    private var table = 2
    private val random = Random(System.currentTimeMillis())
    private var timer: CountDownTimer? = null
    val speedTotalSeconds = 45

    fun modeName(): String = when (mode) {
        Mode.PRACTICE -> "Practice"
        Mode.SPEED -> "Speed Rush"
        Mode.SURVIVAL -> "Survival"
        Mode.MEMORY -> "Memory Match"
        Mode.BOSS -> "Boss Battle"
    }

    fun startGame(m: Mode, table: Int, count: Int) {
        mode = m
        this.table = table
        selectedTable = table
        totalQuestions = count
        questionIndex = 0
        _score.value = 0
        _streak.value = 0
        _lives.value = 3
        _finished.value = false
        memoryValue = 0
        startTimerIfNeeded()
        nextQuestion()
    }

    private fun startTimerIfNeeded() {
        cancelTimer()
        if (mode == Mode.SPEED) {
            timer = object : CountDownTimer(speedTotalSeconds * 1000L, 1000L) {
                override fun onTick(millisUntilFinished: Long) {
                    _timeLeft.value = (millisUntilFinished / 1000L).toInt()
                }

                override fun onFinish() {
                    _timeLeft.value = 0
                    _finished.value = true
                    saveProgress()
                }
            }
            timer?.start()
        } else {
            _timeLeft.value = null
        }
    }

    fun startIfNeeded() {
        if (mode == Mode.SPEED && timer == null) startTimerIfNeeded()
    }

    private fun cancelTimer() {
        timer?.cancel()
        timer = null
    }

    fun nextQuestion() {
        if (mode != Mode.SPEED && mode != Mode.SURVIVAL && questionIndex >= totalQuestions) {
            _finished.value = true
            saveProgress()
            return
        }

        if (mode == Mode.SPEED && (_timeLeft.value ?: 0) <= 0) {
            _finished.value = true
            saveProgress()
            return
        }

        questionIndex++

        val a = when (mode) {
            Mode.PRACTICE, Mode.MEMORY -> table
            else -> random.nextInt(1, 21)
        }
        val b = random.nextInt(1, 13)
        val ans = a * b
        currentAnswer = ans

        if (mode == Mode.MEMORY) {
            memoryValue = ans
            val opts = generateOptionsForMemory(memoryValue)
            _currentQuestion.value = Question(a, b, ans, memoryValue.toString(), opts)
            return
        }

        val questionText = "$a × $b = ?"
        val options = generateOptions(a, b, ans)
        _currentQuestion.value = Question(a, b, ans, questionText, options)
    }

    fun pickAnswer(picked: Int) {
        val current = _currentQuestion.value ?: return
        if (picked == currentAnswer) {
            val newScore = (_score.value ?: 0) + 1
            _score.value = newScore
            _streak.value = (_streak.value ?: 0) + 1
            val newGems = (gems.value ?: 0) + 5
            gems.value = newGems
            prefs.edit().putInt("gems", newGems).apply()
        } else {
            _streak.value = 0
            if (mode == Mode.SURVIVAL) {
                val nextLives = (_lives.value ?: 1) - 1
                _lives.value = nextLives
                if (nextLives <= 0) {
                    _finished.value = true
                    saveProgress()
                }
            }
        }
    }

    fun isFinished(): Boolean = _finished.value == true

    private fun generateOptions(a: Int, b: Int, ans: Int): List<Int> {
        val opts = linkedSetOf<Int>()
        opts.add(ans)
        opts.add((a * (b + if (random.nextBoolean()) 1 else -1)).coerceAtLeast(1))
        opts.add((ans + random.nextInt(1, 4) * if (random.nextBoolean()) 1 else -1).coerceAtLeast(1))
        opts.add((a * (b + 2)).coerceAtLeast(1))
        while (opts.size < 4) {
            opts.add((ans + random.nextInt(-10, 11)).coerceAtLeast(1))
        }
        return opts.toList().shuffled().take(4)
    }

    private fun generateOptionsForMemory(value: Int): List<Int> {
        val opts = linkedSetOf<Int>()
        opts.add(value)
        while (opts.size < 4) {
            opts.add((value + random.nextInt(-7, 8)).coerceAtLeast(1))
        }
        return opts.toList().shuffled().take(4)
    }

    private fun saveProgress() {
        val sc = _score.value ?: 0
        val old = prefs.getInt("best", 0)
        if (sc > old) {
            prefs.edit().putInt("best", sc).apply()
            bestScore.value = sc
        }
    }

    fun restartSameGame() {
        startGame(mode, table, totalQuestions)
    }

    override fun onCleared() {
        super.onCleared()
        cancelTimer()
    }
}
