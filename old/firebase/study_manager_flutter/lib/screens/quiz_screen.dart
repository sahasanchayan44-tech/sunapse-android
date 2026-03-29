import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';

class QuizScreen extends StatefulWidget {
  const QuizScreen({super.key});

  @override
  State<QuizScreen> createState() => _QuizScreenState();
}

class _QuizScreenState extends State<QuizScreen> {
  String _state = 'idle'; // idle, loading, active, results
  int _score = 0;
  final Map<int, String> _answers = {};

  final List<Map<String, dynamic>> _mockQuestions = [
    {
      'question': 'What is the primary purpose of React Hooks?',
      'options': [
        'To manage state in functional components',
        'To replace class components entirely',
        'To handle direct DOM manipulation',
        'To improve CSS performance'
      ],
      'answer': 'To manage state in functional components'
    },
    {
      'question': 'Which hook is used for side effects in React?',
      'options': ['useState', 'useEffect', 'useContext', 'useReducer'],
      'answer': 'useEffect'
    },
    {
      'question': 'What does JSX stand for?',
      'options': ['JavaScript XML', 'Java Syntax Extension', 'JSON Syntax XML', 'JavaScript X-platform'],
      'answer': 'JavaScript XML'
    },
  ];

  void _generateQuiz() async {
    setState(() => _state = 'loading');
    await Future.delayed(const Duration(seconds: 2));
    setState(() {
      _state = 'active';
      _answers.clear();
      _score = 0;
    });
  }

