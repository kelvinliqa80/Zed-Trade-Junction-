package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.CommodityQuote
import com.example.data.model.TraderProfile
import com.example.ui.ZedTradeViewModel
import com.example.ui.components.PacraBadge
import com.example.ui.components.ZraBadge
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.ZedAmberGold
import com.example.ui.theme.ZedCopperAccent
import com.example.ui.theme.ZedEmeraldContainer
import com.example.ui.theme.ZedEmeraldPrimary
import com.example.ui.theme.ZedNavySecondary

@Composable
fun DirectoryAndCommodityScreen(
    viewModel: ZedTradeViewModel,
    quotes: List<CommodityQuote>,
    traders: List<TraderProfile>,
    modifier: Modifier = Modifier
) {
    var contactTargetTrader by remember { mutableStateOf<TraderProfile?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
    ) {
        // Commodity Spot Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = ZedEmeraldPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Commodity & Spot Ticker",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = "Realtime Feed",
                        color = Color(0xFF15803D),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Horizontal Ticker Cards
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quotes) { quote ->
                    CommodityQuoteCard(quote = quote)
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Verified Trader Directory Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = ZedAmberGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Verified Trader Directory",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = "PACRA & ZRA Audited",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Trader Profiles
        items(traders) { trader ->
            TraderProfileCard(
                trader = trader,
                onContact = { contactTargetTrader = trader }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    contactTargetTrader?.let { trader ->
        ContactTraderDialog(
            trader = trader,
            onDismiss = { contactTargetTrader = null },
            onSend = { message ->
                viewModel.showToast("Inquiry sent to ${trader.companyName} via encrypted trade gateway!")
                contactTargetTrader = null
            }
        )
    }
}

@Composable
fun CommodityQuoteCard(quote: CommodityQuote) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(165.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quote.symbol,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = ZedNavySecondary
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isUp = quote.changePct >= 0
                    Icon(
                        imageVector = if (isUp) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (isUp) StatusGreen else StatusRed,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${if (isUp) "+" else ""}${quote.changePct}%",
                        color = if (isUp) StatusGreen else StatusRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = quote.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${quote.currency} ${"%,.2f".format(quote.price)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = ZedEmeraldPrimary,
                    fontSize = 15.sp
                )
            )

            Text(
                text = "per ${quote.unit}",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
            )
        }
    }
}

@Composable
fun TraderProfileCard(
    trader: TraderProfile,
    onContact: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trader_card_${trader.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trader.companyName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "${trader.category} • ${trader.city}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ZedAmberGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (trader.tier == "GOLD_SUPPLIER") "★ GOLD MERCHANT" else "VERIFIED",
                        color = ZedAmberGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Compliance Info Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PacraBadge()
                ZraBadge()
                Text(
                    text = "Score: ${trader.complianceScore}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = StatusGreen
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Registration Numbers
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = trader.pacraRegNo,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = trader.zraTpin,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = ZedAmberGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("%.2f".format(trader.rating), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("(${trader.totalTrades} Completed Trades)", fontSize = 11.sp, color = Color.Gray)
                }

                Button(
                    onClick = onContact,
                    colors = ButtonDefaults.buttonColors(containerColor = ZedNavySecondary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("contact_trader_${trader.id}")
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trade Inquiry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ContactTraderDialog(
    trader: TraderProfile,
    onDismiss: () -> Unit,
    onSend: (message: String) -> Unit
) {
    var message by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Inquire with ${trader.companyName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Direct RFQ & procurement correspondence",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = ZedEmeraldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(trader.contactPhone, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = ZedEmeraldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(trader.contactEmail, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Your Trade Message / Volume Required") },
                    placeholder = { Text("e.g. Seeking quotation for 100 MT Copper Cathodes CIF Durban") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (message.isNotBlank()) onSend(message) },
                        colors = ButtonDefaults.buttonColors(containerColor = ZedEmeraldPrimary)
                    ) {
                        Text("Send Inquiry")
                    }
                }
            }
        }
    }
}
