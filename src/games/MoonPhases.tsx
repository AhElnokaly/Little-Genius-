import { useState } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft, Moon } from 'lucide-react';
import confetti from 'canvas-confetti';
import { speak, getAudioContext } from '../utils/audio';

interface MoonPhasesProps {
  onBack: () => void;
  onWin?: () => void;
}

const PHASES = [
  { id: 'new', name: 'محاق', emoji: '🌑', desc: 'القمر مظلم تماماً' },
  { id: 'waxing-crescent', name: 'هلال متزايد', emoji: '🌒', desc: 'جزء صغير مضيء من اليمين' },
  { id: 'first-quarter', name: 'تربيع أول', emoji: '🌓', desc: 'نصف القمر مضيء من اليمين' },
  { id: 'waxing-gibbous', name: 'أحدب متزايد', emoji: '🌔', desc: 'معظم القمر مضيء من اليمين' },
  { id: 'full', name: 'بدر', emoji: '🌕', desc: 'القمر مضيء بالكامل' },
  { id: 'waning-gibbous', name: 'أحدب متناقص', emoji: '🌖', desc: 'معظم القمر مضيء من اليسار' },
  { id: 'last-quarter', name: 'تربيع أخير', emoji: '🌗', desc: 'نصف القمر مضيء من اليسار' },
  { id: 'waning-crescent', name: 'هلال متناقص', emoji: '🌘', desc: 'جزء صغير مضيء من اليسار' },
];

export default function MoonPhases({ onBack, onWin }: MoonPhasesProps) {
  const [currentPhase, setCurrentPhase] = useState(0);

  const handleNext = () => {
    const next = (currentPhase + 1) % PHASES.length;
    setCurrentPhase(next);
    speak(PHASES[next].name);
    
    try {
      const ctx = getAudioContext();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.type = 'sine';
      osc.frequency.setValueAtTime(300 + (next * 50), ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(600, ctx.currentTime + 0.1);
      gain.gain.setValueAtTime(0.3, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.1);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.1);
    } catch (e) {}

    if (next === 4) { // Full moon
      confetti({
        particleCount: 50,
        spread: 60,
        origin: { y: 0.6 },
        colors: ['#fef08a', '#ffffff']
      });
    }
  };

  const handlePrev = () => {
    const prev = (currentPhase - 1 + PHASES.length) % PHASES.length;
    setCurrentPhase(prev);
    speak(PHASES[prev].name);
  };

  return (
    <div className="w-full h-full relative bg-slate-900 flex flex-col items-center p-4 py-20 overflow-y-auto">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white/10 p-4 rounded-full shadow-lg backdrop-blur-sm">
        <ArrowLeft size={32} className="text-white" />
      </button>

      <h2 className="text-3xl md:text-5xl font-bold text-white mb-12 text-center">
        منازل القمر 🌙
      </h2>

      <div className="flex flex-col items-center justify-center flex-1 w-full max-w-2xl">
        <motion.div
          key={currentPhase}
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          exit={{ scale: 0.8, opacity: 0 }}
          transition={{ type: 'spring', stiffness: 200, damping: 20 }}
          className="text-[150px] md:text-[250px] leading-none drop-shadow-[0_0_50px_rgba(255,255,255,0.3)]"
        >
          {PHASES[currentPhase].emoji}
        </motion.div>

        <motion.div
          key={`text-${currentPhase}`}
          initial={{ y: 20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          className="mt-8 text-center"
        >
          <h3 className="text-4xl md:text-6xl font-bold text-white mb-4">{PHASES[currentPhase].name}</h3>
          <p className="text-xl md:text-2xl text-slate-300">{PHASES[currentPhase].desc}</p>
        </motion.div>

        <div className="flex gap-6 mt-12">
          <button 
            onClick={handlePrev}
            className="bg-white/20 hover:bg-white/30 text-white px-8 py-4 rounded-full text-2xl font-bold backdrop-blur-sm transition-colors"
          >
            السابق
          </button>
          <button 
            onClick={handleNext}
            className="bg-white text-slate-900 px-8 py-4 rounded-full text-2xl font-bold shadow-[0_0_30px_rgba(255,255,255,0.3)] hover:scale-105 transition-transform"
          >
            التالي
          </button>
        </div>
      </div>

      {/* Stars background */}
      <div className="absolute inset-0 pointer-events-none z-[-1]">
        {Array.from({ length: 50 }).map((_, i) => (
          <div
            key={i}
            className="absolute bg-white rounded-full animate-pulse"
            style={{
              width: Math.random() * 3 + 1 + 'px',
              height: Math.random() * 3 + 1 + 'px',
              top: Math.random() * 100 + '%',
              left: Math.random() * 100 + '%',
              animationDuration: Math.random() * 3 + 2 + 's',
              animationDelay: Math.random() * 2 + 's',
            }}
          />
        ))}
      </div>
    </div>
  );
}
