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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.EscrowContract
import com.example.data.model.EscrowStatus
import com.example.ui.ZedTradeViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed
import com.example.ui.theme.ZedAmberGold
import com.example.ui.theme.ZedEmeraldContainer
import com.example.ui.theme.ZedEmeraldPrimary
import com.example.ui.theme.ZedNavySecondary

@Composable
fun EscrowScreen(
    viewModel: ZedTradeViewModel,
    escrows: List<EscrowContract>,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var disputeTargetEscrow by remember { mutableStateOf<EscrowContract?>(null) }

    val filteredEscrows = remember(escrows, selectedFilter) {
        if (selectedFilter == "ALL") escrows
        else escrows.filter { it.status.name == selectedFilter }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
    ) {
        // Escrow Vault Overview Card
        item {
            EscrowVaultHeader()
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Trust Protocol Explainer Card
        item {
            EscrowTrustExplainer()
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf(
                    "ALL" to "All Deals",
                    "FUNDS_DEPOSITED" to "Deposited",
                    "GOODS_DISPATCHED" to "In Transit",
                    "INSPECTION_PASSED" to "Inspection OK",
                    "FUNDS_RELEASED" to "Settled"
                )
                items(filters) { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ZedEmeraldPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("escrow_filter_$key")
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Escrow List
        if (filteredEscrows.isEmpty()) {
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
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No contracts in this stage", fontWeight = FontWeight.Bold)
                        Text("Switch to 'All Deals' or initiate an escrow from Marketplace.", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(filteredEscrows) { escrow ->
                EscrowContractCard(
                    contract = escrow,
                    onTransition = { nextStatus, notes ->
                        viewModel.transitionEscrow(escrow, nextStatus, notes)
                    },
                    onDispute = { disputeTargetEscrow = escrow }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    disputeTargetEscrow?.let { escrow ->
        DisputeDialog(
            escrow = escrow,
            onDismiss = { disputeTargetEscrow = null },
            onConfirmDispute = { reason ->
                viewModel.transitionEscrow(escrow, EscrowStatus.DISPUTED, "Dispute raised by counterparty: $reason")
                disputeTargetEscrow = null
            }
        )
    }
}

@Composable
fun EscrowVaultHeader() {
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
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = ZedAmberGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ZedPay™ Escrow Trust Vault",
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
                        text = "Real-Time Multi-Rail",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Total Active Escrow Lock", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(
                        text = "ZMW 1,545,000 + $492.5k",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Dispute Mediation Rate", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(
                        text = "0.0% (Zero Default)",
                        color = StatusGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EscrowTrustExplainer() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ZedEmeraldContainer.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, ZedEmeraldPrimary.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = ZedEmeraldPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Trustless Milestone Release",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ZedEmeraldPrimary
                    )
                )
                Text(
                    text = "Funds remain locked in escrow until the cargo arrives and passes physical ZABS/ZRA checkpoint inspection.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }
    }
}

@Composable
fun EscrowContractCard(
    contract: EscrowContract,
    onTransition: (nextStatus: EscrowStatus, notes: String) -> Unit,
    onDispute: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("escrow_card_${contract.id}"),
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
                        text = contract.id,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = ZedEmeraldPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${contract.trackingCode}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                }

                StatusBadge(
                    text = contract.status.title,
                    type = when (contract.status) {
                        EscrowStatus.INITIATED, EscrowStatus.FUNDS_DEPOSITED -> "WARNING"
                        EscrowStatus.GOODS_DISPATCHED -> "INFO"
                        EscrowStatus.INSPECTION_PASSED -> "CUSTOMS"
                        EscrowStatus.FUNDS_RELEASED -> "SUCCESS"
                        EscrowStatus.DISPUTED -> "ERROR"
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = contract.itemTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Buyer: ${contract.buyerName}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color.Gray))
                    Text(text = "Seller: ${contract.sellerName}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color.Gray))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${contract.currency} ${"%,.2f".format(contract.amount)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ZedEmeraldPrimary
                        )
                    )
                    Text(
                        text = contract.paymentRail.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.DarkGray, fontSize = 10.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5-Stage Stepper
            MilestoneStepper(currentStatus = contract.status)

            Spacer(modifier = Modifier.height(10.dp))

            // Milestone Notes Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Status Log: ${contract.milestoneNotes}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Audit Hash: ${contract.txHash.take(18)}... (Supabase Synced)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons depending on state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (contract.status != EscrowStatus.FUNDS_RELEASED && contract.status != EscrowStatus.DISPUTED) {
                    OutlinedButton(
                        onClick = onDispute,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                        modifier = Modifier.testTag("dispute_btn_${contract.id}")
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dispute", fontSize = 11.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Advance Action
                when (contract.status) {
                    EscrowStatus.INITIATED -> {
                        Button(
                            onClick = {
                                onTransition(EscrowStatus.FUNDS_DEPOSITED, "Buyer locked funds in ZedPay escrow vault.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ZedEmeraldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("action_escrow_${contract.id}")
                        ) {
                            Text("Deposit & Lock Funds", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    EscrowStatus.FUNDS_DEPOSITED -> {
                        Button(
                            onClick = {
                                onTransition(EscrowStatus.GOODS_DISPATCHED, "Seller verified dispatch with freight carrier tracking.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("action_escrow_${contract.id}")
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirm Dispatch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    EscrowStatus.GOODS_DISPATCHED -> {
                        Button(
                            onClick = {
                                onTransition(EscrowStatus.INSPECTION_PASSED, "Quality and weight certified at destination terminal.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusPurple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("action_escrow_${contract.id}")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirm Inspection OK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    EscrowStatus.INSPECTION_PASSED -> {
                        Button(
                            onClick = {
                                onTransition(EscrowStatus.FUNDS_RELEASED, "Buyer authorized release. Settlement paid out to seller.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("action_escrow_${contract.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Release Funds to Seller", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    EscrowStatus.FUNDS_RELEASED -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Settled & Closed", color = StatusGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    EscrowStatus.DISPUTED -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = StatusRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Arbitration Active", color = StatusRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneStepper(currentStatus: EscrowStatus) {
    val steps = listOf(
        "Deposit" to 2,
        "Transit" to 3,
        "Inspect" to 4,
        "Release" to 5
    )

    val currentStep = currentStatus.stepIndex

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, (label, targetStep) ->
            val isDone = currentStep >= targetStep
            val isCurrent = currentStep == targetStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                currentStatus == EscrowStatus.DISPUTED -> StatusRed
                                isDone -> StatusGreen
                                isCurrent -> ZedAmberGold
                                else -> Color.LightGray
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isDone || isCurrent) Color.Black else Color.Gray
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (currentStep > targetStep) StatusGreen else Color.LightGray)
                )
            }
        }
    }
}

@Composable
fun DisputeDialog(
    escrow: EscrowContract,
    onDismiss: () -> Unit,
    onConfirmDispute: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = StatusRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Raise Escrow Arbitration",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Contract: ${escrow.id} - ${escrow.itemTitle}\nFunds will remain frozen while the Zed Trade Junction compliance ombudsman verifies shipping documents and laboratory assays.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = Color.DarkGray)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Dispute Details / Non-Compliance Reason *") },
                    placeholder = { Text("e.g. Moisture content exceeded contract specs by 3%") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
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
                            if (reason.isNotBlank()) onConfirmDispute(reason)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                        modifier = Modifier.testTag("confirm_dispute_btn")
                    ) {
                        Text("Freeze & Dispute")
                    }
                }
            }
        }
    }
}
