import { useState } from 'react';
import { ArrowLeft } from 'lucide-react';
import { motion } from 'motion/react';
import confetti from 'canvas-confetti';

interface ColorPianoProps {
  onBack: () => void;
  onWin?: () => void;
}

const KEYS = [
  { id: 1, color: 'bg-red-500', freq: 261.63, char: '🐸' },
  { id: 2, color: 'bg-yellow-500', freq: 329.63, char: '🐵' },
  { id: 3, color: 'bg-green-500', freq: 392.00, char: '🐰' },
  { id: 4, color: 'bg-blue-500', freq: 523.25, char: '🐼' },
];

export default function ColorPiano({ onBack, onWin }: ColorPianoProps) {
  const [activeChar, setActiveChar] = useState<{id: number, char: string} | null>(null);

  const playNote = (freq: number, id: number, char: string, e: React.PointerEvent) => {
    e.stopPropagation();
    if (onWin) onWin();
    
    setActiveChar({ id, char });
    setTimeout(() => setActiveChar(null), 500);
    
    confetti({
      particleCount: 20,
      spread: 40,
      origin: { x: e.clientX / window.innerWidth, y: e.clientY / window.innerHeight }
    });

    try {
      const ctx = new (window.AudioContext || (window as any).webkitAudioContext)();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.type = 'sine';
      osc.frequency.setValueAtTime(freq, ctx.currentTime);
      gain.gain.setValueAtTime(0.5, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.5);
    } catch (err) {}
  };

  return (
    <div className="w-full h-full relative bg-pink-100 flex flex-col items-center justify-end p-4 pb-12">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-pink-800" />
      </button>
      
      <div className="flex w-full max-w-3xl h-3/4 gap-2 md:gap-4">
        {KEYS.map((k) => (
          <div key={k.id} className="relative flex-1 h-full">
            {activeChar?.id === k.id && (
              <motion.div
                initial={{ y: 50, opacity: 0 }}
                animate={{ y: -100, opacity: 1 }}
                className="absolute -top-20 left-1/2 -translate-x-1/2 text-6xl md:text-8xl z-10 pointer-events-none"
              >
                {k.char}
              </motion.div>
            )}
            <motion.button
              whileTap={{ scaleY: 0.95, originY: 1 }}
              onPointerDown={(e) => playNote(k.freq, k.id, k.char, e)}
              className={`w-full h-full rounded-b-3xl ${k.color} shadow-xl border-4 border-white/50`}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
