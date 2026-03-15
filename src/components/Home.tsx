import { motion } from 'motion/react';
import { Settings as SettingsIcon, Image as ImageIcon } from 'lucide-react';

interface HomeProps {
  onSelect: (id: string) => void;
  profileName: string;
  isBirthday: boolean;
}

export default function Home({ onSelect, profileName, isBirthday }: HomeProps) {
  const categories = [
    {
      title: 'الحروف والأرقام 🔢',
      games: [
        { id: 'arabic', title: 'حروف عربي', icon: 'أ', color: 'bg-teal-400' },
        { id: 'english', title: 'حروف English', icon: 'A', color: 'bg-rose-400' },
        { id: 'numbers', title: 'أرقام 123', icon: '١', color: 'bg-sky-400' },
        { id: 'counting', title: 'عد الأشياء', icon: '🔢', color: 'bg-amber-400' },
        { id: 'lettermatch', title: 'توصيل حروف', icon: '🔤', color: 'bg-violet-400' },
      ]
    },
    {
      title: 'عالمي الصغير 🌍',
      games: [
        { id: 'house', title: 'بيتي', icon: '🏠', color: 'bg-amber-300' },
        { id: 'healthyfood', title: 'أكل صحي', icon: '🍎', color: 'bg-lime-400' },
        { id: 'nature', title: 'طبيعة', icon: '🌿', color: 'bg-green-500' },
        { id: 'animalfamily', title: 'عائلات', icon: '🐾', color: 'bg-orange-400' },
        { id: 'animal', title: 'حيوانات', icon: '🐶', color: 'bg-green-400' },
      ]
    },
    {
      title: 'ألعاب ومهارات 🎨',
      games: [
        { id: 'balloon', title: 'بالونات', icon: '🎈', color: 'bg-red-400' },
        { id: 'draw', title: 'رسم', icon: '✨', color: 'bg-purple-400' },
        { id: 'color', title: 'ألوان', icon: '🎨', color: 'bg-blue-400' },
        { id: 'sorter', title: 'أشكال', icon: '🧩', color: 'bg-orange-400' },
        { id: 'piano', title: 'بيانو', icon: '🎹', color: 'bg-pink-400' },
        { id: 'fish', title: 'سمك', icon: '🐟', color: 'bg-cyan-400' },
        { id: 'jigsaw', title: 'تركيب', icon: '🧩', color: 'bg-emerald-400' },
        { id: 'memory', title: 'ذاكرة', icon: '🧠', color: 'bg-indigo-400' },
      ]
    }
  ];

  return (
    <div className="w-full h-full flex flex-col items-center justify-start p-2 md:p-4 overflow-hidden relative bg-sky-50">
      {/* Top Bar */}
      <div className="w-full flex justify-between items-center px-2 pt-2 z-50">
        <button onClick={() => onSelect('settings')} className="bg-white p-3 rounded-full shadow-md text-slate-500 hover:text-slate-700 border-2 border-slate-200 active:scale-95 transition-transform">
          <SettingsIcon size={28} />
        </button>
        <button onClick={() => onSelect('stickers')} className="bg-white px-4 py-2 rounded-full shadow-md text-amber-500 flex gap-2 items-center border-2 border-amber-200 active:scale-95 transition-transform font-bold text-lg">
          <ImageIcon size={24} /> الملصقات
        </button>
      </div>

      {/* Greeting & Logo */}
      <div className="mt-2 text-center mb-4 z-10 flex flex-col items-center">
        <motion.div 
          initial={{ y: -20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          className="bg-white/80 backdrop-blur-sm px-6 py-2 rounded-full shadow-sm border-2 border-sky-200 mb-2 flex items-center gap-3"
        >
          <span className="text-3xl">🚀</span>
          <span className="text-2xl font-black text-sky-700 tracking-tight">عبقري الصغير</span>
        </motion.div>
        <motion.h1 
          initial={{ scale: 0.9, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="text-3xl md:text-5xl font-extrabold text-sky-800 drop-shadow-sm"
        >
          {isBirthday ? `🎉 عيد ميلاد سعيد ${profileName}! 🎂` : `أهلاً ${profileName || 'يا بطل'} 🌟`}
        </motion.h1>
      </div>

      {/* Games Categories */}
      <div className="w-full max-w-5xl flex-1 pb-8 px-2 md:px-4 overflow-y-auto space-y-8" dir="rtl">
        {categories.map((category) => (
          <div key={category.title} className="w-full">
            <h3 className="text-2xl md:text-3xl font-bold text-sky-800 mb-4 px-2 drop-shadow-sm border-b-2 border-sky-200 pb-2">
              {category.title}
            </h3>
            <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 gap-3 md:gap-4">
              {category.games.map((game) => (
                <motion.button
                  key={game.id}
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={() => onSelect(game.id)}
                  className={`${game.color} rounded-3xl shadow-lg flex flex-col items-center justify-center p-3 md:p-4 border-4 border-white/40 relative overflow-hidden aspect-square`}
                >
                  <div className="absolute inset-0 bg-gradient-to-b from-white/30 to-transparent pointer-events-none" />
                  <span className="text-5xl md:text-6xl mb-2 drop-shadow-md">{game.icon}</span>
                  <span className="text-white font-extrabold text-sm md:text-lg drop-shadow-md text-center leading-tight">{game.title}</span>
                </motion.button>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
