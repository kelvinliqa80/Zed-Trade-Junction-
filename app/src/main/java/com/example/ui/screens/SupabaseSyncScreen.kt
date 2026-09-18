package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SupabaseConfig
import com.example.data.model.SyncEventLog
import com.example.ui.ZedTradeViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed
import com.example.ui.theme.ZedAmberGold
import com.example.ui.theme.ZedCopperAccent
import com.example.ui.theme.ZedEmeraldContainer
import com.example.ui.theme.ZedEmeraldPrimary
import com.example.ui.theme.ZedNavySecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SupabaseSyncScreen(
    viewModel: ZedTradeViewModel,
    config: SupabaseConfig,
    syncLogs: List<SyncEventLog>,
    modifier: Modifier = Modifier
) {
    var isSyncingNow by remember { mutableStateOf(false) }
    var urlInput by remember(config.projectUrl) { mutableStateOf(config.projectUrl) }
    var keyInput by remember(config.anonKey) { mutableStateOf(config.anonKey) }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
    ) {
        // Connection & Engine Status
        item {
            SupabaseConnectionCard(
                config = config,
                isSyncing = isSyncingNow,
                onForceSync = {
                    coroutineScope.launch {
                        isSyncingNow = true
                        delay(600)
                        viewModel.forceFullSupabaseSync()
                        isSyncingNow = false
                    }
                },
                onToggleRealtime = { viewModel.toggleSupabaseRealtime(it) }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Realtime Architecture & RLS Policies
        item {
            SupabaseArchitectureCard(config = config)
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Credentials & Endpoint Configurator
        item {
            SupabaseConfigEditor(
                url = urlInput,
                key = keyInput,
                onUrlChange = { urlInput = it },
                onKeyChange = { keyInput = it },
                onTestConnection = {
                    viewModel.testSupabaseConnection(urlInput, keyInput)
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Live Event Stream Log Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = ZedEmeraldPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Real-Time Sync Stream (${syncLogs.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.clearAuditLogs() },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Logs", fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Sync Event Logs
        if (syncLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No sync events recorded yet", fontWeight = FontWeight.Bold)
                        Text("Actions in Marketplace, Escrow, and Logistics will stream here in real time.", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(syncLogs) { log ->
                SyncLogCard(log = log)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SupabaseConnectionCard(
    config: SupabaseConfig,
    isSyncing: Boolean,
    onForceSync: () -> Unit,
    onToggleRealtime: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ZedNavySecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (config.realtimeConnected) StatusGreen else StatusRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (config.realtimeConnected) "Supabase Realtime CONNECTED" else "Offline Local Cache Mode",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }

                Switch(
                    checked = config.realtimeConnected,
                    onCheckedChange = onToggleRealtime,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ZedEmeraldPrimary
                    ),
                    modifier = Modifier.testTag("toggle_realtime_switch")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Channel: ${config.channelName}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = ZedAmberGold
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x26FFFFFF))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Protocol", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Text("PostgREST V12", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Latency", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Text("${config.latencyMs} ms", color = StatusGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Local Persistence", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Text("Room SQLite", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onForceSync,
                enabled = !isSyncing,
                colors = ButtonDefaults.buttonColors(containerColor = ZedEmeraldPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("force_sync_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Synchronizing Tables...")
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Force Bi-Directional Sync Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SupabaseArchitectureCard(config: SupabaseConfig) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = ZedEmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Supabase Modules & Row-Level Security",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val tables = listOf(
                "public.trade_listings" to "RLS: Authenticated Sellers (INSERT/UPDATE), Public Read",
                "public.escrow_contracts" to "RLS: Multi-party (Buyer OR Seller OR ZedPay Mediator)",
                "public.freight_corridors" to "RLS: Carrier Dispatches + Customs ASYCUDA Clearance",
                "public.rfq_orders" to "RLS: Verified Procurement Buyers & Registered Bidders"
            )

            tables.forEach { (table, policy) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(table, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        Text(policy, fontSize = 10.sp, color = Color.Gray)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("ACTIVE", color = Color(0xFF15803D), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SupabaseConfigEditor(
    url: String,
    key: String,
    onUrlChange: (String) -> Unit,
    onKeyChange: (String) -> Unit,
    onTestConnection: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Storage, contentDescription = null, tint = ZedCopperAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Supabase Endpoint Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                label = { Text("Supabase Project URL") },
                placeholder = { Text("https://your-project.supabase.co") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("supabase_url_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = key,
                onValueChange = onKeyChange,
                label = { Text("Anon / Public API Key") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("supabase_key_input"),
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onTestConnection,
                colors = ButtonDefaults.buttonColors(containerColor = ZedCopperAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_supabase_connection_button")
            ) {
                Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test & Verify Supabase Connection")
            }
        }
    }
}

@Composable
fun SyncLogCard(log: SyncEventLog) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(log.eventTime))

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(text = log.action, type = if (log.action == "INSERT") "INFO" else if (log.action == "UPDATE") "WARNING" else "SUCCESS")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.table,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = ZedEmeraldPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = log.payload,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp)
                )
            }

            Text(
                text = formattedTime,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
            )
        }
    }
}
