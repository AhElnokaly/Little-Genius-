import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft, X, Sun, Moon, CloudRain, Wind, Snowflake, Sprout, Droplet } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';
import { speak } from '../utils/audio';

interface NatureExplorerProps {
  onBack: () => void;
  onWin?: () => void;
}

const NATURE_ITEMS = [
  { id: 'apple', name: 'تفاحة', emoji: '🍎', type: 'fruit', color: 'bg-red-400' },
  { id: 'banana', name: 'موز', emoji: '🍌', type: 'fruit', color: 'bg-yellow-400' },
  { id: 'carrot', name: 'جزر', emoji: '🥕', type: 'vegetable', color: 'bg-orange-400' },
  { id: 'broccoli', name: 'بروكلي', emoji: '🥦', type: 'vegetable', color: 'bg-green-500' },
  { id: 'tomato', name: 'طماطم', emoji: '🍅', type: 'vegetable', color: 'bg-red-500' },
  { id: 'corn', name: 'درة', emoji: '🌽', type: 'vegetable', color: 'bg-yellow-500' },
  { id: 'grapes', name: 'عنب', emoji: '🍇', type: 'fruit', color: 'bg-purple-400' },
  { id: 'watermelon', name: 'بطيخ', emoji: '🍉', type: 'fruit', color: 'bg-rose-400' },
  { id: 'strawberry', name: 'فراولة', emoji: '🍓', type: 'fruit', color: 'bg-red-400' },
  { id: 'mushroom', name: 'مشروم', emoji: '🍄', type: 'nature', color: 'bg-stone-400' },
  { id: 'flower', name: 'وردة', emoji: '🌻', type: 'nature', color: 'bg-amber-400' },
  { id: 'tree', name: 'شجرة', emoji: '🌳', type: 'nature', color: 'bg-emerald-500' },
  { id: 'leaf', name: 'ورقة شجر', emoji: '🍃', type: 'nature', color: 'bg-lime-400' },
  { id: 'cactus', name: 'صبار', emoji: '🌵', type: 'nature', color: 'bg-green-600' },
  { id: 'potato', name: 'بطاطس', emoji: '🥔', type: 'vegetable', color: 'bg-amber-600' },
  { id: 'onion', name: 'بصل', emoji: '🧅', type: 'vegetable', color: 'bg-orange-300' },
  { id: 'eggplant', name: 'بتنجان', emoji: '🍆', type: 'vegetable', color: 'bg-purple-500' },
  { id: 'cherry', name: 'كريز', emoji: '🍒', type: 'fruit', color: 'bg-rose-500' },
  { id: 'peach', name: 'خوخ', emoji: '🍑', type: 'fruit', color: 'bg-orange-400' },
  { id: 'pineapple', name: 'أناناس', emoji: '🍍', type: 'fruit', color: 'bg-yellow-400' },
];

