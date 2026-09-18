package com.example.data.remote

import com.example.data.model.CommodityQuote
import com.example.data.model.EscrowContract
import com.example.data.model.EscrowStatus
import com.example.data.model.PaymentRail
import com.example.data.model.RfqItem
import com.example.data.model.ShipmentBooking
import com.example.data.model.ShipmentStatus
import com.example.data.model.TradeCategory
import com.example.data.model.TradeItem
import com.example.data.model.TraderProfile

object SeedData {

    val initialTradeItems = listOf(
        TradeItem(
            id = "TJ-ZM-001",
            title = "Grade-A Refined Copper Cathodes (99.99%)",
            category = TradeCategory.MINING_METALS,
            price = 9850.0,
            currency = "USD",
            unit = "Metric Ton (MT)",
            minOrderQuantity = 20,
            quantityAvailable = 450,
            location = "Ndola Smelter Depot, Copperbelt",
            sellerName = "Mopani Copperbelt Metals Ltd",
            sellerRating = 4.95f,
            isPacraVerified = true,
            isZraCompliant = true,
            description = "High-purity LME grade-A electrolytic copper cathodes with certified assay report. Ready for cross-border export via Dar es Salaam or Durban corridors.",
            timestamp = System.currentTimeMillis() - 3600000 * 4
        ),
        TradeItem(
            id = "TJ-ZM-002",
            title = "White Non-GMO Grain Maize (Harvest 2025/2026)",
            category = TradeCategory.AGRICULTURE,
            price = 380.0,
            currency = "ZMW",
            unit = "50kg Bag",
            minOrderQuantity = 200,
            quantityAvailable = 15000,
            location = "Mkushi Commercial Farming Block, Central",
            sellerName = "Mkushi Agro Grain Growers Union",
            sellerRating = 4.88f,
            isPacraVerified = true,
            isZraCompliant = true,
            description = "Grade 1 clean dry white maize, moisture content below 12.5%. FRA compliant, fumigated and certified for regional export or millers.",
            timestamp = System.currentTimeMillis() - 3600000 * 6
        ),
        TradeItem(
            id = "TJ-ZM-003",
            title = "Granular Urea 46% Nitrogen Fertilizer",
            category = TradeCategory.AGRICULTURE,
            price = 920.0,
            currency = "ZMW",
            unit = "50kg Bag",
            minOrderQuantity = 100,
            quantityAvailable = 8400,
            location = "Lusaka South Multi-Facility Economic Zone",
            sellerName = "Zambian Fertilizer Logistics Corp",
            sellerRating = 4.90f,
            isPacraVerified = true,
            isZraCompliant = true,
            description = "Top-dressing agricultural nitrogen fertilizer with anti-caking coating. Direct warehouse release with ZRA customs import clearance.",
            timestamp = System.currentTimeMillis() - 3600000 * 8
        ),
        TradeItem(
            id = "TJ-ZM-004",
            title = "Heavy-Duty Tungsten Mining Drill Bits & PPE Kit",
            category = TradeCategory.INDUSTRIAL_EQUIPMENT,
            price = 14500.0,
            currency = "ZMW",
            unit = "Set / 12 Pcs",
            minOrderQuantity = 5,
            quantityAvailable = 120,
            location = "Kitwe Heavy Industrial Area",
            sellerName = "Kansanshi Tooling & Safety Supplies",
            sellerRating = 4.75f,
            isPacraVerified = true,
            isZraCompliant = true,
            description = "Deep-bore mining exploration bits, high-impact carbide tipped with full OSHA/MSHA certified hi-vis safety gear and respirators.",
            timestamp = System.currentTimeMillis() - 3600000 * 12
        ),
        TradeItem(
            id = "TJ-ZM-005",
            title = "Kiln-Dried Mukwa & Rosewood Timber Planks",
            category = TradeCategory.BUILDING_MATERIALS,
            price = 4200.0,
            currency = "ZMW",
            unit = "Cubic Metre",
            minOrderQuantity = 10,
            quantityAvailable = 350,
            location = "Livingstone Forestry Depot, Southern",
            sellerName = "Zambezi Hardwood Timber Concession",
            sellerRating = 4.82f,
            isPacraVerified = true,
            isZraCompliant = true,
            description = "Sustainably harvested and licensed indigenous Mukwa timber with Department of Forestry conveyance permits. Moisture content 10-12%.",
            timestamp = System.currentTimeMillis() - 3600000 * 18
        ),
        TradeItem(
            id = "TJ-ZM-006",
            title = "Bulk Pure Sunflower Cooking Oil (IBC Totes)",
            category = TradeCategory.FMCG_WHOLESALE,
            price = 32500.0,
            currency = "ZMW",
            unit = "1,000 Litre Tote",
            minOrderQuantity = 2,
            quantityAvailable = 45,
            location = "Choma Industrial Park, Southern",
            sellerName = "Golden Valley Seed Pressers Ltd",
            sellerRating = 4.79f,
            isPacraVerified = true,
            isZraCompliant = true,
            description = "Triple-refined, cholesterol-free sunflower cooking oil. Food-grade IBC containers with tamper-evident seal and ZABS quality mark.",
            timestamp = System.currentTimeMillis() - 3600000 * 24
        ),
        TradeItem(
            id = "TJ-ZM-007",
            title = "Commercial Industrial Solar Inverter System (50kVA)",
            category = TradeCategory.ENERGY_FUELS,
            price = 18500.0,
            currency = "USD",
            unit = "Complete System",
            minOrderQuantity = 1,
            quantityAvailable = 18,
            location = "Lusaka Light Industrial Area",
            sellerName = "Kafue Renewable Power Grid",
            sellerRating = 4.92f,
            isPacraVerified = true,
            isZraCompliant = true,
            description = "Three-phase hybrid off-grid commercial inverter with lithium-iron phosphate battery integration. 5-year manufacturer warranty.",
            timestamp = System.currentTimeMillis() - 3600000 * 30
        )
    )

