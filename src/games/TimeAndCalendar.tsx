import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft, Clock, Calendar, Sun, Moon } from 'lucide-react';
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
  { name: 'الربيع', emoji: '🌸', color: 'bg-pink-200' },
  { name: 'الصيف', emoji: '☀️', color: 'bg-yellow-200' },
  { name: 'الخريف', emoji: '🍂', color: 'bg-orange-200' },
  { name: 'الشتاء', emoji: '❄️', color: 'bg-blue-200' },
];

export default function TimeAndCalendar({ onBack, onWin }: TimeAndCalendarProps) {
  const [category, setCategory] = useState<string | null>(null);
  const [currentIndex, setCurrentIndex] = useState(0);

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

  const renderDays = () => (
    <div className="grid grid-cols-2 md:grid-cols-3 gap-4 w-full max-w-3xl">
      {DAYS.map((day, idx) => (
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
      {MONTHS.map((month, idx) => (
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
          onClick={() => handleItemClick(season.name)}
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
    return (
      <div className="flex flex-col items-center gap-8">
        <div className="relative w-64 h-64 md:w-96 md:h-96 rounded-full bg-white border-8 border-purple-400 shadow-xl flex items-center justify-center">
          <div className="absolute w-4 h-4 bg-purple-800 rounded-full z-10" />
          <motion.div 
            className="absolute w-1 h-24 md:h-32 bg-purple-800 rounded-full origin-bottom"
            style={{ bottom: '50%' }}
            animate={{ rotate: currentIndex * 30 }}
            transition={{ type: 'spring', stiffness: 50 }}
          />
          {hours.map((hour) => {
            const angle = (hour * 30 - 90) * (Math.PI / 180);
            const radius = 100; // Adjust based on size
            const x = Math.cos(angle) * radius;
            const y = Math.sin(angle) * radius;
            return (
              <div 
                key={hour} 
                className="absolute text-xl md:text-3xl font-bold text-purple-800"
                style={{ 
                  transform: `translate(${x}px, ${y}px) scale(${currentIndex === hour ? 1.5 : 1})`,
                  transition: 'transform 0.3s'
                }}
              >
                {hour}
              </div>
            );
          })}
        </div>
        <div className="flex gap-4">
          <button 
            onClick={() => {
              const newHour = currentIndex === 1 ? 12 : currentIndex - 1;
              setCurrentIndex(newHour);
              handleItemClick(`الساعة ${newHour}`);
            }}
            className="bg-white px-6 py-3 rounded-full shadow-lg font-bold text-purple-600 text-xl"
          >
            السابق
          </button>
          <button 
            onClick={() => {
              const newHour = currentIndex === 12 ? 1 : currentIndex + 1;
              setCurrentIndex(newHour);
              handleItemClick(`الساعة ${newHour}`);
            }}
            className="bg-white px-6 py-3 rounded-full shadow-lg font-bold text-purple-600 text-xl"
          >
            التالي
          </button>
        </div>
      </div>
    );
  };

  return (
    <div className="w-full h-full relative bg-indigo-50 flex flex-col items-center p-4 py-20 overflow-y-auto">
      <button 
        onClick={() => category ? setCategory(null) : onBack()} 
        className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg"
      >
        <ArrowLeft size={32} className="text-indigo-800" />
      </button>

      <h2 className="text-3xl md:text-5xl font-bold text-indigo-800 mb-12 text-center">
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
                className={`${cat.color} p-8 rounded-3xl shadow-lg flex items-center gap-6 text-white text-3xl md:text-4xl font-bold`}
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
