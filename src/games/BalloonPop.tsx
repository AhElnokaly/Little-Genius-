import { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';
import { getAudioContext } from '../utils/audio';

interface BalloonPopProps {
  onBack: () => void;
  onWin?: () => void;
}

interface Balloon {
  id: number;
  x: number;
  color: string;
  speed: number;
}

const COLORS = ['bg-red-500', 'bg-blue-500', 'bg-green-500', 'bg-yellow-500', 'bg-purple-500', 'bg-pink-500'];

export default function BalloonPop({ onBack, onWin }: BalloonPopProps) {
  const [balloons, setBalloons] = useState<Balloon[]>([]);
  const balloonIdRef = useRef(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setBalloons((prev) => {
        if (prev.length > 8) return prev; // Max balloons on screen
        const newBalloon: Balloon = {
          id: balloonIdRef.current++,
          x: Math.random() * 80 + 10, // 10% to 90% width
          color: COLORS[Math.floor(Math.random() * COLORS.length)],
          speed: Math.random() * 4 + 4, // 4s to 8s duration
        };
        return [...prev, newBalloon];
      });
    }, 1500);

    return () => clearInterval(interval);
  }, []);

  const popBalloon = (id: number, event: React.MouseEvent | React.TouchEvent | React.PointerEvent) => {
    // +++ أضيف بناءً على طلبك: منع انتقال الحدث للخلفية (الإجراء الخاطئ) +++
    event.stopPropagation();
    if (onWin) onWin();
    // Get coordinates for confetti
    let clientX, clientY;
    if ('touches' in event) {
      clientX = event.touches[0].clientX;
      clientY = event.touches[0].clientY;
    } else {
      clientX = (event as React.MouseEvent).clientX;
      clientY = (event as React.MouseEvent).clientY;
    }

    confetti({
      particleCount: 30,
      spread: 50,
      origin: {
        x: clientX / window.innerWidth,
        y: clientY / window.innerHeight
      },
      colors: ['#FFC700', '#FF0000', '#2E3192', '#1BFFFF']
    });

    // Play pop sound (using Web Audio API for simple beep if no asset)
    try {
      const ctx = getAudioContext();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.type = 'sine';
      osc.frequency.setValueAtTime(800, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(300, ctx.currentTime + 0.1);
      gain.gain.setValueAtTime(1, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.1);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.1);
    } catch (e) {
      // Ignore audio errors
    }

    setBalloons((prev) => prev.filter((b) => b.id !== id));
  };

  // +++ أضيف بناءً على طلبك: تغذية راجعة للإجراء الخاطئ (الضغط خارج البالون) +++
  const handleMiss = () => {
    try {
      const ctx = getAudioContext();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.type = 'sine';
      osc.frequency.setValueAtTime(150, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(100, ctx.currentTime + 0.1);
      gain.gain.setValueAtTime(0.2, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.1);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.1);
    } catch (e) {}
  };

  return (
    <div className="w-full h-full relative bg-sky-200 overflow-hidden" onPointerDown={handleMiss}>
      <button 
        onClick={onBack}
        className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg"
      >
        <ArrowLeft size={32} className="text-sky-800" />
      </button>

      <AnimatePresence>
        {balloons.map((balloon) => (
          <motion.div
            key={balloon.id}
            initial={{ y: '110vh', scale: 0 }}
            animate={{ y: '-20vh', scale: 1 }}
            exit={{ scale: 0, opacity: 0 }}
            transition={{ 
              y: { duration: balloon.speed, ease: 'linear' },
              scale: { duration: 0.3 }
            }}
            onAnimationComplete={() => {
              setBalloons((prev) => prev.filter((b) => b.id !== balloon.id));
            }}
            onPointerDown={(e) => popBalloon(balloon.id, e)}
            className={`absolute w-24 h-32 rounded-[50%] ${balloon.color} shadow-inner cursor-pointer flex items-center justify-center`}
            style={{
              left: `${balloon.x}vw`,
              boxShadow: 'inset -10px -10px 20px rgba(0,0,0,0.2), inset 10px 10px 20px rgba(255,255,255,0.4)'
            }}
          >
            {/* Balloon string */}
            <div className="absolute -bottom-6 w-1 h-8 bg-gray-400/50"></div>
            {/* Balloon knot */}
            <div className="absolute -bottom-2 w-4 h-3 bg-inherit rounded-full"></div>
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  );
}