    val initialEscrowContracts = listOf(
        EscrowContract(
            id = "ESC-2026-881",
            tradeItemId = "TJ-ZM-001",
            itemTitle = "Grade-A Refined Copper Cathodes (50 MT)",
            buyerName = "Southern African Wire & Cable Corp",
            sellerName = "Mopani Copperbelt Metals Ltd",
            amount = 492500.0,
            currency = "USD",
            status = EscrowStatus.GOODS_DISPATCHED,
            paymentRail = PaymentRail.SWIFT_WIRE,
            txHash = "0x8f3c9e2b14a7e938cd45fa28bc3910ad542e88a3",
            trackingCode = "FRT-NDL-092",
            milestoneNotes = "Seller dispatched 2x 30-ton interlink flatbed trucks from Ndola. Approaching Chirundu border weighbridge.",
            createdAt = System.currentTimeMillis() - 86400000 * 2,
            updatedAt = System.currentTimeMillis() - 3600000 * 3
        ),
        EscrowContract(
            id = "ESC-2026-882",
            tradeItemId = "TJ-ZM-002",
            itemTitle = "White Non-GMO Grain Maize (2,000 Bags)",
            buyerName = "National Milling Company Zambia",
            sellerName = "Mkushi Agro Grain Growers Union",
            amount = 760000.0,
            currency = "ZMW",
            status = EscrowStatus.FUNDS_DEPOSITED,
            paymentRail = PaymentRail.ZANACO_BANK,
            txHash = "0x4b78ae9123cfd94356a81e05a8d904bca992f143",
            trackingCode = "FRT-MKU-104",
            milestoneNotes = "Buyer deposited ZMW 760,000 into ZedPay Escrow Trust Account. Awaiting seller loading at Mkushi silos.",
            createdAt = System.currentTimeMillis() - 86400000 * 1,
            updatedAt = System.currentTimeMillis() - 3600000 * 5
        ),
        EscrowContract(
            id = "ESC-2026-883",
            tradeItemId = "TJ-ZM-003",
            itemTitle = "Granular Urea 46% Fertilizer (500 Bags)",
            buyerName = "Mpongwe Cooperative Farm Syndicate",
            sellerName = "Zambian Fertilizer Logistics Corp",
            amount = 460000.0,
            currency = "ZMW",
            status = EscrowStatus.INSPECTION_PASSED,
            paymentRail = PaymentRail.AIRTEL_MONEY,
            txHash = "0x12d59ca837e451b0384f981dc2b14ea98471cd82",
            trackingCode = "FRT-LUS-418",
            milestoneNotes = "ZABS and buyer agronomist certified 46% nitrogen specs. One-touch authorization pending for escrow payout release.",
            createdAt = System.currentTimeMillis() - 86400000 * 3,
            updatedAt = System.currentTimeMillis() - 3600000 * 1
        ),
        EscrowContract(
            id = "ESC-2026-884",
            tradeItemId = "TJ-ZM-006",
            itemTitle = "Bulk Sunflower Cooking Oil (10 Totes)",
            buyerName = "Lusaka Wholesale Distributors",
            sellerName = "Golden Valley Seed Pressers Ltd",
            amount = 325000.0,
            currency = "ZMW",
            status = EscrowStatus.FUNDS_RELEASED,
            paymentRail = PaymentRail.MTN_MOMO,
            txHash = "0x99a3c200547db91380ea682bf54129bc0192a774",
            trackingCode = "FRT-CHO-301",
            milestoneNotes = "Delivery completed and signed at Lusaka Heavy Industrial Depot. ZMW 325,000 released to seller MTN MoMo merchant account.",
            createdAt = System.currentTimeMillis() - 86400000 * 5,
            updatedAt = System.currentTimeMillis() - 3600000 * 20
        )
    )

