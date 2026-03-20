import React, { useState, useEffect } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft, Volume2 } from 'lucide-react';
import confetti from 'canvas-confetti';
import { speak } from '../utils/audio';

interface GuessSoundProps {
  onBack: () => void;
  onWin: () => void;
}

const SOUNDS = [
  { id: 'cat', emoji: '🐱', name: 'قطة', sound: 'مياو مياو' },
  { id: 'dog', emoji: '🐶', name: 'كلب', sound: 'هو هو' },
  { id: 'cow', emoji: '🐮', name: 'بقرة', sound: 'مووو' },
  { id: 'sheep', emoji: '🐑', name: 'خروف', sound: 'ماااء' },
  { id: 'duck', emoji: '🦆', name: 'بطة', sound: 'واك واك' },
  { id: 'car', emoji: '🚗', name: 'عربية', sound: 'بيب بيب' },
  { id: 'train', emoji: '🚂', name: 'قطر', sound: 'توت توت' },
  { id: 'rooster', emoji: '🐓', name: 'ديك', sound: 'كوكو كوكو' },
];

export default function GuessSound({ onBack, onWin }: GuessSoundProps) {
  const [target, setTarget] = useState(SOUNDS[0]);
  const [options, setOptions] = useState<typeof SOUNDS>([]);
  const [score, setScore] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);

  const generateRound = () => {
    const shuffled = [...SOUNDS].sort(() => Math.random() - 0.5);
    const selectedTarget = shuffled[0];
    const roundOptions = shuffled.slice(0, 3).sort(() => Math.random() - 0.5);
    
    setTarget(selectedTarget);
    setOptions(roundOptions);
    playSound(selectedTarget.sound);
  };

  useEffect(() => {
    generateRound();
  }, []);

  const playSound = (soundText: string) => {
    if (isPlaying) return;
    setIsPlaying(true);
    speak(soundText);
    setTimeout(() => setIsPlaying(false), 1500);
  };

  const handleGuess = (item: typeof SOUNDS[0]) => {
    if (item.id === target.id) {
      confetti({
        particleCount: 100,
        spread: 70,
        origin: { y: 0.6 },
        colors: ['#fce7f3', '#fbcfe8', '#f9a8d4']
      });
      speak('شاطر! ده صوت ال' + target.name);
      setScore(s => s + 1);
      onWin();
      setTimeout(generateRound, 2000);
    } else {
      speak('لأ، حاول تاني');
    }
  };

  return (
    <div className="w-full h-full bg-yellow-50 p-4 md:p-6 flex flex-col items-center relative">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg text-slate-500 hover:text-slate-700">
        <ArrowLeft size={32} />
      </button>

      <div className="absolute top-6 right-6 z-50 bg-white px-6 py-2 rounded-full shadow-lg">
        <span className="text-2xl font-bold text-yellow-500">⭐ {score}</span>
      </div>

      <h2 className="text-3xl md:text-4xl font-bold text-yellow-700 mb-8 mt-16 text-center">خمن الصوت 👂</h2>

      <div className="flex-1 w-full max-w-2xl flex flex-col items-center justify-center gap-12">
        <motion.button
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
          onClick={() => playSound(target.sound)}
          className={`w-40 h-40 md:w-48 md:h-48 rounded-full bg-yellow-400 shadow-xl flex items-center justify-center border-8 border-yellow-200 ${isPlaying ? 'animate-pulse' : ''}`}
        >
          <Volume2 size={80} className="text-white" />
        </motion.button>

        <div className="flex gap-4 md:gap-8 justify-center flex-wrap">
          {options.map((option) => (
            <motion.button
              key={option.id}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
              onClick={() => handleGuess(option)}
              className="w-28 h-28 md:w-36 md:h-36 bg-white rounded-3xl shadow-lg text-6xl md:text-7xl flex items-center justify-center border-4 border-yellow-100"
            >
              {option.emoji}
            </motion.button>
          ))}
        </div>
      </div>
    </div>
  );
}
