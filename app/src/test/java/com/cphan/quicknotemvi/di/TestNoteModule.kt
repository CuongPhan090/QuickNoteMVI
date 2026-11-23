package com.cphan.quicknotemvi.di

import android.content.Context
import androidx.room.Room
import com.cphan.quicknotemvi.data.NoteRepository
import com.cphan.quicknotemvi.data.NoteRepositoryImpl
import com.cphan.quicknotemvi.data.local.NoteDao
import com.cphan.quicknotemvi.data.local.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Replaces NoteModule in every @HiltAndroidTest: same bindings, but an in-memory database,
 * so each test starts with an empty table and nothing is written to disk.
 */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [NoteModule::class])
object TestNoteModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).build()
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
