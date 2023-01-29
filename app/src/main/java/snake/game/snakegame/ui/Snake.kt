package snake.game.snakegame.ui

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import snake.game.snakegame.GameViewModel
import snake.game.snakegame.GameViewModel.Companion.BOARD_SIZE
import snake.game.snakegame.R
import snake.game.snakegame.State
import kotlinx.coroutines.flow.collectLatest

@Composable
fun Snake(game: GameViewModel) {
	val state by game.state.collectAsState(initial = null)
	val context = LocalContext.current

	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		state?.let { Board(it) }

		ScoreBoard(state?.score, game.delayTime)

		Buttons { game.move = it }

		GameOptions(game::resetGame, game::changeSpeed)
	}

	LaunchedEffect(context) {
		game.uiEvent.collectLatest {
			Toast.makeText(
				context, context.getString(R.string.game_over), Toast.LENGTH_SHORT
			).show()
			val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
			vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
		}
	}
}

@Composable
fun ScoreBoard(score: Int?, delayTime: Long) {
	Row {
		Text("Score: ")
		Text(text = score.toString())

		Text("Level: ", Modifier.padding(start = 20.dp))
		Text(
			when (delayTime) {
				in 400L..Long.MAX_VALUE -> 1
				in 299L..350L -> 2
				in 349L downTo 300L -> 3
				in 299L downTo 250L -> 4
				in 240L downTo 200L -> 5
				in 199L downTo 150L -> 6
				in 149L downTo 100L -> 7
				in Long.MIN_VALUE..99L -> 8
				else -> 1
			}.toString()
		)
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
			Text("Level Up")
		}

		Button(
			onClick = { changeSpeed(false) },
			Modifier
				.padding(5.dp)
				.fillMaxWidth()
				.weight(1f)
		) {
			Text("Level Down")
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
