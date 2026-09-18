package com.example.data.model

enum class TradeCategory(val label: String, val iconName: String) {
    ALL("All Categories", "grid"),
    AGRICULTURE("Agro & Farming", "leaf"),
    MINING_METALS("Mining & Metals", "gem"),
    INDUSTRIAL_EQUIPMENT("Industrial & PPE", "tools"),
    FMCG_WHOLESALE("FMCG & Consumer", "shopping-bag"),
    BUILDING_MATERIALS("Building & Timber", "building"),
    ENERGY_FUELS("Energy & Fuels", "bolt")
}

enum class EscrowStatus(val stepIndex: Int, val title: String, val description: String) {
    INITIATED(1, "Deal Initiated", "Agreement drafted with mutual buyer & seller terms"),
    FUNDS_DEPOSITED(2, "Funds Locked in Escrow", "Funds safely held in ZedPay secure escrow vault"),
    GOODS_DISPATCHED(3, "Goods in Freight Transit", "Carrier verified dispatch with active corridor tracking"),
    INSPECTION_PASSED(4, "Inspection Verified", "Quality & quantity certified at destination checkpoint"),
    FUNDS_RELEASED(5, "Settlement Released", "Funds released directly to seller mobile money/bank"),
    DISPUTED(0, "In Arbitration", "Under independent trade ombudsman review")
}

enum class PaymentRail(val displayName: String, val provider: String, val feePct: Double) {
    AIRTEL_MONEY("Airtel Money Pay", "Airtel Africa (+260)", 1.0),
    MTN_MOMO("MTN Mobile Money", "MTN Zambia (+260)", 1.0),
    ZAMTEL_KWACHA("Zamtel Kwacha", "Zamtel Mobile", 0.8),
    ZANACO_BANK("Zanaco Express / Bank Wire", "Zambia National Commercial Bank", 0.5),
    SWIFT_WIRE("International Wire / SWIFT", "Standard Chartered / Absa", 1.5)
}

enum class ShipmentStatus(val label: String) {
    DISPATCHED("Dispatched"),
    IN_TRANSIT("In Transit"),
    CUSTOMS_CLEARANCE("ZRA Customs Inspection"),
    CLEARED_ZRA("Cleared & Released"),
    DELIVERED("Delivered & Signed")
}

data class TradeItem(
    val id: String,
    val title: String,
    val category: TradeCategory,
    val price: Double,
    val currency: String = "ZMW",
    val unit: String,
    val minOrderQuantity: Int,
    val quantityAvailable: Int,
    val location: String,
    val sellerName: String,
    val sellerRating: Float,
    val isPacraVerified: Boolean = true,
    val isZraCompliant: Boolean = true,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED"
)

data class EscrowContract(
    val id: String,
    val tradeItemId: String,
    val itemTitle: String,
    val buyerName: String,
    val sellerName: String,
    val amount: Double,
    val currency: String = "ZMW",
    val status: EscrowStatus,
    val paymentRail: PaymentRail,
    val txHash: String,
    val trackingCode: String,
    val milestoneNotes: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class ShipmentBooking(
    val id: String,
    val trackingCode: String,
    val cargoDescription: String,
    val weightTons: Double,
    val corridorName: String,
    val origin: String,
    val destination: String,
    val currentCheckpoint: String,
    val progressPercent: Float,
    val status: ShipmentStatus,
    val driverName: String,
    val vehiclePlate: String,
    val eta: String,
    val cargoTempCelsius: Float? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

data class RfqItem(
    val id: String,
    val buyerCompany: String,
    val requestedItem: String,
    val category: TradeCategory,
    val targetQuantity: String,
    val budgetPerUnit: String,
    val deliveryLocation: String,
    val deadlineDate: String,
    val bidCount: Int,
    val status: String = "OPEN"
)

data class TraderProfile(
    val id: String,
    val companyName: String,
    val pacraRegNo: String,
    val zraTpin: String,
    val category: String,
    val city: String,
    val tier: String, // "GOLD_SUPPLIER", "VERIFIED_MERCHANT", "REGISTERED"
    val rating: Float,
    val totalTrades: Int,
    val contactPhone: String,
    val contactEmail: String,
    val complianceScore: Int = 98
)

data class CommodityQuote(
    val symbol: String,
    val name: String,
    val price: Double,
    val currency: String,
    val changePct: Double,
    val unit: String,
    val category: String
)

data class SupabaseConfig(
    val projectUrl: String = com.zedtrade.junction.core.SupabaseConfig.SUPABASE_URL,
    val anonKey: String = com.zedtrade.junction.core.SupabaseConfig.SUPABASE_ANON_KEY,
    val channelName: String = com.zedtrade.junction.core.SupabaseConfig.REALTIME_CHANNEL,
    val realtimeConnected: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val pendingPushCount: Int = 0,
    val rlsStatus: String = "Enforced (Row Level Security Active)",
    val latencyMs: Long = 42
)

data class SyncEventLog(
    val id: Long = 0,
    val eventTime: Long = System.currentTimeMillis(),
    val table: String,
    val action: String,
    val payload: String,
    val status: String
)
