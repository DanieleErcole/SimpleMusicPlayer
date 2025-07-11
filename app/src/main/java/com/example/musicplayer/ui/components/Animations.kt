package com.example.musicplayer.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.navigation.NavBackStackEntry
import com.example.musicplayer.ui.AppScreen

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInConditional(from: AppScreen?, to: AppScreen): EnterTransition {
    //TODO: change animation in case of tracklist
    val direction =
        if (from == null) return fadeIn()
        else if (to.index == from.index) return EnterTransition.None
        else if (to.index > from.index) AnimatedContentTransitionScope.SlideDirection.Left
        else AnimatedContentTransitionScope.SlideDirection.Right
    return slideIntoContainer(towards = direction, animationSpec = tween(500))
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutConditional(from: AppScreen, to: AppScreen): ExitTransition {
    //TODO: change animation in case of tracklist
    val direction =
        if (to.index == from.index) return ExitTransition.None
        else if (to.index > from.index) AnimatedContentTransitionScope.SlideDirection.Right
        else AnimatedContentTransitionScope.SlideDirection.Left
    return slideOutOfContainer(towards = direction, animationSpec = tween(500))
}