import { useState } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';

interface ShapeSorterProps {
  onBack: () => void;
  onWin?: () => void;
}

const SHAPES = [
  { id: 'circle', class: 'rounded-full bg-purple-500' },
  { id: 'square', class: 'rounded-3xl bg-orange-500' },
];

export default function ShapeSorter({ onBack, onWin }: ShapeSorterProps) {
  const [placed, setPlaced] = useState<string[]>([]);

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

  const handlePlace = (id: string, e: React.PointerEvent) => {
    if (placed.includes(id)) return;
    setPlaced((p) => [...p, id]);
    if (onWin) onWin();
    
    playSound('correct');
    
    confetti({
      particleCount: 40,
      spread: 60,
      origin: { x: e.clientX / window.innerWidth, y: e.clientY / window.innerHeight }
    });

    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance('ممتاز');
      utterance.lang = 'ar-SA';
      window.speechSynthesis.speak(utterance);
    }
  };

  return (
    <div className="w-full h-full relative bg-orange-50 flex flex-col items-center justify-center p-4">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-orange-800" />
      </button>
      
      <div className="flex gap-8 mb-20">
        {SHAPES.map((s) => (
          <div key={`hole-${s.id}`} className={`w-32 h-32 md:w-48 md:h-48 border-8 border-dashed border-orange-200 flex items-center justify-center ${s.id === 'circle' ? 'rounded-full' : 'rounded-3xl'}`}>
            {placed.includes(s.id) && (
              <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${s.class}`} />
            )}
          </div>
        ))}
      </div>

      <div className="flex gap-8">
        {SHAPES.map((s) => (
          !placed.includes(s.id) && (
            <motion.div
              key={`drag-${s.id}`}
              drag
              dragSnapToOrigin
              onDragEnd={(e, info) => {
                if (info.offset.y < -100) {
                  handlePlace(s.id, e as any);
                } else {
                  playSound('incorrect');
                }
              }}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
              className={`w-24 h-24 md:w-32 md:h-32 ${s.class} shadow-xl cursor-grab active:cursor-grabbing`}
            />
          )
        ))}
      </div>
    </div>
  );
}
