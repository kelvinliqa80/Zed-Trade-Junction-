package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zedtrade.junction.core.SupabaseConfig
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppNavTab
import com.example.ui.ZedTradeViewModel
import com.example.ui.components.LivePulseIndicator
import com.example.ui.components.NotificationBanner
import com.example.ui.screens.DirectoryAndCommodityScreen
import com.example.ui.screens.EscrowScreen
import com.example.ui.screens.LogisticsScreen
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.SupabaseSyncScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ZedAmberGold
import com.example.ui.theme.ZedEmeraldContainer
import com.example.ui.theme.ZedEmeraldPrimary
import com.example.ui.theme.ZedNavySecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SUPABASE", "URL: " + SupabaseConfig.SUPABASE_URL.take(25))
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ZedTradeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZedTradeApp(
    viewModel: ZedTradeViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val notification by viewModel.notification.collectAsStateWithLifecycle()
    val supabaseConfig by viewModel.supabaseConfig.collectAsStateWithLifecycle()

    val tradeItems by viewModel.tradeItems.collectAsStateWithLifecycle()
    val rfqs by viewModel.rfqs.collectAsStateWithLifecycle()
    val escrows by viewModel.escrows.collectAsStateWithLifecycle()
    val shipments by viewModel.shipments.collectAsStateWithLifecycle()
    val syncLogs by viewModel.syncLogs.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currencyPref by viewModel.currencyPreference.collectAsStateWithLifecycle()

    val activeEscrowCount = escrows.count { it.status.stepIndex in 2..4 }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon),
                            contentDescription = "Zed Trade Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Zed Trade Junction",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Master Plan v2.2 • Cross-Border Hub",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ZedAmberGold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    LivePulseIndicator(
                        isConnected = supabaseConfig.realtimeConnected,
                        latencyMs = supabaseConfig.latencyMs,
                        onClick = { viewModel.selectTab(AppNavTab.SUPABASE_SYNC) },
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("top_app_bar_status_indicator")
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ZedNavySecondary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // 1. Marketplace Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.MARKETPLACE,
                    onClick = { viewModel.selectTab(AppNavTab.MARKETPLACE) },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Market") },
                    label = { Text("Market", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ZedEmeraldPrimary,
                        selectedTextColor = ZedEmeraldPrimary,
                        indicatorColor = ZedEmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_marketplace")
                )

                // 2. Escrow Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.ESCROW,
                    onClick = { viewModel.selectTab(AppNavTab.ESCROW) },
                    icon = {
                        if (activeEscrowCount > 0) {
                            BadgedBox(badge = { Badge { Text("$activeEscrowCount") } }) {
                                Icon(Icons.Default.Lock, contentDescription = "Escrow")
                            }
                        } else {
                            Icon(Icons.Default.Lock, contentDescription = "Escrow")
                        }
                    },
                    label = { Text("Escrow", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ZedEmeraldPrimary,
                        selectedTextColor = ZedEmeraldPrimary,
                        indicatorColor = ZedEmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_escrow")
                )

                // 3. Corridors Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.LOGISTICS,
                    onClick = { viewModel.selectTab(AppNavTab.LOGISTICS) },
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Corridors") },
                    label = { Text("Corridors", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ZedEmeraldPrimary,
                        selectedTextColor = ZedEmeraldPrimary,
                        indicatorColor = ZedEmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_logistics")
                )

                // 4. Supabase Sync Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.SUPABASE_SYNC,
                    onClick = { viewModel.selectTab(AppNavTab.SUPABASE_SYNC) },
                    icon = { Icon(Icons.Default.CloudSync, contentDescription = "Supabase Hub") },
                    label = { Text("Supabase", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ZedEmeraldPrimary,
                        selectedTextColor = ZedEmeraldPrimary,
                        indicatorColor = ZedEmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_supabase")
                )

                // 5. Directory & Spot Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.DIRECTORY,
                    onClick = { viewModel.selectTab(AppNavTab.DIRECTORY) },
                    icon = { Icon(Icons.Default.Business, contentDescription = "Directory") },
                    label = { Text("Directory", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ZedEmeraldPrimary,
                        selectedTextColor = ZedEmeraldPrimary,
                        indicatorColor = ZedEmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_directory")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Ephemeral Notification Banner
            NotificationBanner(
                notification = notification,
                onDismiss = { viewModel.dismissNotification() }
            )

            // Screen switcher based on selected tab
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentTab) {
                    AppNavTab.MARKETPLACE -> MarketplaceScreen(
                        viewModel = viewModel,
                        tradeItems = tradeItems,
                        rfqs = rfqs,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        currencyPref = currencyPref
                    )
                    AppNavTab.ESCROW -> EscrowScreen(
                        viewModel = viewModel,
                        escrows = escrows
                    )
                    AppNavTab.LOGISTICS -> LogisticsScreen(
                        viewModel = viewModel,
                        shipments = shipments
                    )
                    AppNavTab.SUPABASE_SYNC -> SupabaseSyncScreen(
                        viewModel = viewModel,
                        config = supabaseConfig,
                        syncLogs = syncLogs
                    )
                    AppNavTab.DIRECTORY -> DirectoryAndCommodityScreen(
                        viewModel = viewModel,
                        quotes = viewModel.commodityQuotes,
                        traders = viewModel.verifiedTraders
                    )
                }
            }
        }
    }
}
