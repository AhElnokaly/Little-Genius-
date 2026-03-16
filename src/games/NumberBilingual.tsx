import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft, ArrowRight } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface NumberBilingualProps {
  onBack: () => void;
  onWin?: () => void;
}

const NUMBERS = [
  { val: 1, ar: '١', en: '1', arName: 'واحد', enName: 'One' },
  { val: 2, ar: '٢', en: '2', arName: 'اتنين', enName: 'Two' },
  { val: 3, ar: '٣', en: '3', arName: 'تلاتة', enName: 'Three' },
  { val: 4, ar: '٤', en: '4', arName: 'أربعة', enName: 'Four' },
  { val: 5, ar: '٥', en: '5', arName: 'خمسة', enName: 'Five' },
  { val: 6, ar: '٦', en: '6', arName: 'ستة', enName: 'Six' },
  { val: 7, ar: '٧', en: '7', arName: 'سبعة', enName: 'Seven' },
  { val: 8, ar: '٨', en: '8', arName: 'تمنية', enName: 'Eight' },
  { val: 9, ar: '٩', en: '9', arName: 'تسعة', enName: 'Nine' },
  { val: 10, ar: '١٠', en: '10', arName: 'عشرة', enName: 'Ten' },
];

export default function NumberBilingual({ onBack, onWin }: NumberBilingualProps) {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [showTutorial, setShowTutorial] = useState(true);
  const [arClicked, setArClicked] = useState(false);
  const [enClicked, setEnClicked] = useState(false);

  const currentNumber = NUMBERS[currentIndex];

  useEffect(() => {
    setArClicked(false);
    setEnClicked(false);
  }, [currentIndex]);

  useEffect(() => {
    if (arClicked && enClicked) {
      if (onWin) onWin();
      confetti({ particleCount: 30, spread: 50, origin: { y: 0.8 } });
      
      const timer = setTimeout(() => {
        handleNext();
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [arClicked, enClicked]);

  const speak = (text: string, lang: string) => {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.lang = lang;
      utterance.rate = 0.8;
      window.speechSynthesis.speak(utterance);
    }
  };

  const handleNext = () => {
    setCurrentIndex((prev) => (prev + 1) % NUMBERS.length);
  };

  const handlePrev = () => {
    setCurrentIndex((prev) => (prev - 1 + NUMBERS.length) % NUMBERS.length);
  };

  return (
    <div className="w-full h-full relative bg-sky-50 flex flex-col items-center justify-center p-4">
      <TutorialHand show={showTutorial} y={150} action="tap" />
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-sky-800" />
      </button>

      <h2 className="absolute top-12 text-3xl md:text-5xl font-bold text-sky-800 text-center">
        الأرقام Numbers 🔢
      </h2>

      <div className="flex items-center justify-center gap-4 md:gap-12 w-full max-w-4xl mt-12">
        <button onClick={handlePrev} className="bg-white p-4 rounded-full shadow-md text-sky-500 hover:bg-sky-100">
          <ArrowRight size={32} />
        </button>

        <AnimatePresence mode="wait">
          <motion.div
            key={currentIndex}
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.8, opacity: 0 }}
            className="flex gap-4 md:gap-8"
          >
            {/* Arabic Number */}
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => {
                if (showTutorial) setShowTutorial(false);
                speak(currentNumber.arName, 'ar-EG');
                setArClicked(true);
              }}
              className={`w-40 h-56 md:w-64 md:h-80 rounded-[3rem] shadow-2xl border-8 flex flex-col items-center justify-center transition-colors ${
                arClicked ? 'bg-sky-400 border-sky-500 text-white' : 'bg-white border-sky-200 text-sky-800'
              }`}
            >
              <span className="text-8xl md:text-[10rem] font-black leading-none mb-4">{currentNumber.ar}</span>
              <span className="text-2xl md:text-4xl font-bold">{currentNumber.arName}</span>
            </motion.button>

            {/* English Number */}
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => {
                if (showTutorial) setShowTutorial(false);
                speak(currentNumber.enName, 'en-US');
                setEnClicked(true);
              }}
              className={`w-40 h-56 md:w-64 md:h-80 rounded-[3rem] shadow-2xl border-8 flex flex-col items-center justify-center transition-colors ${
                enClicked ? 'bg-rose-400 border-rose-500 text-white' : 'bg-white border-rose-200 text-rose-800'
              }`}
            >
              <span className="text-8xl md:text-[10rem] font-black leading-none mb-4">{currentNumber.en}</span>
              <span className="text-2xl md:text-4xl font-bold">{currentNumber.enName}</span>
            </motion.button>
          </motion.div>
        </AnimatePresence>

        <button onClick={handleNext} className="bg-white p-4 rounded-full shadow-md text-sky-500 hover:bg-sky-100">
          <ArrowLeft size={32} />
        </button>
      </div>
    </div>
  );
}