  void _submitQuiz() {
    int correct = 0;
    for (int i = 0; i < _mockQuestions.length; i++) {
      if (_answers[i] == _mockQuestions[i]['answer']) {
        correct++;
      }
    }
    setState(() {
      _score = correct;
      _state = 'results';
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_state == 'idle') return _buildIdleView();
    if (_state == 'loading') return _buildLoadingView();
    if (_state == 'active') return _buildActiveView();
    if (_state == 'results') return _buildResultsView();
    return const SizedBox();
  }

  Widget _buildIdleView() {
    return Scaffold(
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(40.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                padding: const EdgeInsets.all(32),
                decoration: BoxDecoration(
                  color: Theme.of(context).primaryColor.withOpacity(0.1),
                  shape: BoxShape.circle,
                ),
                child: Icon(LucideIcons.brainCircuit, size: 80, color: Theme.of(context).primaryColor),
              ),
              const SizedBox(height: 32),
              const Text(
                'AI Practice Quizzes',
                style: TextStyle(fontSize: 28, fontWeight: FontWeight.black, fontStyle: FontStyle.italic),
              ),
              const SizedBox(height: 12),
              const Text(
                'Generate custom multiple-choice quizzes from your library to test your knowledge.',
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.grey, fontSize: 16),
              ),
              const SizedBox(height: 48),
              SizedBox(
                width: double.infinity,
                height: 64,
                child: ElevatedButton(
                  onPressed: _generateQuiz,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Theme.of(context).primaryColor,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                  ),
                  child: const Text('Generate New Quiz', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildLoadingView() {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const CircularProgressIndicator(),
            const SizedBox(height: 32),
            const Text(
              'Synthesizing...',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.black, fontStyle: FontStyle.italic),
            ),
            const SizedBox(height: 12),
            Text(
              'Our AI is analyzing your materials to create relevant questions.',
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.white.withOpacity(0.5)),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActiveView() {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Practice Quiz', style: TextStyle(fontWeight: FontWeight.black, fontStyle: FontStyle.italic)),
        actions: [
          Padding(
            padding: const EdgeInsets.all(12.0),
            child: Chip(
              label: Text('${_mockQuestions.length} Questions', style: const TextStyle(fontSize: 10)),
              backgroundColor: Colors.transparent,
              side: BorderSide(color: Theme.of(context).primaryColor),
            ),
          )
        ],
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(20),
        itemCount: _mockQuestions.length,
        itemBuilder: (context, index) {
          final q = _mockQuestions[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 24),
            color: Theme.of(context).cardColor,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
            child: Padding(
              padding: const EdgeInsets.all(24.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '${index + 1}. ${q['question']}',
                    style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 24),
                  ...q['options'].map<Widget>((opt) {
                    final isSelected = _answers[index] == opt;
                    return GestureDetector(
                      onTap: () => setState(() => _answers[index] = opt),
                      child: Container(
                        margin: const EdgeInsets.only(bottom: 12),
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          color: isSelected ? Theme.of(context).primaryColor : Colors.white.withOpacity(0.05),
                          borderRadius: BorderRadius.circular(16),
                          border: Border.all(color: isSelected ? Theme.of(context).primaryColor : Colors.white10),
                        ),
                        child: Row(
                          children: [
                            Icon(
                              isSelected ? Icons.radio_button_checked : Icons.radio_button_off,
                              color: isSelected ? Colors.white : Colors.white24,
                              size: 20,
                            ),
                            const SizedBox(width: 12),
                            Expanded(
                              child: Text(
                                opt,
                                style: TextStyle(
                                  color: isSelected ? Colors.white : Colors.white70,
                                  fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    );
                  }).toList(),
                ],
              ),
            ),
          );
        },
      ),
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Row(
          children: [
            Expanded(
              child: OutlinedButton(
                onPressed: () => setState(() => _state = 'idle'),
                style: OutlinedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 20),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                ),
                child: const Text('Cancel'),
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: ElevatedButton(
                onPressed: _answers.length == _mockQuestions.length ? _submitQuiz : null,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Theme.of(context).primaryColor,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 20),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                ),
                child: const Text('Submit Answers', style: TextStyle(fontWeight: FontWeight.bold)),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildResultsView() {
    final percentage = (_score / _mockQuestions.length) * 100;
    return Scaffold(
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(40.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                'Quiz Complete!',
                style: TextStyle(fontSize: 32, fontWeight: FontWeight.black, fontStyle: FontStyle.italic),
              ),
              const SizedBox(height: 48),
              Stack(
                alignment: Alignment.center,
                children: [
                  SizedBox(
                    width: 180,
                    height: 180,
                    child: CircularProgressIndicator(
                      value: _score / _mockQuestions.length,
                      strokeWidth: 12,
                      backgroundColor: Colors.white10,
                      color: Theme.of(context).primaryColor,
                      strokeCap: StrokeCap.round,
                    ),
                  ),
                  Column(
                    children: [
                      Text(
                        '$_score',
                        style: const TextStyle(fontSize: 64, fontWeight: FontWeight.black),
                      ),
                      Text(
                        '/ ${_mockQuestions.length}',
                        style: const TextStyle(fontSize: 24, color: Colors.grey),
                      ),
                    ],
                  ),
                ],
              ),
              const SizedBox(height: 48),
              Row(
                children: [
                  Expanded(child: _buildResultStat('Correct', '$_score', Colors.green, LucideIcons.checkCircle2)),
                  const SizedBox(width: 16),
                  Expanded(child: _buildResultStat('Incorrect', '${_mockQuestions.length - _score}', Colors.red, LucideIcons.xCircle)),
                ],
              ),
              const SizedBox(height: 48),
              SizedBox(
                width: double.infinity,
                height: 64,
                child: ElevatedButton(
                  onPressed: _generateQuiz,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Theme.of(context).primaryColor,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                  ),
                  child: const Text('Retake Another', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                ),
              ),
              TextButton(
                onPressed: () => setState(() => _state = 'idle'),
                child: const Text('Back to Overview', style: TextStyle(color: Colors.grey)),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildResultStat(String label, String value, Color color, IconData icon) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: color.withOpacity(0.2)),
      ),
      child: Column(
        children: [
          Icon(icon, color: color, size: 24),
          const SizedBox(height: 8),
          Text(label, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.grey)),
          Text(value, style: TextStyle(fontSize: 24, fontWeight: FontWeight.black, color: color)),
        ],
      ),
    );
  }
}
