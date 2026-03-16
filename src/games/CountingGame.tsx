import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface CountingGameProps {
  onBack: () => void;
  onWin?: () => void;
}

// +++ أضيف بناءً على طلبك: لعبة الأرقام والعد +++
const ITEMS = ['🍎', '🚗', '🎈', '⭐', '🐶', '🍕', '⚽', '🦋', '🍓', '🧸'];

export default function CountingGame({ onBack, onWin }: CountingGameProps) {
  const [level, setLevel] = useState(1);
  const [targetNumber, setTargetNumber] = useState(1);
  const [itemEmoji, setItemEmoji] = useState('🍎');
  const [options, setOptions] = useState<number[]>([]);
  const [showTutorial, setShowTutorial] = useState(true);
  const [isAnimating, setIsAnimating] = useState(false);

  const generateLevel = () => {
    // المستوى يحدد أقصى رقم (المستوى 1: 1-3، المستوى 2: 1-5، المستوى 3: 1-10)
    const maxNumber = Math.min(3 + Math.floor(level / 2), 10);
    const target = Math.floor(Math.random() * maxNumber) + 1;
    setTargetNumber(target);
    setItemEmoji(ITEMS[Math.floor(Math.random() * ITEMS.length)]);

    // توليد خيارات خاطئة
    const newOptions = new Set<number>();
    newOptions.add(target);
    while (newOptions.size < 3) {
      const wrong = Math.floor(Math.random() * maxNumber) + 1;
      newOptions.add(wrong);
    }
    setOptions(Array.from(newOptions).sort(() => Math.random() - 0.5));
  };

  useEffect(() => {
    generateLevel();
  }, [level]);

  const playSound = (type: 'correct' | 'incorrect') => {
    try {
      const ctx = new (window.AudioContext || (window as any).webkitAudioContext)();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      
      if (type === 'correct') {
        osc.type = 'sine';
        osc.frequency.setValueAtTime(400, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(800, ctx.currentTime + 0.1);
        gain.gain.setValueAtTime(0.5, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.1);
      } else {
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(200, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(150, ctx.currentTime + 0.2);
        gain.gain.setValueAtTime(0.3, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.2);
      }
      
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.2);
    } catch (e) {}
  };

  const handleSelect = (num: number, e: React.MouseEvent) => {
    if (isAnimating) return;
    if (showTutorial) setShowTutorial(false);

    if (num === targetNumber) {
      setIsAnimating(true);
      playSound('correct');
      if (onWin) onWin();
      
      confetti({
        particleCount: 50,
        spread: 60,
        origin: { x: e.clientX / window.innerWidth, y: e.clientY / window.innerHeight }
      });

      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
        const utterance = new SpeechSynthesisUtterance(`ممتاز! ${num}`);
        utterance.lang = 'ar-EG';
        window.speechSynthesis.speak(utterance);
      }

      setTimeout(() => {
        setLevel(l => l + 1);
        setIsAnimating(false);
      }, 1500);
    } else {
      playSound('incorrect');
    }
  };

  return (
    <div className="w-full h-full relative bg-amber-50 flex flex-col items-center justify-center p-4">
      <TutorialHand show={showTutorial} y={150} action="tap" />
      
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-amber-800" />
      </button>

      <div className="absolute top-24 right-6 z-50 bg-white px-4 py-2 rounded-full shadow-lg font-bold text-amber-800">
        مرحلة {level}
      </div>

      <h2 className="text-3xl md:text-5xl font-bold text-amber-800 mb-8 text-center">
        كم عددها؟ 🔢
      </h2>

      {/* Items Display */}
      <div className="bg-white/50 backdrop-blur-sm rounded-3xl p-8 mb-12 min-h-[200px] w-full max-w-2xl flex flex-wrap items-center justify-center gap-4 shadow-inner border-4 border-amber-200">
        <AnimatePresence mode="popLayout">
          {Array.from({ length: targetNumber }).map((_, i) => (
            <motion.div
              key={`${level}-${i}`}
              initial={{ scale: 0, rotate: -180 }}
              animate={{ scale: 1, rotate: 0 }}
              exit={{ scale: 0 }}
              transition={{ type: 'spring', delay: i * 0.1 }}
              className="text-6xl md:text-8xl drop-shadow-md"
            >
              {itemEmoji}
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

      {/* Number Options */}
      <div className="flex gap-4 md:gap-8">
        {options.map((num) => (
          <motion.button
            key={num}
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
            onClick={(e) => handleSelect(num, e)}
            className="bg-amber-400 hover:bg-amber-500 text-white w-24 h-24 md:w-32 md:h-32 rounded-3xl shadow-xl border-4 border-white/50 flex items-center justify-center text-5xl md:text-7xl font-black transition-colors"
          >
            {num}
          </motion.button>
        ))}
      </div>
    </div>
  );
}
