import { ArrowLeft, Star, Clock, Gamepad2 } from 'lucide-react';
import { UserProfile } from './Settings';

interface ParentsDashboardProps {
  profile: UserProfile;
  onBack: () => void;
}

export default function ParentsDashboard({ profile, onBack }: ParentsDashboardProps) {
  const stats = profile.stats || {};
  const totalGamesPlayed = Object.values(stats).reduce((acc, curr) => acc + curr.played, 0);
  const totalStarsEarned = Object.values(stats).reduce((acc, curr) => acc + curr.stars, 0);

  const gameNames: Record<string, string> = {
    balloon: 'بالونات',
    animal: 'حيوانات',
    draw: 'رسم حر',
    color: 'ألوان',
    sorter: 'أشكال',
    piano: 'بيانو',
    fish: 'سمك',
    jigsaw: 'تركيب',
    memory: 'ذاكرة',
    arabic: 'حروف عربي',
    english: 'حروف English',
    nature: 'طبيعة',
    counting: 'عد الأشياء',
    lettermatch: 'توصيل حروف',
    healthyfood: 'أكل صحي',
    house: 'بيتي',
    animalfamily: 'عائلات',
    numbers: 'أرقام 123',
    time: 'الوقت',
    drawshapes: 'ارسم شكل',
    moon: 'القمر',
    coloring: 'تلوين',
    guesssound: 'خمن الصوت',
    simplemath: 'حساب بسيط',
    tashkeel: 'تشكيل الحروف',
    letteranimal: 'حروف وحيوانات', // +++ اللعبة الجديدة +++
  };

  return (
    <div className="w-full h-full bg-slate-100 p-4 md:p-6 flex flex-col items-center overflow-y-auto">
      <div className="w-full max-w-2xl bg-white rounded-3xl shadow-xl p-6 mt-4 relative border-4 border-slate-200">
        <button onClick={onBack} className="absolute top-6 left-6 text-slate-400 hover:text-slate-600 bg-slate-100 rounded-full p-2">
          <ArrowLeft size={24} />
        </button>
        <h2 className="text-3xl font-bold text-indigo-800 mb-8 text-center">📊 لوحة متابعة الآباء</h2>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8" dir="rtl">
          <div className="bg-sky-50 p-4 rounded-2xl border-2 border-sky-200 flex items-center gap-4">
            <div className="bg-sky-200 p-3 rounded-full text-sky-600"><Gamepad2 size={32} /></div>
            <div>
              <p className="text-sm text-sky-600 font-bold">إجمالي الألعاب</p>
              <p className="text-2xl font-black text-sky-800">{totalGamesPlayed} مرة</p>
            </div>
          </div>
          <div className="bg-yellow-50 p-4 rounded-2xl border-2 border-yellow-200 flex items-center gap-4">
            <div className="bg-yellow-200 p-3 rounded-full text-yellow-600"><Star size={32} /></div>
            <div>
              <p className="text-sm text-yellow-600 font-bold">النجوم المكتسبة</p>
              <p className="text-2xl font-black text-yellow-800">{totalStarsEarned} نجمة</p>
            </div>
          </div>
        </div>

        <h3 className="text-2xl font-bold text-slate-700 mb-4 text-right">تفاصيل الألعاب</h3>
        <div className="space-y-3" dir="rtl">
          {Object.entries(stats).length === 0 ? (
            <p className="text-center text-slate-500 py-8">لم يتم لعب أي ألعاب بعد.</p>
          ) : (
            Object.entries(stats)
              .sort((a, b) => b[1].played - a[1].played)
              .map(([gameId, data]) => (
                <div key={gameId} className="flex items-center justify-between bg-slate-50 p-4 rounded-2xl border-2 border-slate-100">
                  <span className="font-bold text-lg text-slate-700">{gameNames[gameId] || gameId}</span>
                  <div className="flex gap-6">
                    <div className="flex flex-col items-center">
                      <span className="text-xs text-slate-400 font-bold">لعب</span>
                      <span className="font-black text-sky-600">{data.played}</span>
                    </div>
                    <div className="flex flex-col items-center">
                      <span className="text-xs text-slate-400 font-bold">نجوم</span>
                      <span className="font-black text-yellow-500">{data.stars}</span>
                    </div>
                  </div>
                </div>
              ))
          )}
        </div>
      </div>
    </div>
  );
}
