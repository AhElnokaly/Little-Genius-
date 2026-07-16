import { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft, Volume2, Sparkles, Trophy, Star, RefreshCw, CheckCircle, HelpCircle } from 'lucide-react';
import confetti from 'canvas-confetti';
import { getAudioContext, speak } from '../utils/audio';

interface WordBuilderProps {
  onBack: () => void;
  onWin: (stars: number) => void;
}

// +++ أضيف بناءً على طلبك: قاعدة بيانات الكلمات للأطفال مع صور وإيموجي وحروف مفرقة +++
const WORDS_DATABASE = [
  { id: '1', word: 'أَسَد', letters: ['أ', 'س', 'د'], emoji: '🦁', title: 'أسد', color: 'from-amber-400 to-orange-400' },
  { id: '2', word: 'قِطَّة', letters: ['ق', 'ط', 'ة'], emoji: '🐱', title: 'قطة', color: 'from-yellow-400 to-amber-400' },
  { id: '3', word: 'مَوْز', letters: ['م', 'و', 'ز'], emoji: '🍌', title: 'موز', color: 'from-amber-300 to-yellow-500' },
  { id: '4', word: 'جَمَل', letters: ['ج', 'م', 'ل'], emoji: '🐪', title: 'جمل', color: 'from-orange-400 to-amber-500' },
  { id: '5', word: 'تُفَّاح', letters: ['ت', 'ف', 'ا', 'ح'], emoji: '🍎', title: 'تفاح', color: 'from-rose-400 to-red-500' },
  { id: '6', word: 'بَيْت', letters: ['ب', 'ي', 'ت'], emoji: '🏠', title: 'بيت', color: 'from-sky-400 to-blue-500' },
  { id: '7', word: 'شَمْس', letters: ['ش', 'م', 'س'], emoji: '☀️', title: 'شمس', color: 'from-yellow-400 to-orange-500' },
  { id: '8', word: 'قَمَر', letters: ['ق', 'م', 'ر'], emoji: '🌙', title: 'قمر', color: 'from-indigo-400 to-slate-600' },
  { id: '9', word: 'وَرْدَة', letters: ['و', 'ر', 'د', 'ة'], emoji: '🌹', title: 'وردة', color: 'from-red-400 to-pink-500' },
  { id: '10', word: 'سَمَكَة', letters: ['س', 'م', 'ك', 'ة'], emoji: '🐟', title: 'سمكة', color: 'from-cyan-400 to-blue-500' },
  { id: '11', word: 'فِيل', letters: ['ف', 'ي', 'ل'], emoji: '🐘', title: 'فيل', color: 'from-slate-400 to-blue-400' },
  { id: '12', word: 'بَطَّة', letters: ['ب', 'ط', 'ة'], emoji: '🦆', title: 'بطة', color: 'from-amber-400 to-yellow-400' },
];

