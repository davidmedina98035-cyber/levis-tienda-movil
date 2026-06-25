package com.example.levisappadmin.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CarritoEntity::class], version = 1)
abstract class CarritoDB : RoomDatabase() {

    abstract fun carritoDao(): CarritoDao

    companion object {
        @Volatile
        private var INSTANCE: CarritoDB? = null

        fun getInstance(context: Context): CarritoDB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CarritoDB::class.java,
                    "carrito_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}