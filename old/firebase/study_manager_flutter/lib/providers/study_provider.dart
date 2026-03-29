import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/study_stats.dart';

final userStatsProvider = Provider<UserStats>((ref) {
  return UserStats.initial();
});

final dailyQuestsProvider = Provider<List<DailyQuest>>((ref) {
  return [
    DailyQuest(id: 'q1', task: 'Write 50 Study Notes', current: 32, target: 50, reward: 25),
    DailyQuest(id: 'q2', task: 'Complete 2 Knowledge Trials', current: 1, target: 2, reward: 50),
    DailyQuest(id: 'q3', task: 'Search the Document Library', current: 0, target: 1, reward: 10),
  ];
});
