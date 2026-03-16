import { useState } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface InteractiveHouseProps {
  onBack: () => void;
  onWin?: () => void;
}

export default function InteractiveHouse({ onBack, onWin }: InteractiveHouseProps) {
  const [lightOn, setLightOn] = useState(true);
  const [windowOpen, setWindowOpen] = useState(false);
  const [doorOpen, setDoorOpen] = useState(false);
  const [tvOn, setTvOn] = useState(false);
  const [showTutorial, setShowTutorial] = useState(true);
  const [interactions, setInteractions] = useState(0);

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

  return (
    <div className={`w-full h-full relative transition-colors duration-1000 overflow-hidden ${lightOn ? 'bg-amber-50' : 'bg-slate-900'}`}>
      <TutorialHand show={showTutorial} y={100} action="tap" />
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white/80 backdrop-blur p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-slate-800" />
      </button>

      <h2 className={`absolute top-6 right-6 z-50 text-2xl md:text-4xl font-bold transition-colors ${lightOn ? 'text-amber-800' : 'text-slate-200'}`}>
        بيتي الصغير 🏠
      </h2>

      <div className="absolute inset-0 flex items-center justify-center p-4">
        <div className={`relative w-full max-w-4xl aspect-[4/3] rounded-[3rem] border-8 transition-colors duration-1000 overflow-hidden ${lightOn ? 'bg-orange-100 border-orange-200' : 'bg-slate-800 border-slate-700'}`}>
          
          {/* Floor */}
          <div className={`absolute bottom-0 w-full h-[40%] transition-colors duration-1000 ${lightOn ? 'bg-amber-200' : 'bg-slate-700'}`} />

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

        </div>
      </div>
    </div>
  );
}
