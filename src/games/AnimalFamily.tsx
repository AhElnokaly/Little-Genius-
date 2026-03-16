import { useState, useEffect } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface AnimalFamilyProps {
  onBack: () => void;
  onWin?: () => void;
}

const FAMILIES = [
  { id: 'dog', baby: 'جرو', adult: 'كلب', babyEmoji: '🐶', adultEmoji: '🐕', audioUrl: '/dog.mp3' },
  { id: 'cat', baby: 'قطة نونو', adult: 'قطة', babyEmoji: '🐱', adultEmoji: '🐈', audioUrl: '/cat.mp3' },
  { id: 'chicken', baby: 'كتكوت', adult: 'فرخة', babyEmoji: '🐥', adultEmoji: '🐔', audioUrl: '/chicken.ogg' },
  { id: 'sheep', baby: 'خروف صغير', adult: 'خروف', babyEmoji: '🐑', adultEmoji: '🐏', audioUrl: '/sheep.ogg' },
  { id: 'cow', baby: 'عجل', adult: 'بقرة', babyEmoji: '🐮', adultEmoji: '🐄', audioUrl: '/cow.mp3' },
  { id: 'horse', baby: 'مهر', adult: 'حصان', babyEmoji: '🐴', adultEmoji: '🐎', audioUrl: '/horse.mp3' },
];

export default function AnimalFamily({ onBack, onWin }: AnimalFamilyProps) {
  const [level, setLevel] = useState(1);
  const [pairs, setPairs] = useState<any[]>([]);
  const [leftItems, setLeftItems] = useState<any[]>([]);
  const [rightItems, setRightItems] = useState<any[]>([]);
  const [selectedLeft, setSelectedLeft] = useState<string | null>(null);
  const [selectedRight, setSelectedRight] = useState<string | null>(null);
  const [matched, setMatched] = useState<string[]>([]);
  const [showTutorial, setShowTutorial] = useState(true);

  useEffect(() => {
    const shuffled = [...FAMILIES].sort(() => Math.random() - 0.5).slice(0, 3);
    setPairs(shuffled);
    setLeftItems([...shuffled].sort(() => Math.random() - 0.5));
    setRightItems([...shuffled].sort(() => Math.random() - 0.5));
    setMatched([]);
    setSelectedLeft(null);
    setSelectedRight(null);
  }, [level]);

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
        osc.frequency.setValueAtTime(500, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(1000, ctx.currentTime + 0.1);
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

  const handleLeftClick = (item: any) => {
    if (matched.includes(item.id)) return;
    if (showTutorial) setShowTutorial(false);
    
    // +++ أضيف بناءً على طلبك: تشغيل صوت الحيوان الحقيقي +++
    const audio = new Audio(item.audioUrl);
    audio.play().catch(() => {});
    
    speak(item.baby);
    setSelectedLeft(item.id);
    checkMatch(item.id, selectedRight);
  };

  const handleRightClick = (item: any) => {
    if (matched.includes(item.id)) return;
    if (showTutorial) setShowTutorial(false);
    
    // +++ أضيف بناءً على طلبك: تشغيل صوت الحيوان الحقيقي +++
    const audio = new Audio(item.audioUrl);
    audio.play().catch(() => {});
    
    speak(item.adult);
    setSelectedRight(item.id);
    checkMatch(selectedLeft, item.id);
  };

  const checkMatch = (leftId: string | null, rightId: string | null) => {
    if (leftId && rightId) {
      if (leftId === rightId) {
        playSound('correct');
        setMatched(prev => [...prev, leftId]);
        setSelectedLeft(null);
        setSelectedRight(null);
        if (matched.length + 1 === pairs.length) {
          if (onWin) onWin();
          confetti({ particleCount: 50, spread: 60, origin: { y: 0.8 } });
          setTimeout(() => {
            setLevel(l => l + 1);
          }, 1500);
        }
      } else {
        playSound('incorrect');
        setTimeout(() => {
          setSelectedLeft(null);
          setSelectedRight(null);
        }, 500);
      }
    }
  };

  return (
    <div className="w-full h-full relative bg-orange-50 flex flex-col items-center p-4">
      <TutorialHand show={showTutorial} y={150} action="tap" />
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-orange-800" />
      </button>

      <h2 className="text-3xl md:text-5xl font-bold text-orange-800 mt-20 mb-8 text-center">
        عائلات الحيوانات 🐾
      </h2>

      <div className="flex justify-between w-full max-w-2xl mt-8 px-4">
        {/* Left Column (Babies) */}
        <div className="flex flex-col gap-6 w-1/3">
          {leftItems.map(item => (
            <motion.button
              key={`l-${item.id}`}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => handleLeftClick(item)}
              className={`aspect-square rounded-3xl shadow-lg border-4 flex flex-col items-center justify-center transition-all ${
                matched.includes(item.id) ? 'bg-green-400 border-green-500 text-white opacity-50' :
                selectedLeft === item.id ? 'bg-orange-400 border-orange-500 text-white scale-110' :
                'bg-white border-orange-200 text-orange-800'
              }`}
            >
              <span className="text-5xl md:text-6xl mb-2">{item.babyEmoji}</span>
              <span className="font-bold text-lg md:text-xl">{item.baby}</span>
            </motion.button>
          ))}
        </div>

        {/* Right Column (Adults) */}
        <div className="flex flex-col gap-6 w-1/3">
          {rightItems.map(item => (
            <motion.button
              key={`r-${item.id}`}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => handleRightClick(item)}
              className={`aspect-square rounded-3xl shadow-lg border-4 flex flex-col items-center justify-center transition-all ${
                matched.includes(item.id) ? 'bg-green-400 border-green-500 text-white opacity-50' :
                selectedRight === item.id ? 'bg-orange-400 border-orange-500 text-white scale-110' :
                'bg-white border-orange-200 text-orange-800'
              }`}
            >
              <span className="text-5xl md:text-6xl mb-2">{item.adultEmoji}</span>
              <span className="font-bold text-lg md:text-xl">{item.adult}</span>
            </motion.button>
          ))}
        </div>
      </div>
    </div>
  );
}
