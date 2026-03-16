import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';
import { getAudioContext, speak } from '../utils/audio';

interface CatchFishProps {
  onBack: () => void;
  onWin?: () => void;
}

// +++ أضيف بناءً على طلبك: أنواع مختلفة من الكائنات البحرية مع أصوات مميزة +++
const MARINE_CREATURES = [
  { emoji: '🐟', name: 'سمكة', freq: 600 },
  { emoji: '🐠', name: 'سمكة ملونة', freq: 700 },
  { emoji: '🐡', name: 'سمكة منتفخة', freq: 800 },
  { emoji: '🐙', name: 'أخطبوط', freq: 400 },
  { emoji: '🦑', name: 'حبار', freq: 450 },
  { emoji: '🦀', name: 'سلطعون', freq: 900 },
  { emoji: '🦞', name: 'كركند', freq: 850 },
  { emoji: '🦐', name: 'جمبري', freq: 950 },
  { emoji: '🐢', name: 'سلحفاة', freq: 300 },
  { emoji: '🐬', name: 'دلفين', freq: 1200 },
  { emoji: '🐳', name: 'حوت', freq: 150 },
  { emoji: '🦈', name: 'قرش', freq: 200 },
];

interface Fish {
  id: number;
  x: number;
  y: number;
  speed: number;
  direction: number;
  creature: typeof MARINE_CREATURES[0];
  hue: number;
  scale: number;
}

export default function CatchFish({ onBack, onWin }: CatchFishProps) {
  const [fishes, setFishes] = useState<Fish[]>([]);
  const [score, setScore] = useState(0);
  const [level, setLevel] = useState(1);
  const fishIdRef = useRef(0);

  useEffect(() => {
    const maxFishes = Math.min(5 + Math.floor(level / 2), 15);
    const spawnRate = Math.max(2000 - (level * 150), 500);

    const interval = setInterval(() => {
      setFishes((prev) => {
        if (prev.length >= maxFishes) return prev;
        const direction = Math.random() > 0.5 ? 1 : -1;
        const creature = MARINE_CREATURES[Math.floor(Math.random() * MARINE_CREATURES.length)];
        const hue = Math.floor(Math.random() * 360);
        const scale = 0.5 + Math.random() * 1; // 0.5x to 1.5x size
        
        // Play spawn sound
        try {
          const ctx = getAudioContext();
          const osc = ctx.createOscillator();
          const gain = ctx.createGain();
          osc.connect(gain);
          gain.connect(ctx.destination);
          osc.type = 'sine';
          osc.frequency.setValueAtTime(creature.freq / 2, ctx.currentTime);
          osc.frequency.exponentialRampToValueAtTime(creature.freq / 4, ctx.currentTime + 0.5);
          gain.gain.setValueAtTime(0.05, ctx.currentTime);
          gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);
          osc.start(ctx.currentTime);
          osc.stop(ctx.currentTime + 0.5);
        } catch (e) {}

        return [...prev, {
          id: fishIdRef.current++,
          x: direction === 1 ? -20 : 120, // Initial left percentage
          y: Math.random() * 70 + 10, // 10% to 80% top
          speed: Math.max((Math.random() * 4 + 4) - (level * 0.2), 2), // Faster at higher levels
          direction,
          creature,
          hue,
          scale
        }];
      });
    }, spawnRate);
    return () => clearInterval(interval);
  }, [level]);

  const catchFish = (fish: Fish, e: React.PointerEvent) => {
    e.stopPropagation();
    if (onWin) onWin();
    
    // +++ أضيف بناءً على طلبك: نطق اسم الكائن +++
    speak(fish.creature.name);
    
    // +++ أضيف بناءً على طلبك: نظام النقاط والمراحل +++
    setScore(s => {
      const newScore = s + 1;
      if (newScore % 5 === 0) {
        setLevel(l => {
          const newLevel = l + 1;
          speak(`مرحلة ${newLevel}`);
          return newLevel;
        });
      }
      return newScore;
    });

    confetti({
      particleCount: 30,
      spread: 50,
      origin: { x: e.clientX / window.innerWidth, y: e.clientY / window.innerHeight },
      colors: ['#06b6d4', '#ffffff']
    });

    try {
      const ctx = getAudioContext();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.type = 'sine';
      osc.frequency.setValueAtTime(fish.creature.freq, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(fish.creature.freq * 2, ctx.currentTime + 0.1); // +++ أضيف بناءً على طلبك: صوت إيجابي مميز +++
      gain.gain.setValueAtTime(0.5, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.1);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.1);
    } catch (err) {}

    setFishes((prev) => prev.filter((f) => f.id !== fish.id));
  };

  // +++ أضيف بناءً على طلبك: تغذية راجعة للإجراء الخاطئ +++
  const handleMiss = () => {
    try {
      const ctx = getAudioContext();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.type = 'triangle';
      osc.frequency.setValueAtTime(200, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(150, ctx.currentTime + 0.2);
      gain.gain.setValueAtTime(0.2, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.2);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.2);
    } catch (err) {}
  };

  return (
    <div className="w-full h-full relative bg-cyan-200 overflow-hidden" onPointerDown={handleMiss}>
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-cyan-800" />
      </button>

      {/* +++ أضيف بناءً على طلبك: عرض النقاط والمرحلة +++ */}
      <div className="absolute top-24 right-6 z-50 flex gap-4">
        <div className="bg-white px-6 py-3 rounded-full shadow-lg font-bold text-cyan-800 text-xl">
          مرحلة {level}
        </div>
        <div className="bg-white px-6 py-3 rounded-full shadow-lg font-bold text-cyan-800 text-xl">
          النقاط: {score}
        </div>
      </div>
      
      <AnimatePresence>
        {fishes.map((fish) => (
          <motion.div
            key={fish.id}
            initial={{ left: `${fish.x}%`, top: `${fish.y}%`, scaleX: fish.direction === 1 ? -fish.scale : fish.scale, scaleY: fish.scale }}
            animate={{ left: `${fish.direction === 1 ? 120 : -20}%` }}
            transition={{ duration: fish.speed, ease: 'linear' }}
            onPointerDown={(e) => catchFish(fish, e)}
            onAnimationComplete={() => setFishes(prev => prev.filter(f => f.id !== fish.id))}
            className="absolute text-6xl md:text-8xl cursor-pointer drop-shadow-lg"
            style={{ 
              filter: `hue-rotate(${fish.hue}deg)`
            }}
          >
            {fish.creature.emoji}
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  );
}
