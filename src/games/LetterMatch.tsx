import { useState, useEffect, useRef } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';
import { speak, getAudioContext } from '../utils/audio';

interface LetterMatchProps {
  onBack: () => void;
  onWin?: () => void;
}

const ARABIC_PAIRS = [
  { id: 'a', letter: 'أ', word: 'أرنب', emoji: '🐰', lang: 'ar-SA' },
  { id: 'b', letter: 'ب', word: 'بطة', emoji: '🦆', lang: 'ar-SA' },
  { id: 't', letter: 'ت', word: 'تفاحة', emoji: '🍎', lang: 'ar-SA' },
  { id: 'th', letter: 'ث', word: 'ثعلب', emoji: '🦊', lang: 'ar-SA' },
  { id: 'j', letter: 'ج', word: 'جمل', emoji: '🐪', lang: 'ar-SA' },
];

const ENGLISH_PAIRS = [
  { id: 'ea', letter: 'A', word: 'Apple', emoji: '🍎', lang: 'en-US' },
  { id: 'eb', letter: 'B', word: 'Bear', emoji: '🐻', lang: 'en-US' },
  { id: 'ec', letter: 'C', word: 'Cat', emoji: '🐱', lang: 'en-US' },
  { id: 'ed', letter: 'D', word: 'Dog', emoji: '🐶', lang: 'en-US' },
  { id: 'ee', letter: 'E', word: 'Elephant', emoji: '🐘', lang: 'en-US' },
];

export default function LetterMatch({ onBack, onWin }: LetterMatchProps) {
  const [level, setLevel] = useState(1);
  const [pairs, setPairs] = useState<any[]>([]);
  const [leftItems, setLeftItems] = useState<any[]>([]);
  const [rightItems, setRightItems] = useState<any[]>([]);
  const [selectedLeft, setSelectedLeft] = useState<string | null>(null);
  const [selectedRight, setSelectedRight] = useState<string | null>(null);
  const [matched, setMatched] = useState<string[]>([]);
  const [showTutorial, setShowTutorial] = useState(true);
  const [language, setLanguage] = useState<'ar' | 'en'>('ar');
  const leftRefs = useRef<{ [key: string]: HTMLButtonElement | null }>({});
  const rightRefs = useRef<{ [key: string]: HTMLButtonElement | null }>({});
  const [lines, setLines] = useState<{ id: string, x1: number, y1: number, x2: number, y2: number }[]>([]);

  const updateLines = () => {
    const newLines = matched.map(id => {
      const leftEl = leftRefs.current[id];
      const rightEl = rightRefs.current[id];
      if (leftEl && rightEl) {
        const leftRect = leftEl.getBoundingClientRect();
        const rightRect = rightEl.getBoundingClientRect();
        return {
          id,
          x1: leftRect.right,
          y1: leftRect.top + leftRect.height / 2,
          x2: rightRect.left,
          y2: rightRect.top + rightRect.height / 2,
        };
      }
      return null;
    }).filter(Boolean) as any[];
    setLines(newLines);
  };

  useEffect(() => {
    updateLines();
    window.addEventListener('resize', updateLines);
    return () => window.removeEventListener('resize', updateLines);
  }, [matched, leftItems, rightItems]);

  useEffect(() => {
    const source = language === 'ar' ? ARABIC_PAIRS : ENGLISH_PAIRS;
    const shuffled = [...source].sort(() => Math.random() - 0.5).slice(0, 3);
    setPairs(shuffled);
    setLeftItems([...shuffled].sort(() => Math.random() - 0.5));
    setRightItems([...shuffled].sort(() => Math.random() - 0.5));
    setMatched([]);
    setSelectedLeft(null);
    setSelectedRight(null);
  }, [level, language]);

  const playSound = (type: 'correct' | 'incorrect') => {
    try {
      const ctx = getAudioContext();
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
    speak(item.letter, item.lang);
    setSelectedLeft(item.id);
    checkMatch(item.id, selectedRight);
  };

  const handleRightClick = (item: any) => {
    if (matched.includes(item.id)) return;
    if (showTutorial) setShowTutorial(false);
    speak(item.word, item.lang);
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
    <div className="w-full h-full relative bg-violet-50 flex flex-col items-center p-4">
      <TutorialHand show={showTutorial} y={150} action="tap" />
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-violet-800" />
      </button>
      
      <div className="absolute top-24 right-6 z-50 flex gap-2">
        <button onClick={() => setLanguage('ar')} className={`px-4 py-2 rounded-full font-bold shadow-md ${language === 'ar' ? 'bg-violet-500 text-white' : 'bg-white text-violet-800'}`}>عربي</button>
        <button onClick={() => setLanguage('en')} className={`px-4 py-2 rounded-full font-bold shadow-md ${language === 'en' ? 'bg-violet-500 text-white' : 'bg-white text-violet-800'}`}>English</button>
      </div>

      <h2 className="text-3xl md:text-5xl font-bold text-violet-800 mt-20 mb-8 text-center z-10 relative">
        توصيل الحروف 🔤
      </h2>

      {/* +++ أضيف بناءً على طلبك: خطوط التوصيل بين العناصر +++ */}
      <svg className="absolute inset-0 w-full h-full pointer-events-none z-0">
        {lines.map(line => (
          <motion.line
            key={line.id}
            initial={{ pathLength: 0 }}
            animate={{ pathLength: 1 }}
            transition={{ duration: 0.5 }}
            x1={line.x1}
            y1={line.y1}
            x2={line.x2}
            y2={line.y2}
            stroke="#4ade80"
            strokeWidth="8"
            strokeLinecap="round"
            className="drop-shadow-md"
          />
        ))}
      </svg>

      <div className="flex justify-between w-full max-w-2xl mt-8 px-4 z-10 relative">
        {/* Left Column (Letters) */}
        <div className="flex flex-col gap-6 w-1/3">
          {leftItems.map(item => (
            <motion.button
              key={`l-${item.id}`}
              ref={el => leftRefs.current[item.id] = el}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => handleLeftClick(item)}
              className={`aspect-square rounded-3xl shadow-lg border-4 flex items-center justify-center text-5xl md:text-7xl font-bold transition-all ${
                matched.includes(item.id) ? 'bg-green-400 border-green-500 text-white opacity-50' :
                selectedLeft === item.id ? 'bg-violet-400 border-violet-500 text-white scale-110' :
                'bg-white border-violet-200 text-violet-800'
              }`}
            >
              {item.letter}
            </motion.button>
          ))}
        </div>

        {/* Right Column (Words/Images) */}
        <div className="flex flex-col gap-6 w-1/3">
          {rightItems.map(item => (
            <motion.button
              key={`r-${item.id}`}
              ref={el => rightRefs.current[item.id] = el}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => handleRightClick(item)}
              className={`aspect-square rounded-3xl shadow-lg border-4 flex flex-col items-center justify-center transition-all ${
                matched.includes(item.id) ? 'bg-green-400 border-green-500 text-white opacity-50' :
                selectedRight === item.id ? 'bg-violet-400 border-violet-500 text-white scale-110' :
                'bg-white border-violet-200 text-violet-800'
              }`}
            >
              <span className="text-5xl md:text-6xl mb-2">{item.emoji}</span>
              <span className="font-bold text-lg md:text-xl">{item.word}</span>
            </motion.button>
          ))}
        </div>
      </div>
    </div>
  );
}
