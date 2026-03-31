import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft, Star, RefreshCw } from 'lucide-react';

interface LetterAnimalMatchProps {
  onBack: () => void;
  onWin: (stars?: number) => void;
  difficulty?: 'easy' | 'medium' | 'hard';
}

const ANIMALS = [
  { letter: 'أ', name: 'أسد', icon: '🦁' },
  { letter: 'ب', name: 'بقرة', icon: '🐄' },
  { letter: 'ت', name: 'تمساح', icon: '🐊' },
  { letter: 'ث', name: 'ثعلب', icon: '🦊' },
  { letter: 'ج', name: 'جمل', icon: '🐪' },
  { letter: 'ح', name: 'حصان', icon: '🐎' },
  { letter: 'د', name: 'دب', icon: '🐻' },
  { letter: 'ز', name: 'زرافة', icon: '🦒' },
  { letter: 'س', name: 'سمكة', icon: '🐟' },
  { letter: 'ش', name: 'شبل', icon: '🦁' },
  { letter: 'ص', name: 'صقر', icon: '🦅' },
  { letter: 'ض', name: 'ضفدع', icon: '🐸' },
  { letter: 'ط', name: 'طاووس', icon: '🦚' },
  { letter: 'ع', name: 'عصفور', icon: '🐦' },
  { letter: 'غ', name: 'غراب', icon: '🐦‍⬛' },
  { letter: 'ف', name: 'فيل', icon: '🐘' },
  { letter: 'ق', name: 'قرد', icon: '🐒' },
  { letter: 'ك', name: 'كلب', icon: '🐶' },
  { letter: 'ن', name: 'نمر', icon: '🐯' },
  { letter: 'هـ', name: 'هرة', icon: '🐱' },
];

export default function LetterAnimalMatch({ onBack, onWin, difficulty = 'medium' }: LetterAnimalMatchProps) {
  const [currentRound, setCurrentRound] = useState(0);
  const [options, setOptions] = useState<typeof ANIMALS>([]);
  const [targetLetter, setTargetLetter] = useState('');
  const [targetAnimal, setTargetAnimal] = useState<typeof ANIMALS[0] | null>(null);
  const [won, setWon] = useState(false);
  const [wrongAnswers, setWrongAnswers] = useState<string[]>([]);
  const [score, setScore] = useState(0);

  const roundsToWin = difficulty === 'easy' ? 3 : difficulty === 'medium' ? 5 : 7;
  const optionsCount = difficulty === 'easy' ? 2 : difficulty === 'medium' ? 3 : 4;

  useEffect(() => {
    startRound();
  }, [currentRound]);

  const startRound = () => {
    const shuffled = [...ANIMALS].sort(() => Math.random() - 0.5);
    const selectedOptions = shuffled.slice(0, optionsCount);
    const target = selectedOptions[Math.floor(Math.random() * selectedOptions.length)];
    
    setOptions(selectedOptions);
    setTargetLetter(target.letter);
    setTargetAnimal(target);
    setWrongAnswers([]);
  };

  const handleSelect = (animal: typeof ANIMALS[0]) => {
    if (won || wrongAnswers.includes(animal.name)) return;

    if (animal.name === targetAnimal?.name) {
      const isPerfect = wrongAnswers.length === 0;
      setScore(s => s + (isPerfect ? 3 : 1));
      
      if (currentRound + 1 >= roundsToWin) {
        setWon(true);
        // Calculate total stars (max 3 per game)
        const totalPossible = roundsToWin * 3;
        const finalScore = score + (isPerfect ? 3 : 1);
        const starsEarned = Math.max(1, Math.round((finalScore / totalPossible) * 3));
        setTimeout(() => onWin(starsEarned), 1500);
      } else {
        setTimeout(() => setCurrentRound(r => r + 1), 1000);
      }
    } else {
      setWrongAnswers(prev => [...prev, animal.name]);
    }
  };

  return (
    <div className="w-full h-full bg-amber-50 flex flex-col items-center justify-center relative overflow-hidden">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 text-amber-600 hover:text-amber-800 bg-white/80 backdrop-blur-sm rounded-full p-3 shadow-md">
        <ArrowLeft size={32} />
      </button>

      <div className="absolute top-6 right-6 bg-white/80 backdrop-blur-sm px-6 py-2 rounded-full shadow-md border-2 border-amber-200">
        <span className="text-xl font-bold text-amber-700">المرحلة {currentRound + 1} / {roundsToWin}</span>
      </div>

      <AnimatePresence mode="wait">
        {!won ? (
          <motion.div
            key={currentRound}
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 1.2 }}
            className="flex flex-col items-center z-10 w-full max-w-4xl px-4"
          >
            <h2 className="text-3xl md:text-5xl font-black text-amber-800 mb-8 text-center drop-shadow-sm">
              أين الحيوان الذي يبدأ بحرف
            </h2>
            
            <motion.div 
              initial={{ y: -20 }}
              animate={{ y: 0 }}
              className="bg-white w-32 h-32 md:w-48 md:h-48 rounded-3xl shadow-xl flex items-center justify-center border-8 border-amber-300 mb-12"
            >
              <span className="text-7xl md:text-9xl font-black text-amber-600">{targetLetter}</span>
            </motion.div>

            <div className="flex flex-wrap justify-center gap-4 md:gap-8 w-full">
              {options.map((animal, index) => {
                const isWrong = wrongAnswers.includes(animal.name);
                return (
                  <motion.button
                    key={animal.name}
                    initial={{ y: 50, opacity: 0 }}
                    animate={{ y: 0, opacity: 1 }}
                    transition={{ delay: index * 0.1 }}
                    whileHover={!isWrong ? { scale: 1.1, rotate: 5 } : {}}
                    whileTap={!isWrong ? { scale: 0.9 } : {}}
                    onClick={() => handleSelect(animal)}
                    className={`w-28 h-28 md:w-40 md:h-40 rounded-3xl shadow-lg flex flex-col items-center justify-center border-4 transition-all
                      ${isWrong ? 'bg-slate-200 border-slate-300 opacity-50 scale-95' : 'bg-white border-amber-200 hover:border-amber-400 hover:shadow-xl'}`}
                    disabled={isWrong}
                  >
                    <span className="text-5xl md:text-7xl mb-2">{animal.icon}</span>
                    <span className={`text-sm md:text-xl font-bold ${isWrong ? 'text-slate-400' : 'text-amber-700'}`}>{animal.name}</span>
                  </motion.button>
                );
              })}
            </div>
          </motion.div>
        ) : (
          <motion.div
            initial={{ scale: 0, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="flex flex-col items-center z-10 bg-white/90 backdrop-blur-sm p-12 rounded-3xl shadow-2xl border-4 border-amber-300"
          >
            <Star size={120} className="text-yellow-400 fill-yellow-400 mb-6 drop-shadow-lg" />
            <h2 className="text-5xl font-black text-amber-600 mb-4">أحسنت!</h2>
            <p className="text-2xl text-amber-800 font-bold mb-8">لقد طابقت كل الحروف بنجاح!</p>
            <button
              onClick={() => {
                setWon(false);
                setCurrentRound(0);
                setScore(0);
              }}
              className="px-8 py-4 bg-amber-500 text-white rounded-full text-2xl font-bold hover:bg-amber-600 shadow-lg flex items-center gap-3 active:scale-95 transition-transform"
            >
              <RefreshCw size={28} />
              العب مرة أخرى
            </button>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
