package com.example.snakegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

class GameViewModel : ViewModel() {
	private val mutex = Mutex()
	private val mutableState =
		MutableStateFlow(State(food = getRandomPair(), snake = listOf(getRandomPair()), score = 0))

	val state: StateFlow<State> = mutableState

	private var snakeLength = 4

	var move = Pair(1, 0)
		set(value) {
			viewModelScope.launch {
				mutex.withLock {
					if (field.first != -value.first && field.second != -value.second) {
						field = value
					}
				}
			}
		}

	fun changeSpeed(up: Boolean) {
		if (up && delayTime > 50L) {
			delayTime -= 50L
		} else {
			delayTime += 50L
		}
	}


	private var delayTime: Long = 350L
		set(value) {
			viewModelScope.launch {
				mutex.withLock {
					field = value
				}
			}
		}

	private fun getRandomPair() = Pair(
		Random().nextInt(BOARD_SIZE),
		Random().nextInt(BOARD_SIZE),
	)

	fun resetGame() {
		snakeLength = 4
		delayTime = 350L
		move = Pair(1, 0)
		viewModelScope.launch {
			mutableState.emit(
				State(
					food = getRandomPair(),
					snake = listOf(getRandomPair()),
					score = 0
				)
			)
		}
	}

	init {
		viewModelScope.launch {
			while (true) {
				delay(delayTime)
				val newPosition = mutableState.value.snake.first().let {
					mutex.withLock {
						Pair(
							(it.first + move.first + BOARD_SIZE) % BOARD_SIZE,
							(it.second + move.second + BOARD_SIZE) % BOARD_SIZE
						)
					}
				}
				if (mutableState.value.snake.contains(newPosition)) {
					resetGame()
				} else {
					mutableState.update {
						var score = it.score
						if (newPosition == it.food) {
							snakeLength++
							score++
							delayTime -= 10L
						}

						val food = if (newPosition == it.food) {
							var pair = getRandomPair()
							while (it.snake.contains(pair)) {
								pair = getRandomPair()
							}
							pair
						} else {
							it.food
						}

						it.copy(
							food = food,
							snake = listOf(newPosition) + it.snake.take(snakeLength - 1),
							score = score
						)
					}
				}
			}
		}
	}

	companion object {
		const val BOARD_SIZE = 16
	}
}