package com.cphan.quicknotemvi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cphan.quicknotemvi.ui.noteslist.NotesListRoute
import com.cphan.quicknotemvi.ui.theme.QuickNoteMVITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickNoteMVITheme {
                NotesListRoute(
                    onOpenEditor = {}
                )
            }
        }
    }
}
