const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');

// 1. PLACE YOUR SERVICE ACCOUNT KEY AT: scripts/serviceAccountKey.json
// You can get this from Firebase Console -> Project Settings -> Service Accounts -> Generate new private key
const serviceAccountPath = path.join(__dirname, 'serviceAccountKey.json');

if (!fs.existsSync(serviceAccountPath)) {
  console.error('Error: serviceAccountKey.json not found at ' + serviceAccountPath);
  console.log('Please download it from Firebase Console and place it in the scripts directory.');
  process.exit(1);
}

const serviceAccount = require(serviceAccountPath);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

async function seedData() {
  console.log('🚀 Starting database seed...');

  // --- 1. Seed Subjects ---
  const subjects = [
    {
      id: 'physics_subject',
      title: 'QUANTUM',
      subtitle: 'PHYSICS',
      bottomText: 'CORE',
      iconName: 'Bolt',
      startColor: '#8E2DE2',
      endColor: '#4A00E0',
      order: 1
    },
    {
      id: 'chemistry_subject',
      title: 'ORGANIC',
      subtitle: 'CHEMISTRY',
      bottomText: 'ELEMENT',
      iconName: 'Science',
      startColor: '#11998E',
      endColor: '#38EF7D',
      order: 2
    },
    {
      id: 'math_subject',
      title: 'ADVANCED',
      subtitle: 'MATHEMATICS',
      bottomText: 'LOGIC',
      iconName: 'Functions',
      startColor: '#F43F5E',
      endColor: '#881337',
      order: 3
    }
  ];

  for (const subject of subjects) {
    const { id, ...data } = subject;
    await db.collection('subjects').doc(id).set(data);
    console.log(`✅ Subject added: ${data.subtitle}`);
  }

  // --- 2. Seed Topics ---
  const topics = [
    {
      id: 'mechanics_topic',
      name: 'Classical Mechanics',
      subjectIds: ['physics_subject'],
      order: 1,
      lessons: [
        {
          name: 'Newton\'s Laws',
          order: 1,
          studyPoints: ['First Law: Inertia', 'Second Law: F=ma', 'Third Law: Action-Reaction']
        }
      ]
    },
    {
      id: 'thermo_topic',
      name: 'Thermodynamics',
      subjectIds: ['physics_subject', 'chemistry_subject'], // Shared!
      order: 2,
      lessons: [
        {
          name: 'Laws of Thermo',
          order: 1,
          studyPoints: ['Zeroth Law: Equilibrium', 'First Law: Energy Conservation', 'Second Law: Entropy']
        }
      ]
    }
  ];

  for (const topic of topics) {
    const { id, lessons, ...data } = topic;
    await db.collection('topics').doc(id).set(data);
    console.log(`✅ Topic added: ${data.name}`);

    // --- 3. Seed Lessons (Sub-collection) ---
    for (const lesson of lessons) {
      await db.collection('topics').doc(id).collection('lessons').add(lesson);
    }
    console.log(`   - Added ${lessons.length} lessons for ${data.name}`);
  }

  // --- 4. Update Current User Stats ---
  // Change this to your actual UID from Firebase Auth
  const currentUserId = 'YOUR_ACTUAL_USER_UID';

  const userStats = {
    level: 25,
    totalPoints: 1200,
    syncCoins: 50,
    correctAnswers: 412,
    wrongAnswers: 32,
    currentStreak: 12,
    totalDays: 45,
    subjectsStudiedToday: ['PHYSICS', 'MATHEMATICS'],
    yearlyStats: [
      { month: 'Oct', correct: 120, wrong: 15 },
      { month: 'Nov', correct: 150, wrong: 10 },
      { month: 'Dec', correct: 142, wrong: 7 }
    ]
  };

  if (currentUserId !== 'YOUR_ACTUAL_USER_UID') {
    await db.collection('users').doc(currentUserId).set(userStats, { merge: true });
    console.log(`✅ User stats updated for UID: ${currentUserId}`);
  } else {
    console.log('⚠️  User update skipped: No UID provided.');
  }

  console.log('✨ Seeding complete!');
}

seedData().catch(err => {
  console.error('❌ Error seeding data:', err);
  process.exit(1);
});
