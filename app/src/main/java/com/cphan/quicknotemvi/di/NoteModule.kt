package com.cphan.quicknotemvi.di

import android.content.Context
import androidx.room.Room
import com.cphan.quicknotemvi.data.NoteRepository
import com.cphan.quicknotemvi.data.NoteRepositoryImpl
import com.cphan.quicknotemvi.data.local.NoteDao
import com.cphan.quicknotemvi.data.local.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context = context,
            name = "note_database",
            klass = NoteDatabase::class.java
        ).build()
    }

    @Provides
    fun provideNoteDao(noteDatabase: NoteDatabase): NoteDao {
        return noteDatabase.getNoteDao()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepositoryImpl(
            noteDao = noteDao,
            now = { System.currentTimeMillis() }
        )
    }
}
