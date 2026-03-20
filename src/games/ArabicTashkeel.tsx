import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft, Volume2 } from 'lucide-react';
import confetti from 'canvas-confetti';
import { speak } from '../utils/audio';

interface ArabicTashkeelProps {
  onBack: () => void;
  onWin?: () => void;
}

const LETTERS = ['أ', 'ب', 'ت', 'ث', 'ج', 'ح', 'خ', 'د', 'ذ', 'ر', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ع', 'غ', 'ف', 'ق', 'ك', 'ل', 'م', 'ن', 'ه', 'و', 'ي'];

const LETTER_NAMES: Record<string, string> = {
  'أ': 'ألف', 'ب': 'باء', 'ت': 'تاء', 'ث': 'ثاء', 'ج': 'جيم', 'ح': 'حاء', 'خ': 'خاء',
  'د': 'دال', 'ذ': 'ذال', 'ر': 'راء', 'ز': 'زاي', 'س': 'سين', 'ش': 'شين', 'ص': 'صاد',
  'ض': 'ضاد', 'ط': 'طاء', 'ظ': 'ظاء', 'ع': 'عين', 'غ': 'غين', 'ف': 'فاء', 'ق': 'قاف',
  'ك': 'كاف', 'ل': 'لام', 'م': 'ميم', 'ن': 'نون', 'ه': 'هاء', 'و': 'واو', 'ي': 'ياء'
};

const DIACRITICS = [
  { id: 'fatha', char: 'َ', name: 'فتحة', mouth: '👄', color: 'bg-red-400' },
  { id: 'damma', char: 'ُ', name: 'ضمة', mouth: '😗', color: 'bg-blue-400' },
  { id: 'kasra', char: 'ِ', name: 'كسرة', mouth: '😬', color: 'bg-green-400' },
  { id: 'sukoon', char: 'ْ', name: 'سكون', mouth: '😶', color: 'bg-slate-400' }
];

export default function ArabicTashkeel({ onBack, onWin }: ArabicTashkeelProps) {
  const [stage, setStage] = useState(0);
  const [target, setTarget] = useState({ letter: 'ب', diacritic: DIACRITICS[0] });
  const [options, setOptions] = useState<any[]>([]);
  const [score, setScore] = useState(0);

  const generateOptions = (correctL: string, correctD: any) => {
    const opts = [{ letter: correctL, diacritic: correctD }];
    while(opts.length < 3) {
      const randL = LETTERS[Math.floor(Math.random() * LETTERS.length)];
      const randD = DIACRITICS[Math.floor(Math.random() * DIACRITICS.length)];
      if (!opts.find(o => o.letter === randL && o.diacritic.id === randD.id)) {
        opts.push({ letter: randL, diacritic: randD });
      }
    }
    return opts.sort(() => Math.random() - 0.5);
  };

  const generateRound = () => {
    const newStage = Math.floor(Math.random() * 5); // 0 to 4
    const l = LETTERS[Math.floor(Math.random() * LETTERS.length)];
    const d = DIACRITICS[Math.floor(Math.random() * DIACRITICS.length)];
    
    setTarget({ letter: l, diacritic: d });
    setStage(newStage);
    setOptions(generateOptions(l, d));
    
    // Play prompt based on stage
    setTimeout(() => {
      const letterName = LETTER_NAMES[l];
      if (newStage === 0) speak(`فين عربية ال${d.name}؟`);
      else if (newStage === 1) speak(`فين الحرف ${letterName} ${d.name}؟`);
      else if (newStage === 2) speak(`فرقع الحرف ${letterName} ${d.name}!`);
      else if (newStage === 3) speak(`شكل البق ده بيعمل صوت إيه؟`);
      else if (newStage === 4) speak(`عايزين نعمل ${letterName} ${d.name}، نختار كريمة إيه؟`);
    }, 500);
  };

  useEffect(() => {
    generateRound();
  }, []);

  const handleGuess = (isCorrect: boolean) => {
    if (isCorrect) {
      confetti({ particleCount: 100, spread: 70, origin: { y: 0.6 } });
      const letterName = LETTER_NAMES[target.letter];
      speak(`ممتاز! ${letterName} ${target.diacritic.name}، ${target.letter}${target.diacritic.char}`);
      setScore(s => s + 1);
      if (onWin) onWin();
      setTimeout(generateRound, 2500);
    } else {
      speak('حاول تاني');
    }
  };

  const playPrompt = () => {
    const letterName = LETTER_NAMES[target.letter];
    if (stage === 0) speak(`فين عربية ال${target.diacritic.name}؟`);
    else if (stage === 1) speak(`فين الحرف ${letterName} ${target.diacritic.name}؟`);
    else if (stage === 2) speak(`فرقع الحرف ${letterName} ${target.diacritic.name}!`);
    else if (stage === 3) speak(`شكل البق ده بيعمل صوت إيه؟`);
    else if (stage === 4) speak(`عايزين نعمل ${letterName} ${target.diacritic.name}، نختار كريمة إيه؟`);
  };

  return (
    <div className="w-full h-full bg-sky-50 p-4 md:p-6 flex flex-col items-center overflow-hidden relative">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg text-slate-500 hover:text-slate-700">
        <ArrowLeft size={32} />
      </button>

      <div className="absolute top-6 right-6 z-50 flex gap-4">
        <button onClick={playPrompt} className="bg-white p-4 rounded-full shadow-lg text-sky-500 hover:text-sky-700">
          <Volume2 size={32} />
        </button>
        <div className="bg-white px-6 py-2 rounded-full shadow-lg flex items-center">
          <span className="text-2xl font-bold text-sky-500">⭐ {score}</span>
        </div>
      </div>

      <h2 className="text-3xl md:text-4xl font-bold text-sky-800 mb-8 mt-16 text-center" dir="rtl">
        الحروف بالتشكيل َ ُ ِ ْ
      </h2>

      <div className="flex-1 w-full max-w-4xl flex items-center justify-center relative">
        <AnimatePresence mode="wait">
          <motion.div
            key={stage + score}
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.8 }}
            className="w-full flex justify-center"
          >
            {/* Stage 0: Train */}
            {stage === 0 && (
              <div className="flex flex-col items-center gap-12 w-full">
                <motion.div 
                  animate={{ y: [0, -10, 0] }}
                  transition={{ repeat: Infinity, duration: 2 }}
                  className="text-8xl md:text-9xl bg-white p-8 rounded-3xl shadow-xl border-4 border-sky-200 font-bold text-slate-800"
                >
                  {target.letter}{target.diacritic.char}
                </motion.div>
                <div className="flex gap-2 md:gap-4 justify-center w-full overflow-x-auto px-4 pb-4" dir="rtl">
                  {DIACRITICS.map(d => (
                    <motion.button 
                      key={d.id} 
                      whileHover={{ scale: 1.1 }}
                      whileTap={{ scale: 0.9 }}
                      onClick={() => handleGuess(d.id === target.diacritic.id)} 
                      className={`relative flex flex-col items-center ${d.color} p-4 md:p-6 rounded-2xl border-4 border-white shadow-lg min-w-[80px] md:min-w-[100px]`}
                    >
                      <span className="text-5xl md:text-6xl">🚃</span>
                      <span className="text-5xl md:text-6xl font-bold text-white absolute top-1/2 -translate-y-1/2">ـ{d.char}</span>
                    </motion.button>
                  ))}
                  <div className="text-6xl md:text-7xl self-center">🚂</div>
                </div>
              </div>
            )}

            {/* Stage 1: Apple Tree */}
            {stage === 1 && (
              <div className="relative w-full max-w-md aspect-square bg-green-500 rounded-t-full border-b-[16px] border-amber-800 flex items-center justify-center shadow-2xl mt-12">
                {options.map((opt, i) => {
                  const positions = [{top: '15%', left: '25%'}, {top: '25%', right: '15%'}, {top: '50%', left: '40%'}];
                  return (
                    <motion.button 
                      key={i} 
                      whileHover={{ scale: 1.1 }}
                      whileTap={{ scale: 0.9 }}
                      onClick={() => handleGuess(opt.letter === target.letter && opt.diacritic.id === target.diacritic.id)} 
                      className="absolute w-24 h-24 md:w-28 md:h-28 bg-red-500 rounded-full shadow-[inset_-5px_-5px_15px_rgba(0,0,0,0.3)] border-b-4 border-red-700 flex items-center justify-center text-5xl md:text-6xl text-white font-bold" 
                      style={positions[i]}
                    >
                      <span className="absolute -top-6 text-3xl">🌿</span>
                      {opt.letter}{opt.diacritic.char}
                    </motion.button>
                  )
                })}
              </div>
            )}

            {/* Stage 2: Bubble Pop */}
            {stage === 2 && (
              <div className="relative w-full max-w-3xl h-96 bg-gradient-to-b from-blue-300 to-blue-500 rounded-[3rem] overflow-hidden border-8 border-white shadow-2xl flex items-center justify-center gap-4 md:gap-12 p-4">
                {options.map((opt, i) => (
                  <motion.button 
                    key={i} 
                    onClick={() => handleGuess(opt.letter === target.letter && opt.diacritic.id === target.diacritic.id)} 
                    animate={{ y: [20, -20, 20] }} 
                    transition={{ repeat: Infinity, duration: 3 + i, ease: "easeInOut" }} 
                    className="w-28 h-28 md:w-36 md:h-36 rounded-full bg-white/30 border-2 border-white/60 backdrop-blur-sm shadow-[inset_0_0_30px_rgba(255,255,255,0.8)] flex items-center justify-center text-6xl md:text-7xl text-blue-900 font-bold hover:bg-white/50 active:scale-90 transition-colors"
                  >
                    {opt.letter}{opt.diacritic.char}
                  </motion.button>
                ))}
              </div>
            )}

            {/* Stage 3: Mouth Shapes */}
            {stage === 3 && (
              <div className="flex flex-col items-center gap-12 w-full">
                <motion.div 
                  animate={{ scale: [1, 1.1, 1] }}
                  transition={{ repeat: Infinity, duration: 2 }}
                  className="text-[10rem] md:text-[12rem] drop-shadow-2xl"
                >
                  {target.diacritic.mouth}
                </motion.div>
                <div className="flex gap-4 md:gap-8 justify-center flex-wrap">
                  {options.map((opt, i) => (
                    <motion.button 
                      key={i} 
                      whileHover={{ scale: 1.1 }}
                      whileTap={{ scale: 0.9 }}
                      onClick={() => handleGuess(opt.letter === target.letter && opt.diacritic.id === target.diacritic.id)} 
                      className="w-28 h-28 md:w-32 md:h-32 bg-white rounded-3xl shadow-xl text-6xl md:text-7xl font-bold flex items-center justify-center border-4 border-pink-200 text-pink-600"
                    >
                      {opt.letter}{opt.diacritic.char}
                    </motion.button>
                  ))}
                </div>
              </div>
            )}

            {/* Stage 4: Cupcake Factory */}
            {stage === 4 && (
              <div className="flex flex-col items-center gap-16 w-full mt-8">
                <div className="flex gap-4 md:gap-8 justify-center flex-wrap" dir="rtl">
                  {DIACRITICS.map((d, i) => (
                    <motion.button 
                      key={i} 
                      whileHover={{ scale: 1.1, y: -10 }}
                      whileTap={{ scale: 0.9 }}
                      onClick={() => handleGuess(d.id === target.diacritic.id)} 
                      className={`w-24 h-24 md:w-32 md:h-32 rounded-t-full ${d.color} shadow-xl text-6xl md:text-7xl font-bold flex items-center justify-center border-4 border-white text-white`}
                    >
                      ـ{d.char}
                    </motion.button>
                  ))}
                </div>
                <div className="relative w-48 h-40 md:w-64 md:h-48 bg-amber-200 rounded-b-[3rem] border-x-8 border-b-8 border-amber-400 flex items-center justify-center shadow-2xl">
                  <div className="absolute -top-6 w-56 md:w-72 h-12 bg-amber-300 rounded-full border-4 border-amber-400 shadow-inner" />
                  <span className="text-7xl md:text-8xl font-bold text-amber-800 z-10 mt-4">{target.letter}</span>
                </div>
              </div>
            )}
          </motion.div>
        </AnimatePresence>
      </div>
    </div>
  );
}
