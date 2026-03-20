import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft, Clock, Calendar, Sun, Moon, Star, Cloud } from 'lucide-react';
import confetti from 'canvas-confetti';
import { speak, getAudioContext } from '../utils/audio';

interface TimeAndCalendarProps {
  onBack: () => void;
  onWin?: () => void;
}

const CATEGORIES = [
  { id: 'days', title: 'أيام الأسبوع', icon: <Calendar size={32} />, color: 'bg-blue-400' },
  { id: 'months', title: 'شهور السنة', icon: <Calendar size={32} />, color: 'bg-green-400' },
  { id: 'seasons', title: 'فصول السنة', icon: <Sun size={32} />, color: 'bg-orange-400' },
  { id: 'clock', title: 'الساعة', icon: <Clock size={32} />, color: 'bg-purple-400' },
];

const DAYS = ['السبت', 'الأحد', 'الإثنين', 'الثلاثاء', 'الأربعاء', 'الخميس', 'الجمعة'];
const MONTHS = ['يناير', 'فبراير', 'مارس', 'أبريل', 'مايو', 'يونيو', 'يوليو', 'أغسطس', 'سبتمبر', 'أكتوبر', 'نوفمبر', 'ديسمبر'];
const SEASONS = [
  { name: 'الربيع', emoji: '🌸', color: 'bg-pink-200', bg: 'bg-pink-50' },
  { name: 'الصيف', emoji: '☀️', color: 'bg-yellow-200', bg: 'bg-yellow-50' },
  { name: 'الخريف', emoji: '🍂', color: 'bg-orange-200', bg: 'bg-orange-50' },
  { name: 'الشتاء', emoji: '❄️', color: 'bg-blue-200', bg: 'bg-blue-50' },
];

