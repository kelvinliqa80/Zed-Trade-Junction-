package com.example

import com.example.data.model.EscrowStatus
import com.example.data.model.SupabaseConfig
import com.example.data.remote.SeedData
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testSeedDataCompleteness() {
    assertTrue(SeedData.initialTradeItems.isNotEmpty())
    assertTrue(SeedData.initialEscrowContracts.isNotEmpty())
    assertTrue(SeedData.initialShipments.isNotEmpty())
    assertTrue(SeedData.initialRfqs.isNotEmpty())
    assertTrue(SeedData.initialCommodityQuotes.isNotEmpty())
    assertTrue(SeedData.initialTraders.isNotEmpty())
  }

  @Test
  fun testEscrowStatusStepProgression() {
    assertEquals(1, EscrowStatus.INITIATED.stepIndex)
    assertEquals(2, EscrowStatus.FUNDS_DEPOSITED.stepIndex)
    assertEquals(3, EscrowStatus.GOODS_DISPATCHED.stepIndex)
    assertEquals(4, EscrowStatus.INSPECTION_PASSED.stepIndex)
    assertEquals(5, EscrowStatus.FUNDS_RELEASED.stepIndex)
  }

  @Test
  fun testSupabaseConfigDefaults() {
    val config = SupabaseConfig()
    assertTrue(config.realtimeConnected)
    assertTrue(config.projectUrl.contains("supabase"))
    assertEquals("realtime:trade_junction_v2.2", config.channelName)
  }
}

