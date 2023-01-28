package com.example.snakegame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.unit.dp
import com.example.snakegame.GameViewModel
import com.example.snakegame.GameViewModel.Companion.BOARD_SIZE
import com.example.snakegame.State

@Composable
fun Snake(game: GameViewModel) {
	val state by game.state.collectAsState(initial = null)

	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		state?.let { Board(it) }

		ScoreBoard(state?.score)

		Buttons { game.move = it }

		GameOptions(game::resetGame, game::changeSpeed)
	}
}

@Composable
fun ScoreBoard(score: Int?) {
	Row {
		Text("Score: ")
		Text(text = score.toString())
	}
}

@Composable
fun GameOptions(reset: () -> Unit, changeSpeed: (Boolean) -> Unit) {
	Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
		Button(
			onClick = { reset() },
			Modifier
				.padding(5.dp)
				.fillMaxWidth()
				.weight(1f)
		) {
			Text("Restart")
		}

		Button(
			onClick = { changeSpeed(true) },
			Modifier
				.padding(5.dp)
				.fillMaxWidth()
				.weight(1f)
		) {
			Text("Speed Up")
		}

		Button(
			onClick = { changeSpeed(false) },
			Modifier
				.padding(5.dp)
				.fillMaxWidth()
				.weight(1f)
		) {
			Text("Slow Down")
		}
	}
}

@Composable
fun Buttons(onDirectionChange: (Pair<Int, Int>) -> Unit) {
	val buttonSize = Modifier.size(64.dp)
	Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
		Button(onClick = { onDirectionChange(Pair(0, -1)) }, modifier = buttonSize) {
			Icon(Icons.Default.KeyboardArrowUp, null)
		}

		Row {
			Button(onClick = { onDirectionChange(Pair(-1, 0)) }, modifier = buttonSize) {
				Icon(Icons.Default.KeyboardArrowLeft, null)
			}
			Spacer(modifier = buttonSize)
			Button(onClick = { onDirectionChange(Pair(1, 0)) }, modifier = buttonSize) {
				Icon(Icons.Default.KeyboardArrowRight, null)
			}
		}
		Button(onClick = { onDirectionChange(Pair(0, 1)) }, modifier = buttonSize) {
			Icon(Icons.Default.KeyboardArrowDown, null)
		}
	}
}

@Composable
fun Board(state: State) {
	BoxWithConstraints(Modifier.padding(16.dp)) {
		val tileSize = maxWidth / BOARD_SIZE
		Box(
			Modifier
				.size(maxWidth)
				.border(2.dp, Green)
		)

		Box(
			Modifier
				.offset(x = tileSize * state.food.first, y = tileSize * state.food.second)
				.size(tileSize)
				.background(Green, CircleShape)
		)

		state.snake.forEach {
			Box(
				Modifier
					.offset(x = tileSize * it.first, y = tileSize * it.second)
					.size(tileSize)
					.background(Green, MaterialTheme.shapes.small)
			)
		}
	}
}
