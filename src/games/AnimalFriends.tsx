import React, { useState } from 'react';
import { ArrowLeft } from 'lucide-react';
import { motion } from 'motion/react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';
import { speak, getAudioContext } from '../utils/audio';

interface AnimalFriendsProps {
  onBack: () => void;
  onWin?: () => void;
}

// +++ بناءً على طلبك: تم استبعاد الخنزير وإضافة حيوانات أخرى +++
const ANIMALS = [
  { id: 'cat', name: 'القطة', emoji: '🐱', sound: 'مياو', audioUrl: '/cat.mp3', color: 'bg-orange-300' },
  { id: 'dog', name: 'الكلب', emoji: '🐶', sound: 'نباح', audioUrl: '/dog.mp3', color: 'bg-blue-300' },
  { id: 'cow', name: 'البقرة', emoji: '🐮', sound: 'خوار', audioUrl: '/cow.mp3', color: 'bg-green-300' },
  { id: 'duck', name: 'البطة', emoji: '🦆', sound: 'بطبطة', audioUrl: '/duck.ogg', color: 'bg-yellow-300' },
  { id: 'sheep', name: 'الخروف', emoji: '🐑', sound: 'مأمأة', audioUrl: '/sheep.ogg', color: 'bg-slate-300' },
  { id: 'frog', name: 'الضفدع', emoji: '🐸', sound: 'نقيق', audioUrl: '/frog.ogg', color: 'bg-green-400' },
  { id: 'bird', name: 'العصفور', emoji: '🐦', sound: 'زقزقة', audioUrl: '/bird.mp3', color: 'bg-sky-300' },
  { id: 'monkey', name: 'القرد', emoji: '🐵', sound: 'ضحك', audioUrl: '/monkey.mp3', color: 'bg-amber-300' },
  { id: 'horse', name: 'الحصان', emoji: '🐴', sound: 'صهيل', audioUrl: '/horse.mp3', color: 'bg-amber-500' },
  { id: 'elephant', name: 'الفيل', emoji: '🐘', sound: 'نهيم', audioUrl: '/elephant.ogg', color: 'bg-slate-400' },
  { id: 'lion', name: 'الأسد', emoji: '🦁', sound: 'زئير', audioUrl: '/lion.mp3', color: 'bg-orange-400' },
  { id: 'rooster', name: 'الديك', emoji: '🐔', sound: 'صياح', audioUrl: '/rooster.ogg', color: 'bg-red-300' },
  { id: 'owl', name: 'البومة', emoji: '🦉', sound: 'نعيب', audioUrl: '/owl.ogg', color: 'bg-indigo-300' },
  { id: 'bee', name: 'النحلة', emoji: '🐝', sound: 'طنين', audioUrl: '/bee.ogg', color: 'bg-yellow-400' },
  { id: 'wolf', name: 'الديب', emoji: '🐺', sound: 'عواء', audioUrl: '/wolf.mp3', color: 'bg-slate-500' },
  { id: 'turkey', name: 'الديك الرومي', emoji: '🦃', sound: 'قرقرة', audioUrl: '/turkey.ogg', color: 'bg-orange-500' },
  { id: 'mouse', name: 'الفار', emoji: '🐭', sound: 'صرير', audioUrl: '/mouse.mp3', color: 'bg-stone-300' },
  { id: 'dolphin', name: 'الدرفيل', emoji: '🐬', sound: 'صفير', audioUrl: '/dolphin.ogg', color: 'bg-cyan-300' },
  { id: 'goat', name: 'المعزة', emoji: '🐐', sound: 'ثغاء', audioUrl: '/goat.ogg', color: 'bg-stone-400' },
  { id: 'penguin', name: 'البطريق', emoji: '🐧', sound: 'صياح', audioUrl: '/penguin.ogg', color: 'bg-sky-200' },
];

export default function AnimalFriends({ onBack, onWin }: AnimalFriendsProps) {
  const [showTutorial, setShowTutorial] = useState(true);
  // +++ أضيف بناءً على طلبك: نظام المراحل +++
  const [level, setLevel] = useState(1);
  const [interacted, setInteracted] = useState<string[]>([]);
  
  const currentAnimals = ANIMALS.slice(0, level * 2);

  const playSound = async (animal: typeof ANIMALS[0], e: React.MouseEvent | React.TouchEvent | React.PointerEvent) => {
    e.stopPropagation();
    if (showTutorial) setShowTutorial(false);
    
    // +++ أضيف بناءً على طلبك: نطق اسم الحيوان وصوته ثم تشغيل الصوت +++
    await speak(`صوت ${animal.name} ${animal.sound}`, 'ar-EG');
    
    const audio = new Audio(animal.audioUrl);
    audio.play().catch(() => {});

    // +++ أضيف بناءً على طلبك: التحقق من اكتمال المرحلة +++
    if (!interacted.includes(animal.id)) {
      const newInteracted = [...interacted, animal.id];
      setInteracted(newInteracted);
      if (newInteracted.length === currentAnimals.length) {
        if (onWin) onWin();
        let clientX = window.innerWidth / 2;
        let clientY = window.innerHeight / 2;
        if ('touches' in e && e.touches.length > 0) {
          clientX = e.touches[0].clientX;
          clientY = e.touches[0].clientY;
        } else if ('clientX' in e) {
          clientX = (e as React.MouseEvent).clientX;
          clientY = (e as React.MouseEvent).clientY;
        }
        confetti({
          particleCount: 50,
          spread: 60,
          origin: { x: clientX / window.innerWidth, y: clientY / window.innerHeight },
          colors: ['#ffffff', '#ffeb3b']
        });
        if (level < 10) {
          setTimeout(() => {
            setLevel(l => l + 1);
            setInteracted([]);
          }, 1500);
        }
      }
    }
  };

  // +++ أضيف بناءً على طلبك: تغذية راجعة للإجراء الخاطئ (الضغط خارج الحيوانات) +++
  const handleMiss = () => {
    try {
      const ctx = getAudioContext();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.type = 'sine';
      osc.frequency.setValueAtTime(200, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(150, ctx.currentTime + 0.1);
      gain.gain.setValueAtTime(0.2, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.1);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.1);
    } catch (e) {}
  };

  return (
    <div className="w-full h-full relative bg-emerald-100 overflow-y-auto" onPointerDown={handleMiss}>
      <div className="min-h-full flex flex-col items-center justify-center p-4 py-24">
        <TutorialHand show={showTutorial} y={-50} />
        <button 
          onClick={onBack}
          className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg"
        >
          <ArrowLeft size={32} className="text-emerald-800" />
        </button>

        {/* +++ أضيف بناءً على طلبك: مؤشر المرحلة وتغيير الشبكة +++ */}
        <div className="absolute top-24 right-6 z-50 bg-white px-4 py-2 rounded-full shadow-lg font-bold text-emerald-800">
          مرحلة {level} / 10
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4 md:gap-6 w-full max-w-6xl mt-12">
          {currentAnimals.map((animal) => (
            <motion.button
              key={animal.id}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.9, rotate: [0, -10, 10, -10, 0] }}
              onPointerDown={(e) => playSound(animal, e)}
              className={`${animal.color} ${interacted.includes(animal.id) ? 'opacity-50' : 'opacity-100'} aspect-square rounded-3xl flex items-center justify-center shadow-xl border-4 border-white/50 text-6xl md:text-8xl transition-opacity`}
            >
              {animal.emoji}
            </motion.button>
          ))}
        </div>
      </div>
    </div>
  );
}