const ARABIC_ALPHABET = ['أ', 'ب', 'ت', 'ث', 'ج', 'ح', 'خ', 'د', 'ذ', 'ر', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ع', 'غ', 'ف', 'ق', 'ك', 'ل', 'م', 'ن', 'هـ', 'و', 'ي', 'ة', 'ا'];

export default function WordBuilder({ onBack, onWin }: WordBuilderProps) {
  const [levelIndex, setLevelIndex] = useState(0);
  const currentWord = WORDS_DATABASE[levelIndex];
  
  // Placed letters in slots (matching the word length)
  const [placedLetters, setPlacedLetters] = useState<(string | null)[]>([]);
  // Shuffled letter pool for the bottom area
  const [letterPool, setLetterPool] = useState<{ id: string; letter: string; isPlaced: boolean }[]>([]);
  // To keep track of progress & tutorials
  const [successCount, setSuccessCount] = useState(0);
  const [showHint, setShowHint] = useState(false);

  // Initialize Level
  useEffect(() => {
    initLevel(levelIndex);
  }, [levelIndex]);

  const initLevel = (idx: number) => {
    const word = WORDS_DATABASE[idx];
    setPlacedLetters(Array(word.letters.length).fill(null));
    setShowHint(false);

    // Create the pool of letters: target letters + some random distractors
    const targetLetters = [...word.letters];
    const poolSize = Math.max(6, word.letters.length + 3);
    const poolLetters = [...targetLetters];

    while (poolLetters.length < poolSize) {
      const randLetter = ARABIC_ALPHABET[Math.floor(Math.random() * ARABIC_ALPHABET.length)];
      if (!poolLetters.includes(randLetter)) {
        poolLetters.push(randLetter);
      }
    }

    // Shuffle pool
    const shuffled = poolLetters
      .map((letter, i) => ({ id: `pool-${letter}-${i}-${Math.random()}`, letter, isPlaced: false }))
      .sort(() => Math.random() - 0.5);

    setLetterPool(shuffled);

    // Prompt Egyptian welcome speech
    speak(`كون كِلمِة ${word.title}`, 'ar-EG');
  };

  // Play custom synthesizer sounds
  const playSuccessChime = () => {
    try {
      const ctx = getAudioContext();
      const now = ctx.currentTime;
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = 'sine';
      osc.frequency.setValueAtTime(587.33, now); // D5
      osc.frequency.exponentialRampToValueAtTime(1174.66, now + 0.15); // D6
      
      gain.gain.setValueAtTime(0, now);
      gain.gain.linearRampToValueAtTime(0.2, now + 0.04);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.2);
      
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start(now);
      osc.stop(now + 0.2);
    } catch (e) {
      console.error(e);
    }
  };

  const playWrongChime = () => {
    try {
      const ctx = getAudioContext();
      const now = ctx.currentTime;
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = 'triangle';
      osc.frequency.setValueAtTime(220, now);
      osc.frequency.linearRampToValueAtTime(165, now + 0.2);
      
      gain.gain.setValueAtTime(0, now);
      gain.gain.linearRampToValueAtTime(0.15, now + 0.05);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.25);
      
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start(now);
      osc.stop(now + 0.25);
    } catch (e) {
      console.error(e);
    }
  };

  const playCompleteChime = () => {
    try {
      const ctx = getAudioContext();
      const now = ctx.currentTime;
      // Beautiful major arpeggio
      const notes = [523.25, 659.25, 783.99, 1046.50]; // C Major
      notes.forEach((freq, idx) => {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(freq, now + idx * 0.1);
        gain.gain.setValueAtTime(0, now + idx * 0.1);
        gain.gain.linearRampToValueAtTime(0.25, now + idx * 0.1 + 0.05);
        gain.gain.exponentialRampToValueAtTime(0.001, now + idx * 0.1 + 0.3);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(now + idx * 0.1);
        osc.stop(now + idx * 0.1 + 0.35);
      });
    } catch (e) {
      console.error(e);
    }
  };

  // Speaks the letter names
  const speakLetter = (letter: string) => {
    const letterNames: { [key: string]: string } = {
      'أ': 'أَلِف', 'ب': 'بَاء', 'ت': 'تَاء', 'ث': 'ثَاء', 'ج': 'جِيم',
      'ح': 'حَاء', 'خ': 'خَاء', 'د': 'دَال', 'ذ': 'ذَال', 'ر': 'رَاء',
      'ز': 'زَاي', 'س': 'سِين', 'ش': 'شِين', 'ص': 'صَاد', 'ض': 'ضَاد',
      'ط': 'طَاء', 'ظ': 'ظَاء', 'ع': 'عَيْن', 'غ': 'غَيْن', 'ف': 'فَاء',
      'ق': 'قَاف', 'ك': 'كَاف', 'ل': 'لاَم', 'م': 'مِيم', 'ن': 'نُون',
      'هـ': 'هَاء', 'و': 'وَاو', 'ي': 'يَاء', 'ة': 'تَاء مَرْبُوطَة', 'ا': 'أَلِف مَد'
    };
    const name = letterNames[letter] || letter;
    speak(name, 'ar-EG');
  };

  // Places a letter in a specific slot
  const placeLetter = (letter: string, slotIdx: number, poolId: string) => {
    const nextPlaced = [...placedLetters];
    nextPlaced[slotIdx] = letter;
    setPlacedLetters(nextPlaced);

    // Mark the letter in pool as placed
    setLetterPool(prev => prev.map(item => item.id === poolId ? { ...item, isPlaced: true } : item));

    // Speak the letter
    speakLetter(letter);

    // Check if current word is completed
    const finalPlaced = nextPlaced;
    const isCompleted = finalPlaced.every((val, i) => val === currentWord.letters[i]);
    
    if (isCompleted) {
      handleWordComplete();
    }
  };

  // If child taps a letter from pool
  const handlePoolItemTap = (letter: string, poolId: string) => {
    // Find first empty slot that matches this letter
    const targetIdx = currentWord.letters.findIndex((l, idx) => l === letter && placedLetters[idx] === null);
    
    if (targetIdx !== -1) {
      playSuccessChime();
      placeLetter(letter, targetIdx, poolId);
    } else {
      // Play invalid or wrong feedback
      playWrongChime();
      speak('هذا الحرف ليس في مكانه الصحيح', 'ar-EG');
    }
  };

  // Drag and Drop checking logic
  const handleDragEnd = (info: any, letter: string, poolId: string) => {
    const x = info.point.x;
    const y = info.point.y;
    
    let snapped = false;

    // Iterate through all slots to find if released over any empty matching slot
    for (let i = 0; i < currentWord.letters.length; i++) {
      if (placedLetters[i] === null) {
        const slotEl = document.getElementById(`slot-${i}`);
        if (slotEl) {
          const rect = slotEl.getBoundingClientRect();
          const padding = 25; // larger tolerance for kid-friendly interaction
          
          if (
            x >= rect.left - padding &&
            x <= rect.right + padding &&
            y >= rect.top - padding &&
            y <= rect.bottom + padding
          ) {
            if (currentWord.letters[i] === letter) {
              playSuccessChime();
              placeLetter(letter, i, poolId);
              snapped = true;
              break;
            } else {
              playWrongChime();
              speak('حاول مرة أخرى', 'ar-EG');
            }
          }
        }
      }
    }
  };

  // Celebration when word is fully spelt
  const handleWordComplete = async () => {
    playCompleteChime();
    confetti({
      particleCount: 100,
      spread: 70,
      origin: { y: 0.6 }
    });

    // Egypt congrats phrase
    const congratsPhrases = [
      `أحسنت يا شاطر! لقد كونت كلمة ${currentWord.title}!`,
      `يا سلام عليك! فوز ممتاز! كلمة ${currentWord.title} صحيحة!`,
      `رائع جداً! كلمة ${currentWord.title}! أنت بطل ذكي!`
    ];
    const phrase = congratsPhrases[Math.floor(Math.random() * congratsPhrases.length)];
    await speak(phrase, 'ar-EG');

    setSuccessCount(prev => prev + 1);

    // Show celebration modal via custom win handler
    // Wait slightly to let the child appreciate their word
    setTimeout(() => {
      onWin(2); // Award 2 stars for completing spelling word!
      
      // Auto move to next level or loop
      if (levelIndex < WORDS_DATABASE.length - 1) {
        setLevelIndex(prev => prev + 1);
      } else {
        // Complete game, loop back
        setLevelIndex(0);
        speak('رائع! لقد أكملت كل الكلمات! لنبدأ من جديد!', 'ar-EG');
      }
    }, 1200);
  };

  // Reset current level
  const resetLevel = () => {
    initLevel(levelIndex);
  };

  // Speak the entire word hint
  const speakHint = () => {
    speak(currentWord.title, 'ar-EG');
    setShowHint(true);
    setTimeout(() => setShowHint(false), 2500);
  };

  return (
    <div id="wordbuilder-root" className="w-full h-full relative bg-gradient-to-b from-sky-100 to-indigo-100 flex flex-col items-center justify-between p-4 overflow-hidden select-none">
      {/* Top Header Controls */}
      <div id="wordbuilder-header" className="w-full flex justify-between items-center z-50">
        <button 
          id="btn-back"
          onClick={onBack}
          className="bg-white p-4 rounded-full shadow-lg border-2 border-indigo-200 active:scale-90 transition-transform cursor-pointer"
        >
          <ArrowLeft size={28} className="text-indigo-800" />
        </button>

        <div className="flex items-center gap-3">
          {/* Level Progress */}
          <div id="level-indicator" className="bg-white/90 px-6 py-2 rounded-full shadow-md border-4 border-amber-300 font-extrabold text-indigo-900 flex items-center gap-2">
            <Trophy className="text-amber-500" size={24} />
            <span className="font-sans text-xl">الكلمة {levelIndex + 1} / {WORDS_DATABASE.length}</span>
          </div>
        </div>

        <div className="flex gap-2">
          <button 
            id="btn-hint"
            onClick={speakHint}
            className="bg-amber-400 hover:bg-amber-500 p-4 rounded-full shadow-lg border-2 border-white active:scale-90 transition-transform cursor-pointer"
            title="مساعدة"
          >
            <HelpCircle size={28} className="text-amber-950" />
          </button>
          <button 
            id="btn-reset"
            onClick={resetLevel}
            className="bg-white p-4 rounded-full shadow-lg border-2 border-rose-200 active:scale-90 transition-transform cursor-pointer"
            title="إعادة المحاولة"
          >
            <RefreshCw size={28} className="text-rose-600" />
          </button>
        </div>
      </div>

      {/* Main Content Dashboard */}
      <div id="wordbuilder-dashboard" className="flex-1 w-full max-w-4xl flex flex-col items-center justify-center gap-6 mt-2">
        
        {/* Playful Word Card (The Image & Animation) */}
        <motion.div 
          id="word-card"
          key={currentWord.id}
          initial={{ scale: 0.8, opacity: 0, rotate: -3 }}
          animate={{ scale: 1, opacity: 1, rotate: 0 }}
          transition={{ type: 'spring', stiffness: 200, damping: 15 }}
          className={`relative bg-gradient-to-br ${currentWord.color} rounded-3xl p-6 shadow-2xl border-8 border-white text-center w-64 h-64 md:w-72 md:h-72 flex flex-col items-center justify-center`}
        >
          {/* Audio Chime Button overlay on Card */}
          <button 
            id="btn-pronounce"
            onClick={() => speak(currentWord.title, 'ar-EG')}
            className="absolute top-4 right-4 bg-white/80 p-3 rounded-full hover:bg-white transition-colors cursor-pointer"
          >
            <Volume2 className="text-amber-700" size={24} />
          </button>

          {/* Floating Stars for ambient look */}
          <Sparkles className="absolute top-4 left-4 text-white/50 animate-pulse" size={28} />

          {/* Large Emoji / Icon */}
          <motion.span 
            id="word-emoji"
            animate={{ y: [0, -10, 0] }}
            transition={{ duration: 2, repeat: Infinity, ease: 'easeInOut' }}
            className="text-8xl md:text-9xl drop-shadow-xl"
          >
            {currentWord.emoji}
          </motion.span>

          {/* Dotted word reference on hover/hint */}
          <AnimatePresence>
            {showHint && (
              <motion.div 
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                className="absolute bottom-4 bg-white/90 px-4 py-1.5 rounded-full shadow border-2 border-amber-300 text-amber-950 font-black text-2xl font-sans"
              >
                {currentWord.word}
              </motion.div>
            )}
          </AnimatePresence>
        </motion.div>

        {/* Word Spelling Slots (Right-To-Left!) */}
        <div id="spelling-slots-container" className="flex flex-row-reverse items-center justify-center gap-4 py-4 w-full" dir="rtl">
          {currentWord.letters.map((letter, idx) => {
            const hasPlaced = placedLetters[idx] !== null;
            return (
              <div 
                key={idx}
                id={`slot-${idx}`}
                className={`relative w-16 h-16 sm:w-20 sm:h-20 rounded-2xl flex items-center justify-center border-4 border-dashed transition-all duration-300 shadow-inner ${
                  hasPlaced 
                    ? 'bg-emerald-100 border-emerald-400 scale-105' 
                    : 'bg-white/50 border-indigo-300'
                }`}
              >
                {/* Dotted target letter background inside empty slot */}
                {!hasPlaced && (
                  <span className="text-2xl sm:text-3xl font-black text-indigo-300/40 select-none font-sans select-none">
                    {letter}
                  </span>
                )}

                {/* Placed letter card */}
                <AnimatePresence>
                  {hasPlaced && (
                    <motion.div
                      initial={{ scale: 0, rotate: -15 }}
                      animate={{ scale: 1, rotate: 0 }}
                      exit={{ scale: 0 }}
                      className="absolute inset-0 bg-gradient-to-br from-emerald-400 to-teal-500 rounded-xl flex items-center justify-center text-white text-3xl sm:text-4xl font-black shadow-md border-2 border-white font-sans"
                    >
                      {placedLetters[idx]}
                      <motion.div
                        animate={{ scale: [1, 1.3, 1] }}
                        transition={{ duration: 1, repeat: Infinity }}
                        className="absolute -top-2 -right-2 text-yellow-300 fill-yellow-300 bg-white rounded-full p-0.5 border border-emerald-300"
                      >
                        <Star size={14} className="fill-yellow-400 text-yellow-400" />
                      </motion.div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            );
          })}
        </div>
      </div>

      {/* Interactive Letter Pool Area (The floating letters at bottom) */}
      <div id="letter-pool-area" className="w-full max-w-3xl bg-white/75 backdrop-blur-md rounded-3xl p-6 border-4 border-indigo-200/50 shadow-xl flex flex-col items-center gap-4 mb-4">
        <p className="text-xl font-bold text-indigo-900 font-sans text-center">
          👇 اسحب الحرف لمكانه أو اضغط عليه ليطير إلى مكانه الصحيح!
        </p>

        <div id="letters-grid" className="flex flex-wrap justify-center gap-4 px-2" dir="rtl">
          {letterPool.map((item) => (
            <div key={item.id} className="relative w-16 h-16 sm:w-18 sm:h-18">
              <AnimatePresence>
                {!item.isPlaced && (
                  <motion.div
                    drag
                    dragConstraints={{ left: 0, right: 0, top: 0, bottom: 0 }}
                    dragElastic={0.9}
                    dragTransition={{ bounceStiffness: 600, bounceDamping: 15 }}
                    onDragEnd={(event, info) => handleDragEnd(info, item.letter, item.id)}
                    whileHover={{ scale: 1.1, rotate: 5 }}
                    whileTap={{ scale: 0.9, zIndex: 100 }}
                    onClick={() => handlePoolItemTap(item.letter, item.id)}
                    className="absolute inset-0 bg-gradient-to-br from-pink-400 via-rose-400 to-amber-400 rounded-2xl flex items-center justify-center text-white text-3xl sm:text-4xl font-black shadow-lg border-2 border-white cursor-grab active:cursor-grabbing font-sans select-none"
                    style={{ touchAction: 'none' }} // Crucial for mobile dragging
                  >
                    {item.letter}
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
