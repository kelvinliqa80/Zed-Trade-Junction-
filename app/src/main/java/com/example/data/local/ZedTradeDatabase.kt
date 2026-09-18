package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trade_items ORDER BY timestamp DESC")
    fun getAllTradeItems(): Flow<List<TradeItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTradeItems(items: List<TradeItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTradeItem(item: TradeItemEntity)

    @Update
    suspend fun updateTradeItem(item: TradeItemEntity)

    @Query("DELETE FROM trade_items WHERE id = :id")
    suspend fun deleteTradeItem(id: String)
}

@Dao
interface EscrowDao {
    @Query("SELECT * FROM escrow_contracts ORDER BY updatedAt DESC")
    fun getAllEscrows(): Flow<List<EscrowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEscrows(escrows: List<EscrowEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEscrow(escrow: EscrowEntity)

    @Update
    suspend fun updateEscrow(escrow: EscrowEntity)

    @Query("UPDATE escrow_contracts SET status = :status, updatedAt = :updatedAt, milestoneNotes = :notes WHERE id = :id")
    suspend fun updateEscrowStatus(id: String, status: String, updatedAt: Long, notes: String)
}

@Dao
interface ShipmentDao {
    @Query("SELECT * FROM shipment_bookings ORDER BY updatedAt DESC")
    fun getAllShipments(): Flow<List<ShipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipments(shipments: List<ShipmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipment(shipment: ShipmentEntity)

    @Update
    suspend fun updateShipment(shipment: ShipmentEntity)

    @Query("UPDATE shipment_bookings SET currentCheckpoint = :checkpoint, progressPercent = :progress, status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateShipmentCheckpoint(id: String, checkpoint: String, progress: Float, status: String, updatedAt: Long)
}

@Dao
interface RfqDao {
    @Query("SELECT * FROM rfq_items ORDER BY id DESC")
    fun getAllRfqs(): Flow<List<RfqEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRfqs(rfqs: List<RfqEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRfq(rfq: RfqEntity)

    @Query("UPDATE rfq_items SET bidCount = bidCount + 1 WHERE id = :id")
    suspend fun incrementBidCount(id: String)
}

@Dao
interface SyncLogDao {
    @Query("SELECT * FROM sync_event_logs ORDER BY id DESC LIMIT 50")
    fun getRecentSyncLogs(): Flow<List<SyncLogEntity>>

    @Insert
    suspend fun insertLog(log: SyncLogEntity)

    @Query("DELETE FROM sync_event_logs")
    suspend fun clearLogs()
}

@Database(
    entities = [
        TradeItemEntity::class,
        EscrowEntity::class,
        ShipmentEntity::class,
        RfqEntity::class,
        SyncLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ZedTradeDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao
    abstract fun escrowDao(): EscrowDao
    abstract fun shipmentDao(): ShipmentDao
    abstract fun rfqDao(): RfqDao
    abstract fun syncLogDao(): SyncLogDao

    companion object {
        @Volatile
        private var INSTANCE: ZedTradeDatabase? = null

        fun getDatabase(context: Context): ZedTradeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZedTradeDatabase::class.java,
                    "zed_trade_junction_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
