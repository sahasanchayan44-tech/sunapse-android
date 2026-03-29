
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import 'screens/main_screen.dart';


void main() {
  runApp(
    const ProviderScope(
      child: StudyManagerApp(),
    ),
  );
}

class StudyManagerApp extends StatelessWidget {
  const StudyManagerApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Study Manager',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        brightness: Brightness.dark,
        primaryColor: const Color(0xFF6366f1),
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF6366f1),
          brightness: Brightness.dark,
          primary: const Color(0xFF6366f1),
          secondary: const Color(0xFFf59e0b), // accent color from web
          surface: const Color(0xFF1e293b),
        ),
        scaffoldBackgroundColor: const Color(0xFF020617),
        cardColor: const Color(0xFF1e293b).withOpacity(0.4),
        textTheme: GoogleFonts.interTextTheme(ThemeData.dark().textTheme),
        useMaterial3: true,
      ),
      home: const MainScreen(),
    );
  }
}
