package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ZedTradeDatabase
import com.example.data.model.CommodityQuote
import com.example.data.model.EscrowContract
import com.example.data.model.EscrowStatus
import com.example.data.model.PaymentRail
import com.example.data.model.RfqItem
import com.example.data.model.ShipmentBooking
import com.example.data.model.ShipmentStatus
import com.example.data.model.SupabaseConfig
import com.example.data.model.SyncEventLog
import com.example.data.model.TradeCategory
import com.example.data.model.TradeItem
import com.example.data.model.TraderProfile
import com.example.data.remote.SupabaseClient
import com.example.data.repository.TradeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String, val badgeCount: Int = 0) {
    MARKETPLACE("Market"),
    ESCROW("ZedPay Escrow"),
    LOGISTICS("Corridors"),
    SUPABASE_SYNC("Supabase Hub"),
    DIRECTORY("Directory & Spot")
}

data class UiNotification(
    val message: String,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

class ZedTradeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ZedTradeDatabase.getDatabase(application)
    val supabaseClient = SupabaseClient(viewModelScope)
    val repository = TradeRepository(db, supabaseClient, viewModelScope)

    // Navigation
    private val _currentTab = MutableStateFlow(AppNavTab.MARKETPLACE)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    // Marketplace Filters & Search
    private val _selectedCategory = MutableStateFlow(TradeCategory.ALL)
    val selectedCategory: StateFlow<TradeCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currencyPreference = MutableStateFlow("ALL") // "ALL", "ZMW", "USD"
    val currencyPreference: StateFlow<String> = _currencyPreference.asStateFlow()

    val tradeItems: StateFlow<List<TradeItem>> = combine(
        repository.tradeItems,
        _selectedCategory,
        _searchQuery,
        _currencyPreference
    ) { items, category, query, currency ->
        items.filter { item ->
            val matchesCategory = (category == TradeCategory.ALL || item.category == category)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.location.contains(query, ignoreCase = true) ||
                    item.sellerName.contains(query, ignoreCase = true)
            val matchesCurrency = currency == "ALL" || item.currency.equals(currency, ignoreCase = true)
            matchesCategory && matchesQuery && matchesCurrency
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val escrows: StateFlow<List<EscrowContract>> = repository.escrows
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shipments: StateFlow<List<ShipmentBooking>> = repository.shipments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rfqs: StateFlow<List<RfqItem>> = repository.rfqs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncLogs: StateFlow<List<SyncEventLog>> = repository.syncLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supabaseConfig: StateFlow<SupabaseConfig> = supabaseClient.config

    val verifiedTraders: List<TraderProfile> = repository.verifiedTraders
    val commodityQuotes: List<CommodityQuote> = repository.commodityQuotes

    // Notification banner
    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    fun dismissNotification() {
        _notification.value = null
    }

    fun showToast(msg: String, isError: Boolean = false) {
        _notification.value = UiNotification(message = msg, isError = isError)
    }

    fun setCategory(category: TradeCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCurrencyPreference(curr: String) {
        _currencyPreference.value = curr
    }

    // Trade Listing Actions
    fun postNewListing(
        title: String,
        category: TradeCategory,
        price: Double,
        currency: String,
        unit: String,
        moq: Int,
        quantity: Int,
        location: String,
        description: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newItem = TradeItem(
                id = "TJ-ZM-${(100..999).random()}",
                title = title,
                category = category,
                price = price,
                currency = currency,
                unit = unit,
                minOrderQuantity = moq,
                quantityAvailable = quantity,
                location = location,
                sellerName = "My Verified Business (You)",
                sellerRating = 5.0f,
                isPacraVerified = true,
                isZraCompliant = true,
                description = description,
                timestamp = System.currentTimeMillis()
            )
            repository.addTradeListing(newItem)
            showToast("Listing '$title' published and synced to Supabase!")
        }
    }

    // Escrow Actions
    fun createEscrowForListing(
        item: TradeItem,
        buyerName: String,
        quantity: Int,
        rail: PaymentRail
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val totalAmount = item.price * quantity
            val newEscrow = EscrowContract(
                id = "ESC-2026-${(100..999).random()}",
                tradeItemId = item.id,
                itemTitle = "${item.title} ($quantity ${item.unit})",
                buyerName = buyerName.ifBlank { "Smart Buyer Ltd" },
                sellerName = item.sellerName,
                amount = totalAmount,
                currency = item.currency,
                status = EscrowStatus.FUNDS_DEPOSITED,
                paymentRail = rail,
                txHash = "0x" + (1..32).map { "0123456789abcdef".random() }.joinToString(""),
                trackingCode = "FRT-DISP-${(100..999).random()}",
                milestoneNotes = "Buyer deposited ${item.currency} $totalAmount into ZedPay Escrow Vault via ${rail.displayName}."
            )
            repository.createEscrow(newEscrow)
            showToast("Escrow ${newEscrow.id} created & funds secured in ZedPay vault!")
        }
    }

    fun transitionEscrow(escrow: EscrowContract, nextStatus: EscrowStatus, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEscrowStatus(escrow.id, nextStatus, notes)
            showToast("Escrow ${escrow.id} updated: ${nextStatus.title}")
        }
    }

    // Logistics Actions
    fun bookFreightShipment(
        cargoDescription: String,
        weightTons: Double,
        corridorName: String,
        origin: String,
        destination: String,
        driverName: String,
        plate: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val booking = ShipmentBooking(
                id = "SHP-${(100..999).random()}",
                trackingCode = "FRT-${(100..999).random()}",
                cargoDescription = cargoDescription,
                weightTons = weightTons,
                corridorName = corridorName,
                origin = origin,
                destination = destination,
                currentCheckpoint = "$origin Departure Weighbridge",
                progressPercent = 0.10f,
                status = ShipmentStatus.DISPATCHED,
                driverName = driverName,
                vehiclePlate = plate,
                eta = "18 Hours"
            )
            repository.bookShipment(booking)
            showToast("Freight booking ${booking.trackingCode} dispatched on corridor!")
        }
    }

    fun advanceShipmentWaypoint(shipment: ShipmentBooking) {
        viewModelScope.launch(Dispatchers.IO) {
            val (nextCheckpoint, nextProgress, nextStatus) = when (shipment.status) {
                ShipmentStatus.DISPATCHED -> Triple(
                    "Highway Corridor En-Route Checkpoint",
                    0.35f,
                    ShipmentStatus.IN_TRANSIT
                )
                ShipmentStatus.IN_TRANSIT -> Triple(
                    "One-Stop Border Post / ASYCUDA Clearance",
                    0.70f,
                    ShipmentStatus.CUSTOMS_CLEARANCE
                )
                ShipmentStatus.CUSTOMS_CLEARANCE -> Triple(
                    "ZRA Customs Green-Lane Cleared",
                    0.90f,
                    ShipmentStatus.CLEARED_ZRA
                )
                ShipmentStatus.CLEARED_ZRA -> Triple(
                    "${shipment.destination} (Final Delivery Terminal)",
                    1.0f,
                    ShipmentStatus.DELIVERED
                )
                ShipmentStatus.DELIVERED -> Triple(
                    "${shipment.destination} (Completed & Audited)",
                    1.0f,
                    ShipmentStatus.DELIVERED
                )
            }
            repository.advanceShipmentCheckpoint(shipment.id, nextCheckpoint, nextProgress, nextStatus)
            showToast("Tracking updated for ${shipment.trackingCode}: $nextCheckpoint")
        }
    }

    // RFQ Actions
    fun submitBidOnRfq(rfqId: String, bidderName: String, bidAmount: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.submitRfqBid(rfqId, bidderName.ifBlank { "Pro Trader Zambia" }, bidAmount)
            showToast("Bid registered and synced to Supabase for RFQ $rfqId")
        }
    }

    fun postNewRfq(
        buyerCompany: String,
        item: String,
        category: TradeCategory,
        quantity: String,
        targetPrice: String,
        location: String,
        deadline: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newRfq = RfqItem(
                id = "RFQ-2026-${(100..999).random()}",
                buyerCompany = buyerCompany,
                requestedItem = item,
                category = category,
                targetQuantity = quantity,
                budgetPerUnit = targetPrice,
                deliveryLocation = location,
                deadlineDate = deadline,
                bidCount = 1,
                status = "OPEN"
            )
            repository.createRfq(newRfq)
            showToast("RFQ ${newRfq.id} published to junction network!")
        }
    }

    // Supabase Engine Actions
    fun testSupabaseConnection(url: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val (success, message) = supabaseClient.testConnection(url, key)
            showToast(message, isError = !success)
        }
    }

    fun forceFullSupabaseSync() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.forceSyncAll()
            showToast("Real-time sync cycle complete across all platform modules!")
        }
    }

    fun toggleSupabaseRealtime(enabled: Boolean) {
        supabaseClient.toggleRealtime(enabled)
        showToast(if (enabled) "Supabase Realtime Channel Connected" else "Supabase Realtime Paused (Local Room Offline Mode)")
    }

    fun clearAuditLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearSyncLogs()
            showToast("Supabase audit log cleared.")
        }
    }
}
