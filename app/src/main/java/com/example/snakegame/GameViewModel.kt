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
		MutableStateFlow(State(food = Pair(5, 5), snake = listOf(Pair(7, 7)), score = 0))
	val state: StateFlow<State> = mutableState

	var snakeLength = 4

	var move = Pair(1, 0)
		set(value) {
			viewModelScope.launch {
				mutex.withLock {
					field = value
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

	fun resetGame() {
		snakeLength = 4
		delayTime = 350L
		move = Pair(1, 0)
		viewModelScope.launch {
			mutableState.emit(State(food = Pair(5, 5), snake = listOf(Pair(7, 7)), score = 0))
		}
	}

	init {
		viewModelScope.launch {
			while (true) {
				delay(delayTime)
				mutableState.update {
					val newPosition = it.snake.first().let {
						mutex.withLock {
							Pair(
								(it.first + move.first + BOARD_SIZE) % BOARD_SIZE,
								(it.second + move.second + BOARD_SIZE) % BOARD_SIZE
							)
						}
					}
					var score = it.score
					if (newPosition == it.food) {
						snakeLength++
						score++
					}

					if (it.snake.contains(newPosition)) {
						snakeLength = 4
					}
					it.copy(
						food = if (newPosition == it.food) Pair(
							Random().nextInt(BOARD_SIZE),
							Random().nextInt(BOARD_SIZE),
						) else it.food,
						snake = listOf(newPosition) + it.snake.take(snakeLength - 1),
						score = score
					)
				}
			}
		}
	}

	companion object {
		const val BOARD_SIZE = 16
	}
}