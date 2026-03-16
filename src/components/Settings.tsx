import { useState } from 'react';
import { ArrowLeft, Save } from 'lucide-react';

export interface UserProfile {
  name: string;
  dob: string;
  lockEnabled: boolean;
  playTimeLimit?: number; // +++ أضيف بناءً على طلبك +++
}

interface SettingsProps {
  profile: UserProfile;
  onSave: (p: UserProfile) => void;
  onBack: () => void;
}

export default function Settings({ profile, onSave, onBack }: SettingsProps) {
  const [name, setName] = useState(profile.name);
  const [dob, setDob] = useState(profile.dob);
  const [lockEnabled, setLockEnabled] = useState(profile.lockEnabled);
  const [playTimeLimit, setPlayTimeLimit] = useState(profile.playTimeLimit || 0); // +++ أضيف بناءً على طلبك +++

  const handleSave = () => {
    onSave({ name, dob, lockEnabled, playTimeLimit }); // +++ أضيف بناءً على طلبك +++
    onBack();
  };

  return (
    <div className="w-full h-full bg-slate-100 p-4 md:p-6 flex flex-col items-center overflow-y-auto">
      <div className="w-full max-w-md bg-white rounded-3xl shadow-xl p-6 mt-4 relative border-4 border-slate-200">
        <button onClick={onBack} className="absolute top-6 left-6 text-slate-400 hover:text-slate-600 bg-slate-100 rounded-full p-2">
          <ArrowLeft size={24} />
        </button>
        <h2 className="text-3xl font-bold text-slate-800 mb-8 text-center">الإعدادات ⚙️</h2>
        
        <div className="space-y-6 text-right" dir="rtl">
          <div>
            <label className="block text-lg font-bold text-slate-700 mb-2">اسم الطفل</label>
            <input 
              type="text" 
              value={name} 
              onChange={e => setName(e.target.value)} 
              className="w-full p-4 rounded-2xl border-2 border-slate-200 focus:border-sky-400 outline-none text-lg font-medium bg-slate-50" 
              placeholder="مثال: أحمد" 
            />
          </div>
          <div>
            <label className="block text-lg font-bold text-slate-700 mb-2">تاريخ الميلاد (لتحديد الصعوبة)</label>
            <input 
              type="date" 
              value={dob} 
              onChange={e => setDob(e.target.value)} 
              className="w-full p-4 rounded-2xl border-2 border-slate-200 focus:border-sky-400 outline-none text-lg font-medium bg-slate-50" 
            />
          </div>
          <div className="flex items-center justify-between bg-slate-50 border-2 border-slate-200 p-4 rounded-2xl">
            <label className="text-lg font-bold text-slate-700">قفل الخروج من الألعاب</label>
            <button 
              onClick={() => setLockEnabled(!lockEnabled)} 
              className={`w-16 h-8 rounded-full p-1 transition-colors shadow-inner ${lockEnabled ? 'bg-green-500' : 'bg-slate-300'}`}
            >
              <div className={`w-6 h-6 bg-white rounded-full shadow-md transition-transform ${lockEnabled ? 'translate-x-[-32px]' : ''}`} />
            </button>
          </div>
          {/* +++ أضيف بناءً على طلبك: إعداد وقت اللعب +++ */}
          <div className="bg-slate-50 border-2 border-slate-200 p-4 rounded-2xl">
            <label className="block text-lg font-bold text-slate-700 mb-2">وقت اللعب المسموح (بالدقائق)</label>
            <p className="text-sm text-slate-500 mb-4">ضع 0 لإلغاء التايمر</p>
            <input 
              type="number" 
              min="0"
              max="120"
              value={playTimeLimit} 
              onChange={e => setPlayTimeLimit(Number(e.target.value))} 
              className="w-full p-4 rounded-2xl border-2 border-slate-200 focus:border-sky-400 outline-none text-lg font-medium bg-white text-left" 
              dir="ltr"
            />
          </div>
          <button 
            onClick={handleSave} 
            className="w-full py-4 mt-4 bg-sky-500 text-white rounded-2xl text-xl font-bold flex items-center justify-center gap-2 hover:bg-sky-600 shadow-lg active:scale-95 transition-transform"
          >
            <Save size={24} /> حفظ الإعدادات
          </button>
        </div>
      </div>
    </div>
  );
}