    val initialShipments = listOf(
        ShipmentBooking(
            id = "SHP-CRD-101",
            trackingCode = "FRT-NDL-092",
            cargoDescription = "50 MT Grade-A Copper Cathodes",
            weightTons = 50.0,
            corridorName = "Copperbelt - Lusaka - Chirundu Corridor",
            origin = "Ndola Smelter, Copperbelt",
            destination = "Durban Port via Chirundu Border",
            currentCheckpoint = "Chirundu One-Stop Border Post (OSBP)",
            progressPercent = 0.68f,
            status = ShipmentStatus.CUSTOMS_CLEARANCE,
            driverName = "Mulenga Banda",
            vehiclePlate = "BAX 4920 ZM / ALK 891 T",
            eta = "14 Hours (Customs Fast-Track)",
            cargoTempCelsius = null,
            updatedAt = System.currentTimeMillis() - 1800000
        ),
        ShipmentBooking(
            id = "SHP-CRD-102",
            trackingCode = "FRT-MKU-104",
            cargoDescription = "100 MT White Non-GMO Maize",
            weightTons = 100.0,
            corridorName = "North-South Corridor (Central - Lusaka)",
            origin = "Mkushi Farm Silos, Central",
            destination = "National Milling Silos, Lusaka",
            currentCheckpoint = "Kabwe Weighbridge & Inspection Station",
            progressPercent = 0.42f,
            status = ShipmentStatus.IN_TRANSIT,
            driverName = "Patrick Phiri",
            vehiclePlate = "BAB 8172 ZM",
            eta = "4 Hours",
            cargoTempCelsius = 21.5f,
            updatedAt = System.currentTimeMillis() - 3600000
        ),
        ShipmentBooking(
            id = "SHP-CRD-103",
            trackingCode = "FRT-DAR-552",
            cargoDescription = "30-Ton Container Mining Spares & Lubricants",
            weightTons = 30.0,
            corridorName = "Dar es Salaam - Nakonde Hub",
            origin = "Dar es Salaam Port, Tanzania",
            destination = "Lumwana Mine, North-Western Province",
            currentCheckpoint = "Nakonde Border ASYCUDA Gate 3",
            progressPercent = 0.85f,
            status = ShipmentStatus.CLEARED_ZRA,
            driverName = "Juma Mwamburi",
            vehiclePlate = "T 392 DFX",
            eta = "9 Hours",
            cargoTempCelsius = null,
            updatedAt = System.currentTimeMillis() - 5400000
        ),
        ShipmentBooking(
            id = "SHP-CRD-104",
            trackingCode = "FRT-WLV-719",
            cargoDescription = "25kVA Solar Inverter Power Packs",
            weightTons = 14.5,
            corridorName = "Walvis Bay - Kazungula Route",
            origin = "Walvis Bay Dry Port, Namibia",
            destination = "Livingstone Logistics Hub",
            currentCheckpoint = "Kazungula One-Stop Border Bridge",
            progressPercent = 0.55f,
            status = ShipmentStatus.IN_TRANSIT,
            driverName = "Gideon Chilufya",
            vehiclePlate = "N 8192 WB",
            eta = "6 Hours",
            cargoTempCelsius = 24.0f,
            updatedAt = System.currentTimeMillis() - 7200000
        )
    )

