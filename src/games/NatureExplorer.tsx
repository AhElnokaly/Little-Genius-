import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { ArrowLeft, X } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface NatureExplorerProps {
  onBack: () => void;
  onWin?: () => void;
}

// +++ أضيف بناءً على طلبك: لعبة النباتات والطبيعة +++
const NATURE_ITEMS = [
  { id: 'apple', name: 'تفاحة', emoji: '🍎', type: 'fruit', color: 'bg-red-400' },
  { id: 'banana', name: 'موز', emoji: '🍌', type: 'fruit', color: 'bg-yellow-400' },
  { id: 'carrot', name: 'جزر', emoji: '🥕', type: 'vegetable', color: 'bg-orange-400' },
  { id: 'broccoli', name: 'بروكلي', emoji: '🥦', type: 'vegetable', color: 'bg-green-500' },
  { id: 'tomato', name: 'طماطم', emoji: '🍅', type: 'vegetable', color: 'bg-red-500' },
  { id: 'corn', name: 'ذرة', emoji: '🌽', type: 'vegetable', color: 'bg-yellow-500' },
  { id: 'grapes', name: 'عنب', emoji: '🍇', type: 'fruit', color: 'bg-purple-400' },
  { id: 'watermelon', name: 'بطيخ', emoji: '🍉', type: 'fruit', color: 'bg-rose-400' },
  { id: 'strawberry', name: 'فراولة', emoji: '🍓', type: 'fruit', color: 'bg-red-400' },
  { id: 'mushroom', name: 'فطر', emoji: '🍄', type: 'nature', color: 'bg-stone-400' },
  { id: 'flower', name: 'زهرة', emoji: '🌻', type: 'nature', color: 'bg-amber-400' },
  { id: 'tree', name: 'شجرة', emoji: '🌳', type: 'nature', color: 'bg-emerald-500' },
  { id: 'leaf', name: 'ورقة شجر', emoji: '🍃', type: 'nature', color: 'bg-lime-400' },
  { id: 'cactus', name: 'صبار', emoji: '🌵', type: 'nature', color: 'bg-green-600' },
  { id: 'potato', name: 'بطاطس', emoji: '🥔', type: 'vegetable', color: 'bg-amber-600' },
  { id: 'onion', name: 'بصل', emoji: '🧅', type: 'vegetable', color: 'bg-orange-300' },
  { id: 'eggplant', name: 'باذنجان', emoji: '🍆', type: 'vegetable', color: 'bg-purple-500' },
  { id: 'cherry', name: 'كرز', emoji: '🍒', type: 'fruit', color: 'bg-rose-500' },
  { id: 'peach', name: 'خوخ', emoji: '🍑', type: 'fruit', color: 'bg-orange-400' },
  { id: 'pineapple', name: 'أناناس', emoji: '🍍', type: 'fruit', color: 'bg-yellow-400' },
];

export default function NatureExplorer({ onBack, onWin }: NatureExplorerProps) {
  const [selectedItem, setSelectedItem] = useState<typeof NATURE_ITEMS[0] | null>(null);
  const [showTutorial, setShowTutorial] = useState(true);
  const [viewedCount, setViewedCount] = useState(0);

  const handleSelect = (item: typeof NATURE_ITEMS[0]) => {
    if (showTutorial) setShowTutorial(false);
    setSelectedItem(item);
    
    // النطق الصوتي
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(item.name);
      utterance.lang = 'ar-SA';
      utterance.rate = 0.8;
      window.speechSynthesis.speak(utterance);
    }

    // زيادة العداد وإعطاء نجمة كل 5 نباتات
    const newCount = viewedCount + 1;
    setViewedCount(newCount);
    if (newCount % 5 === 0 && onWin) {
      onWin();
      confetti({
        particleCount: 50,
        spread: 60,
        origin: { y: 0.8 },
        colors: ['#4ade80', '#facc15', '#f87171']
      });
    }
  };

  return (
    <div className="w-full h-full relative bg-green-50 overflow-y-auto">
      <TutorialHand show={showTutorial} y={100} action="tap" />
      
      <button 
        onClick={onBack}
        className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg"
      >
        <ArrowLeft size={32} className="text-green-800" />
      </button>

      <div className="min-h-full flex flex-col items-center p-4 py-24">
        <h2 className="text-3xl md:text-5xl font-bold text-green-800 mb-8 text-center bg-white px-8 py-4 rounded-full shadow-sm border-4 border-green-200">
          مستكشف الطبيعة 🌿
        </h2>

        <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 gap-4 md:gap-6 w-full max-w-5xl" dir="rtl">
          {NATURE_ITEMS.map((item) => (
            <motion.button
              key={item.id}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => handleSelect(item)}
              className={`${item.color} aspect-square rounded-3xl shadow-lg border-4 border-white/50 flex flex-col items-center justify-center text-white overflow-hidden relative`}
            >
              <div className="absolute inset-0 bg-gradient-to-b from-white/20 to-transparent pointer-events-none" />
              <span className="text-6xl md:text-7xl drop-shadow-md mb-2">{item.emoji}</span>
              <span className="font-bold text-lg md:text-xl drop-shadow-md bg-black/20 px-3 py-1 rounded-full w-[90%] truncate text-center">
                {item.name}
              </span>
            </motion.button>
          ))}
        </div>
      </div>

      {/* نافذة عرض النبات/الفاكهة */}
      <AnimatePresence>
        {selectedItem && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4"
            onClick={() => setSelectedItem(null)}
          >
            <motion.div
              initial={{ scale: 0.5, y: 50, rotate: -10 }}
              animate={{ scale: 1, y: 0, rotate: 0 }}
              exit={{ scale: 0.5, y: 50, rotate: 10 }}
              className={`${selectedItem.color} w-full max-w-md rounded-[3rem] p-8 shadow-2xl border-8 border-white flex flex-col items-center relative`}
              onClick={(e) => e.stopPropagation()}
            >
              <button 
                onClick={() => setSelectedItem(null)}
                className="absolute top-4 right-4 bg-white/30 hover:bg-white/50 p-2 rounded-full transition-colors"
              >
                <X size={32} className="text-white" />
              </button>
              
              <motion.div 
                animate={{ y: [0, -10, 0] }} 
                transition={{ repeat: Infinity, duration: 2 }}
                className="text-9xl md:text-[10rem] drop-shadow-2xl mb-6"
              >
                {selectedItem.emoji}
              </motion.div>
              
              <div className="bg-white w-full rounded-3xl p-6 flex flex-col items-center gap-2 shadow-inner">
                <span className="text-4xl md:text-5xl font-black text-slate-800">{selectedItem.name}</span>
                <span className="text-lg font-bold text-slate-400">
                  {selectedItem.type === 'fruit' ? 'فواكه 🍎' : selectedItem.type === 'vegetable' ? 'خضروات 🥕' : 'طبيعة 🌿'}
                </span>
              </div>
              
              <button 
                onClick={() => handleSelect(selectedItem)}
                className="mt-6 bg-white/20 hover:bg-white/30 text-white px-8 py-4 rounded-full font-bold text-2xl transition-colors flex items-center gap-3 shadow-lg"
              >
                🔊 استمع
              </button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
