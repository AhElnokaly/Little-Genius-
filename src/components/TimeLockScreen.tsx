import React, { useState } from 'react';
import { motion } from 'motion/react';
import { Lock, Unlock } from 'lucide-react';

interface TimeLockScreenProps {
  onUnlock: () => void;
}

export default function TimeLockScreen({ onUnlock }: TimeLockScreenProps) {
  const [num1] = useState(Math.floor(Math.random() * 10) + 1);
  const [num2] = useState(Math.floor(Math.random() * 10) + 1);
  const [answer, setAnswer] = useState('');
  const [error, setError] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (parseInt(answer) === num1 * num2) {
      onUnlock();
    } else {
      setError(true);
      setAnswer('');
      setTimeout(() => setError(false), 1000);
    }
  };

  return (
    <div className="fixed inset-0 z-[100] bg-slate-900/95 backdrop-blur-md flex items-center justify-center p-4">
      <motion.div 
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        className="bg-white rounded-3xl shadow-2xl p-8 max-w-md w-full text-center border-4 border-slate-200"
        dir="rtl"
      >
        <div className="w-24 h-24 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-6">
          <Lock size={48} className="text-red-500" />
        </div>
        
        <h2 className="text-3xl font-bold text-slate-800 mb-4">انتهى وقت اللعب! ⏰</h2>
        <p className="text-lg text-slate-600 mb-8">
          لقد انتهى الوقت المخصص للعب. يرجى من الأهل حل المسألة لفتح الشاشة أو تعديل الوقت.
        </p>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="bg-slate-50 p-6 rounded-2xl border-2 border-slate-200">
            <p className="text-2xl font-bold text-slate-700 mb-4" dir="ltr">
              {num1} × {num2} = ?
            </p>
            <input
              type="number"
              value={answer}
              onChange={(e) => setAnswer(e.target.value)}
              className={`w-full text-center text-3xl font-bold p-4 rounded-xl border-4 outline-none transition-colors ${
                error ? 'border-red-400 bg-red-50' : 'border-slate-200 focus:border-sky-400'
              }`}
              placeholder="النتيجة"
              autoFocus
              dir="ltr"
            />
          </div>
          
          <button
            type="submit"
            className="w-full bg-sky-500 hover:bg-sky-600 text-white text-xl font-bold py-4 rounded-xl shadow-lg active:scale-95 transition-transform flex items-center justify-center gap-2"
          >
            <Unlock size={24} /> فتح الشاشة
          </button>
        </form>
      </motion.div>
    </div>
  );
}
