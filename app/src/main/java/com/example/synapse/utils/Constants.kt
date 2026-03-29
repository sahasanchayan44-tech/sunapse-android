package com.example.synapse.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import com.example.synapse.models.RankInfo

val ALL_RANKS = listOf(
    RankInfo(
        name = "Novice Initiate",
        levelRange = "1-10",
        icon = Icons.Default.Description,
        color = Color(0xFF94A3B8),
        bgColor = Color(0xFF94A3B8).copy(alpha = 0.1f),
        tier = 1,
        desc = "The first steps into the arcane archives."
    ),
    RankInfo(
        name = "Apprentice Scribe",
        levelRange = "11-20",
        icon = Icons.Default.Book,
        color = Color(0xFF34D399),
        bgColor = Color(0xFF34D399).copy(alpha = 0.1f),
        tier = 2,
        desc = "Mastering the art of transcription and focus."
    ),
    RankInfo(
        name = "Practitioner",
        levelRange = "21-30",
        icon = Icons.Default.Explore,
        color = Color(0xFF22D3EE),
        bgColor = Color(0xFF22D3EE).copy(alpha = 0.1f),
        tier = 3,
        desc = "Navigating complex knowledge paths with ease."
    ),
    RankInfo(
        name = "Adept Scholar",
        levelRange = "31-40",
        icon = Icons.Default.Bolt,
        color = Color(0xFF60A5FA),
        bgColor = Color(0xFF60A5FA).copy(alpha = 0.1f),
        tier = 4,
        desc = "A seasoned veteran of the knowledge trials."
    ),
    RankInfo(
        name = "Technomancer",
        levelRange = "41-50",
        icon = Icons.Default.Memory,
        color = Color(0xFF818CF8),
        bgColor = Color(0xFF818CF8).copy(alpha = 0.1f),
        tier = 5,
        desc = "Blending logic and intuition into mastery."
    ),
    RankInfo(
        name = "Arcane Expert",
        levelRange = "51-60",
        icon = Icons.Default.AutoFixHigh,
        color = Color(0xFFC084FC),
        bgColor = Color(0xFFC084FC).copy(alpha = 0.1f),
        tier = 6,
        desc = "The archives respond to your very presence."
    ),
    RankInfo(
        name = "Master Alchemist",
        levelRange = "61-70",
        icon = Icons.Default.Whatshot,
        color = Color(0xFFFB7185),
        bgColor = Color(0xFFFB7185).copy(alpha = 0.1f),
        tier = 7,
        desc = "Transmuting raw data into pure wisdom."
    ),
    RankInfo(
        name = "Grandmaster Sage",
        levelRange = "71-80",
        icon = Icons.Default.Shield,
        color = Color(0xFFFBBF24),
        bgColor = Color(0xFFFBBF24).copy(alpha = 0.1f),
        tier = 8,
        desc = "A pillar of knowledge for the next generation."
    ),
    RankInfo(
        name = "Sovereign Seeker",
        levelRange = "81-90",
        icon = Icons.Default.MonetizationOn,
        color = Color(0xFFF97316),
        bgColor = Color(0xFFF97316).copy(alpha = 0.1f),
        tier = 9,
        desc = "Gazing into the ultimate truths of the library."
    ),
    RankInfo(
        name = "Elite Grandmaster",
        levelRange = "91-100",
        icon = Icons.Default.EmojiEvents,
        color = Color(0xFFFACC15),
        bgColor = Color(0xFFFACC15).copy(alpha = 0.1f),
        tier = 10,
        desc = "The ultimate peak of academic transcendence."
    )
)

val LEVEL_NAMES = mapOf(
    1 to "The First Spark", 2 to "Ink Seeker", 3 to "Page Turner", 4 to "Scroll Carrier", 5 to "Archive Finder", 6 to "Dust Brusher", 7 to "Quiet Observer", 8 to "Word Weaver", 9 to "Library Scout", 10 to "Initiate's Grace",
    11 to "Scribe's Apprentice", 12 to "Draft Maker", 13 to "Parchment Handler", 14 to "Quill Sharpener", 15 to "Margin Scribbler", 16 to "Copyist", 17 to "Letter Binder", 18 to "Record Keeper", 19 to "Fact Checker", 20 to "Scribe's Dedication",
    21 to "Wayfinder", 22 to "Path Carver", 23 to "Bridge Builder", 24 to "Map Reader", 25 to "Journey Man", 26 to "Trail Blazer", 27 to "Star Navigator", 28 to "Gate Opener", 29 to "Vista Seeker", 30 to "Practitioner's Resolve",
    31 to "Thesis Writer", 32 to "Deep Researcher", 33 to "Fact Finder", 34 to "Methodologist", 35 to "Logic Weaver", 36 to "Pattern Spotter", 37 to "Theory Builder", 38 to "Data Cruncher", 39 to "Insight Giver", 40 to "Scholar's Precision",
    41 to "Circuit Breaker", 42 to "Code Weaver", 43 to "Logic Wizard", 44 to "Pulse Finder", 45 to "Matrix Walker", 46 to "System Architect", 47 to "Flow Master", 48 to "Core Striker", 49 to "Nerve Center", 50 to "Technomancer's Pride",
    51 to "Mystery Solver", 52 to "Enigma Unraveller", 53 to "Shadow Seer", 54 to "Hidden Hand", 55 to "Veil Lifter", 56 to "Truth Bearer", 57 to "Ancient Speaker", 58 to "Secret Guard", 59 to "Riddle Master", 60 to "Expert's Clarity",
    61 to "Formula Mixer", 62 to "Base Transmuter", 63 to "Essence Distiller", 64 to "Catalyst Maker", 65 to "Lead Melter", 66 to "Gold Finder", 67 to "Vial Keeper", 68 to "Aura Binder", 69 to "Pure Alchemist", 70 to "Master's Touch",
    71 to "Wise One", 72 to "Council Member", 73 to "Truth Pillar", 74 to "Knowledge Beacon", 75 to "Elder Scribe", 76 to "Archive Warden", 77 to "Library Guardian", 78 to "Chronicle Keeper", 79 to "High Sage", 80 to "Sage's Wisdom",
    81 to "Void Walker", 82 to "Ether Diver", 83 to "Truth Diver", 84 to "Beyond Seer", 85 to "Cosmic Reader", 86 to "Universe Mapper", 87 to "Dimensional Key", 88 to "Reality Weaver", 89 to "Infinite Seeker", 90 to "Sovereign's Sight",
    91 to "Zenith Master", 92 to "Peak Dweller", 93 to "Crown Bearer", 94 to "Ultimate Authority", 95 to "Archives Heart", 96 to "Living History", 97 to "Knowledge God", 98 to "Eternal Scholar", 99 to "The Architect", 100 to "Ascended One"
)