export default function NatureExplorer({ onBack, onWin }: NatureExplorerProps) {
  const [mode, setMode] = useState<'explore' | 'plant'>('explore');
  const [selectedItem, setSelectedItem] = useState<typeof NATURE_ITEMS[0] | null>(null);
  const [showTutorial, setShowTutorial] = useState(true);
  const [viewedCount, setViewedCount] = useState(0);
  
  // Environment State
  const [isNight, setIsNight] = useState(false);
  const [weather, setWeather] = useState<'clear' | 'rain' | 'wind' | 'snow'>('clear');

  // Plant a Seed State
  const [plantStage, setPlantStage] = useState(0); // 0: dirt, 1: seed, 2: watered, 3: sun, 4: grown

  const handleSelect = (item: typeof NATURE_ITEMS[0]) => {
    if (showTutorial) setShowTutorial(false);
    setSelectedItem(item);
    
    speak(item.name);

    const newCount = viewedCount + 1;
    setViewedCount(newCount);
    if (newCount % 5 === 0 && onWin) {
      onWin();
      confetti({
        particleCount: 50,
        spread: 60,
        origin: { y: 0.8 },
        colors: ['#4ade80', '#facc15', '#f87171']
      });
    }
  };

  const handleWeatherChange = (newWeather: 'clear' | 'rain' | 'wind' | 'snow') => {
    setWeather(newWeather);
    const weatherNames = {
      clear: 'جو مشمس',
      rain: 'مطر',
      wind: 'رياح',
      snow: 'تلج'
    };
    speak(weatherNames[newWeather]);
  };

  const toggleDayNight = () => {
    setIsNight(!isNight);
    speak(isNight ? 'النهار طلع' : 'الليل جه');
  };

  const handlePlantAction = () => {
    if (plantStage === 0) {
      speak('حطينا البذرة');
      setPlantStage(1);
    } else if (plantStage === 1) {
      speak('سقيناها مية');
      setPlantStage(2);
    } else if (plantStage === 2) {
      speak('الشمس طلعت عليها');
      setPlantStage(3);
      setTimeout(() => {
        speak('النبتة كبرت! شاطر');
        setPlantStage(4);
        if (onWin) onWin();
        confetti({ particleCount: 100, spread: 70, origin: { y: 0.6 } });
      }, 2000);
    } else if (plantStage === 4) {
      setPlantStage(0);
    }
  };

  return (
    <div className={`w-full h-full relative overflow-y-auto transition-colors duration-1000 ${isNight ? 'bg-slate-900' : 'bg-sky-100'}`}>
      <TutorialHand show={showTutorial} y={100} action="tap" />
      
      {/* Weather Effects Layer */}
      <div className="fixed inset-0 pointer-events-none z-0 overflow-hidden">
        {weather === 'rain' && (
          <div className="absolute inset-0 opacity-50 bg-[url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI0IiBoZWlnaHQ9IjIwIj48cmVjdCB3aWR0aD0iMiIgaGVpZ2h0PSIxMCIgZmlsbD0iIzYwYTVmYSIvPjwvc3ZnPg==')] animate-[rain_0.5s_linear_infinite]" />
        )}
        {weather === 'snow' && (
          <div className="absolute inset-0 opacity-70 bg-[url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMCIgaGVpZ2h0PSIxMCI+PGNpcmNsZSBjeD0iNSIgY3k9IjUiIHI9IjIiIGZpbGw9IiNmZmZmZmYiLz48L3N2Zz4=')] animate-[snow_3s_linear_infinite]" />
        )}
        {weather === 'wind' && (
          <motion.div 
            animate={{ x: ['100%', '-100%'] }}
            transition={{ repeat: Infinity, duration: 2, ease: "linear" }}
            className="absolute top-1/4 w-full h-2 bg-white/30 blur-sm rounded-full"
          />
        )}
      </div>

      <button 
        onClick={onBack}
        className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg"
      >
        <ArrowLeft size={32} className="text-green-800" />
      </button>

      {/* Mode Switcher */}
      <div className="absolute top-6 right-6 z-50 flex gap-2">
        <button 
          onClick={() => { setMode('explore'); speak('مستكشف الطبيعة'); }}
          className={`px-4 py-2 rounded-full font-bold shadow-md transition-colors ${mode === 'explore' ? 'bg-green-500 text-white' : 'bg-white text-green-700'}`}
        >
          استكشاف 🌿
        </button>
        <button 
          onClick={() => { setMode('plant'); speak('ازرع نبتة'); }}
          className={`px-4 py-2 rounded-full font-bold shadow-md transition-colors ${mode === 'plant' ? 'bg-amber-500 text-white' : 'bg-white text-amber-700'}`}
        >
          زراعة 🌱
        </button>
      </div>

      <div className="min-h-full flex flex-col items-center p-4 py-24 relative z-10">
        
        {mode === 'explore' && (
          <>
            {/* Environment Controls */}
            <div className="bg-white/80 backdrop-blur-md p-4 rounded-3xl shadow-lg mb-8 flex flex-wrap justify-center gap-4 border-2 border-white">
              <button 
                onClick={toggleDayNight}
                className={`p-4 rounded-full shadow-inner transition-colors ${isNight ? 'bg-indigo-900 text-yellow-300' : 'bg-sky-200 text-amber-500'}`}
              >
                {isNight ? <Moon size={32} /> : <Sun size={32} />}
              </button>
              <div className="w-px h-12 bg-slate-300 mx-2 self-center" />
              <button onClick={() => handleWeatherChange('clear')} className={`p-4 rounded-full transition-colors ${weather === 'clear' ? 'bg-amber-100 text-amber-500 shadow-inner' : 'bg-white text-slate-400 hover:bg-slate-50'}`}><Sun size={32} /></button>
              <button onClick={() => handleWeatherChange('rain')} className={`p-4 rounded-full transition-colors ${weather === 'rain' ? 'bg-blue-100 text-blue-500 shadow-inner' : 'bg-white text-slate-400 hover:bg-slate-50'}`}><CloudRain size={32} /></button>
              <button onClick={() => handleWeatherChange('wind')} className={`p-4 rounded-full transition-colors ${weather === 'wind' ? 'bg-teal-100 text-teal-500 shadow-inner' : 'bg-white text-slate-400 hover:bg-slate-50'}`}><Wind size={32} /></button>
              <button onClick={() => handleWeatherChange('snow')} className={`p-4 rounded-full transition-colors ${weather === 'snow' ? 'bg-sky-100 text-sky-500 shadow-inner' : 'bg-white text-slate-400 hover:bg-slate-50'}`}><Snowflake size={32} /></button>
            </div>

            <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 gap-4 md:gap-6 w-full max-w-5xl" dir="rtl">
              {NATURE_ITEMS.map((item) => (
                <motion.button
                  key={item.id}
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={() => handleSelect(item)}
                  className={`${item.color} aspect-square rounded-3xl shadow-lg border-4 border-white/50 flex flex-col items-center justify-center text-white overflow-hidden relative ${isNight ? 'brightness-75' : ''}`}
                >
                  <div className="absolute inset-0 bg-gradient-to-b from-white/20 to-transparent pointer-events-none" />
                  <span className="text-6xl md:text-7xl drop-shadow-md mb-2">{item.emoji}</span>
                  <span className="font-bold text-lg md:text-xl drop-shadow-md bg-black/20 px-3 py-1 rounded-full w-[90%] truncate text-center">
                    {item.name}
                  </span>
                </motion.button>
              ))}
            </div>
          </>
        )}

        {mode === 'plant' && (
          <div className="flex-1 w-full max-w-2xl flex flex-col items-center justify-center gap-12">
            <h2 className="text-4xl font-bold text-amber-800 bg-white/80 px-8 py-4 rounded-full shadow-md">ازرع نبتة 🌱</h2>
            
            <div className="relative w-64 h-64 md:w-80 md:h-80 bg-sky-50 rounded-full border-8 border-amber-200 shadow-2xl flex flex-col items-center justify-end overflow-hidden">
              {/* Sky background */}
              <div className="absolute inset-0 bg-gradient-to-b from-sky-300 to-sky-100" />
              
              {/* Sun effect */}
              <AnimatePresence>
                {plantStage >= 3 && (
                  <motion.div 
                    initial={{ y: -100, opacity: 0 }}
                    animate={{ y: 0, opacity: 1 }}
                    className="absolute top-4 right-4 text-6xl drop-shadow-[0_0_30px_rgba(250,204,21,0.8)]"
                  >
                    ☀️
                  </motion.div>
                )}
              </AnimatePresence>

              {/* Water effect */}
              <AnimatePresence>
                {plantStage === 2 && (
                  <motion.div 
                    initial={{ y: -50, opacity: 0 }}
                    animate={{ y: 0, opacity: 1 }}
                    exit={{ opacity: 0 }}
                    className="absolute top-1/4 text-5xl animate-bounce"
                  >
                    💧
                  </motion.div>
                )}
              </AnimatePresence>

              {/* Plant */}
              <AnimatePresence>
                {plantStage >= 1 && (
                  <motion.div 
                    initial={{ scale: 0, y: 50 }}
                    animate={{ scale: plantStage >= 4 ? 2 : plantStage >= 3 ? 1 : 0.5, y: plantStage >= 4 ? -20 : 10 }}
                    className="z-10 text-6xl origin-bottom transition-all duration-1000"
                  >
                    {plantStage >= 4 ? '🌻' : plantStage >= 3 ? '🌱' : '🌰'}
                  </motion.div>
                )}
              </AnimatePresence>

              {/* Dirt */}
              <div className="w-full h-1/3 bg-amber-800 z-20 rounded-b-full border-t-8 border-amber-900" />
            </div>

            <motion.button
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
              onClick={handlePlantAction}
              className="bg-white px-8 py-6 rounded-3xl shadow-xl border-4 border-amber-200 flex items-center gap-4 text-2xl font-bold text-amber-700"
            >
              {plantStage === 0 && <><Sprout size={36} className="text-amber-600" /> حط البذرة</>}
              {plantStage === 1 && <><Droplet size={36} className="text-blue-500" /> اسقيها مية</>}
              {plantStage === 2 && <><Sun size={36} className="text-yellow-500" /> خلي الشمس تطلع</>}
              {plantStage >= 3 && plantStage < 4 && <span className="animate-pulse">بتكبر...</span>}
              {plantStage === 4 && 'ازرع تاني 🔄'}
            </motion.button>
          </div>
        )}

      </div>

      {/* Item Modal */}
      <AnimatePresence>
        {selectedItem && mode === 'explore' && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4"
            onClick={() => setSelectedItem(null)}
          >
            <motion.div
              initial={{ scale: 0.5, y: 50, rotate: -10 }}
              animate={{ scale: 1, y: 0, rotate: 0 }}
              exit={{ scale: 0.5, y: 50, rotate: 10 }}
              className={`${selectedItem.color} w-full max-w-md rounded-[3rem] p-8 shadow-2xl border-8 border-white flex flex-col items-center relative`}
              onClick={(e) => e.stopPropagation()}
            >
              <button 
                onClick={() => setSelectedItem(null)}
                className="absolute top-4 right-4 bg-white/30 hover:bg-white/50 p-2 rounded-full transition-colors"
              >
                <X size={32} className="text-white" />
              </button>
              
              <motion.div 
                animate={{ y: [0, -10, 0] }} 
                transition={{ repeat: Infinity, duration: 2 }}
                className="text-9xl md:text-[10rem] drop-shadow-2xl mb-6"
              >
                {selectedItem.emoji}
              </motion.div>
              
              <div className="bg-white w-full rounded-3xl p-6 flex flex-col items-center gap-2 shadow-inner">
                <span className="text-4xl md:text-5xl font-black text-slate-800">{selectedItem.name}</span>
                <span className="text-lg font-bold text-slate-400">
                  {selectedItem.type === 'fruit' ? 'فواكه 🍎' : selectedItem.type === 'vegetable' ? 'خضروات 🥕' : 'طبيعة 🌿'}
                </span>
              </div>
              
              <button 
                onClick={() => handleSelect(selectedItem)}
                className="mt-6 bg-white/20 hover:bg-white/30 text-white px-8 py-4 rounded-full font-bold text-2xl transition-colors flex items-center gap-3 shadow-lg"
              >
                🔊 استمع
              </button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
