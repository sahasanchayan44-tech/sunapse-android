import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'dart:math' as math;

class Flashcard {
  final String question;
  final String answer;

  Flashcard({required this.question, required this.answer});
}

class FlashcardsScreen extends StatefulWidget {
  const FlashcardsScreen({super.key});

  @override
  State<FlashcardsScreen> createState() => _FlashcardsScreenState();
}

class _FlashcardsScreenState extends State<FlashcardsScreen> with SingleTickerProviderStateMixin {
  final List<Flashcard> _flashcards = [
    Flashcard(question: "What is a React Hook?", answer: "A special function that lets you 'hook into' React state and lifecycle features from function components."),
    Flashcard(question: "Difference between state and props?", answer: "Props are passed to components (like function parameters), while state is managed within the component (like variables declared within a function)."),
    Flashcard(question: "What is JSX?", answer: "A syntax extension for JavaScript that looks like HTML and describes what the UI should look like."),
    Flashcard(question: "What does useEffect do?", answer: "It allows you to perform side effects in functional components, such as data fetching, subscriptions, or manually changing the DOM."),
    Flashcard(question: "What is the Virtual DOM?", answer: "A lightweight representation of the actual DOM in memory, used by React to efficiently update the UI."),
  ];

  int _currentIndex = 0;
  bool _isFlipped = false;
  late AnimationController _flipController;
  late Animation<double> _flipAnimation;

  @override
  void initState() {
    super.initState();
    _flipController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    _flipAnimation = Tween<double>(begin: 0, end: 1).animate(CurvedAnimation(
      parent: _flipController,
      curve: Curves.easeInOut,
    ));
  }

  @override
  void dispose() {
    _flipController.dispose();
    super.dispose();
  }

  void _toggleFlip() {
    if (_isFlipped) {
      _flipController.reverse();
    } else {
      _flipController.forward();
    }
    setState(() {
      _isFlipped = !_isFlipped;
    });
  }

  void _nextCard() {
    if (_currentIndex < _flashcards.length - 1) {
      setState(() {
        _currentIndex++;
        if (_isFlipped) {
          _isFlipped = false;
          _flipController.reset();
        }
      });
    }
  }

  void _prevCard() {
    if (_currentIndex > 0) {
      setState(() {
        _currentIndex--;
        if (_isFlipped) {
          _isFlipped = false;
          _flipController.reset();
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Arcane Flashcards', style: TextStyle(fontWeight: FontWeight.black, fontStyle: FontStyle.italic)),
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            const Text(
              'Recite the knowledge stored in the Zenith Archives.',
              style: TextStyle(color: Colors.grey, fontStyle: FontStyle.italic),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 40),
            Expanded(
              child: Center(
                child: GestureDetector(
                  onTap: _toggleFlip,
                  child: AnimatedBuilder(
                    animation: _flipAnimation,
                    builder: (context, child) {
                      final angle = _flipAnimation.value * math.pi;
                      return Transform(
                        transform: Matrix4.identity()
                          ..setEntry(3, 2, 0.001)
                          ..rotateY(angle),
                        alignment: Alignment.center,
                        child: angle < math.pi / 2
                            ? _buildCardSide(
                                title: 'INCANTATION',
                                content: _flashcards[_currentIndex].question,
                                icon: LucideIcons.graduationCap,
                                color: Theme.of(context).primaryColor,
                              )
                            : Transform(
                                transform: Matrix4.identity()..rotateY(math.pi),
                                alignment: Alignment.center,
                                child: _buildCardSide(
                                  title: 'REVEALED WISDOM',
                                  content: _flashcards[_currentIndex].answer,
                                  icon: LucideIcons.brainCircuit,
                                  color: Colors.amber,
                                  isBack: true,
                                ),
                              ),
                      );
                    },
                  ),
                ),
              ),
            ),
            const SizedBox(height: 40),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                IconButton.filledTonal(
                  onPressed: _currentIndex > 0 ? _prevCard : null,
                  icon: const Icon(LucideIcons.chevronLeft, size: 32),
                  padding: const EdgeInsets.all(16),
                ),
                const SizedBox(width: 24),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                  decoration: BoxDecoration(
                    color: Theme.of(context).cardColor,
                    borderRadius: BorderRadius.circular(32),
                    border: Border.all(color: Colors.white10),
                  ),
                  child: Text(
                    '${_currentIndex + 1} / ${_flashcards.length}',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.black,
                      fontStyle: FontStyle.italic,
                      color: Theme.of(context).primaryColor,
                    ),
                  ),
                ),
                const SizedBox(width: 24),
                IconButton.filledTonal(
                  onPressed: _currentIndex < _flashcards.length - 1 ? _nextCard : null,
                  icon: const Icon(LucideIcons.chevronRight, size: 32),
                  padding: const EdgeInsets.all(16),
                ),
              ],
            ),
            const SizedBox(height: 40),
            _buildRecallOptions(),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }

