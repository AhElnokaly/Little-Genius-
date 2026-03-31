import { useState, useEffect } from 'react';
import { motion } from 'motion/react';
import { Settings as SettingsIcon, Image as ImageIcon, Download } from 'lucide-react';
import { speak } from '../utils/audio'; // +++ أضيف بناءً على طلبك +++

interface HomeProps {
  onSelect: (id: string) => void;
  profileName: string;
  isBirthday: boolean;
  avatar?: string; // +++ أضيف بناءً على طلبك +++
}

export default function Home({ onSelect, profileName, isBirthday, avatar = '👦' }: HomeProps) {
  const [deferredPrompt, setDeferredPrompt] = useState<any>(null);

  useEffect(() => {
    const handleBeforeInstallPrompt = (e: Event) => {
      e.preventDefault();
      setDeferredPrompt(e);
    };

    window.addEventListener('beforeinstallprompt', handleBeforeInstallPrompt);

    return () => {
      window.removeEventListener('beforeinstallprompt', handleBeforeInstallPrompt);
    };
  }, []);

  const handleInstallClick = async () => {
    if (!deferredPrompt) return;
    deferredPrompt.prompt();
    const { outcome } = await deferredPrompt.userChoice;
    if (outcome === 'accepted') {
      setDeferredPrompt(null);
    }
  };

  const handleGameClick = (id: string, title: string) => {
    speak(title); // +++ أضيف بناءً على طلبك: نطق اسم اللعبة +++
    onSelect(id);
  };

  const categories = [
    {
      title: 'الحروف والأرقام 🔢',
      games: [
        { id: 'arabic', title: 'حروف عربي', icon: 'أ', color: 'bg-teal-400' },
        { id: 'tashkeel', title: 'تشكيل الحروف', icon: 'َُِ', color: 'bg-indigo-400' },
        { id: 'english', title: 'حروف English', icon: 'A', color: 'bg-rose-400' },
        { id: 'letteranimal', title: 'حروف وحيوانات', icon: '🦁', color: 'bg-amber-500' }, // +++ أضيف بناءً على طلبك +++
        { id: 'numbers', title: 'أرقام 123', icon: '١', color: 'bg-sky-400' },
        { id: 'counting', title: 'عد الأشياء', icon: '🔢', color: 'bg-amber-400' },
        { id: 'lettermatch', title: 'توصيل حروف', icon: '🔤', color: 'bg-violet-400' },
        { id: 'simplemath', title: 'حساب بسيط', icon: '➕', color: 'bg-fuchsia-400' },
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
        { id: 'guesssound', title: 'خمن الصوت', icon: '👂', color: 'bg-yellow-400' }, // +++ أضيف بناءً على طلبك +++
        { id: 'time', title: 'الوقت', icon: '⏰', color: 'bg-blue-500' },
        { id: 'moon', title: 'القمر', icon: '🌙', color: 'bg-slate-700' },
      ]
    },
    {
      title: 'ألعاب ومهارات 🎨',
      games: [
        { id: 'balloon', title: 'بالونات', icon: '🎈', color: 'bg-red-400' },
        { id: 'draw', title: 'رسم حر', icon: '✨', color: 'bg-purple-400' },
        { id: 'drawshapes', title: 'ارسم شكل', icon: '✏️', color: 'bg-indigo-500' },
        { id: 'color', title: 'ألوان', icon: '🎨', color: 'bg-blue-400' },
        { id: 'stickers', title: 'الملصقات', icon: '🖼️', color: 'bg-amber-400' }, // +++ أضيف بناءً على طلبك +++
        { id: 'coloring', title: 'تلوين', icon: '🖍️', color: 'bg-rose-400' },
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
        <div className="flex gap-2">
          <button onClick={() => onSelect('settings')} className="bg-white p-3 rounded-full shadow-md text-slate-500 hover:text-slate-700 border-2 border-slate-200 active:scale-95 transition-transform">
            <SettingsIcon size={28} />
          </button>
          {deferredPrompt && (
            <button onClick={handleInstallClick} className="bg-white px-4 py-2 rounded-full shadow-md text-sky-500 flex gap-2 items-center border-2 border-sky-200 active:scale-95 transition-transform font-bold text-sm md:text-lg">
              <Download size={24} /> تثبيت التطبيق
            </button>
          )}
        </div>
        {/* +++ تم نقل زر الملصقات إلى وسط الألعاب بناءً على طلبك +++ */}
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
          className="text-3xl md:text-5xl font-extrabold text-sky-800 drop-shadow-sm flex items-center justify-center gap-4"
        >
          <span className="text-5xl">{avatar}</span> {/* +++ أضيف بناءً على طلبك +++ */}
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
                  onClick={() => handleGameClick(game.id, game.title)}
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
