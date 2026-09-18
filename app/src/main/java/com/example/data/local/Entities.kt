package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.EscrowContract
import com.example.data.model.EscrowStatus
import com.example.data.model.PaymentRail
import com.example.data.model.RfqItem
import com.example.data.model.ShipmentBooking
import com.example.data.model.ShipmentStatus
import com.example.data.model.SyncEventLog
import com.example.data.model.TradeCategory
import com.example.data.model.TradeItem

@Entity(tableName = "trade_items")
data class TradeItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val price: Double,
    val currency: String,
    val unit: String,
    val minOrderQuantity: Int,
    val quantityAvailable: Int,
    val location: String,
    val sellerName: String,
    val sellerRating: Float,
    val isPacraVerified: Boolean,
    val isZraCompliant: Boolean,
    val description: String,
    val timestamp: Long,
    val syncStatus: String
) {
    fun toDomain(): TradeItem {
        return TradeItem(
            id = id,
            title = title,
            category = try { TradeCategory.valueOf(category) } catch (e: Exception) { TradeCategory.ALL },
            price = price,
            currency = currency,
            unit = unit,
            minOrderQuantity = minOrderQuantity,
            quantityAvailable = quantityAvailable,
            location = location,
            sellerName = sellerName,
            sellerRating = sellerRating,
            isPacraVerified = isPacraVerified,
            isZraCompliant = isZraCompliant,
            description = description,
            timestamp = timestamp,
            syncStatus = syncStatus
        )
    }

    companion object {
        fun fromDomain(item: TradeItem): TradeItemEntity {
            return TradeItemEntity(
                id = item.id,
                title = item.title,
                category = item.category.name,
                price = item.price,
                currency = item.currency,
                unit = item.unit,
                minOrderQuantity = item.minOrderQuantity,
                quantityAvailable = item.quantityAvailable,
                location = item.location,
                sellerName = item.sellerName,
                sellerRating = item.sellerRating,
                isPacraVerified = item.isPacraVerified,
                isZraCompliant = item.isZraCompliant,
                description = item.description,
                timestamp = item.timestamp,
                syncStatus = item.syncStatus
            )
        }
    }
}

@Entity(tableName = "escrow_contracts")
data class EscrowEntity(
    @PrimaryKey val id: String,
    val tradeItemId: String,
    val itemTitle: String,
    val buyerName: String,
    val sellerName: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val paymentRail: String,
    val txHash: String,
    val trackingCode: String,
    val milestoneNotes: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): EscrowContract {
        return EscrowContract(
            id = id,
            tradeItemId = tradeItemId,
            itemTitle = itemTitle,
            buyerName = buyerName,
            sellerName = sellerName,
            amount = amount,
            currency = currency,
            status = try { EscrowStatus.valueOf(status) } catch (e: Exception) { EscrowStatus.INITIATED },
            paymentRail = try { PaymentRail.valueOf(paymentRail) } catch (e: Exception) { PaymentRail.AIRTEL_MONEY },
            txHash = txHash,
            trackingCode = trackingCode,
            milestoneNotes = milestoneNotes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(contract: EscrowContract): EscrowEntity {
            return EscrowEntity(
                id = contract.id,
                tradeItemId = contract.tradeItemId,
                itemTitle = contract.itemTitle,
                buyerName = contract.buyerName,
                sellerName = contract.sellerName,
                amount = contract.amount,
                currency = contract.currency,
                status = contract.status.name,
                paymentRail = contract.paymentRail.name,
                txHash = contract.txHash,
                trackingCode = contract.trackingCode,
                milestoneNotes = contract.milestoneNotes,
                createdAt = contract.createdAt,
                updatedAt = contract.updatedAt
            )
        }
    }
}

@Entity(tableName = "shipment_bookings")
data class ShipmentEntity(
    @PrimaryKey val id: String,
    val trackingCode: String,
    val cargoDescription: String,
    val weightTons: Double,
    val corridorName: String,
    val origin: String,
    val destination: String,
    val currentCheckpoint: String,
    val progressPercent: Float,
    val status: String,
    val driverName: String,
    val vehiclePlate: String,
    val eta: String,
    val cargoTempCelsius: Float?,
    val updatedAt: Long
) {
    fun toDomain(): ShipmentBooking {
        return ShipmentBooking(
            id = id,
            trackingCode = trackingCode,
            cargoDescription = cargoDescription,
            weightTons = weightTons,
            corridorName = corridorName,
            origin = origin,
            destination = destination,
            currentCheckpoint = currentCheckpoint,
            progressPercent = progressPercent,
            status = try { ShipmentStatus.valueOf(status) } catch (e: Exception) { ShipmentStatus.IN_TRANSIT },
            driverName = driverName,
            vehiclePlate = vehiclePlate,
            eta = eta,
            cargoTempCelsius = cargoTempCelsius,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(booking: ShipmentBooking): ShipmentEntity {
            return ShipmentEntity(
                id = booking.id,
                trackingCode = booking.trackingCode,
                cargoDescription = booking.cargoDescription,
                weightTons = booking.weightTons,
                corridorName = booking.corridorName,
                origin = booking.origin,
                destination = booking.destination,
                currentCheckpoint = booking.currentCheckpoint,
                progressPercent = booking.progressPercent,
                status = booking.status.name,
                driverName = booking.driverName,
                vehiclePlate = booking.vehiclePlate,
                eta = booking.eta,
                cargoTempCelsius = booking.cargoTempCelsius,
                updatedAt = booking.updatedAt
            )
        }
    }
}

@Entity(tableName = "rfq_items")
data class RfqEntity(
    @PrimaryKey val id: String,
    val buyerCompany: String,
    val requestedItem: String,
    val category: String,
    val targetQuantity: String,
    val budgetPerUnit: String,
    val deliveryLocation: String,
    val deadlineDate: String,
    val bidCount: Int,
    val status: String
) {
    fun toDomain(): RfqItem {
        return RfqItem(
            id = id,
            buyerCompany = buyerCompany,
            requestedItem = requestedItem,
            category = try { TradeCategory.valueOf(category) } catch (e: Exception) { TradeCategory.ALL },
            targetQuantity = targetQuantity,
            budgetPerUnit = budgetPerUnit,
            deliveryLocation = deliveryLocation,
            deadlineDate = deadlineDate,
            bidCount = bidCount,
            status = status
        )
    }

    companion object {
        fun fromDomain(rfq: RfqItem): RfqEntity {
            return RfqEntity(
                id = rfq.id,
                buyerCompany = rfq.buyerCompany,
                requestedItem = rfq.requestedItem,
                category = rfq.category.name,
                targetQuantity = rfq.targetQuantity,
                budgetPerUnit = rfq.budgetPerUnit,
                deliveryLocation = rfq.deliveryLocation,
                deadlineDate = rfq.deadlineDate,
                bidCount = rfq.bidCount,
                status = rfq.status
            )
        }
    }
}

@Entity(tableName = "sync_event_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventTime: Long,
    val tableName: String,
    val action: String,
    val payload: String,
    val status: String
) {
    fun toDomain(): SyncEventLog {
        return SyncEventLog(
            id = id,
            eventTime = eventTime,
            table = tableName,
            action = action,
            payload = payload,
            status = status
        )
    }

    companion object {
        fun fromDomain(log: SyncEventLog): SyncLogEntity {
            return SyncLogEntity(
                id = log.id,
                eventTime = log.eventTime,
                tableName = log.table,
                action = log.action,
                payload = log.payload,
                status = log.status
            )
        }
    }
}