  Widget _buildCardSide({
    required String title,
    required String content,
    required IconData icon,
    required Color color,
    bool isBack = false,
  }) {
    return Container(
      width: double.infinity,
      height: 350,
      decoration: BoxDecoration(
        color: isBack ? color : Theme.of(context).cardColor,
        borderRadius: BorderRadius.circular(40),
        border: Border.all(color: color.withOpacity(0.4), width: 4),
        boxShadow: [
          BoxShadow(
            color: color.withOpacity(0.2),
            blurRadius: 20,
            spreadRadius: 5,
          ),
        ],
      ),
      padding: const EdgeInsets.all(32),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Align(
            alignment: Alignment.topLeft,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: isBack ? Colors.black26 : color.withOpacity(0.3)),
              ),
              child: Text(
                title,
                style: TextStyle(
                  fontSize: 10,
                  fontWeight: FontWeight.black,
                  color: isBack ? Colors.black87 : color,
                  letterSpacing: 1.2,
                ),
              ),
            ),
          ),
          const Spacer(),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: (isBack ? Colors.black12 : color.withOpacity(0.1)),
              borderRadius: BorderRadius.circular(20),
            ),
            child: Icon(icon, size: 48, color: isBack ? Colors.black87 : color),
          ),
          const SizedBox(height: 24),
          Text(
            content,
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 20,
              fontWeight: isBack ? FontWeight.bold : FontWeight.black,
              fontStyle: FontStyle.italic,
              color: isBack ? Colors.black : Colors.white,
            ),
          ),
          const Spacer(),
          Text(
            isBack ? 'SEAL KNOWLEDGE' : 'REVEAL TRUTH',
            style: TextStyle(
              fontSize: 10,
              fontWeight: FontWeight.black,
              letterSpacing: 2,
              color: isBack ? Colors.black54 : Colors.white38,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRecallOptions() {
    return Row(
      children: [
        Expanded(child: _buildRecallButton('Weak', 'Needs Review', Colors.red)),
        const SizedBox(width: 12),
        Expanded(child: _buildRecallButton('Moderate', 'Hard', Colors.orange)),
        const SizedBox(width: 12),
        Expanded(child: _buildRecallButton('Strong', 'Mastered', Colors.green)),
      ],
    );
  }

  Widget _buildRecallButton(String top, String bottom, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 16),
      decoration: BoxDecoration(
        color: Theme.of(context).cardColor,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.white.withOpacity(0.05)),
      ),
      child: Column(
        children: [
          Text('Recall: $top', style: const TextStyle(fontSize: 8, color: Colors.grey, fontWeight: FontWeight.bold)),
          const SizedBox(height: 4),
          Text(bottom, style: TextStyle(fontSize: 14, fontWeight: FontWeight.black, fontStyle: FontStyle.italic, color: color)),
        ],
      ),
    );
  }
}
