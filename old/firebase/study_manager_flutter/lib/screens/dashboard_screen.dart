import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'dart:math' as math;
import '../providers/study_provider.dart';
import '../constants.dart';
import '../models/study_stats.dart';

class DashboardScreen extends ConsumerStatefulWidget {
  const DashboardScreen({super.key});

  @override
  ConsumerState<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends ConsumerState<DashboardScreen> with TickerProviderStateMixin {
  late TabController _tabController;
  late AnimationController _orbController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    _orbController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    )..forward();
  }

  @override
  void dispose() {
    _tabController.dispose();
    _orbController.dispose();
    super.dispose();
  }

  RankInfo getRankInfo(int level) {
    int tier = ((level - 1) ~/ 10).clamp(0, 9);
    return ALL_RANKS[tier];
  }

  @override
  Widget build(BuildContext context) {
    final stats = ref.watch(userStatsProvider);
    final rank = getRankInfo(stats.level);
    final levelName = LEVEL_NAMES[stats.level] ?? "Mysterious Seeker";
    final rankLevelProgress = (stats.level % 10 == 0 ? 10 : stats.level % 10) / 10.0;

    return Scaffold(
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 40),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 20),
            _buildHeroSection(stats, rank, levelName),
            const SizedBox(height: 32),
            _buildTabs(),
            const SizedBox(height: 24),
            SizedBox(
              height: 600, // Fixed height for tab content for now
              child: TabBarView(
                controller: _tabController,
                children: [
                  _buildOverviewTab(stats, rank, rankLevelProgress),
                  _buildRankTab(stats, rank),
                  _buildQuestsTab(),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeroSection(UserStats stats, RankInfo rank, String levelName) {
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).cardColor,
        borderRadius: BorderRadius.circular(32),
        border: Border.all(color: Colors.white.withOpacity(0.05)),
      ),
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          // Rank Visual
          Center(
            child: Stack(
              alignment: Alignment.center,
              children: [
                // Glow
                Container(
                  width: 150,
                  height: 150,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    boxShadow: [
                      BoxShadow(
                        color: rank.color.withOpacity(0.2),
                        blurRadius: 40,
                        spreadRadius: 10,
                      ),
                    ],
                  ),
                ),
                // Icon
                Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(rank.icon, size: 64, color: rank.color),
                    const SizedBox(height: 8),
                    Text(
                      'LVL ${stats.level}',
                      style: const TextStyle(
                        fontSize: 32,
                        fontWeight: FontWeight.black,
                        fontStyle: FontStyle.italic,
                      ),
                    ),
                  ],
                ),
                // Badge
                Positioned(
                  bottom: -10,
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    decoration: BoxDecoration(
                      color: const Color(0xFF0f172a).withOpacity(0.8),
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(color: rank.color.withOpacity(0.5)),
                    ),
                    child: Text(
                      levelName.toUpperCase(),
                      style: TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.black,
                        letterSpacing: 1.2,
                        color: rank.color,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 40),
          // Stats Row
          Row(
            children: [
              Expanded(child: _buildStatCard('STUDY STREAK', '${stats.streak}', 'Days', LucideIcons.flame, Colors.orange)),
              const SizedBox(width: 16),
              Expanded(child: _buildMomentumCard()),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStatCard(String label, String value, String unit, IconData icon, Color color) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.05),
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.white.withOpacity(0.05)),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: color.withOpacity(0.1),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(icon, color: color, size: 24),
          ),
          const SizedBox(width: 12),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label, style: TextStyle(fontSize: 8, fontWeight: FontWeight.black, color: color.withOpacity(0.7), letterSpacing: 1)),
              Row(
                crossAxisAlignment: CrossAxisAlignment.baseline,
                textBaseline: TextBaseline.alphabetic,
                children: [
                  Text(value, style: const TextStyle(fontSize: 24, fontWeight: FontWeight.black, fontStyle: FontStyle.italic)),
                  const SizedBox(width: 4),
                  Text(unit, style: TextStyle(fontSize: 10, color: Colors.grey.shade500)),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildMomentumCard() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.05),
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.white.withOpacity(0.05)),
      ),
      child: Row(
        children: [
          SizedBox(
            width: 40,
            height: 40,
            child: AnimatedBuilder(
              animation: _orbController,
              builder: (context, child) {
                return CircularProgressIndicator(
                  value: 0.75 * _orbController.value,
                  strokeWidth: 6,
                  backgroundColor: Colors.white.withOpacity(0.05),
                  color: Theme.of(context).primaryColor,
                  strokeCap: StrokeCap.round,
                );
              },
            ),
          ),
          const SizedBox(width: 12),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('FOCUS MOMENTUM', style: TextStyle(fontSize: 8, fontWeight: FontWeight.black, color: Theme.of(context).primaryColor.withOpacity(0.7), letterSpacing: 1)),
              const Text('AURA ACTIVE', style: TextStyle(fontSize: 12, fontWeight: FontWeight.black, fontStyle: FontStyle.italic)),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildTabs() {
    return Container(
      height: 64,
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: Theme.of(context).cardColor,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: Colors.white.withOpacity(0.05)),
      ),
      child: TabBar(
        controller: _tabController,
        indicator: BoxDecoration(
          color: Theme.of(context).primaryColor,
          borderRadius: BorderRadius.circular(12),
        ),
        indicatorSize: TabBarIndicatorSize.tab,
        dividerColor: Colors.transparent,
        labelColor: Colors.white,
        unselectedLabelColor: Colors.grey,
        tabs: const [
          Tab(child: Row(mainAxisAlignment: MainAxisAlignment.center, children: [Icon(LucideIcons.layoutDashboard, size: 16), SizedBox(width: 8), Text('Overview', style: TextStyle(fontWeight: FontWeight.bold, fontStyle: FontStyle.italic))])),
          Tab(child: Row(mainAxisAlignment: MainAxisAlignment.center, children: [Icon(LucideIcons.medal, size: 16), SizedBox(width: 8), Text('Rank', style: TextStyle(fontWeight: FontWeight.bold, fontStyle: FontStyle.italic))])),
          Tab(child: Row(mainAxisAlignment: MainAxisAlignment.center, children: [Icon(LucideIcons.sword, size: 16), SizedBox(width: 8), Text('Quests', style: TextStyle(fontWeight: FontWeight.bold, fontStyle: FontStyle.italic))])),
        ],
      ),
    );
  }

  Widget _buildOverviewTab(UserStats stats, RankInfo rank, double progress) {
    return Padding(
      padding: const EdgeInsets.only(top: 20),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [Theme.of(context).primaryColor.withOpacity(0.2), Theme.of(context).scaffoldBackgroundColor, Colors.purple.withOpacity(0.1)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(32),
              border: Border.all(color: Colors.white.withOpacity(0.05)),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('RANK TIER ${rank.tier}/10', style: TextStyle(fontSize: 10, fontWeight: FontWeight.black, color: Theme.of(context).primaryColor)),
                        Text(rank.name, style: const TextStyle(fontSize: 24, fontWeight: FontWeight.black, fontStyle: FontStyle.italic)),
                      ],
                    ),
                    Text('Level ${stats.level} / 100', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.grey.shade400, fontStyle: FontStyle.italic)),
                  ],
                ),
                const SizedBox(height: 20),
                Text('RANK TIER PROGRESS ${ (progress * 100).toInt()}%', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Colors.grey.shade600)),
                const SizedBox(height: 8),
                ClipRRect(
                  borderRadius: BorderRadius.circular(8),
                  child: LinearProgressIndicator(
                    value: progress,
                    minHeight: 12,
                    backgroundColor: Colors.white.withOpacity(0.05),
                    color: Theme.of(context).primaryColor,
                  ),
                ),
                const SizedBox(height: 8),
                Center(
                  child: Text(
                    '${stats.xpToNextLevel} XP until Level ${stats.level + 1}',
                    style: TextStyle(fontSize: 10, color: Colors.grey.shade600, fontStyle: FontStyle.italic),
                  ),
                ),
                const SizedBox(height: 24),
                SizedBox(
                  width: double.infinity,
                  height: 54,
                  child: ElevatedButton(
                    onPressed: () {},
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Theme.of(context).primaryColor,
                      foregroundColor: Colors.white,
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                    ),
                    child: const Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text('Boost', style: TextStyle(fontSize: 18, fontWeight: FontWeight.black, fontStyle: FontStyle.italic)),
                        SizedBox(width: 8),
                        Icon(LucideIcons.chevronRight),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRankTab(UserStats stats, RankInfo currentRank) {
    return ListView.builder(
      padding: const EdgeInsets.only(top: 20),
      itemCount: ALL_RANKS.length,
      itemBuilder: (context, index) {
        final rank = ALL_RANKS[index];
        final isUnlocked = stats.level >= (index * 10 + 1);
        final isCurrent = currentRank.tier == rank.tier;

        return Opacity(
          opacity: isUnlocked ? 1.0 : 0.4,
          child: Padding(
            padding: const EdgeInsets.bottom(24.0),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(8),
                          border: Border.all(color: isCurrent ? Colors.amber : Colors.white10),
                        ),
                        child: Text('LEVELS ${rank.levelRange}', style: TextStyle(fontSize: 8, fontWeight: FontWeight.black, color: isCurrent ? Colors.amber : Colors.white54)),
                      ),
                      Text(rank.name, style: TextStyle(fontSize: 18, fontWeight: FontWeight.black, fontStyle: FontStyle.italic, color: isCurrent ? Colors.amber : Colors.white)),
                      Text(rank.desc, textAlign: TextAlign.right, style: TextStyle(fontSize: 12, color: Colors.grey.shade400, fontStyle: FontStyle.italic)),
                    ],
                  ),
                ),
                const SizedBox(width: 20),
                Stack(
                  alignment: Alignment.bottomRight,
                  children: [
                    Container(
                      width: 64,
                      height: 64,
                      decoration: BoxDecoration(
                        color: Theme.of(context).cardColor,
                        borderRadius: BorderRadius.circular(16),
                        border: Border.all(color: isCurrent ? Colors.amber : Colors.white.withOpacity(0.05)),
                        boxShadow: isCurrent ? [BoxShadow(color: Colors.amber.withOpacity(0.2), blurRadius: 10)] : null,
                      ),
                      child: Icon(isUnlocked ? rank.icon : LucideIcons.lock, color: isCurrent ? Colors.amber : (isUnlocked ? Theme.of(context).primaryColor : Colors.grey), size: 32),
                    ),
                    Container(
                      width: 20,
                      height: 20,
                      decoration: BoxDecoration(
                        color: Theme.of(context).scaffoldBackgroundColor,
                        borderRadius: BorderRadius.circular(4),
                        border: Border.all(color: Colors.white10),
                      ),
                      alignment: Alignment.center,
                      child: Text('${rank.tier}', style: const TextStyle(fontSize: 10, fontWeight: FontWeight.black)),
                    ),
                  ],
                ),
                const Expanded(child: SizedBox()),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildQuestsTab() {
    final quests = ref.watch(dailyQuestsProvider);
    final stats = ref.watch(userStatsProvider);

    return SingleChildScrollView(
      padding: const EdgeInsets.only(top: 20),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Theme.of(context).cardColor,
              borderRadius: BorderRadius.circular(32),
              border: Border.all(color: Colors.white.withOpacity(0.05)),
            ),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Row(
                      children: [
                        Icon(LucideIcons.target, color: Colors.amber, size: 24),
                        SizedBox(width: 8),
                        Text('Daily Quest Log', style: TextStyle(fontSize: 20, fontWeight: FontWeight.black, fontStyle: FontStyle.italic)),
                      ],
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: Colors.amber),
                      ),
                      child: const Text('14H REMAINING', style: TextStyle(fontSize: 8, fontWeight: FontWeight.black, color: Colors.amber)),
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                ...quests.map((quest) => _buildQuestItem(quest)).toList(),
              ],
            ),
          ),
          const SizedBox(height: 24),
          Row(
            children: [
              Expanded(
                child: Container(
                  padding: const EdgeInsets.all(24),
                  decoration: BoxDecoration(
                    color: Colors.amber,
                    borderRadius: BorderRadius.circular(32),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('SyncCoins', style: TextStyle(fontSize: 10, fontWeight: FontWeight.black, color: Colors.black54)),
                      Text('${stats.coins}', style: const TextStyle(fontSize: 32, fontWeight: FontWeight.black, fontStyle: FontStyle.italic, color: Colors.black)),
                      const SizedBox(height: 12),
                      ElevatedButton(
                        onPressed: () {},
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.black,
                          foregroundColor: Colors.white,
                          minimumSize: const Size(double.infinity, 40),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                        ),
                        child: const Text('Redeem', style: TextStyle(fontWeight: FontWeight.black, fontStyle: FontStyle.italic)),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Container(
                  padding: const EdgeInsets.all(24),
                  decoration: BoxDecoration(
                    color: Theme.of(context).cardColor,
                    borderRadius: BorderRadius.circular(32),
                    border: Border.all(color: Colors.white.withOpacity(0.05)),
                  ),
                  child: Column(
                    children: [
                      Icon(LucideIcons.flame, color: Colors.orange, size: 32),
                      const Text('Active Streak', style: TextStyle(fontSize: 14, fontWeight: FontWeight.black, fontStyle: FontStyle.italic)),
                      const Text('Weekly consistent!', style: TextStyle(fontSize: 10, color: Colors.grey)),
                      const SizedBox(height: 8),
                      Text('${stats.streak} Days', style: const TextStyle(fontSize: 24, fontWeight: FontWeight.black, color: Colors.orange)),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildQuestItem(DailyQuest quest) {
    final isCompleted = quest.current >= quest.target;
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.05),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.white.withOpacity(0.05)),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: (isCompleted ? Colors.green : Theme.of(context).primaryColor).withOpacity(0.1),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(isCompleted ? LucideIcons.shieldCheck : LucideIcons.target, color: isCompleted ? Colors.green : Theme.of(context).primaryColor, size: 20),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(quest.task, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    Row(
                      children: [
                        const Icon(LucideIcons.star, color: Colors.orange, size: 12),
                        const SizedBox(width: 4),
                        Text('+${quest.reward}', style: const TextStyle(fontSize: 10, fontWeight: FontWeight.black, color: Colors.orange)),
                      ],
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Expanded(
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(4),
                        child: LinearProgressIndicator(
                          value: (quest.current / quest.target).clamp(0.0, 1.0),
                          minHeight: 6,
                          backgroundColor: Colors.white.withOpacity(0.05),
                          color: isCompleted ? Colors.green : Theme.of(context).primaryColor,
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Text('${quest.current}/${quest.target}', style: const TextStyle(fontSize: 10, fontWeight: FontWeight.black, color: Colors.grey)),
                  ],
                ),
              ],
            ),
          ),
          const SizedBox(width: 12),
          ElevatedButton(
            onPressed: isCompleted ? () {} : null,
            style: ElevatedButton.styleFrom(
              backgroundColor: Theme.of(context).primaryColor,
              disabledBackgroundColor: Colors.white.withOpacity(0.05),
              minimumSize: const Size(64, 32),
              padding: const EdgeInsets.symmetric(horizontal: 12),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            ),
            child: Text(isCompleted ? 'CLAIM' : 'LOCKED', style: const TextStyle(fontSize: 8, fontWeight: FontWeight.black)),
          ),
        ],
      ),
    );
  }
}
