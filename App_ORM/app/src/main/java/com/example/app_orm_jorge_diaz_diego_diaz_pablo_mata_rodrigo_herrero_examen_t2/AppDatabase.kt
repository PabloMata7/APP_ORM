package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Carta::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cartaDao(): CartaDao
}