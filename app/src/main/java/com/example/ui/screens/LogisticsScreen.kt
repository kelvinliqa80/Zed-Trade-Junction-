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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.ShipmentBooking
import com.example.data.model.ShipmentStatus
import com.example.ui.ZedTradeViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.ZedAmberGold
import com.example.ui.theme.ZedCopperAccent
import com.example.ui.theme.ZedEmeraldContainer
import com.example.ui.theme.ZedEmeraldPrimary
import com.example.ui.theme.ZedNavySecondary

@Composable
fun LogisticsScreen(
    viewModel: ZedTradeViewModel,
    shipments: List<ShipmentBooking>,
    modifier: Modifier = Modifier
) {
    var showBookDispatchDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
        ) {
            // Freight Corridors Header
            item {
                FreightCorridorHeader()
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Corridor Waypoint Map summary
            item {
                CorridorSummaryPills()
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Active Shipments List
            item {
                Text(
                    text = "Active Freight Convoys (${shipments.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(shipments) { shipment ->
                ShipmentCard(
                    shipment = shipment,
                    onAdvanceWaypoint = {
                        viewModel.advanceShipmentWaypoint(shipment)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // FAB to book dispatch
        FloatingActionButton(
            onClick = { showBookDispatchDialog = true },
            containerColor = ZedCopperAccent,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_book_freight")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Dispatch Cargo", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    if (showBookDispatchDialog) {
        BookDispatchDialog(
            onDismiss = { showBookDispatchDialog = false },
            onSubmit = { cargo, tons, corridor, origin, dest, driver, plate ->
                viewModel.bookFreightShipment(cargo, tons, corridor, origin, dest, driver, plate)
                showBookDispatchDialog = false
            }
        )
    }
}

@Composable
fun FreightCorridorHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ZedEmeraldPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cross-Border Freight Corridors",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x33FFFFFF)
                ) {
                    Text(
                        text = "GPS & ASYCUDA Synced",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tracking heavy haulage, agricultural bulk, and copper mining freight across Zambia's primary regional junctions connecting SADC and COMESA.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFD1E8DF),
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Composable
fun CorridorSummaryPills() {
    val corridors = listOf(
        "Chirundu (South Corridor)" to "Fast-Track Green Lane",
        "Nakonde (Tanzania/Dar)" to "ASYCUDA Connected",
        "Kazungula Bridge (Walvis)" to "Tri-National Border",
        "Copperbelt - Lusaka" to "Dual-Carriageway"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(corridors) { (name, status) ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(status, color = ZedEmeraldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun ShipmentCard(
    shipment: ShipmentBooking,
    onAdvanceWaypoint: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shipment_card_${shipment.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = shipment.trackingCode,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = ZedCopperAccent
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${shipment.weightTons} MT Cargo",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                StatusBadge(
                    text = shipment.status.label,
                    type = when (shipment.status) {
                        ShipmentStatus.DISPATCHED -> "WARNING"
                        ShipmentStatus.IN_TRANSIT -> "INFO"
                        ShipmentStatus.CUSTOMS_CLEARANCE -> "CUSTOMS"
                        ShipmentStatus.CLEARED_ZRA, ShipmentStatus.DELIVERED -> "SUCCESS"
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = shipment.cargoDescription,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp)
            )

            Text(
                text = "Corridor: ${shipment.corridorName}",
                style = MaterialTheme.typography.bodySmall.copy(color = ZedEmeraldPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Origin -> Destination
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(shipment.origin, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(shipment.destination, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar & Checkpoint
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Waypoint: ${shipment.currentCheckpoint}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    )
                    Text(
                        text = "${(shipment.progressPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, color = ZedEmeraldPrimary)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { shipment.progressPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ZedEmeraldPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Driver & Telemetry Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${shipment.driverName} (${shipment.vehiclePlate})", fontSize = 11.sp, color = Color.DarkGray)
                    }
                    if (shipment.cargoTempCelsius != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Thermostat, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cold-Chain: ${shipment.cargoTempCelsius}°C", fontSize = 10.sp, color = StatusBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("ETA: ${shipment.eta}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Realtime GPS Active", fontSize = 9.sp, color = StatusGreen)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action to advance waypoint
            if (shipment.status != ShipmentStatus.DELIVERED) {
                OutlinedButton(
                    onClick = onAdvanceWaypoint,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("advance_waypoint_btn_${shipment.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ZedEmeraldPrimary)
                ) {
                    Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simulate Corridor Waypoint Advance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Consignment Delivered & POD Signed", color = StatusGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BookDispatchDialog(
    onDismiss: () -> Unit,
    onSubmit: (cargo: String, tons: Double, corridor: String, origin: String, dest: String, driver: String, plate: String) -> Unit
) {
    var cargo by remember { mutableStateOf("") }
    var tonsText by remember { mutableStateOf("30") }
    var selectedCorridor by remember { mutableStateOf("Copperbelt - Lusaka - Chirundu Corridor") }
    var origin by remember { mutableStateOf("Ndola Smelter Depot") }
    var destination by remember { mutableStateOf("Durban Port via Chirundu") }
    var driver by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("BAX 3918 ZM") }

    val corridors = listOf(
        "Copperbelt - Lusaka - Chirundu Corridor",
        "Dar es Salaam - Nakonde Hub",
        "Walvis Bay - Kazungula Route",
        "Beira - Chirundu - Lusaka"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(8.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Text(
                        text = "Book Freight Corridor Dispatch",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Creates active convoy telemetry synced to Supabase",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cargo,
                        onValueChange = { cargo = it },
                        label = { Text("Cargo Description *") },
                        placeholder = { Text("e.g. 30 MT Granular Urea Fertilizer") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = tonsText,
                            onValueChange = { tonsText = it },
                            label = { Text("Weight (Tons)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = plate,
                            onValueChange = { plate = it },
                            label = { Text("Vehicle Reg") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Select Trade Corridor:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(corridors) { corr ->
                            FilterChip(
                                selected = selectedCorridor == corr,
                                onClick = { selectedCorridor = corr },
                                label = { Text(corr.take(25) + "...", fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = origin,
                            onValueChange = { origin = it },
                            label = { Text("Origin") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = destination,
                            onValueChange = { destination = it },
                            label = { Text("Destination") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = driver,
                        onValueChange = { driver = it },
                        label = { Text("Assigned Driver Name *") },
                        placeholder = { Text("e.g. Chileshe Musonda") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (cargo.isNotBlank() && driver.isNotBlank()) {
                                    val tons = tonsText.toDoubleOrNull() ?: 30.0
                                    onSubmit(cargo, tons, selectedCorridor, origin, destination, driver, plate)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ZedCopperAccent),
                            modifier = Modifier.testTag("submit_dispatch_btn")
                        ) {
                            Text("Dispatch Convoy")
                        }
                    }
                }
            }
        }
    }
}
