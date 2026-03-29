class UserStats {
  final int xp;
  final int streak;
  final int coins;
  final int level;
  final double levelProgress;
  final int xpToNextLevel;

  UserStats({
    required this.xp,
    required this.streak,
    required this.coins,
    required this.level,
    required this.levelProgress,
    required this.xpToNextLevel,
  });

  factory UserStats.initial() {
    return UserStats(
      xp: 1250,
      streak: 5,
      coins: 450,
      level: 2,
      levelProgress: 25.0,
      xpToNextLevel: 750,
    );
  }
}

class DailyQuest {
  final String id;
  final String task;
  final int current;
  final int target;
  final int reward;
  final bool isClaimed;

  DailyQuest({
    required this.id,
    required this.task,
    required this.current,
    required this.target,
    required this.reward,
    this.isClaimed = false,
  });
}

class RankInfo {
  final String name;
  final String levelRange;
  final dynamic icon; // We'll use IconData
  final String color;
  final String bg;
  final int tier;
  final String desc;

  RankInfo({
    required this.name,
    required this.levelRange,
    required this.icon,
    required this.color,
    required this.bg,
    required this.tier,
    required this.desc,
  });
}