    val initialRfqs = listOf(
        RfqItem(
            id = "RFQ-2026-041",
            buyerCompany = "Zambia Breweries Procurement Unit",
            requestedItem = "High-Quality Malting Sorghum / Barley (500 MT)",
            category = TradeCategory.AGRICULTURE,
            targetQuantity = "500 Metric Tons",
            budgetPerUnit = "ZMW 8,200 / MT",
            deliveryLocation = "Lusaka Industrial Brewery Plant",
            deadlineDate = "30 Sept 2026",
            bidCount = 7,
            status = "BIDDING_ACTIVE"
        ),
        RfqItem(
            id = "RFQ-2026-042",
            buyerCompany = "Konkola Copper Mines Subcontractor Group",
            requestedItem = "Certified Industrial Safety Boots (S3 Spec) & Hardhats",
            category = TradeCategory.INDUSTRIAL_EQUIPMENT,
            targetQuantity = "1,200 Pairs + Helmets",
            budgetPerUnit = "ZMW 650 / Set",
            deliveryLocation = "Chililabombwe Warehouse",
            deadlineDate = "25 Sept 2026",
            bidCount = 12,
            status = "BIDDING_ACTIVE"
        ),
        RfqItem(
            id = "RFQ-2026-043",
            buyerCompany = "Kafue River Poultry Farming Consortium",
            requestedItem = "Soya Bean Cake (Crude Protein min 44%)",
            category = TradeCategory.AGRICULTURE,
            targetQuantity = "250 MT Bulk",
            budgetPerUnit = "USD 540 / MT",
            deliveryLocation = "Kafue Processing Center",
            deadlineDate = "05 Oct 2026",
            bidCount = 4,
            status = "OPEN"
        )
    )

    val initialTraders = listOf(
        TraderProfile(
            id = "TRD-001",
            companyName = "Mopani Copperbelt Metals Ltd",
            pacraRegNo = "PACRA-120049281-CO",
            zraTpin = "TPIN-1004829103",
            category = "Mining & Metallurgical Smelting",
            city = "Ndola / Kitwe",
            tier = "GOLD_SUPPLIER",
            rating = 4.95f,
            totalTrades = 318,
            contactPhone = "+260 212 654 300",
            contactEmail = "trades@mopanicm.zm",
            complianceScore = 99
        ),
        TraderProfile(
            id = "TRD-002",
            companyName = "Mkushi Agro Grain Growers Union",
            pacraRegNo = "PACRA-110938210-AG",
            zraTpin = "TPIN-1002938190",
            category = "Commercial Agriculture & Silos",
            city = "Mkushi, Central Province",
            tier = "GOLD_SUPPLIER",
            rating = 4.88f,
            totalTrades = 542,
            contactPhone = "+260 977 410 829",
            contactEmail = "agri-exchange@mkushi-union.zm",
            complianceScore = 98
        ),
        TraderProfile(
            id = "TRD-003",
            companyName = "Zambian Fertilizer Logistics Corp",
            pacraRegNo = "PACRA-109382019-CO",
            zraTpin = "TPIN-1008472910",
            category = "Agro-Chemicals & Plant Nutrition",
            city = "Lusaka MFEZ",
            tier = "VERIFIED_MERCHANT",
            rating = 4.90f,
            totalTrades = 210,
            contactPhone = "+260 966 829 110",
            contactEmail = "sales@zambiafert.com",
            complianceScore = 96
        ),
        TraderProfile(
            id = "TRD-004",
            companyName = "Zambezi Freight & Corridor Logistics",
            pacraRegNo = "PACRA-130492817-TR",
            zraTpin = "TPIN-1009182374",
            category = "Cross-Border Fleet & Customs Brokerage",
            city = "Chirundu / Lusaka",
            tier = "VERIFIED_MERCHANT",
            rating = 4.84f,
            totalTrades = 189,
            contactPhone = "+260 971 304 992",
            contactEmail = "dispatch@zambezifreight.zm",
            complianceScore = 95
        )
    )

    val initialCommodityQuotes = listOf(
        CommodityQuote("COPPER-LME", "Refined Copper Cathode", 9850.0, "USD", 2.4, "MT", "Metals"),
        CommodityQuote("MAIZE-ZMW", "White Maize Grade 1", 380.0, "ZMW", 1.8, "50kg Bag", "Agriculture"),
        CommodityQuote("SOYA-USD", "Soya Beans Non-GMO", 545.0, "USD", -0.6, "MT", "Agriculture"),
        CommodityQuote("UREA-ZMW", "Urea 46% Fertilizer", 920.0, "ZMW", 0.0, "50kg Bag", "Inputs"),
        CommodityQuote("DIESEL-ZMW", "Low Sulphur Gasoil", 28.50, "ZMW", 0.9, "Litre", "Energy"),
        CommodityQuote("USD/ZMW", "Forex Bank Spot Rate", 27.85, "ZMW", -0.3, "1 USD", "Forex")
    )
}
