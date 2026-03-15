import { useState } from 'react';
import { ArrowLeft } from 'lucide-react';
import { motion } from 'motion/react';

const AVAILABLE_STICKERS = ['🌟', '🚗', '🦖', '🦄', '🚀', '🐱', '🎈', '🍎', '⚽', '🎨'];

interface StickerBookProps {
  stars: number;
  onBack: () => void;
}

export default function StickerBook({ stars, onBack }: StickerBookProps) {
  const [placedStickers, setPlacedStickers] = useState<{id: number, emoji: string, x: number, y: number}[]>([]);

  const handleAddSticker = (emoji: string) => {
    if (stars <= placedStickers.length) return;
    setPlacedStickers([...placedStickers, { 
      id: Date.now(), 
      emoji, 
      x: Math.random() * (window.innerWidth - 100), 
      y: Math.random() * (window.innerHeight - 300) + 150 
    }]);
  };

  return (
    <div className="w-full h-full bg-amber-50 relative overflow-hidden flex flex-col">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg border-2 border-amber-200 text-amber-800">
        <ArrowLeft size={32} />
      </button>
      
      <div className="p-6 text-center z-10 bg-white/80 backdrop-blur-md shadow-sm border-b-4 border-amber-200">
        <h2 className="text-3xl md:text-4xl font-bold text-amber-800 mb-2">كتاب الملصقات 📖</h2>
        <p className="text-amber-600 font-bold text-lg">لديك {stars} نجمة! (استخدمت {placedStickers.length})</p>
        <div className="flex justify-center gap-3 mt-4 overflow-x-auto pb-4 px-4 snap-x">
          {AVAILABLE_STICKERS.map(s => (
            <button 
              key={s} 
              onClick={() => handleAddSticker(s)}
              className={`text-4xl md:text-5xl p-3 bg-white rounded-2xl shadow-md border-2 border-amber-100 snap-center shrink-0 transition-transform ${stars <= placedStickers.length ? 'opacity-50 grayscale cursor-not-allowed' : 'hover:scale-110 active:scale-95'}`}
            >
              {s}
            </button>
          ))}
        </div>
      </div>

      <div className="flex-1 relative w-full h-full">
        {placedStickers.map(s => (
          <motion.div
            key={s.id}
            drag
            dragMomentum={false}
            initial={{ x: window.innerWidth/2 - 40, y: 100, scale: 0 }}
            animate={{ x: s.x, y: s.y, scale: 1 }}
            className="absolute text-7xl cursor-grab active:cursor-grabbing drop-shadow-xl"
          >
            {s.emoji}
          </motion.div>
        ))}
      </div>
    </div>
  );
}
