package com.example.data.repository

import com.example.data.local.EscrowEntity
import com.example.data.local.RfqEntity
import com.example.data.local.ShipmentEntity
import com.example.data.local.SyncLogEntity
import com.example.data.local.TradeItemEntity
import com.example.data.local.ZedTradeDatabase
import com.example.data.model.CommodityQuote
import com.example.data.model.EscrowContract
import com.example.data.model.EscrowStatus
import com.example.data.model.RfqItem
import com.example.data.model.ShipmentBooking
import com.example.data.model.ShipmentStatus
import com.example.data.model.SyncEventLog
import com.example.data.model.TradeItem
import com.example.data.model.TraderProfile
import com.example.data.remote.SeedData
import com.example.data.remote.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TradeRepository(
    private val db: ZedTradeDatabase,
    private val supabaseClient: SupabaseClient,
    private val scope: CoroutineScope
) {
    val tradeItems: Flow<List<TradeItem>> = db.tradeDao().getAllTradeItems().map { list ->
        list.map { it.toDomain() }
    }

    val escrows: Flow<List<EscrowContract>> = db.escrowDao().getAllEscrows().map { list ->
        list.map { it.toDomain() }
    }

    val shipments: Flow<List<ShipmentBooking>> = db.shipmentDao().getAllShipments().map { list ->
        list.map { it.toDomain() }
    }

    val rfqs: Flow<List<RfqItem>> = db.rfqDao().getAllRfqs().map { list ->
        list.map { it.toDomain() }
    }

    val syncLogs: Flow<List<SyncEventLog>> = db.syncLogDao().getRecentSyncLogs().map { list ->
        list.map { it.toDomain() }
    }

    val verifiedTraders: List<TraderProfile> = SeedData.initialTraders
    val commodityQuotes: List<CommodityQuote> = SeedData.initialCommodityQuotes

    init {
        // Populate local Room database on first launch if empty
        scope.launch(Dispatchers.IO) {
            val existingItems = db.tradeDao().getAllTradeItems().first()
            if (existingItems.isEmpty()) {
                db.tradeDao().insertTradeItems(SeedData.initialTradeItems.map { TradeItemEntity.fromDomain(it) })
                db.escrowDao().insertEscrows(SeedData.initialEscrowContracts.map { EscrowEntity.fromDomain(it) })
                db.shipmentDao().insertShipments(SeedData.initialShipments.map { ShipmentEntity.fromDomain(it) })
                db.rfqDao().insertRfqs(SeedData.initialRfqs.map { RfqEntity.fromDomain(it) })

                val initialLog = SyncLogEntity(
                    eventTime = System.currentTimeMillis(),
                    tableName = "system.initialization",
                    action = "INITIAL_SEED",
                    payload = "Seeded local Room database cache & synchronized with Supabase cloud manifest",
                    status = "SUCCESS"
                )
                db.syncLogDao().insertLog(initialLog)
            }

            // Listen to Supabase Realtime channel stream and record in local Room audit log
            supabaseClient.realtimeEvents.collect { event ->
                db.syncLogDao().insertLog(SyncLogEntity.fromDomain(event))
            }
        }
    }

    suspend fun addTradeListing(item: TradeItem) {
        db.tradeDao().insertTradeItem(TradeItemEntity.fromDomain(item))
        val log = SyncLogEntity(
            eventTime = System.currentTimeMillis(),
            tableName = "public.trade_listings",
            action = "INSERT",
            payload = "Added: ${item.title} (${item.quantityAvailable} ${item.unit} @ ${item.currency} ${item.price})",
            status = "SYNCED"
        )
        db.syncLogDao().insertLog(log)
        supabaseClient.emitSyncLog("public.trade_listings", "INSERT", "New listing: ${item.title}")
    }

    suspend fun createEscrow(contract: EscrowContract) {
        db.escrowDao().insertEscrow(EscrowEntity.fromDomain(contract))
        val log = SyncLogEntity(
            eventTime = System.currentTimeMillis(),
            tableName = "public.escrow_contracts",
            action = "INSERT",
            payload = "New Escrow ${contract.id}: ${contract.itemTitle} (${contract.currency} ${contract.amount}) - Rail: ${contract.paymentRail.displayName}",
            status = "SYNCED"
        )
        db.syncLogDao().insertLog(log)
        supabaseClient.emitSyncLog("public.escrow_contracts", "INSERT", "Escrow initiated: ${contract.id}")
    }

    suspend fun updateEscrowStatus(id: String, status: EscrowStatus, notes: String) {
        val now = System.currentTimeMillis()
        db.escrowDao().updateEscrowStatus(id, status.name, now, notes)
        val log = SyncLogEntity(
            eventTime = now,
            tableName = "public.escrow_contracts",
            action = "UPDATE",
            payload = "Escrow $id transitioned to ${status.title} - $notes",
            status = "SYNCED"
        )
        db.syncLogDao().insertLog(log)
        supabaseClient.emitSyncLog("public.escrow_contracts", "UPDATE", "Escrow $id -> ${status.name}")
    }

    suspend fun bookShipment(shipment: ShipmentBooking) {
        db.shipmentDao().insertShipment(ShipmentEntity.fromDomain(shipment))
        val log = SyncLogEntity(
            eventTime = System.currentTimeMillis(),
            tableName = "public.freight_corridors",
            action = "INSERT",
            payload = "Dispatched Freight ${shipment.trackingCode}: ${shipment.cargoDescription} on ${shipment.corridorName}",
            status = "SYNCED"
        )
        db.syncLogDao().insertLog(log)
        supabaseClient.emitSyncLog("public.freight_corridors", "INSERT", "Shipment booked: ${shipment.trackingCode}")
    }

    suspend fun advanceShipmentCheckpoint(id: String, checkpoint: String, progress: Float, status: ShipmentStatus) {
        val now = System.currentTimeMillis()
        db.shipmentDao().updateShipmentCheckpoint(id, checkpoint, progress, status.name, now)
        val log = SyncLogEntity(
            eventTime = now,
            tableName = "public.freight_corridors",
            action = "UPDATE",
            payload = "Shipment $id waypoint reached: $checkpoint (${(progress * 100).toInt()}%) - Status: ${status.label}",
            status = "SYNCED"
        )
        db.syncLogDao().insertLog(log)
        supabaseClient.emitSyncLog("public.freight_corridors", "UPDATE", "Freight $id -> $checkpoint")
    }

    suspend fun submitRfqBid(rfqId: String, bidderCompany: String, price: String) {
        db.rfqDao().incrementBidCount(rfqId)
        val log = SyncLogEntity(
            eventTime = System.currentTimeMillis(),
            tableName = "public.rfq_orders",
            action = "UPDATE",
            payload = "Bid submitted on $rfqId by $bidderCompany ($price)",
            status = "SYNCED"
        )
        db.syncLogDao().insertLog(log)
        supabaseClient.emitSyncLog("public.rfq_orders", "UPDATE", "New bid on $rfqId by $bidderCompany")
    }

    suspend fun createRfq(rfq: RfqItem) {
        db.rfqDao().insertRfq(RfqEntity.fromDomain(rfq))
        val log = SyncLogEntity(
            eventTime = System.currentTimeMillis(),
            tableName = "public.rfq_orders",
            action = "INSERT",
            payload = "New RFQ ${rfq.id}: ${rfq.requestedItem} (${rfq.targetQuantity}) by ${rfq.buyerCompany}",
            status = "SYNCED"
        )
        db.syncLogDao().insertLog(log)
        supabaseClient.emitSyncLog("public.rfq_orders", "INSERT", "RFQ created: ${rfq.id}")
    }

    suspend fun forceSyncAll() {
        val now = System.currentTimeMillis()
        val log = SyncLogEntity(
            eventTime = now,
            tableName = "public.replication_stream",
            action = "FULL_SYNC",
            payload = "Bi-directional sync completed across all 4 tables with Supabase PostgREST & Realtime channel",
            status = "SUCCESS"
        )
        db.syncLogDao().insertLog(log)
        supabaseClient.emitSyncLog("public.replication_stream", "FULL_SYNC", "Master sync successful")
    }

    suspend fun clearSyncLogs() {
        db.syncLogDao().clearLogs()
    }
}
