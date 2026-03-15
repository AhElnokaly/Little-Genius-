import { useState } from 'react';
import { ArrowLeft } from 'lucide-react';
import { motion } from 'motion/react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface AnimalFriendsProps {
  onBack: () => void;
  onWin?: () => void;
}

// +++ بناءً على طلبك: تم استبعاد الخنزير وإضافة حيوانات أخرى +++
const ANIMALS = [
  { id: 'cat', emoji: '🐱', sound: 'مياو', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/b/b1/Cat_Meow_2.ogg', color: 'bg-orange-300' },
  { id: 'dog', emoji: '🐶', sound: 'هوهو', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/7/71/Dog_bark_1.ogg', color: 'bg-blue-300' },
  { id: 'cow', emoji: '🐮', sound: 'مووو', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/d/d4/Cow_moo.ogg', color: 'bg-green-300' },
  { id: 'duck', emoji: '🦆', sound: 'كواك', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/a/a3/Duck_quack.ogg', color: 'bg-yellow-300' },
  { id: 'sheep', emoji: '🐑', sound: 'باع', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/b/b2/Sheep_bleat.ogg', color: 'bg-slate-300' },
  { id: 'frog', emoji: '🐸', sound: 'ريبیت', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/6/6d/Frog_croaking.ogg', color: 'bg-green-400' },
  { id: 'bird', emoji: '🐦', sound: 'زقزقة', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/1/15/Canary_singing.ogg', color: 'bg-sky-300' },
  { id: 'monkey', emoji: '🐵', sound: 'أوو أوو', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/4/4c/Chimpanzee_pant-hoot.ogg', color: 'bg-amber-300' },
  { id: 'horse', emoji: '🐴', sound: 'صهيل', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/8/87/Horse_whinny.ogg', color: 'bg-amber-500' },
  { id: 'elephant', emoji: '🐘', sound: 'بوق الفيل', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/b/b2/Elephant_trumpet.ogg', color: 'bg-slate-400' },
  { id: 'lion', emoji: '🦁', sound: 'زئير', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/e/e0/Lion_roar.ogg', color: 'bg-orange-400' },
  { id: 'rooster', emoji: '🐔', sound: 'كوكوكو', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/d/d1/Rooster_crow.ogg', color: 'bg-red-300' },
  { id: 'owl', emoji: '🦉', sound: 'هوو هوو', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/1/16/Owl_hoot.ogg', color: 'bg-indigo-300' },
  { id: 'bee', emoji: '🐝', sound: 'طنين', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/f/f6/Bee_buzz.ogg', color: 'bg-yellow-400' },
  { id: 'wolf', emoji: '🐺', sound: 'عواء', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/f/f3/Wolf_howl.ogg', color: 'bg-slate-500' },
  { id: 'turkey', emoji: '🦃', sound: 'جوبل', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/c/c4/Turkey_gobble.ogg', color: 'bg-orange-500' },
  { id: 'mouse', emoji: '🐭', sound: 'سقسقة', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/f/f1/Mouse_squeak.ogg', color: 'bg-stone-300' },
  { id: 'dolphin', emoji: '🐬', sound: 'كليك', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/7/7a/Dolphin_clicks.ogg', color: 'bg-cyan-300' },
  { id: 'goat', emoji: '🐐', sound: 'ماء', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/1/1b/Goat_bleat.ogg', color: 'bg-stone-400' },
  { id: 'penguin', emoji: '🐧', sound: 'بطريق', audioUrl: 'https://upload.wikimedia.org/wikipedia/commons/5/5a/Penguin_call.ogg', color: 'bg-sky-200' },
];

export default function AnimalFriends({ onBack, onWin }: AnimalFriendsProps) {
  const [showTutorial, setShowTutorial] = useState(true);
  // +++ أضيف بناءً على طلبك: نظام المراحل +++
  const [level, setLevel] = useState(1);
  const [interacted, setInteracted] = useState<string[]>([]);
  
  const currentAnimals = ANIMALS.slice(0, level * 2);

  const playSound = (animalId: string, sound: string, audioUrl: string, e: React.MouseEvent | React.TouchEvent | React.PointerEvent) => {
    e.stopPropagation();
    if (showTutorial) setShowTutorial(false);
    
    // +++ أضيف بناءً على طلبك: أصوات حقيقية مع بديل صوتي +++
    const audio = new Audio(audioUrl);
    audio.play().catch(() => {
      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
        const utterance = new SpeechSynthesisUtterance(sound);
        utterance.lang = 'ar-SA';
        utterance.rate = 0.8;
        utterance.pitch = 1.5;
        window.speechSynthesis.speak(utterance);
      }
    });

    // +++ أضيف بناءً على طلبك: التحقق من اكتمال المرحلة +++
    if (!interacted.includes(animalId)) {
      const newInteracted = [...interacted, animalId];
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
      const ctx = new (window.AudioContext || (window as any).webkitAudioContext)();
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
              onPointerDown={(e) => playSound(animal.id, animal.sound, animal.audioUrl, e)}
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
