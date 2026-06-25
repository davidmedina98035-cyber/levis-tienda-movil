package com.example.levisappadmin.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    @Query("SELECT * FROM carrito")
    fun obtenerCarrito(): Flow<List<CarritoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: CarritoEntity)

    @Delete
    suspend fun eliminar(item: CarritoEntity)

    @Query("DELETE FROM carrito")
    suspend fun limpiarCarrito()
}