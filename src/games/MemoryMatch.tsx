import { useState, useEffect } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface MemoryMatchProps {
  onBack: () => void;
  onWin?: () => void;
  age?: number;
}

// +++ أضيف بناءً على طلبك: لعبة الذاكرة للأطفال (Memory Match) +++
const EMOJIS = ['🐶', '🐱', '🐰', '🐼', '🦊'];

export default function MemoryMatch({ onBack, onWin, age = 3 }: MemoryMatchProps) {
  // +++ أضيف بناءً على طلبك: نظام المراحل +++
  const [level, setLevel] = useState(1);
  const [cards, setCards] = useState<{id: number, emoji: string}[]>([]);
  const [flipped, setFlipped] = useState<number[]>([]);
  const [matched, setMatched] = useState<number[]>([]);
  const [isChecking, setIsChecking] = useState(false);
  const [showTutorial, setShowTutorial] = useState(true);

  useEffect(() => {
    // +++ أضيف بناءً على طلبك: زيادة الصعوبة تدريجياً +++
    const pairCount = Math.min(level + 1, 5); // L1: 2, L2: 3, L3: 4, L4: 5
    const selectedEmojis = EMOJIS.slice(0, pairCount);
    const duplicated = [...selectedEmojis, ...selectedEmojis];
    const shuffled = duplicated
      .sort(() => Math.random() - 0.5)
      .map((emoji, index) => ({ id: index, emoji }));
    setCards(shuffled);
  }, [level]);

  const playSound = (type: 'flip' | 'correct' | 'incorrect') => {
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
      } else if (type === 'incorrect') {
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(200, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(150, ctx.currentTime + 0.2);
        gain.gain.setValueAtTime(0.3, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.2);
      } else {
        // flip
        osc.type = 'sine';
        osc.frequency.setValueAtTime(300, ctx.currentTime);
        gain.gain.setValueAtTime(0.1, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.05);
      }
      
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.2);
    } catch (e) {}
  };

  const handleCardClick = (index: number, e: React.MouseEvent) => {
    if (isChecking || flipped.includes(index) || matched.includes(index)) return;

    if (showTutorial) setShowTutorial(false);
    playSound('flip');
    const newFlipped = [...flipped, index];
    setFlipped(newFlipped);

    if (newFlipped.length === 2) {
      setIsChecking(true);
      const [first, second] = newFlipped;
      
      if (cards[first].emoji === cards[second].emoji) {
        // Match
        setTimeout(() => {
          playSound('correct');
          setMatched([...matched, first, second]);
          setFlipped([]);
          setIsChecking(false);
          
          if (matched.length + 2 === cards.length) {
            if (onWin) onWin();
            confetti({
              particleCount: 50,
              spread: 60,
              origin: { x: e.clientX / window.innerWidth, y: e.clientY / window.innerHeight }
            });
            if ('speechSynthesis' in window) {
              window.speechSynthesis.cancel();
              const utterance = new SpeechSynthesisUtterance('عمل رائع!');
              utterance.lang = 'ar-SA';
              window.speechSynthesis.speak(utterance);
            }
            // +++ أضيف بناءً على طلبك: الانتقال للمرحلة التالية +++
            if (level < 4) {
              setTimeout(() => {
                setLevel(l => l + 1);
                setFlipped([]);
                setMatched([]);
                setIsChecking(false);
              }, 2000);
            }
          }
        }, 500);
      } else {
        // Mismatch
        setTimeout(() => {
          playSound('incorrect');
          setFlipped([]);
          setIsChecking(false);
        }, 1000);
      }
    }
  };

  return (
    <div className="w-full h-full relative bg-indigo-50 flex flex-col items-center justify-center p-4">
      <TutorialHand show={showTutorial} y={0} action="tap" />
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-indigo-800" />
      </button>
      {/* +++ أضيف بناءً على طلبك: مؤشر المرحلة +++ */}
      <div className="absolute top-24 right-6 z-50 bg-white px-4 py-2 rounded-full shadow-lg font-bold text-indigo-800">
        مرحلة {level}
      </div>
      
      <h2 className="text-3xl md:text-5xl font-bold text-indigo-800 mb-8 text-center">
        أين المتشابه؟ 🧠
      </h2>

      <div className="grid grid-cols-3 gap-4 md:gap-6 max-w-2xl w-full">
        {cards.map((card, index) => {
          const isFlipped = flipped.includes(index) || matched.includes(index);
          return (
            <motion.button
              key={card.id}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={(e) => handleCardClick(index, e)}
              className={`aspect-[3/4] rounded-2xl md:rounded-3xl shadow-xl border-4 border-white/50 flex items-center justify-center text-6xl md:text-8xl transition-colors duration-300 ${
                isFlipped ? 'bg-white' : 'bg-indigo-400'
              }`}
            >
              {isFlipped ? card.emoji : '❓'}
            </motion.button>
          );
        })}
      </div>
    </div>
  );
}
