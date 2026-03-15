import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft, X } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface ArabicLettersProps {
  onBack: () => void;
  onWin?: () => void;
}

// +++ أضيف بناءً على طلبك: بيانات الحروف العربية مع أمثلة +++
const ARABIC_LETTERS = [
  { letter: 'أ', word: 'أرنب', emoji: '🐰', color: 'bg-red-400' },
  { letter: 'ب', word: 'بطة', emoji: '🦆', color: 'bg-orange-400' },
  { letter: 'ت', word: 'تفاحة', emoji: '🍎', color: 'bg-amber-400' },
  { letter: 'ث', word: 'ثعلب', emoji: '🦊', color: 'bg-yellow-400' },
  { letter: 'ج', word: 'جمل', emoji: '🐪', color: 'bg-lime-400' },
  { letter: 'ح', word: 'حصان', emoji: '🐴', color: 'bg-green-400' },
  { letter: 'خ', word: 'خروف', emoji: '🐑', color: 'bg-emerald-400' },
  { letter: 'د', word: 'دب', emoji: '🐻', color: 'bg-teal-400' },
  { letter: 'ذ', word: 'ذرة', emoji: '🌽', color: 'bg-cyan-400' },
  { letter: 'ر', word: 'رمان', emoji: '🔴', color: 'bg-sky-400' },
  { letter: 'ز', word: 'زرافة', emoji: '🦒', color: 'bg-blue-400' },
  { letter: 'س', word: 'سمكة', emoji: '🐟', color: 'bg-indigo-400' },
  { letter: 'ش', word: 'شجرة', emoji: '🌳', color: 'bg-violet-400' },
  { letter: 'ص', word: 'صقر', emoji: '🦅', color: 'bg-purple-400' },
  { letter: 'ض', word: 'ضفدع', emoji: '🐸', color: 'bg-fuchsia-400' },
  { letter: 'ط', word: 'طائرة', emoji: '✈️', color: 'bg-pink-400' },
  { letter: 'ظ', word: 'ظرف', emoji: '✉️', color: 'bg-rose-400' },
  { letter: 'ع', word: 'عصفور', emoji: '🐦', color: 'bg-red-500' },
  { letter: 'غ', word: 'غزالة', emoji: '🦌', color: 'bg-orange-500' },
  { letter: 'ف', word: 'فيل', emoji: '🐘', color: 'bg-amber-500' },
  { letter: 'ق', word: 'قرد', emoji: '🐒', color: 'bg-yellow-500' },
  { letter: 'ك', word: 'كلب', emoji: '🐶', color: 'bg-lime-500' },
  { letter: 'ل', word: 'ليمون', emoji: '🍋', color: 'bg-green-500' },
  { letter: 'م', word: 'موز', emoji: '🍌', color: 'bg-emerald-500' },
  { letter: 'ن', word: 'نحلة', emoji: '🐝', color: 'bg-teal-500' },
  { letter: 'هـ', word: 'هلال', emoji: '🌙', color: 'bg-cyan-500' },
  { letter: 'و', word: 'وردة', emoji: '🌹', color: 'bg-sky-500' },
  { letter: 'ي', word: 'يد', emoji: '✋', color: 'bg-blue-500' },
];

export default function ArabicLetters({ onBack, onWin }: ArabicLettersProps) {
  const [selectedLetter, setSelectedLetter] = useState<typeof ARABIC_LETTERS[0] | null>(null);
  const [showTutorial, setShowTutorial] = useState(true);
  const [viewedCount, setViewedCount] = useState(0);

  const handleSelect = (item: typeof ARABIC_LETTERS[0]) => {
    if (showTutorial) setShowTutorial(false);
    setSelectedLetter(item);
    
    // النطق الصوتي
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(`${item.letter}، ${item.word}`);
      utterance.lang = 'ar-SA';
      utterance.rate = 0.8;
      window.speechSynthesis.speak(utterance);
    }

    // زيادة العداد وإعطاء نجمة كل 5 حروف
    const newCount = viewedCount + 1;
    setViewedCount(newCount);
    if (newCount % 5 === 0 && onWin) {
      onWin();
      confetti({
        particleCount: 50,
        spread: 60,
        origin: { y: 0.8 },
        colors: ['#f87171', '#fbbf24', '#34d399', '#60a5fa']
      });
    }
  };

  return (
    <div className="w-full h-full relative bg-teal-50 overflow-y-auto">
      <TutorialHand show={showTutorial} y={100} action="tap" />
      
      <button 
        onClick={onBack}
        className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg"
      >
        <ArrowLeft size={32} className="text-teal-800" />
      </button>

      <div className="min-h-full flex flex-col items-center p-4 py-24">
        <h2 className="text-3xl md:text-5xl font-bold text-teal-800 mb-8 text-center bg-white px-8 py-4 rounded-full shadow-sm border-4 border-teal-200">
          الحروف العربية
        </h2>

        <div className="grid grid-cols-4 sm:grid-cols-5 md:grid-cols-7 gap-3 md:gap-4 w-full max-w-5xl" dir="rtl">
          {ARABIC_LETTERS.map((item) => (
            <motion.button
              key={item.letter}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => handleSelect(item)}
              className={`${item.color} aspect-square rounded-2xl shadow-md border-2 border-white/50 flex items-center justify-center text-3xl md:text-5xl font-bold text-white`}
            >
              {item.letter}
            </motion.button>
          ))}
        </div>
      </div>

      {/* نافذة عرض الحرف والكلمة */}
      <AnimatePresence>
        {selectedLetter && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4"
            onClick={() => setSelectedLetter(null)}
          >
            <motion.div
              initial={{ scale: 0.5, y: 50 }}
              animate={{ scale: 1, y: 0 }}
              exit={{ scale: 0.5, y: 50 }}
              className={`${selectedLetter.color} w-full max-w-md rounded-3xl p-8 shadow-2xl border-8 border-white flex flex-col items-center relative`}
              onClick={(e) => e.stopPropagation()}
            >
              <button 
                onClick={() => setSelectedLetter(null)}
                className="absolute top-4 right-4 bg-white/30 hover:bg-white/50 p-2 rounded-full transition-colors"
              >
                <X size={32} className="text-white" />
              </button>
              
              <div className="text-8xl md:text-9xl font-black text-white mb-4 drop-shadow-lg">
                {selectedLetter.letter}
              </div>
              
              <div className="bg-white w-full rounded-2xl p-6 flex flex-col items-center gap-4 shadow-inner">
                <span className="text-7xl md:text-8xl">{selectedLetter.emoji}</span>
                <span className="text-4xl md:text-5xl font-bold text-slate-800">{selectedLetter.word}</span>
              </div>
              
              <button 
                onClick={() => handleSelect(selectedLetter)}
                className="mt-6 bg-white/20 hover:bg-white/30 text-white px-6 py-3 rounded-full font-bold text-xl transition-colors flex items-center gap-2"
              >
                🔊 استمع مرة أخرى
              </button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
