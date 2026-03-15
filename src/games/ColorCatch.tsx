import { useState, useEffect } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';

interface ColorCatchProps {
  onBack: () => void;
  onWin?: () => void;
}

const COLORS = [
  { id: 'red', name: 'أحمر', hex: '#ef4444', colorClass: 'bg-red-500' },
  { id: 'blue', name: 'أزرق', hex: '#3b82f6', colorClass: 'bg-blue-500' },
  { id: 'green', name: 'أخضر', hex: '#22c55e', colorClass: 'bg-green-500' },
  { id: 'yellow', name: 'أصفر', hex: '#eab308', colorClass: 'bg-yellow-500' },
  { id: 'purple', name: 'بنفسجي', hex: '#a855f7', colorClass: 'bg-purple-500' },
  { id: 'orange', name: 'برتقالي', hex: '#f97316', colorClass: 'bg-orange-500' },
];

const SHAPES = [
  { id: 'circle', name: 'دائرة', style: { borderRadius: '9999px' } },
  { id: 'square', name: 'مربع', style: { borderRadius: '16px' } },
  { id: 'triangle', name: 'مثلث', style: { clipPath: 'polygon(50% 0%, 0% 100%, 100% 100%)', borderRadius: '0' } },
  { id: 'star', name: 'نجمة', style: { clipPath: 'polygon(50% 0%, 61% 35%, 98% 35%, 68% 57%, 79% 91%, 50% 70%, 21% 91%, 32% 57%, 2% 35%, 39% 35%)', borderRadius: '0' } },
];

interface GameItem {
  id: string;
  color: typeof COLORS[0];
  shape: typeof SHAPES[0];
}

export default function ColorCatch({ onBack, onWin }: ColorCatchProps) {
  const [level, setLevel] = useState(1);
  const [targetItem, setTargetItem] = useState<GameItem | null>(null);
  const [options, setOptions] = useState<GameItem[]>([]);
  const [shakeId, setShakeId] = useState<string | null>(null);

  const generateRound = (currentLevel: number) => {
    // Determine available shapes and colors based on level
    const availableColors = COLORS.slice(0, Math.min(3 + Math.floor(currentLevel / 2), COLORS.length));
    const availableShapes = SHAPES.slice(0, Math.min(1 + Math.floor(currentLevel / 3), SHAPES.length));
    
    // Generate all possible combinations for this level
    const allCombinations: GameItem[] = [];
    availableColors.forEach(c => {
      availableShapes.forEach(s => {
        allCombinations.push({ id: `${c.id}-${s.id}`, color: c, shape: s });
      });
    });

    // Shuffle and pick options
    const shuffled = allCombinations.sort(() => Math.random() - 0.5);
    const numOptions = Math.min(3 + Math.floor(currentLevel / 4), 6); // 3 to 6 options
    const selectedOptions = shuffled.slice(0, numOptions);
    const target = selectedOptions[Math.floor(Math.random() * selectedOptions.length)];
    
    setOptions(selectedOptions);
    setTargetItem(target);

    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const shapeName = availableShapes.length > 1 ? target.shape.name : 'اللون';
      const utterance = new SpeechSynthesisUtterance(`أين ال${shapeName} ال${target.color.name}؟`);
      utterance.lang = 'ar-SA';
      utterance.rate = 0.9;
      window.speechSynthesis.speak(utterance);
    }
  };

  useEffect(() => {
    generateRound(level);
  }, [level]);

  const handleSelect = (itemId: string, e: React.MouseEvent | React.TouchEvent) => {
    if (!targetItem) return;
    
    if (itemId === targetItem.id) {
      // Success
      if (onWin) onWin();
      let clientX, clientY;
      if ('touches' in e) {
        clientX = e.touches[0].clientX;
        clientY = e.touches[0].clientY;
      } else {
        clientX = (e as React.MouseEvent).clientX;
        clientY = (e as React.MouseEvent).clientY;
      }

      confetti({
        particleCount: 50,
        spread: 80,
        origin: {
          x: clientX / window.innerWidth,
          y: clientY / window.innerHeight
        },
        colors: [targetItem.color.hex, '#ffffff']
      });

      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
        const utterance = new SpeechSynthesisUtterance('شاطر!');
        utterance.lang = 'ar-SA';
        utterance.rate = 1;
        window.speechSynthesis.speak(utterance);
      }

      setTimeout(() => setLevel(l => l + 1), 1500);
    } else {
      // Wrong
      setShakeId(itemId);
      setTimeout(() => setShakeId(null), 500);
      
      // +++ أضيف بناءً على طلبك: تغذية راجعة صوتية للإجابة الخاطئة +++
      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
        const utterance = new SpeechSynthesisUtterance('حاول مرة أخرى');
        utterance.lang = 'ar-SA';
        utterance.rate = 1;
        window.speechSynthesis.speak(utterance);
      }

      try {
        const ctx = new (window.AudioContext || (window as any).webkitAudioContext)();
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(200, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(100, ctx.currentTime + 0.2);
        gain.gain.setValueAtTime(0.5, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.2);
        osc.start(ctx.currentTime);
        osc.stop(ctx.currentTime + 0.2);
      } catch (err) {}
    }
  };

  if (!targetItem) return null;

  return (
    <div className="w-full h-full relative bg-amber-50 flex flex-col items-center justify-center p-4">
      <button 
        onClick={onBack}
        className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg"
      >
        <ArrowLeft size={32} className="text-amber-800" />
      </button>

      {/* +++ أضيف بناءً على طلبك: مؤشر المرحلة +++ */}
      <div className="absolute top-24 right-6 z-50 bg-white px-4 py-2 rounded-full shadow-lg font-bold text-amber-800">
        مرحلة {level}
      </div>

      <h2 className="text-3xl md:text-5xl font-bold text-amber-900 mb-12 text-center bg-white px-8 py-4 rounded-full shadow-sm border-4 border-amber-200">
        أين ال{level >= 3 ? targetItem.shape.name : 'لون'} <span style={{ color: targetItem.color.hex }}>ال{targetItem.color.name}</span>؟
      </h2>

      <div className="flex flex-wrap justify-center gap-6 md:gap-12 max-w-4xl">
        {options.map((item) => (
          <motion.button
            key={item.id}
            animate={shakeId === item.id ? { x: [-10, 10, -10, 10, 0] } : {}}
            transition={{ duration: 0.4 }}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.9 }}
            onPointerDown={(e) => handleSelect(item.id, e)}
            className={`w-24 h-24 md:w-32 md:h-32 ${item.color.colorClass} shadow-xl border-4 border-white/50`}
            style={item.shape.style}
          />
        ))}
      </div>
    </div>
  );
}
