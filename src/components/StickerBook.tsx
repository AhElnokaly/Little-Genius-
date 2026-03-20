import { useState } from 'react';
import { ArrowLeft, Trash2 } from 'lucide-react';
import { motion } from 'motion/react';

const AVAILABLE_STICKERS = ['🌟', '🚗', '🦖', '🦄', '🚀', '🐱', '🎈', '🍎', '⚽', '🎨', '🌳', '☀️', '☁️', '🏠', '🦋']; // +++ أضيف بناءً على طلبك +++

interface StickerBookProps {
  stars: number;
  onBack: () => void;
}

export default function StickerBook({ stars, onBack }: StickerBookProps) {
  const [placedStickers, setPlacedStickers] = useState<{id: number, emoji: string, left: string, top: string}[]>([]);

  const handleAddSticker = (emoji: string) => {
    if (stars <= placedStickers.length) return;
    setPlacedStickers([...placedStickers, { 
      id: Date.now(), 
      emoji, 
      left: `${Math.random() * 60 + 15}%`,
      top: `${Math.random() * 50 + 10}%`
    }]);
  };

  const handleClear = () => {
    setPlacedStickers([]);
  };

  return (
    <div className="w-full h-full bg-sky-100 relative overflow-hidden flex flex-col">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg border-2 border-amber-200 text-amber-800 hover:bg-amber-50">
        <ArrowLeft size={32} />
      </button>
      
      {placedStickers.length > 0 && (
        <button onClick={handleClear} className="absolute top-6 right-6 z-50 bg-white p-4 rounded-full shadow-lg border-2 border-red-200 text-red-500 hover:bg-red-50">
          <Trash2 size={32} />
        </button>
      )}
      
      <div className="p-6 text-center z-10 bg-white/90 backdrop-blur-md shadow-sm border-b-4 border-amber-200">
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

      {/* +++ أضيف بناءً على طلبك: خلفية تفاعلية (حديقة) +++ */}
      <div className="flex-1 relative w-full h-full bg-gradient-to-b from-sky-200 to-sky-100 overflow-hidden">
        {/* Sun */}
        <div className="absolute top-10 right-10 w-32 h-32 bg-yellow-300 rounded-full opacity-80 blur-lg" />
        <div className="absolute top-12 right-12 w-24 h-24 bg-yellow-400 rounded-full shadow-[0_0_40px_rgba(250,204,21,0.6)]" />
        
        {/* Clouds */}
        <div className="absolute top-20 left-20 w-32 h-12 bg-white rounded-full opacity-80 blur-sm" />
        <div className="absolute top-16 left-28 w-20 h-20 bg-white rounded-full opacity-80 blur-sm" />
        <div className="absolute top-32 right-1/3 w-40 h-16 bg-white rounded-full opacity-60 blur-sm" />
        <div className="absolute top-24 right-[30%] w-24 h-24 bg-white rounded-full opacity-60 blur-sm" />

        {/* Ground */}
        <div className="absolute bottom-0 left-0 right-0 h-1/3 bg-gradient-to-t from-green-500 to-green-400 rounded-t-[100px] shadow-[0_-10px_30px_rgba(74,222,128,0.3)]" />
        <div className="absolute bottom-0 left-[-10%] right-1/2 h-1/4 bg-green-600 rounded-t-[150px] opacity-40" />
        <div className="absolute bottom-0 left-1/2 right-[-10%] h-1/5 bg-green-500 rounded-t-[120px] opacity-50" />

        {placedStickers.map(s => (
          <motion.div
            key={s.id}
            drag
            dragMomentum={false}
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            style={{ left: s.left, top: s.top }}
            className="absolute text-7xl cursor-grab active:cursor-grabbing drop-shadow-xl z-20"
          >
            {s.emoji}
          </motion.div>
        ))}
      </div>
    </div>
  );
}
