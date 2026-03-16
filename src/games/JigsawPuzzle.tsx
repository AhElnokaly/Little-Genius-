import React, { useState } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft } from 'lucide-react';
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';

interface JigsawPuzzleProps {
  onBack: () => void;
  onWin?: () => void;
  age?: number;
}

// +++ أضيف بناءً على طلبك: لعبة تركيب بسيطة (Jigsaw) للأطفال +++
const PIECES = [
  { id: 'roof', name: 'سقف', color: 'bg-red-500', shape: 'polygon(50% 0%, 0% 100%, 100% 100%)', w: 'w-32 md:w-48', h: 'h-24 md:h-32' },
  { id: 'base', name: 'جدار', color: 'bg-yellow-400', shape: 'none', w: 'w-32 md:w-48', h: 'h-32 md:h-48' },
  { id: 'door', name: 'باب', color: 'bg-blue-500', shape: 'none', w: 'w-12 md:w-16', h: 'h-16 md:h-24' },
  { id: 'window', name: 'نافذة', color: 'bg-sky-300', shape: 'circle(50% at 50% 50%)', w: 'w-10 md:w-12', h: 'h-10 md:h-12' },
];

export default function JigsawPuzzle({ onBack, onWin, age = 3 }: JigsawPuzzleProps) {
  // +++ أضيف بناءً على طلبك: نظام المراحل +++
  const [level, setLevel] = useState(1);
  const [placed, setPlaced] = useState<string[]>([]);
  const [showTutorial, setShowTutorial] = useState(true);

  // L1: 2 pieces, L2: 3 pieces, L3: 4 pieces
  const activePieces = PIECES.slice(0, level + 1);

  const playSound = (type: 'correct' | 'incorrect') => {
    try {
      const ctx = new (window.AudioContext || (window as any).webkitAudioContext)();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      
      if (type === 'correct') {
        osc.type = 'sine';
        osc.frequency.setValueAtTime(400, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(800, ctx.currentTime + 0.1);
        gain.gain.setValueAtTime(0.5, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.1);
      } else {
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(200, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(150, ctx.currentTime + 0.2);
        gain.gain.setValueAtTime(0.3, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.2);
      }
      
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.2);
    } catch (e) {}
  };

  const handlePlace = (id: string, e: React.PointerEvent) => {
    if (placed.includes(id)) return;
    
    if (showTutorial) setShowTutorial(false);
    
    const newPlaced = [...placed, id];
    setPlaced(newPlaced);
    playSound('correct');
    
    if (newPlaced.length === activePieces.length) {
      if (onWin) onWin();
      confetti({
        particleCount: 50,
        spread: 70,
        origin: { x: e.clientX / window.innerWidth, y: e.clientY / window.innerHeight }
      });
      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
        const utterance = new SpeechSynthesisUtterance('بيت جميل!');
        utterance.lang = 'ar-EG';
        window.speechSynthesis.speak(utterance);
      }
      // +++ أضيف بناءً على طلبك: الانتقال للمرحلة التالية +++
      if (level < 3) {
        setTimeout(() => {
          setLevel(l => l + 1);
          setPlaced([]);
        }, 2000);
      }
    }
  };

  const handleMiss = () => {
    playSound('incorrect');
  };

  return (
    <div className="w-full h-full relative bg-green-50 flex flex-col items-center justify-center p-4">
      <TutorialHand show={showTutorial} y={100} action="drag" />
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-green-800" />
      </button>
      {/* +++ أضيف بناءً على طلبك: مؤشر المرحلة +++ */}
      <div className="absolute top-24 right-6 z-50 bg-white px-4 py-2 rounded-full shadow-lg font-bold text-green-800">
        مرحلة {level}
      </div>
      
      <h2 className="text-3xl md:text-5xl font-bold text-green-800 mb-8 text-center">
        يلا نبني البيت! 🏠
      </h2>

      {/* Target Area */}
      <div className="flex flex-col items-center mb-16 relative">
        {/* Roof Target */}
        <div className={`border-4 border-dashed border-green-300 flex items-center justify-center mb-1 ${PIECES[0].w} ${PIECES[0].h}`} style={{ clipPath: PIECES[0].shape }}>
          {placed.includes('roof') && (
            <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${PIECES[0].color}`} style={{ clipPath: PIECES[0].shape }} />
          )}
        </div>
        {/* Base Target */}
        <div className={`border-4 border-dashed border-green-300 flex items-center justify-center ${PIECES[1].w} ${PIECES[1].h} relative`}>
          {placed.includes('base') && (
            <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${PIECES[1].color}`} />
          )}
          {/* Door Target (Only if level >= 2) */}
          {level >= 2 && (
            <div className={`absolute bottom-0 border-4 border-dashed border-green-400 ${PIECES[2].w} ${PIECES[2].h}`}>
              {placed.includes('door') && (
                <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${PIECES[2].color}`} />
              )}
            </div>
          )}
          {/* Window Target (Only if level >= 3) */}
          {level >= 3 && (
            <div className={`absolute top-4 right-4 border-4 border-dashed border-green-400 rounded-full ${PIECES[3].w} ${PIECES[3].h}`}>
              {placed.includes('window') && (
                <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full rounded-full ${PIECES[3].color}`} />
              )}
            </div>
          )}
        </div>
      </div>

      {/* Draggable Pieces */}
      <div className="flex gap-4 md:gap-8 items-end">
        {activePieces.map((p) => (
          !placed.includes(p.id) && (
            <motion.div
              key={`drag-${p.id}`}
              drag
              dragSnapToOrigin
              onDragEnd={(e, info) => {
                if (info.offset.y < -50) {
                  handlePlace(p.id, e as any);
                } else {
                  handleMiss();
                }
              }}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
              className={`${p.w} ${p.h} ${p.color} shadow-xl cursor-grab active:cursor-grabbing`}
              style={{ clipPath: p.shape !== 'none' ? p.shape : undefined }}
            />
          )
        ))}
      </div>
    </div>
  );
}
