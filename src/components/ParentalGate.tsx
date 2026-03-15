import { useState, useEffect } from 'react';
import { X } from 'lucide-react';

interface ParentalGateProps {
  onSuccess: () => void;
  onClose: () => void;
}

export default function ParentalGate({ onSuccess, onClose }: ParentalGateProps) {
  const [num1, setNum1] = useState(0);
  const [num2, setNum2] = useState(0);
  const [options, setOptions] = useState<number[]>([]);

  useEffect(() => {
    const n1 = Math.floor(Math.random() * 5) + 1;
    const n2 = Math.floor(Math.random() * 5) + 1;
    setNum1(n1);
    setNum2(n2);
    const ans = n1 + n2;
    const opts = [ans, ans + 1, ans - 1, ans + 2].sort(() => Math.random() - 0.5).slice(0, 3);
    if (!opts.includes(ans)) opts[0] = ans;
    setOptions(opts.sort(() => Math.random() - 0.5));
  }, []);

  return (
    <div className="absolute inset-0 z-[100] bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl p-8 max-w-sm w-full relative text-center shadow-2xl border-4 border-sky-200">
        <button onClick={onClose} className="absolute top-4 left-4 text-slate-400 hover:text-slate-600 bg-slate-100 rounded-full p-2">
          <X size={24} />
        </button>
        <h2 className="text-2xl font-bold text-slate-800 mb-2">للوالدين فقط 👨‍👩‍👧</h2>
        <p className="text-slate-600 mb-6 font-medium">الرجاء حل المسألة للمتابعة:</p>
        <div className="text-5xl font-bold text-sky-600 mb-8 drop-shadow-sm" dir="ltr">
          {num1} + {num2} = ?
        </div>
        <div className="flex justify-center gap-4">
          {options.map((opt, i) => (
            <button
              key={i}
              onClick={() => opt === num1 + num2 ? onSuccess() : onClose()}
              className="w-16 h-16 rounded-2xl bg-sky-100 text-sky-800 text-3xl font-bold hover:bg-sky-200 shadow-sm border-2 border-sky-200 active:scale-95 transition-transform"
            >
              {opt}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
