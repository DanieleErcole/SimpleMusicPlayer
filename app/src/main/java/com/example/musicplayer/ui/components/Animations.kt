package com.example.musicplayer.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import com.example.musicplayer.ui.AppScreen

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInConditional(animSpec: FiniteAnimationSpec<IntOffset>): EnterTransition {
    val from = AppScreen.valueOf(this.initialState.destination.route ?: AppScreen.Playing.name)
    val to = AppScreen.valueOf(this.targetState.destination.route ?: AppScreen.Playing.name)

    return slideIntoContainer(
        towards = when {
            from.index < to.index -> AnimatedContentTransitionScope.SlideDirection.Start
            from.index > to.index -> AnimatedContentTransitionScope.SlideDirection.End
            else -> AnimatedContentTransitionScope.SlideDirection.Up
        },
        animationSpec = animSpec
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutConditional(animSpec: FiniteAnimationSpec<IntOffset>): ExitTransition {
    val from = AppScreen.valueOf(this.initialState.destination.route ?: AppScreen.Playing.name)
    val to = AppScreen.valueOf(this.targetState.destination.route ?: AppScreen.Playing.name)

    return slideOutOfContainer(
        towards = when {
            from.index < to.index -> AnimatedContentTransitionScope.SlideDirection.Start
            from.index > to.index -> AnimatedContentTransitionScope.SlideDirection.End
            else -> AnimatedContentTransitionScope.SlideDirection.Up
        },
        animationSpec = animSpec
    )
}