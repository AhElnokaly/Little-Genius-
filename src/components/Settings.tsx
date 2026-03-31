import { useState } from 'react';
import { ArrowLeft, Save } from 'lucide-react';

export interface UserProfile {
  name: string;
  dob: string;
  lockEnabled: boolean;
  playTimeLimit?: number;
  avatar?: string;
  difficulty?: 'easy' | 'medium' | 'hard'; // +++ أضيف بناءً على طلبك +++
  stats?: Record<string, { played: number; stars: number }>; // +++ أضيف بناءً على طلبك +++
}

interface SettingsProps {
  profile: UserProfile;
  onSave: (p: UserProfile) => void;
  onBack: () => void;
  onOpenDashboard?: () => void; // +++ أضيف بناءً على طلبك +++
}

const AVATARS = ['👦', '👧', '🐶', '🐱', '🦁', '🐯', '🐰', '🐼', '🦊', '🐸'];

export default function Settings({ profile, onSave, onBack, onOpenDashboard }: SettingsProps) {
  const [name, setName] = useState(profile.name);
  const [dob, setDob] = useState(profile.dob);
  const [lockEnabled, setLockEnabled] = useState(profile.lockEnabled);
  const [playTimeLimit, setPlayTimeLimit] = useState(profile.playTimeLimit || 0);
  const [avatar, setAvatar] = useState(profile.avatar || '👦');
  const [difficulty, setDifficulty] = useState<'easy' | 'medium' | 'hard'>(profile.difficulty || 'medium'); // +++ أضيف بناءً على طلبك +++

  const handleSave = () => {
    onSave({ ...profile, name, dob, lockEnabled, playTimeLimit, avatar, difficulty }); // +++ أضيف بناءً على طلبك +++
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
          {/* +++ أضيف بناءً على طلبك: اختيار الأفاتار +++ */}
          <div>
            <label className="block text-lg font-bold text-slate-700 mb-2">اختر شخصيتك</label>
            <div className="flex flex-wrap gap-2 justify-center bg-slate-50 p-4 rounded-2xl border-2 border-slate-200">
              {AVATARS.map((a) => (
                <button
                  key={a}
                  onClick={() => setAvatar(a)}
                  className={`text-4xl p-2 rounded-full transition-transform ${avatar === a ? 'bg-sky-200 scale-110 shadow-md' : 'hover:bg-slate-200 hover:scale-105'}`}
                >
                  {a}
                </button>
              ))}
            </div>
          </div>
          
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

          {/* +++ أضيف بناءً على طلبك: مستوى الصعوبة +++ */}
          <div className="bg-slate-50 border-2 border-slate-200 p-4 rounded-2xl">
            <label className="block text-lg font-bold text-slate-700 mb-2">مستوى الصعوبة</label>
            <div className="flex gap-2">
              <button onClick={() => setDifficulty('easy')} className={`flex-1 py-2 rounded-xl font-bold transition-colors ${difficulty === 'easy' ? 'bg-green-500 text-white' : 'bg-slate-200 text-slate-600 hover:bg-slate-300'}`}>سهل</button>
              <button onClick={() => setDifficulty('medium')} className={`flex-1 py-2 rounded-xl font-bold transition-colors ${difficulty === 'medium' ? 'bg-yellow-500 text-white' : 'bg-slate-200 text-slate-600 hover:bg-slate-300'}`}>متوسط</button>
              <button onClick={() => setDifficulty('hard')} className={`flex-1 py-2 rounded-xl font-bold transition-colors ${difficulty === 'hard' ? 'bg-red-500 text-white' : 'bg-slate-200 text-slate-600 hover:bg-slate-300'}`}>صعب</button>
            </div>
          </div>

          {/* +++ أضيف بناءً على طلبك: لوحة متابعة الآباء +++ */}
          {onOpenDashboard && (
            <button 
              onClick={onOpenDashboard}
              className="w-full py-3 mt-2 bg-indigo-100 text-indigo-700 rounded-2xl text-lg font-bold flex items-center justify-center gap-2 hover:bg-indigo-200 border-2 border-indigo-200 transition-colors"
            >
              📊 لوحة متابعة الآباء
            </button>
          )}

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
