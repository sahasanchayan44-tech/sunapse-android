import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';

class RankInfo {
  final String name;
  final String levelRange;
  final IconData icon;
  final Color color;
  final Color bgColor;
  final int tier;
  final String desc;

  RankInfo({
    required this.name,
    required this.levelRange,
    required this.icon,
    required this.color,
    required this.bgColor,
    required this.tier,
    required this.desc,
  });
}

final List<RankInfo> ALL_RANKS = [
  RankInfo(
      name: "Novice Initiate",
      levelRange: "1-10",
      icon: LucideIcons.scroll,
      color: Colors.slate.shade400,
      bgColor: Colors.slate.shade400.withOpacity(0.1),
      tier: 1,
      desc: "The first steps into the arcane archives."),
  RankInfo(
      name: "Apprentice Scribe",
      levelRange: "11-20",
      icon: LucideIcons.bookOpen,
      color: Colors.emerald.shade400,
      bgColor: Colors.emerald.shade400.withOpacity(0.1),
      tier: 2,
      desc: "Mastering the art of transcription and focus."),
  RankInfo(
      name: "Practitioner",
      levelRange: "21-30",
      icon: LucideIcons.compass,
      color: Colors.cyan.shade400,
      bgColor: Colors.cyan.shade400.withOpacity(0.1),
      tier: 3,
      desc: "Navigating complex knowledge paths with ease."),
  RankInfo(
      name: "Adept Scholar",
      levelRange: "31-40",
      icon: LucideIcons.zap,
      color: Colors.blue.shade400,
      bgColor: Colors.blue.shade400.withOpacity(0.1),
      tier: 4,
      desc: "A seasoned veteran of the knowledge trials."),
  RankInfo(
      name: "Technomancer",
      levelRange: "41-50",
      icon: LucideIcons.brainCircuit,
      color: Colors.indigo.shade400,
      bgColor: Colors.indigo.shade400.withOpacity(0.1),
      tier: 5,
      desc: "Blending logic and intuition into mastery."),
  RankInfo(
      name: "Arcane Expert",
      levelRange: "51-60",
      icon: LucideIcons.wand2,
      color: Colors.purple.shade400,
      bgColor: Colors.purple.shade400.withOpacity(0.1),
      tier: 6,
      desc: "The archives respond to your very presence."),
  RankInfo(
      name: "Master Alchemist",
      levelRange: "61-70",
      icon: LucideIcons.flame,
      color: Colors.rose.shade400,
      bgColor: Colors.rose.shade400.withOpacity(0.1),
      tier: 7,
      desc: "Transmuting raw data into pure wisdom."),
  RankInfo(
      name: "Grandmaster Sage",
      levelRange: "71-80",
      icon: LucideIcons.shield,
      color: Colors.amber.shade400,
      bgColor: Colors.amber.shade400.withOpacity(0.1),
      tier: 8,
      desc: "A pillar of knowledge for the next generation."),
  RankInfo(
      name: "Sovereign Seeker",
      levelRange: "81-90",
      icon: LucideIcons.circleDollarSign,
      color: Colors.orange.shade500,
      bgColor: Colors.orange.shade500.withOpacity(0.1),
      tier: 9,
      desc: "Gazing into the ultimate truths of the library."),
  RankInfo(
      name: "Elite Grandmaster",
      levelRange: "91-100",
      icon: LucideIcons.crown,
      color: Colors.yellow.shade400,
      bgColor: Colors.yellow.shade400.withOpacity(0.1),
      tier: 10,
      desc: "The ultimate peak of academic transcendence."),
];

final Map<int, String> LEVEL_NAMES = {
  1: "The First Spark", 2: "Ink Seeker", 3: "Page Turner", 4: "Scroll Carrier", 5: "Archive Finder", 6: "Dust Brusher", 7: "Quiet Observer", 8: "Word Weaver", 9: "Library Scout", 10: "Initiate's Grace",
  11: "Scribe's Apprentice", 12: "Draft Maker", 13: "Parchment Handler", 14: "Quill Sharpener", 15: "Margin Scribbler", 16: "Copyist", 17: "Letter Binder", 18: "Record Keeper", 19: "Fact Checker", 20: "Scribe's Dedication",
  21: "Wayfinder", 22: "Path Carver", 23: "Bridge Builder", 24: "Map Reader", 25: "Journey Man", 26: "Trail Blazer", 27: "Star Navigator", 28: "Gate Opener", 29: "Vista Seeker", 30: "Practitioner's Resolve",
  31: "Thesis Writer", 32: "Deep Researcher", 33: "Fact Finder", 34: "Methodologist", 35: "Logic Weaver", 36: "Pattern Spotter", 37: "Theory Builder", 38: "Data Cruncher", 39: "Insight Giver", 40: "Scholar's Precision",
  41: "Circuit Breaker", 42: "Code Weaver", 43: "Logic Wizard", 44: "Pulse Finder", 45: "Matrix Walker", 46: "System Architect", 47: "Flow Master", 48: "Core Striker", 49: "Nerve Center", 50: "Technomancer's Pride",
  51: "Mystery Solver", 52: "Enigma Unraveller", 53: "Shadow Seer", 54: "Hidden Hand", 55: "Veil Lifter", 56: "Truth Bearer", 57: "Ancient Speaker", 58: "Secret Guard", 59: "Riddle Master", 60: "Expert's Clarity",
  61: "Formula Mixer", 62: "Base Transmuter", 63: "Essence Distiller", 64: "Catalyst Maker", 65: "Lead Melter", 66: "Gold Finder", 67: "Vial Keeper", 68: "Aura Binder", 69: "Pure Alchemist", 70: "Master's Touch",
  71: "Wise One", 72: "Council Member", 73: "Truth Pillar", 74: "Knowledge Beacon", 75: "Elder Scribe", 76: "Archive Warden", 77: "Library Guardian", 78: "Chronicle Keeper", 79: "High Sage", 80: "Sage's Wisdom",
  81: "Void Walker", 82: "Ether Diver", 83: "Truth Diver", 84: "Beyond Seer", 85: "Cosmic Reader", 86: "Universe Mapper", 87: "Dimensional Key", 88: "Reality Weaver", 89: "Infinite Seeker", 90: "Sovereign's Sight",
  91: "Zenith Master", 92: "Peak Dweller", 93: "Crown Bearer", 94: "Ultimate Authority", 95: "Archives Heart", 96: "Living History", 97: "Knowledge God", 98: "Eternal Scholar", 99: "The Architect", 100: "Ascended One"
};
