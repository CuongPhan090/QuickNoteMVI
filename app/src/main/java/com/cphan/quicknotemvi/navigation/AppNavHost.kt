package com.cphan.quicknotemvi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cphan.quicknotemvi.ui.noteeditor.NoteEditorRoute
import com.cphan.quicknotemvi.ui.noteslist.NotesListRoute

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    NavHost(modifier = modifier, navController = navController, startDestination = NotesListDestination) {
        composable<NotesListDestination> {
            NotesListRoute(onOpenEditor = { id ->
                navController.navigate(NoteEditorDestination(id)) {
                    launchSingleTop = true // prevent adding same destination twice to the backstack on quick double tap
                }
            })
        }
        composable<NoteEditorDestination> {
            NoteEditorRoute(onClose = {
                navController.navigateUp()
            })
        }
    }
}
