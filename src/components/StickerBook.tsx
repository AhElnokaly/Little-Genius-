import { useState, useEffect } from 'react';
import { ArrowLeft, Trash2, Image as ImageIcon, Puzzle, Ear, CheckCircle2 } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { speak } from '../utils/audio';

const AVAILABLE_STICKERS = [
  { emoji: '🌟', name: 'نجمة', sound: 'نَجْمَة' },
  { emoji: '🚗', name: 'سيارة', sound: 'سَيَّارَة' },
  { emoji: '🦖', name: 'ديناصور', sound: 'دِينَاصُور' },
  { emoji: '🦄', name: 'وحيد القرن', sound: 'وَحِيدُ الْقَرْن' },
  { emoji: '🚀', name: 'صاروخ', sound: 'صَارُوخ' },
  { emoji: '🐱', name: 'قطة', sound: 'قِطَّة' },
  { emoji: '🎈', name: 'بالون', sound: 'بَالُون' },
  { emoji: '🍎', name: 'تفاحة', sound: 'تُفَّاحَة' },
  { emoji: '⚽', name: 'كرة', sound: 'كُرَة' },
  { emoji: '🎨', name: 'ألوان', sound: 'أَلْوَان' },
  { emoji: '🌳', name: 'شجرة', sound: 'شَجَرَة' },
  { emoji: '☀️', name: 'شمس', sound: 'شَمْس' },
  { emoji: '☁️', name: 'سحابة', sound: 'سَحَابَة' },
  { emoji: '🏠', name: 'بيت', sound: 'بَيْت' },
  { emoji: '🦋', name: 'فراشة', sound: 'فَرَاشَة' }
];

type GameMode = 'builder' | 'shadow' | 'sound';

interface StickerBookProps {
  stars: number;
  onBack: () => void;
}

