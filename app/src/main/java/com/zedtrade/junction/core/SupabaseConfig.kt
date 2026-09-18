package com.zedtrade.junction.core

/**
 * Supabase Project Configuration for Zed Trade Junction v2.2
 */
object SupabaseConfig {

    // Base URL without /rest/v1/ suffix
    const val SUPABASE_URL: String = "https://nbymbmkcbfeysrukbxmm.supabase.co"
    const val SUPABASE_ANON_KEY: String = "sb_publishable_QrGV9L7EgR3mYXON-RJOVw_q5ErT757"

    // Realtime & Channel Definitions
    const val REALTIME_CHANNEL: String = "realtime:trade_junction_v2.2"
    const val SCHEMA: String = "public"

    // Database Tables (Row-Level Security enforced)
    const val TABLE_TRADE_LISTINGS: String = "trade_listings"
    const val TABLE_ESCROW_CONTRACTS: String = "escrow_contracts"
    const val TABLE_FREIGHT_CORRIDORS: String = "freight_corridors"
    const val TABLE_RFQ_ORDERS: String = "rfq_orders"
    const val TABLE_COMMODITY_QUOTES: String = "commodity_quotes"
    const val TABLE_TRADER_PROFILES: String = "trader_profiles"

    /**
     * Checks if real credentials have been supplied.
     */
    val isConfigured: Boolean
        get() = SUPABASE_URL.isNotBlank() &&
                !SUPABASE_URL.contains("your-project-id") &&
                SUPABASE_ANON_KEY.isNotBlank() &&
                !SUPABASE_ANON_KEY.contains("your-anon-public-key")

    /**
     * Base PostgREST API Endpoint
     */
    val restApiUrl: String
        get() = "${SUPABASE_URL.trimEnd('/')}/rest/v1"

    /**
     * Base Realtime WebSocket Endpoint
     */
    val realtimeWebSocketUrl: String
        get() {
            val base = SUPABASE_URL.trimEnd('/')
                .replace("https://", "wss://")
                .replace("http://", "ws://")
            return "$base/realtime/v1/websocket?apikey=$SUPABASE_ANON_KEY&vsn=1.0.0"
        }

    /**
     * Standard authentication and content headers for PostgREST requests
     */
    fun getAuthHeaders(): Map<String, String> = mapOf(
        "apikey" to SUPABASE_ANON_KEY,
        "Authorization" to "Bearer $SUPABASE_ANON_KEY",
        "Content-Type" to "application/json",
        "Prefer" to "return=representation"
    )
}
