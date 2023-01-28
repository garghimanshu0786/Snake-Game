package com.example.snakegame

data class State(val food: Pair<Int, Int>, val snake: List<Pair<Int, Int>>, val score: Int)