export default function TimeAndCalendar({ onBack, onWin }: TimeAndCalendarProps) {
  const [category, setCategory] = useState<string | null>(null);
  const [currentIndex, setCurrentIndex] = useState(12);
  const [activeSeasonBg, setActiveSeasonBg] = useState<string>('bg-indigo-50');
  
  // Clock Challenge State
  const [clockMode, setClockMode] = useState<'learn' | 'challenge'>('learn');
  const [targetHour, setTargetHour] = useState<number>(0);

  const playSound = () => {
    try {
      const ctx = getAudioContext();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.type = 'sine';
      osc.frequency.setValueAtTime(600, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(1200, ctx.currentTime + 0.1);
      gain.gain.setValueAtTime(0.3, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.1);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.1);
    } catch (e) {}
  };

  const handleItemClick = (text: string) => {
    speak(text);
    playSound();
    confetti({
      particleCount: 20,
      spread: 40,
      origin: { y: 0.8 },
      colors: ['#4ade80', '#60a5fa', '#f472b6']
    });
  };

  const generateNewTargetHour = () => {
    const newTarget = Math.floor(Math.random() * 12) + 1;
    setTargetHour(newTarget);
    speak(`اضبط الساعة على ${newTarget}`);
  };

  useEffect(() => {
    if (clockMode === 'challenge' && category === 'clock') {
      generateNewTargetHour();
    }
  }, [clockMode, category]);

  const handleHourClick = (hour: number) => {
    setCurrentIndex(hour);
    
    if (clockMode === 'learn') {
      handleItemClick(`الساعة ${hour}`);
    } else if (clockMode === 'challenge') {
      if (hour === targetHour) {
        speak('شاطر! إجابة صحيحة');
        confetti({ particleCount: 50, spread: 60, origin: { y: 0.6 } });
        if (onWin) onWin();
        setTimeout(generateNewTargetHour, 2000);
      } else {
        speak('حاول تاني');
      }
    }
  };

  const renderDays = () => (
    <div className="grid grid-cols-2 md:grid-cols-3 gap-4 w-full max-w-3xl">
      {DAYS.map((day) => (
        <motion.button
          key={day}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => handleItemClick(day)}
          className="bg-white p-6 rounded-3xl shadow-lg text-2xl md:text-4xl font-bold text-blue-600 border-4 border-blue-200"
        >
          {day}
        </motion.button>
      ))}
    </div>
  );

  const renderMonths = () => (
    <div className="grid grid-cols-3 md:grid-cols-4 gap-4 w-full max-w-4xl">
      {MONTHS.map((month) => (
        <motion.button
          key={month}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => handleItemClick(month)}
          className="bg-white p-4 rounded-2xl shadow-lg text-xl md:text-3xl font-bold text-green-600 border-4 border-green-200"
        >
          {month}
        </motion.button>
      ))}
    </div>
  );

  const renderSeasons = () => (
    <div className="grid grid-cols-2 gap-6 w-full max-w-2xl">
      {SEASONS.map((season) => (
        <motion.button
          key={season.name}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => {
            handleItemClick(season.name);
            setActiveSeasonBg(season.bg);
          }}
          className={`${season.color} p-8 rounded-3xl shadow-lg flex flex-col items-center justify-center gap-4 border-4 border-white`}
        >
          <span className="text-6xl md:text-8xl">{season.emoji}</span>
          <span className="text-3xl md:text-5xl font-bold text-gray-800">{season.name}</span>
        </motion.button>
      ))}
    </div>
  );

  const renderClock = () => {
    const hours = Array.from({ length: 12 }, (_, i) => i + 1);
    
    // Determine background based on time (assuming 1-5 is night, 6-11 morning, 12 is noon)
    let timeBg = 'bg-sky-200';
    let timeIcon = <Sun size={48} className="text-yellow-500" />;
    if (currentIndex >= 1 && currentIndex <= 5) {
      timeBg = 'bg-indigo-900';
      timeIcon = <Moon size={48} className="text-yellow-200" />;
    } else if (currentIndex >= 6 && currentIndex <= 8) {
      timeBg = 'bg-orange-200';
      timeIcon = <Sun size={48} className="text-orange-500" />;
    } else if (currentIndex >= 9 && currentIndex <= 11) {
      timeBg = 'bg-sky-300';
      timeIcon = <Sun size={48} className="text-yellow-400" />;
    } else if (currentIndex === 12) {
      timeBg = 'bg-blue-400';
      timeIcon = <Sun size={48} className="text-yellow-300" />;
    }

    return (
      <div className="flex flex-col items-center gap-8 w-full max-w-2xl">
        
        <div className="flex gap-4 mb-4 bg-white/50 p-2 rounded-full shadow-sm">
          <button 
            onClick={() => { setClockMode('learn'); speak('التعلم'); }}
            className={`px-6 py-2 rounded-full font-bold text-xl transition-colors ${clockMode === 'learn' ? 'bg-purple-500 text-white shadow-md' : 'text-purple-700 hover:bg-white'}`}
          >
            التعلم 📖
          </button>
          <button 
            onClick={() => { setClockMode('challenge'); }}
            className={`px-6 py-2 rounded-full font-bold text-xl transition-colors ${clockMode === 'challenge' ? 'bg-amber-500 text-white shadow-md' : 'text-amber-700 hover:bg-white'}`}
          >
            تحدي الوقت 🎯
          </button>
        </div>

        {clockMode === 'challenge' && (
          <div className="bg-white px-8 py-4 rounded-3xl shadow-lg border-4 border-amber-200 flex items-center gap-4">
            <span className="text-3xl font-bold text-amber-800">اضبط الساعة على {targetHour}</span>
            <button onClick={() => speak(`اضبط الساعة على ${targetHour}`)} className="bg-amber-100 p-3 rounded-full hover:bg-amber-200">🔊</button>
          </div>
        )}

        <div className={`relative w-72 h-72 md:w-96 md:h-96 rounded-full ${timeBg} border-8 border-white shadow-2xl flex items-center justify-center transition-colors duration-1000 overflow-hidden`}>
          
          {/* Time indicator icon */}
          <div className="absolute top-8 opacity-50">
            {timeIcon}
          </div>
          {currentIndex >= 1 && currentIndex <= 5 && (
            <div className="absolute inset-0 flex items-center justify-center opacity-30">
              <Star className="absolute top-10 left-10 text-white" size={24} />
              <Star className="absolute bottom-20 right-16 text-white" size={16} />
              <Star className="absolute top-24 right-12 text-white" size={20} />
            </div>
          )}

          <div className="absolute w-6 h-6 bg-slate-800 rounded-full z-20 shadow-md" />
          
          {/* Hour Hand */}
          <motion.div 
            className="absolute w-2 h-20 md:h-28 bg-slate-800 rounded-full origin-bottom z-10 shadow-sm"
            style={{ bottom: '50%' }}
            animate={{ rotate: currentIndex * 30 }}
            transition={{ type: 'spring', stiffness: 50 }}
          />
          
          {/* Minute Hand (Static at 12 for simplicity) */}
          <div 
            className="absolute w-1 h-28 md:h-36 bg-slate-500 rounded-full origin-bottom z-0"
            style={{ bottom: '50%', transform: 'rotate(0deg)' }}
          />

          {hours.map((hour) => {
            const angle = (hour * 30 - 90) * (Math.PI / 180);
            const radius = 110; // Adjust based on size
            const x = Math.cos(angle) * radius;
            const y = Math.sin(angle) * radius;
            return (
              <button 
                key={hour} 
                onClick={() => handleHourClick(hour)}
                className={`absolute w-12 h-12 flex items-center justify-center rounded-full text-xl md:text-2xl font-black transition-all z-20
                  ${currentIndex === hour ? 'bg-white text-slate-900 shadow-lg scale-125' : 'bg-white/80 text-slate-700 hover:bg-white hover:scale-110'}`}
                style={{ 
                  transform: `translate(${x}px, ${y}px)`,
                }}
              >
                {hour}
              </button>
            );
          })}
        </div>
      </div>
    );
  };

  return (
    <div className={`w-full h-full relative ${category === 'seasons' ? activeSeasonBg : 'bg-indigo-50'} flex flex-col items-center p-4 py-20 overflow-y-auto transition-colors duration-1000`}>
      <button 
        onClick={() => {
          if (category) {
            setCategory(null);
            setActiveSeasonBg('bg-indigo-50');
          } else {
            onBack();
          }
        }} 
        className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg"
      >
        <ArrowLeft size={32} className="text-indigo-800" />
      </button>

      <h2 className="text-3xl md:text-5xl font-bold text-indigo-800 mb-12 text-center bg-white/50 px-8 py-4 rounded-full shadow-sm">
        {category === 'days' ? 'أيام الأسبوع 📅' :
         category === 'months' ? 'شهور السنة 📆' :
         category === 'seasons' ? 'فصول السنة 🌤️' :
         category === 'clock' ? 'الساعة ⏰' : 'الوقت والتاريخ ⏱️'}
      </h2>

      <AnimatePresence mode="wait">
        {!category ? (
          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="grid grid-cols-1 md:grid-cols-2 gap-6 w-full max-w-3xl"
          >
            {CATEGORIES.map((cat) => (
              <motion.button
                key={cat.id}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => {
                  setCategory(cat.id);
                  setCurrentIndex(12); // Default for clock
                  speak(cat.title);
                }}
                className={`${cat.color} p-8 rounded-3xl shadow-lg flex items-center gap-6 text-white text-3xl md:text-4xl font-bold border-4 border-white/20`}
              >
                <div className="bg-white/20 p-4 rounded-full">
                  {cat.icon}
                </div>
                {cat.title}
              </motion.button>
            ))}
          </motion.div>
        ) : (
          <motion.div
            key={category}
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.9 }}
            className="w-full flex justify-center"
          >
            {category === 'days' && renderDays()}
            {category === 'months' && renderMonths()}
            {category === 'seasons' && renderSeasons()}
            {category === 'clock' && renderClock()}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
