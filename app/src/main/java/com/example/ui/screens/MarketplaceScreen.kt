package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.PaymentRail
import com.example.data.model.RfqItem
import com.example.data.model.TradeCategory
import com.example.data.model.TradeItem
import com.example.ui.ZedTradeViewModel
import com.example.ui.components.PacraBadge
import com.example.ui.components.StatusBadge
import com.example.ui.components.ZraBadge
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.ZedAmberGold
import com.example.ui.theme.ZedCopperAccent
import com.example.ui.theme.ZedEmeraldContainer
import com.example.ui.theme.ZedEmeraldPrimary
import com.example.ui.theme.ZedNavySecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: ZedTradeViewModel,
    tradeItems: List<TradeItem>,
    rfqs: List<RfqItem>,
    selectedCategory: TradeCategory,
    searchQuery: String,
    currencyPref: String,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Listings, 1: RFQs
    var showPostListingDialog by remember { mutableStateOf(false) }
    var showNewRfqDialog by remember { mutableStateOf(false) }
    var selectedItemForEscrow by remember { mutableStateOf<TradeItem?>(null) }
    var selectedRfqForBid by remember { mutableStateOf<RfqItem?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Hero Banner & Quick Stats
            item {
                MarketHeroSection(
                    totalListings = tradeItems.size,
                    activeRfqs = rfqs.size
                )
            }

            // Search Bar & Filter Controls
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search copper, maize, fertilizer, tools...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = ZedEmeraldPrimary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("marketplace_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Currency Filter Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Currency:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        listOf("ALL", "ZMW", "USD").forEach { curr ->
                            FilterChip(
                                selected = currencyPref == curr,
                                onClick = { viewModel.setCurrencyPreference(curr) },
                                label = { Text(curr, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ZedEmeraldContainer,
                                    selectedLabelColor = ZedEmeraldPrimary
                                ),
                                modifier = Modifier.testTag("currency_chip_$curr")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Scroll
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(TradeCategory.values()) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { viewModel.setCategory(cat) },
                                label = { Text(cat.label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ZedNavySecondary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("category_chip_${cat.name}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sub-tab: Listings vs RFQs
                    TabRow(
                        selectedTabIndex = activeSubTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = ZedEmeraldPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    ) {
                        Tab(
                            selected = activeSubTab == 0,
                            onClick = { activeSubTab = 0 },
                            text = { Text("Available Listings (${tradeItems.size})", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = activeSubTab == 1,
                            onClick = { activeSubTab = 1 },
                            text = { Text("Active RFQs (${rfqs.size})", fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            // Content List based on active sub tab
            if (activeSubTab == 0) {
                if (tradeItems.isEmpty()) {
                    item {
                        EmptyListingsView {
                            viewModel.setCategory(TradeCategory.ALL)
                            viewModel.setSearchQuery("")
                            viewModel.setCurrencyPreference("ALL")
                        }
                    }
                } else {
                    items(tradeItems) { item ->
                        TradeItemCard(
                            item = item,
                            onInitiateEscrow = { selectedItemForEscrow = item }
                        )
                    }
                }
            } else {
                items(rfqs) { rfq ->
                    RfqItemCard(
                        rfq = rfq,
                        onBid = { selectedRfqForBid = rfq }
                    )
                }
            }
        }

        // Floating Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (activeSubTab == 1) {
                FloatingActionButton(
                    onClick = { showNewRfqDialog = true },
                    containerColor = ZedAmberGold,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_post_rfq")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Post RFQ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            } else {
                FloatingActionButton(
                    onClick = { showPostListingDialog = true },
                    containerColor = ZedEmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_post_listing")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sell Commodity", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Dialogs
    if (showPostListingDialog) {
        PostListingDialog(
            onDismiss = { showPostListingDialog = false },
            onSubmit = { title, cat, price, curr, unit, moq, qty, loc, desc ->
                viewModel.postNewListing(title, cat, price, curr, unit, moq, qty, loc, desc)
                showPostListingDialog = false
            }
        )
    }

    if (showNewRfqDialog) {
        NewRfqDialog(
            onDismiss = { showNewRfqDialog = false },
            onSubmit = { buyer, item, cat, qty, budget, loc, deadline ->
                viewModel.postNewRfq(buyer, item, cat, qty, budget, loc, deadline)
                showNewRfqDialog = false
            }
        )
    }

    selectedItemForEscrow?.let { item ->
        InitiateEscrowDialog(
            item = item,
            onDismiss = { selectedItemForEscrow = null },
            onConfirm = { buyerName, qty, rail ->
                viewModel.createEscrowForListing(item, buyerName, qty, rail)
                selectedItemForEscrow = null
            }
        )
    }

    selectedRfqForBid?.let { rfq ->
        SubmitBidDialog(
            rfq = rfq,
            onDismiss = { selectedRfqForBid = null },
            onSubmitBid = { bidder, amount ->
                viewModel.submitBidOnRfq(rfq.id, bidder, amount)
                selectedRfqForBid = null
            }
        )
    }
}

@Composable
fun MarketHeroSection(
    totalListings: Int,
    activeRfqs: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_hero_trade),
            contentDescription = "Zed Trade Junction Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x990A192F),
                            Color(0xE6002B20)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ZED TRADE JUNCTION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ZedAmberGold,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Text(
                        text = "Regional B2B & Commodity Exchange",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x33FFFFFF)
                ) {
                    Text(
                        text = "Zambia & SADC Hub",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Quick Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33000000))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricItem(label = "Listings", value = "$totalListings Live")
                MetricItem(label = "Active RFQs", value = "$activeRfqs Bids")
                MetricItem(label = "Escrow Prot.", value = "100% Guaranteed")
                MetricItem(label = "Corridors", value = "4 Connected")
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(text = label, color = Color(0xFFCBD5E1), fontSize = 10.sp)
    }
}

@Composable
fun TradeItemCard(
    item: TradeItem,
    onInitiateEscrow: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("trade_card_${item.id}"),
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
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.category.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ZedCopperAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${item.currency} ${"%,.2f".format(item.price)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ZedEmeraldPrimary,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "per ${item.unit}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Specs row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = item.location,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color.Gray)
                    )
                }

                Text(
                    text = "MOQ: ${item.minOrderQuantity} | Stock: ${item.quantityAvailable}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seller credentials & Escrow action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.sellerName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = ZedAmberGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "%.1f".format(item.sellerRating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (item.isPacraVerified) PacraBadge()
                        if (item.isZraCompliant) ZraBadge()
                    }
                }

                Button(
                    onClick = onInitiateEscrow,
                    colors = ButtonDefaults.buttonColors(containerColor = ZedEmeraldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("buy_escrow_button_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ZedPay Escrow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RfqItemCard(
    rfq: RfqItem,
    onBid: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("rfq_card_${rfq.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(text = rfq.status, type = if (rfq.status == "BIDDING_ACTIVE") "INFO" else "SUCCESS")
                Text(
                    text = "${rfq.bidCount} Bids Submitted",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ZedEmeraldPrimary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = rfq.requestedItem,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp)
            )

            Text(
                text = "Procured by: ${rfq.buyerCompany}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Target Quantity", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                    Text(text = rfq.targetQuantity, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Target Budget", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                    Text(text = rfq.budgetPerUnit, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZedCopperAccent)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delivery: ${rfq.deliveryLocation} • Deadline: ${rfq.deadlineDate}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Color.Gray)
                )

                OutlinedButton(
                    onClick = onBid,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("submit_bid_button_${rfq.id}")
                ) {
                    Text("Submit Quote", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmptyListingsView(onReset: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No listings matching your filters",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Try clearing category filters or adjusting search terms.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(containerColor = ZedEmeraldPrimary)
        ) {
            Text("Reset All Filters")
        }
    }
}

@Composable
fun InitiateEscrowDialog(
    item: TradeItem,
    onDismiss: () -> Unit,
    onConfirm: (buyerName: String, qty: Int, rail: PaymentRail) -> Unit
) {
    var buyerName by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf(item.minOrderQuantity.toString()) }
    var selectedRail by remember { mutableStateOf(PaymentRail.AIRTEL_MONEY) }

    val qty = quantityText.toIntOrNull() ?: item.minOrderQuantity
    val totalEstimated = item.price * qty

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = ZedEmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ZedPay Escrow Deposit",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Initiating safe escrow agreement for:\n${item.title}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = buyerName,
                    onValueChange = { buyerName = it },
                    label = { Text("Buyer Company / Name") },
                    placeholder = { Text("e.g. Copperbelt Agro Traders") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Quantity (${item.unit}) - Min ${item.minOrderQuantity}") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Select Settlement Payment Rail:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                PaymentRail.values().forEach { rail ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedRail == rail) ZedEmeraldContainer else Color.Transparent,
                        border = BorderStroke(if (selectedRail == rail) 2.dp else 1.dp, if (selectedRail == rail) ZedEmeraldPrimary else Color.LightGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { selectedRail = rail }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(rail.displayName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(rail.provider, fontSize = 10.sp, color = Color.Gray)
                            }
                            Text("Fee: ${rail.feePct}%", fontSize = 11.sp, color = ZedEmeraldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Escrow Lock:", fontWeight = FontWeight.Bold)
                        Text(
                            "${item.currency} ${"%,.2f".format(totalEstimated)}",
                            fontWeight = FontWeight.ExtraBold,
                            color = ZedEmeraldPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(buyerName, qty, selectedRail) },
                        colors = ButtonDefaults.buttonColors(containerColor = ZedEmeraldPrimary),
                        modifier = Modifier.testTag("confirm_escrow_deposit_button")
                    ) {
                        Text("Deposit & Lock")
                    }
                }
            }
        }
    }
}

@Composable
fun PostListingDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        category: TradeCategory,
        price: Double,
        currency: String,
        unit: String,
        moq: Int,
        quantity: Int,
        location: String,
        description: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(TradeCategory.AGRICULTURE) }
    var priceText by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("ZMW") }
    var unit by remember { mutableStateOf("50kg Bag") }
    var moqText by remember { mutableStateOf("50") }
    var qtyText by remember { mutableStateOf("1000") }
    var location by remember { mutableStateOf("Lusaka MFEZ") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(8.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Text(
                        text = "List Trade Commodity / Goods",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Synced in real time to Supabase marketplace",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Commodity Title *") },
                        placeholder = { Text("e.g. Soya Beans Non-GMO") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(TradeCategory.values().filter { it != TradeCategory.ALL }) { cat ->
                            FilterChip(
                                selected = selectedCat == cat,
                                onClick = { selectedCat = cat },
                                label = { Text(cat.label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Price *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = currency,
                            onValueChange = { currency = it },
                            label = { Text("Currency") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = moqText,
                            onValueChange = { moqText = it },
                            label = { Text("Min Order") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = qtyText,
                            onValueChange = { qtyText = it },
                            label = { Text("Available Stock") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Warehouse Location") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Specifications") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    val price = priceText.toDoubleOrNull() ?: 100.0
                                    val moq = moqText.toIntOrNull() ?: 10
                                    val qty = qtyText.toIntOrNull() ?: 500
                                    onSubmit(title, selectedCat, price, currency, unit, moq, qty, location, description)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ZedEmeraldPrimary),
                            modifier = Modifier.testTag("submit_new_listing_btn")
                        ) {
                            Text("Publish Listing")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewRfqDialog(
    onDismiss: () -> Unit,
    onSubmit: (buyer: String, item: String, category: TradeCategory, qty: String, budget: String, loc: String, deadline: String) -> Unit
) {
    var buyer by remember { mutableStateOf("") }
    var item by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(TradeCategory.AGRICULTURE) }
    var qty by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var loc by remember { mutableStateOf("Lusaka Heavy Industrial") }
    var deadline by remember { mutableStateOf("15 Oct 2026") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Broadcast Request For Quotation (RFQ)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Suppliers across Zambia & SADC will receive instant alerts",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = buyer,
                    onValueChange = { buyer = it },
                    label = { Text("Your Company / Entity *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = item,
                    onValueChange = { item = it },
                    label = { Text("Required Commodity / Goods *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = qty,
                        onValueChange = { qty = it },
                        label = { Text("Target Quantity") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = { Text("Target Budget") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = loc,
                        onValueChange = { loc = it },
                        label = { Text("Delivery Site") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Deadline") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (buyer.isNotBlank() && item.isNotBlank()) {
                                onSubmit(buyer, item, selectedCat, qty, budget, loc, deadline)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZedAmberGold),
                        modifier = Modifier.testTag("submit_new_rfq_btn")
                    ) {
                        Text("Broadcast RFQ")
                    }
                }
            }
        }
    }
}

@Composable
fun SubmitBidDialog(
    rfq: RfqItem,
    onDismiss: () -> Unit,
    onSubmitBid: (bidder: String, amount: String) -> Unit
) {
    var bidderName by remember { mutableStateOf("") }
    var bidPrice by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Submit Official Quotation",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Responding to RFQ: ${rfq.requestedItem} (${rfq.targetQuantity})",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = bidderName,
                    onValueChange = { bidderName = it },
                    label = { Text("Your Supplier Company Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bidPrice,
                    onValueChange = { bidPrice = it },
                    label = { Text("Offered Price / Unit (e.g. ZMW 7,800 / MT) *") },
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
                            if (bidderName.isNotBlank() && bidPrice.isNotBlank()) {
                                onSubmitBid(bidderName, bidPrice)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZedEmeraldPrimary),
                        modifier = Modifier.testTag("confirm_bid_btn")
                    ) {
                        Text("Submit Quote")
                    }
                }
            }
        }
    }
}