export default function StickerBook({ stars, onBack }: StickerBookProps) {
  const [mode, setMode] = useState<GameMode>('builder');
  const [placedStickers, setPlacedStickers] = useState<{id: number, emoji: string, left: string, top: string}[]>([]);
  
  // Shadow Match State
  const [shadowTarget, setShadowTarget] = useState(AVAILABLE_STICKERS[0]);
  const [shadowOptions, setShadowOptions] = useState<typeof AVAILABLE_STICKERS>([]);
  const [shadowMatched, setShadowMatched] = useState(false);

  // Sound Detective State
  const [soundTarget, setSoundTarget] = useState(AVAILABLE_STICKERS[0]);
  const [soundOptions, setSoundOptions] = useState<typeof AVAILABLE_STICKERS>([]);
  const [soundGuessed, setSoundGuessed] = useState(false);

  // Initialize mini-games
  useEffect(() => {
    if (mode === 'shadow') {
      setupShadowGame();
    } else if (mode === 'sound') {
      setupSoundGame();
    }
  }, [mode]);

  const setupShadowGame = () => {
    const shuffled = [...AVAILABLE_STICKERS].sort(() => 0.5 - Math.random());
    const target = shuffled[0];
    const options = shuffled.slice(0, 4);
    if (!options.includes(target)) options[0] = target;
    options.sort(() => 0.5 - Math.random());
    
    setShadowTarget(target);
    setShadowOptions(options);
    setShadowMatched(false);
  };

  const setupSoundGame = () => {
    const shuffled = [...AVAILABLE_STICKERS].sort(() => 0.5 - Math.random());
    const target = shuffled[0];
    const options = shuffled.slice(0, 6);
    if (!options.includes(target)) options[0] = target;
    options.sort(() => 0.5 - Math.random());
    
    setSoundTarget(target);
    setSoundOptions(options);
    setSoundGuessed(false);
    setTimeout(() => playTargetSound(target.sound), 500);
  };

  const playTargetSound = (text: string) => {
    speak(text);
  };

  const handleAddSticker = (emoji: string) => {
    if (mode !== 'builder') return; // +++ تم التعديل بناءً على طلبك: إتاحة الملصقات بدون نجوم +++
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

  const handleShadowMatch = (sticker: typeof AVAILABLE_STICKERS[0]) => {
    if (sticker.emoji === shadowTarget.emoji) {
      setShadowMatched(true);
      speak('أَحْسَنْت');
      setTimeout(setupShadowGame, 2000);
    } else {
      speak('حَاوِل مَرَّة أُخْرَى');
    }
  };

  const handleSoundGuess = (sticker: typeof AVAILABLE_STICKERS[0]) => {
    if (sticker.emoji === soundTarget.emoji) {
      setSoundGuessed(true);
      speak('مُمْتَاز');
      setTimeout(setupSoundGame, 2000);
    } else {
      speak('حَاوِل مَرَّة أُخْرَى');
    }
  };

  return (
    <div className="w-full h-full bg-sky-100 relative overflow-hidden flex flex-col">
      <button onClick={onBack} className="absolute top-6 left-6 z-[70] bg-white p-4 rounded-full shadow-lg border-2 border-amber-200 text-amber-800 hover:bg-amber-50">
        <ArrowLeft size={32} />
      </button>
      
      {mode === 'builder' && placedStickers.length > 0 && (
        <button onClick={handleClear} className="absolute top-6 right-6 z-[70] bg-white p-4 rounded-full shadow-lg border-2 border-red-200 text-red-500 hover:bg-red-50">
          <Trash2 size={32} />
        </button>
      )}

      {/* Mode Selector */}
      <div className="absolute top-20 md:top-6 left-1/2 -translate-x-1/2 z-[60] flex flex-wrap justify-center gap-2 bg-white/90 backdrop-blur-md p-2 rounded-3xl shadow-md border-2 border-amber-200 w-[90%] md:w-auto max-w-md"> {/* +++ تم التعديل بناءً على طلبك: إصلاح تداخل الأزرار +++ */}
        <button 
          onClick={() => setMode('builder')}
          className={`p-2 md:p-3 rounded-full flex items-center gap-2 transition-colors ${mode === 'builder' ? 'bg-amber-400 text-white' : 'text-amber-600 hover:bg-amber-100'}`}
        >
          <ImageIcon size={20} className="md:w-6 md:h-6" />
          <span className="text-sm md:text-base font-bold">لوحتي</span>
        </button>
        <button 
          onClick={() => setMode('shadow')}
          className={`p-2 md:p-3 rounded-full flex items-center gap-2 transition-colors ${mode === 'shadow' ? 'bg-indigo-400 text-white' : 'text-indigo-600 hover:bg-indigo-100'}`}
        >
          <Puzzle size={20} className="md:w-6 md:h-6" />
          <span className="text-sm md:text-base font-bold">طابق الظل</span>
        </button>
        <button 
          onClick={() => setMode('sound')}
          className={`p-2 md:p-3 rounded-full flex items-center gap-2 transition-colors ${mode === 'sound' ? 'bg-emerald-400 text-white' : 'text-emerald-600 hover:bg-emerald-100'}`}
        >
          <Ear size={20} className="md:w-6 md:h-6" />
          <span className="text-sm md:text-base font-bold">اسمع واختار</span>
        </button>
      </div>
      
      {/* Builder Mode Header */}
      {mode === 'builder' && (
        <div className="pt-40 md:pt-28 pb-6 px-6 text-center z-10 bg-white/90 backdrop-blur-md shadow-sm border-b-4 border-amber-200">
          <h2 className="text-3xl md:text-4xl font-bold text-amber-800 mb-2">كتاب الملصقات 📖</h2>
          {/* +++ تم التعديل بناءً على طلبك: إزالة شرط النجوم +++ */}
          <p className="text-amber-600 font-bold text-lg">استخدمت {placedStickers.length} ملصق</p>
          <div className="flex justify-center gap-3 mt-4 overflow-x-auto pb-4 px-4 snap-x">
            {AVAILABLE_STICKERS.map(s => (
              <button 
                key={s.emoji} 
                onClick={() => handleAddSticker(s.emoji)}
                className="text-4xl md:text-5xl p-3 bg-white rounded-2xl shadow-md border-2 border-amber-100 snap-center shrink-0 transition-transform hover:scale-110 active:scale-95"
              >
                {s.emoji}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Game Area */}
      <div className="flex-1 relative w-full h-full bg-gradient-to-b from-sky-200 to-sky-100 overflow-hidden">
        
        {/* Builder Mode Background */}
        {mode === 'builder' && (
          <>
            <div className="absolute top-10 right-10 w-32 h-32 bg-yellow-300 rounded-full opacity-80 blur-lg" />
            <div className="absolute top-12 right-12 w-24 h-24 bg-yellow-400 rounded-full shadow-[0_0_40px_rgba(250,204,21,0.6)]" />
            <div className="absolute top-20 left-20 w-32 h-12 bg-white rounded-full opacity-80 blur-sm" />
            <div className="absolute top-16 left-28 w-20 h-20 bg-white rounded-full opacity-80 blur-sm" />
            <div className="absolute bottom-0 left-0 right-0 h-1/3 bg-gradient-to-t from-green-500 to-green-400 rounded-t-[100px] shadow-[0_-10px_30px_rgba(74,222,128,0.3)]" />
            
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
          </>
        )}

        {/* Shadow Match Mode */}
        {mode === 'shadow' && (
          <div className="flex flex-col items-center justify-center h-full p-8 pt-40 md:pt-24">
            <h2 className="text-3xl font-bold text-indigo-800 mb-8 bg-white/80 px-8 py-4 rounded-3xl shadow-sm">
              طابق الملصق مع ظله
            </h2>
            
            <div className="relative w-48 h-48 md:w-64 md:h-64 bg-white/50 rounded-3xl border-4 border-dashed border-indigo-300 flex items-center justify-center mb-8 md:mb-12">
              <AnimatePresence mode="wait">
                <motion.div
                  key={shadowTarget.emoji + (shadowMatched ? 'matched' : 'shadow')}
                  initial={{ scale: 0.5, opacity: 0 }}
                  animate={{ scale: 1, opacity: 1 }}
                  exit={{ scale: 0.5, opacity: 0 }}
                  className={`text-7xl md:text-9xl ${!shadowMatched ? 'brightness-0 opacity-30' : 'drop-shadow-2xl'}`}
                >
                  {shadowTarget.emoji}
                </motion.div>
              </AnimatePresence>
              {shadowMatched && (
                <motion.div 
                  initial={{ scale: 0 }} animate={{ scale: 1 }}
                  className="absolute -top-4 -right-4 bg-green-500 text-white rounded-full p-2"
                >
                  <CheckCircle2 size={32} className="md:w-12 md:h-12" />
                </motion.div>
              )}
            </div>

            <div className="flex flex-wrap justify-center gap-3 md:gap-4">
              {shadowOptions.map((sticker, index) => (
                <motion.button
                  key={index}
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.9 }}
                  onClick={() => handleShadowMatch(sticker)}
                  disabled={shadowMatched}
                  className="text-4xl md:text-6xl bg-white p-4 md:p-6 rounded-3xl shadow-lg border-4 border-indigo-100 hover:border-indigo-300 transition-colors"
                >
                  {sticker.emoji}
                </motion.button>
              ))}
            </div>
          </div>
        )}

        {/* Sound Detective Mode */}
        {mode === 'sound' && (
          <div className="flex flex-col items-center justify-center h-full p-8 pt-40 md:pt-24">
            <h2 className="text-3xl font-bold text-emerald-800 mb-8 bg-white/80 px-8 py-4 rounded-3xl shadow-sm text-center">
              ابحث عن الملصق الصحيح
            </h2>
            
            <motion.button
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
              onClick={() => playTargetSound(soundTarget.sound)}
              className="mb-8 md:mb-12 bg-emerald-500 text-white p-6 md:p-8 rounded-full shadow-xl border-4 border-emerald-300 flex flex-col items-center gap-2 md:gap-4"
            >
              <Ear size={48} className="md:w-16 md:h-16" />
              <span className="text-lg md:text-xl font-bold">اسمع الصوت</span>
            </motion.button>

            <div className="grid grid-cols-2 md:grid-cols-3 gap-4 md:gap-6 max-w-2xl w-full">
              {soundOptions.map((sticker, index) => (
                <motion.button
                  key={index}
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.9 }}
                  onClick={() => handleSoundGuess(sticker)}
                  disabled={soundGuessed}
                  className="relative text-5xl md:text-7xl bg-white p-4 md:p-6 rounded-3xl shadow-lg border-4 border-emerald-100 hover:border-emerald-300 transition-colors flex items-center justify-center aspect-square"
                >
                  {sticker.emoji}
                  {soundGuessed && sticker.emoji === soundTarget.emoji && (
                    <motion.div 
                      initial={{ scale: 0 }} animate={{ scale: 1 }}
                      className="absolute -top-4 -right-4 bg-green-500 text-white rounded-full p-2 z-10"
                    >
                      <CheckCircle2 size={32} />
                    </motion.div>
                  )}
                </motion.button>
              ))}
            </div>
          </div>
        )}

      </div>
    </div>
  );
}
