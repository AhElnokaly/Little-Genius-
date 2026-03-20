import React, { useState, useEffect } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';
import { speak } from '../utils/audio';

interface SimpleMathProps {
  onBack: () => void;
  onWin: () => void;
}

const EMOJIS = ['🍎', '🚗', '🎈', '⭐', '🐶', '⚽', '🍓', '🧸'];

export default function SimpleMath({ onBack, onWin }: SimpleMathProps) {
  const [num1, setNum1] = useState(1);
  const [num2, setNum2] = useState(1);
  const [operator, setOperator] = useState('+');
  const [options, setOptions] = useState<number[]>([]);
  const [emoji, setEmoji] = useState('🍎');
  const [score, setScore] = useState(0);

  const generateRound = () => {
    const isAddition = Math.random() > 0.5;
    let n1 = Math.floor(Math.random() * 5) + 1;
    let n2 = Math.floor(Math.random() * 5) + 1;
    
    if (!isAddition && n1 < n2) {
      // Swap to avoid negative numbers
      const temp = n1;
      n1 = n2;
      n2 = temp;
    }

    const answer = isAddition ? n1 + n2 : n1 - n2;
    let wrong1 = answer + Math.floor(Math.random() * 3) + 1;
    let wrong2 = answer - Math.floor(Math.random() * 3) - 1;
    
    if (wrong2 < 0) {
      wrong2 = answer + Math.floor(Math.random() * 3) + 4;
    }
    
    const newOptions = [answer, wrong1, wrong2].sort(() => Math.random() - 0.5);
    
    setNum1(n1);
    setNum2(n2);
    setOperator(isAddition ? '+' : '-');
    setOptions(newOptions);
    setEmoji(EMOJIS[Math.floor(Math.random() * EMOJIS.length)]);
    
    speak(`${n1} ${isAddition ? 'زائد' : 'ناقص'} ${n2} يساوي كام؟`);
  };

  useEffect(() => {
    generateRound();
  }, []);

  const handleGuess = (guess: number) => {
    const answer = operator === '+' ? num1 + num2 : num1 - num2;
    if (guess === answer) {
      confetti({
        particleCount: 100,
        spread: 70,
        origin: { y: 0.6 },
        colors: ['#c084fc', '#e879f9', '#f472b6']
      });
      speak('شاطر! إجابة صحيحة');
      setScore(s => s + 1);
      onWin();
      setTimeout(generateRound, 2000);
    } else {
      speak('لأ، حاول تاني');
    }
  };

  const renderEmojis = (count: number) => {
    return Array.from({ length: count }).map((_, i) => (
      <span key={i} className="text-3xl md:text-4xl">{emoji}</span>
    ));
  };

  return (
    <div className="w-full h-full bg-fuchsia-50 p-4 md:p-6 flex flex-col items-center relative">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg text-slate-500 hover:text-slate-700">
        <ArrowLeft size={32} />
      </button>

      <div className="absolute top-6 right-6 z-50 bg-white px-6 py-2 rounded-full shadow-lg">
        <span className="text-2xl font-bold text-fuchsia-500">⭐ {score}</span>
      </div>

      <h2 className="text-3xl md:text-4xl font-bold text-fuchsia-700 mb-8 mt-16 text-center" dir="rtl">حساب بسيط ➕</h2>

      <div className="flex-1 w-full max-w-3xl flex flex-col items-center justify-center gap-12">
        <div className="flex items-center justify-center gap-4 md:gap-8 bg-white p-8 rounded-3xl shadow-xl w-full" dir="ltr">
          <div className="flex flex-col items-center gap-2">
            <span className="text-5xl md:text-7xl font-bold text-slate-800">{num1}</span>
            <div className="flex flex-wrap justify-center max-w-[120px]">{renderEmojis(num1)}</div>
          </div>
          
          <span className="text-5xl md:text-7xl font-bold text-fuchsia-500">{operator}</span>
          
          <div className="flex flex-col items-center gap-2">
            <span className="text-5xl md:text-7xl font-bold text-slate-800">{num2}</span>
            <div className="flex flex-wrap justify-center max-w-[120px]">{renderEmojis(num2)}</div>
          </div>
          
          <span className="text-5xl md:text-7xl font-bold text-fuchsia-500">=</span>
          <span className="text-5xl md:text-7xl font-bold text-slate-300">?</span>
        </div>

        <div className="flex gap-4 md:gap-8 justify-center flex-wrap w-full">
          {options.map((option, idx) => (
            <motion.button
              key={`${option}-${idx}`}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
              onClick={() => handleGuess(option)}
              className="w-24 h-24 md:w-32 md:h-32 bg-fuchsia-400 text-white rounded-3xl shadow-lg text-5xl md:text-6xl font-bold flex items-center justify-center border-4 border-fuchsia-200"
            >
              {option}
            </motion.button>
          ))}
        </div>
      </div>
    </div>
  );
}
