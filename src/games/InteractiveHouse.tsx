import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft, Sparkles, Shirt, Trash2 } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface InteractiveHouseProps {
  onBack: () => void;
  onWin?: () => void;
}

export default function InteractiveHouse({ onBack, onWin }: InteractiveHouseProps) {
  const [mode, setMode] = useState<'explore' | 'cleanup' | 'laundry'>('explore');
  const [showTutorial, setShowTutorial] = useState(true);

  // Explore State
  const [lightOn, setLightOn] = useState(true);
  const [windowOpen, setWindowOpen] = useState(false);
  const [doorOpen, setDoorOpen] = useState(false);
  const [tvOn, setTvOn] = useState(false);
  const [interactions, setInteractions] = useState(0);

  // Clean Up State
  const [messyItems, setMessyItems] = useState([
    { id: 1, emoji: '🧸', type: 'toy', x: 20, y: 30 },
    { id: 2, emoji: '🚗', type: 'toy', x: 40, y: 40 },
    { id: 3, emoji: '👕', type: 'clothes', x: 60, y: 35 },
    { id: 4, emoji: '🧦', type: 'clothes', x: 80, y: 45 },
    { id: 5, emoji: '⚽', type: 'toy', x: 30, y: 20 },
  ]);

  // Laundry State
  const [laundryItems, setLaundryItems] = useState([
    { id: 1, emoji: '👕', color: 'white', x: 20, y: 20 },
    { id: 2, emoji: '👗', color: 'color', x: 40, y: 30 },
    { id: 3, emoji: '🧦', color: 'white', x: 60, y: 25 },
    { id: 4, emoji: '👖', color: 'color', x: 80, y: 35 },
    { id: 5, emoji: '👚', color: 'color', x: 50, y: 40 },
  ]);

  const speak = (text: string) => {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.lang = 'ar-EG';
      utterance.rate = 0.8;
      window.speechSynthesis.speak(utterance);
    }
  };

  const handleInteract = (item: string, action: () => void) => {
    if (showTutorial) setShowTutorial(false);
    speak(item);
    action();
    
    const newCount = interactions + 1;
    setInteractions(newCount);
    if (newCount % 10 === 0 && onWin) {
      onWin();
      confetti({ particleCount: 50, spread: 60, origin: { y: 0.8 } });
    }
  };

  const handleCleanUp = (id: number, type: string, target: string) => {
    if (type === target) {
      speak('شاطر!');
      setMessyItems(prev => prev.filter(item => item.id !== id));
      if (messyItems.length === 1) {
        speak('الأوضة بقت نظيفة!');
        confetti({ particleCount: 100, spread: 70, origin: { y: 0.6 } });
        if (onWin) onWin();
      }
    } else {
      speak('لا، مكانها مش هنا');
    }
  };

  const handleLaundrySort = (id: number, color: string, target: string) => {
    if (color === target) {
      speak('ممتاز!');
      setLaundryItems(prev => prev.filter(item => item.id !== id));
      if (laundryItems.length === 1) {
        speak('خلصنا الغسيل!');
        confetti({ particleCount: 100, spread: 70, origin: { y: 0.6 } });
        if (onWin) onWin();
      }
    } else {
      speak('حطها في السبت الصح');
    }
  };

  return (
    <div className={`w-full h-full relative transition-colors duration-1000 overflow-hidden ${mode === 'explore' && !lightOn ? 'bg-slate-900' : 'bg-amber-50'}`}>
      <TutorialHand show={showTutorial} y={100} action="tap" />
      <button onClick={onBack} className="absolute top-6 left-6 z-[100] bg-white/80 backdrop-blur p-4 rounded-full shadow-lg hover:bg-gray-100 active:scale-95 transition-all">
        <ArrowLeft size={32} className="text-slate-800" />
      </button>

      {/* Mode Switcher */}
      <div className="absolute top-6 right-6 z-50 flex gap-2">
        <button 
          onClick={() => { setMode('explore'); speak('استكشاف البيت'); }}
          className={`px-4 py-2 rounded-full font-bold shadow-md transition-colors ${mode === 'explore' ? 'bg-amber-500 text-white' : 'bg-white text-amber-700'}`}
        >
          استكشاف 🏠
        </button>
        <button 
          onClick={() => { setMode('cleanup'); speak('ترتيب الأوضة'); }}
          className={`px-4 py-2 rounded-full font-bold shadow-md transition-colors ${mode === 'cleanup' ? 'bg-green-500 text-white' : 'bg-white text-green-700'}`}
        >
          ترتيب 🧹
        </button>
        <button 
          onClick={() => { setMode('laundry'); speak('فرز الغسيل'); }}
          className={`px-4 py-2 rounded-full font-bold shadow-md transition-colors ${mode === 'laundry' ? 'bg-blue-500 text-white' : 'bg-white text-blue-700'}`}
        >
          غسيل 🧺
        </button>
      </div>

      <div className="absolute inset-0 flex items-center justify-center p-4 pt-24">
        <div className={`relative w-full max-w-4xl aspect-[4/3] rounded-[3rem] border-8 transition-colors duration-1000 overflow-hidden ${mode === 'explore' && !lightOn ? 'bg-slate-800 border-slate-700' : 'bg-orange-100 border-orange-200'}`}>
          
          {/* Floor */}
          <div className={`absolute bottom-0 w-full h-[40%] transition-colors duration-1000 ${mode === 'explore' && !lightOn ? 'bg-slate-700' : 'bg-amber-200'}`} />

          {mode === 'explore' && (
            <>
              {/* Window */}
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => handleInteract(windowOpen ? 'شباك مفتوح' : 'شباك مقفول', () => setWindowOpen(!windowOpen))}
                className="absolute top-[15%] left-[10%] w-[25%] aspect-square bg-sky-200 border-8 border-amber-700 flex items-center justify-center overflow-hidden shadow-inner"
              >
                <div className={`absolute inset-0 bg-sky-900 transition-opacity duration-1000 ${windowOpen ? 'opacity-0' : 'opacity-50'}`} />
                <span className="text-5xl md:text-7xl z-10">{windowOpen ? '☀️' : '🪟'}</span>
              </motion.button>

              {/* Door */}
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => handleInteract(doorOpen ? 'باب مفتوح' : 'باب مقفول', () => setDoorOpen(!doorOpen))}
                className="absolute bottom-0 right-[40%] w-[20%] h-[55%] bg-amber-800 border-t-8 border-l-8 border-r-8 border-amber-900 flex items-center justify-start p-4 origin-right transition-transform duration-500 shadow-xl"
                style={{ transform: doorOpen ? 'perspective(1000px) rotateY(-60deg)' : 'none' }}
              >
                <div className="w-4 h-4 md:w-6 md:h-6 rounded-full bg-yellow-400 shadow-sm" />
              </motion.button>

              {/* Lamp */}
              <motion.button
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.9 }}
                onClick={() => handleInteract(lightOn ? 'نور منور' : 'نور مطفي', () => setLightOn(!lightOn))}
                className="absolute top-0 left-1/2 -translate-x-1/2 flex flex-col items-center z-20"
              >
                <div className="w-2 h-12 md:h-20 bg-slate-400" />
                <div className={`w-16 h-12 md:w-24 md:h-16 rounded-b-full transition-colors duration-500 flex items-center justify-center ${lightOn ? 'bg-yellow-300 shadow-[0_20px_50px_rgba(253,224,71,0.8)]' : 'bg-slate-500'}`}>
                  <span className="text-3xl md:text-4xl mt-2">💡</span>
                </div>
              </motion.button>

              {/* TV */}
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => handleInteract(tvOn ? 'تلفزيون شغال' : 'تلفزيون مقفول', () => setTvOn(!tvOn))}
                className="absolute top-[20%] right-[10%] w-[25%] aspect-video bg-slate-800 border-4 border-slate-900 rounded-xl flex items-center justify-center overflow-hidden shadow-lg"
              >
                {tvOn ? (
                  <div className="w-full h-full bg-sky-400 flex items-center justify-center">
                    <span className="text-4xl md:text-6xl">📺 🦁</span>
                  </div>
                ) : (
                  <div className="w-full h-full bg-black" />
                )}
              </motion.button>

              {/* Chair */}
              <motion.button
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.9 }}
                onClick={() => handleInteract('كرسي', () => {})}
                className="absolute bottom-[5%] right-[10%] text-[4rem] md:text-[6rem] drop-shadow-2xl z-10"
              >
                🪑
              </motion.button>

              {/* Bed */}
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => handleInteract('سرير', () => {})}
                className="absolute bottom-[5%] left-[10%] text-[5rem] md:text-[8rem] drop-shadow-2xl z-10"
              >
                🛏️
              </motion.button>
            </>
          )}

          {mode === 'cleanup' && (
            <>
              {/* Targets */}
              <div className="absolute bottom-[10%] left-[10%] w-32 h-32 bg-amber-700 rounded-xl border-4 border-amber-900 flex flex-col items-center justify-center shadow-xl z-10">
                <span className="text-4xl mb-2">📦</span>
                <span className="text-white font-bold bg-black/30 px-2 rounded">ألعاب</span>
              </div>
              
              <div className="absolute bottom-[10%] right-[10%] w-32 h-48 bg-slate-700 rounded-t-xl border-4 border-slate-800 flex flex-col items-center justify-center shadow-xl z-10">
                <span className="text-4xl mb-2">🚪</span>
                <span className="text-white font-bold bg-black/30 px-2 rounded">هدوم</span>
              </div>

              {/* Draggable Items */}
              {messyItems.map((item) => (
                <motion.div
                  key={item.id}
                  drag
                  dragMomentum={false}
                  onDragEnd={(e, info) => {
                    const x = info.point.x;
                    const w = window.innerWidth;
                    // Simple hit detection based on screen halves
                    if (x < w / 2) {
                      handleCleanUp(item.id, item.type, 'toy');
                    } else {
                      handleCleanUp(item.id, item.type, 'clothes');
                    }
                  }}
                  className="absolute text-5xl md:text-6xl drop-shadow-xl cursor-grab active:cursor-grabbing z-20"
                  style={{ left: `${item.x}%`, top: `${item.y}%` }}
                  whileHover={{ scale: 1.2 }}
                  whileTap={{ scale: 1.1 }}
                >
                  {item.emoji}
                </motion.div>
              ))}

              {messyItems.length === 0 && (
                <div className="absolute inset-0 flex items-center justify-center z-30">
                  <div className="bg-white/90 px-8 py-4 rounded-full shadow-2xl flex items-center gap-4">
                    <Sparkles className="text-yellow-500" size={32} />
                    <span className="text-3xl font-bold text-green-600">الأوضة نظيفة!</span>
                  </div>
                </div>
              )}
            </>
          )}

          {mode === 'laundry' && (
            <>
              {/* Baskets */}
              <div className="absolute bottom-[10%] left-[20%] w-40 h-32 bg-slate-200 rounded-b-3xl border-4 border-slate-300 flex flex-col items-center justify-center shadow-xl z-10">
                <span className="text-4xl mb-2">🧺</span>
                <span className="text-slate-700 font-bold bg-white/50 px-2 rounded">أبيض</span>
              </div>
              
              <div className="absolute bottom-[10%] right-[20%] w-40 h-32 bg-blue-200 rounded-b-3xl border-4 border-blue-400 flex flex-col items-center justify-center shadow-xl z-10">
                <span className="text-4xl mb-2">🧺</span>
                <span className="text-blue-800 font-bold bg-white/50 px-2 rounded">ألوان</span>
              </div>

              {/* Draggable Clothes */}
              {laundryItems.map((item) => (
                <motion.div
                  key={item.id}
                  drag
                  dragMomentum={false}
                  onDragEnd={(e, info) => {
                    const x = info.point.x;
                    const w = window.innerWidth;
                    if (x < w / 2) {
                      handleLaundrySort(item.id, item.color, 'white');
                    } else {
                      handleLaundrySort(item.id, item.color, 'color');
                    }
                  }}
                  className="absolute text-5xl md:text-6xl drop-shadow-xl cursor-grab active:cursor-grabbing z-20"
                  style={{ left: `${item.x}%`, top: `${item.y}%` }}
                  whileHover={{ scale: 1.2 }}
                  whileTap={{ scale: 1.1 }}
                >
                  {item.emoji}
                </motion.div>
              ))}

              {laundryItems.length === 0 && (
                <div className="absolute inset-0 flex items-center justify-center z-30">
                  <div className="bg-white/90 px-8 py-4 rounded-full shadow-2xl flex items-center gap-4">
                    <Shirt className="text-blue-500" size={32} />
                    <span className="text-3xl font-bold text-blue-600">خلصنا الغسيل!</span>
                  </div>
                </div>
              )}
            </>
          )}

        </div>
      </div>
    </div>
  );
}
