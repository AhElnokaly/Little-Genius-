import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface HealthyFoodProps {
  onBack: () => void;
  onWin?: () => void;
}

const FOODS = [
  { id: 'f1', name: 'تفاحة', emoji: '🍎', isHealthy: true },
  { id: 'f2', name: 'موز', emoji: '🍌', isHealthy: true },
  { id: 'f3', name: 'جزر', emoji: '🥕', isHealthy: true },
  { id: 'f4', name: 'بروكلي', emoji: '🥦', isHealthy: true },
  { id: 'f5', name: 'حليب', emoji: '🥛', isHealthy: true },
  { id: 'f6', name: 'حلويات', emoji: '🍬', isHealthy: false },
  { id: 'f7', name: 'شوكولاتة', emoji: '🍫', isHealthy: false },
  { id: 'f8', name: 'بطاطس محمرة', emoji: '🍟', isHealthy: false },
  { id: 'f9', name: 'حاجة ساقعة', emoji: '🥤', isHealthy: false },
  { id: 'f10', name: 'برجر', emoji: '🍔', isHealthy: false },
];

export default function HealthyFood({ onBack, onWin }: HealthyFoodProps) {
  const [level, setLevel] = useState(1);
  const [currentFood, setCurrentFood] = useState<any>(null);
  const [score, setScore] = useState(0);
  const [showTutorial, setShowTutorial] = useState(true);

  useEffect(() => {
    nextFood();
  }, [level]);

  const nextFood = () => {
    const randomFood = FOODS[Math.floor(Math.random() * FOODS.length)];
    setCurrentFood(randomFood);
    speak(randomFood.name);
  };

  const speak = (text: string) => {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.lang = 'ar-EG';
      utterance.rate = 0.8;
      window.speechSynthesis.speak(utterance);
    }
  };

  const playSound = (type: 'correct' | 'incorrect') => {
    try {
      const ctx = new (window.AudioContext || (window as any).webkitAudioContext)();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      if (type === 'correct') {
        osc.type = 'sine';
        osc.frequency.setValueAtTime(600, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(1200, ctx.currentTime + 0.1);
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

  const handleChoice = (isHealthyChoice: boolean) => {
    if (showTutorial) setShowTutorial(false);
    if (!currentFood) return;

    if (currentFood.isHealthy === isHealthyChoice) {
      playSound('correct');
      const newScore = score + 1;
      setScore(newScore);
      if (newScore % 5 === 0) {
        if (onWin) onWin();
        confetti({ particleCount: 50, spread: 60, origin: { y: 0.8 } });
        setLevel(l => l + 1);
      } else {
        nextFood();
      }
    } else {
      playSound('incorrect');
      speak(currentFood.isHealthy ? 'ده أكل صحي!' : 'ده أكل مش صحي!');
    }
  };

  return (
    <div className="w-full h-full relative bg-lime-50 flex flex-col items-center p-4">
      <TutorialHand show={showTutorial} y={150} action="tap" />
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-lime-800" />
      </button>

      <div className="absolute top-24 right-6 z-50 bg-white px-4 py-2 rounded-full shadow-lg font-bold text-lime-800">
        النقاط: {score}
      </div>

      <h2 className="text-3xl md:text-5xl font-bold text-lime-800 mt-12 mb-8 text-center">
        أكل صحي ولا مضر؟ 🍎🍔
      </h2>

      <div className="flex-1 flex flex-col items-center justify-center w-full max-w-2xl">
        <AnimatePresence mode="wait">
          {currentFood && (
            <motion.div
              key={currentFood.id + score}
              initial={{ scale: 0, rotate: -180 }}
              animate={{ scale: 1, rotate: 0 }}
              exit={{ scale: 0, opacity: 0 }}
              className="bg-white rounded-[3rem] shadow-2xl p-12 flex flex-col items-center border-8 border-lime-200 mb-12"
            >
              <span className="text-9xl drop-shadow-lg mb-4">{currentFood.emoji}</span>
              <span className="text-4xl font-black text-slate-700">{currentFood.name}</span>
            </motion.div>
          )}
        </AnimatePresence>

        <div className="flex gap-4 md:gap-8 w-full justify-center">
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => handleChoice(true)}
            className="bg-green-500 text-white rounded-3xl p-6 shadow-lg border-4 border-green-400 flex flex-col items-center flex-1 max-w-[200px]"
          >
            <span className="text-5xl mb-2">💪</span>
            <span className="text-2xl font-bold">صحي</span>
          </motion.button>
          
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => handleChoice(false)}
            className="bg-red-500 text-white rounded-3xl p-6 shadow-lg border-4 border-red-400 flex flex-col items-center flex-1 max-w-[200px]"
          >
            <span className="text-5xl mb-2">🤒</span>
            <span className="text-2xl font-bold">مش صحي</span>
          </motion.button>
        </div>
      </div>
    </div>
  );
}
