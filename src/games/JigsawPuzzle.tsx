import React, { useState } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft, RotateCcw } from 'lucide-react'; // +++ أضيف بناءً على طلبك +++
import confetti from 'canvas-confetti';
import TutorialHand from '../components/TutorialHand';
import { speak } from '../utils/audio'; // +++ أضيف بناءً على طلبك +++

interface JigsawPuzzleProps {
  onBack: () => void;
  onWin?: () => void;
  age?: number;
}

// +++ أضيف بناءً على طلبك: لعبة تركيب بسيطة (Jigsaw) للأطفال مع مراحل إضافية +++
const PIECES = [
  { id: 'roof', name: 'سقف', color: 'bg-red-500', shape: 'polygon(50% 0%, 0% 100%, 100% 100%)', w: 'w-32 md:w-48', h: 'h-24 md:h-32' },
  { id: 'base', name: 'جدار', color: 'bg-yellow-400', shape: 'none', w: 'w-32 md:w-48', h: 'h-32 md:h-48' },
  { id: 'door', name: 'باب', color: 'bg-blue-500', shape: 'none', w: 'w-12 md:w-16', h: 'h-16 md:h-24' },
  { id: 'window', name: 'نافذة', color: 'bg-sky-300', shape: 'circle(50% at 50% 50%)', w: 'w-10 md:w-12', h: 'h-10 md:h-12' },
  { id: 'chimney', name: 'مدخنة', color: 'bg-orange-600', shape: 'none', w: 'w-8 md:w-12', h: 'h-16 md:h-20' },
  { id: 'sun', name: 'شمس', color: 'bg-yellow-500', shape: 'circle(50% at 50% 50%)', w: 'w-16 md:w-24', h: 'h-16 md:h-24' },
  { id: 'cloud', name: 'سحابة', color: 'bg-white', shape: 'ellipse(50% 50% at 50% 50%)', w: 'w-24 md:w-32', h: 'h-12 md:h-16' },
  { id: 'tree', name: 'شجرة', color: 'bg-green-600', shape: 'polygon(50% 0%, 0% 100%, 100% 100%)', w: 'w-20 md:w-28', h: 'h-32 md:h-40' },
  { id: 'trunk', name: 'جذع', color: 'bg-amber-800', shape: 'none', w: 'w-6 md:w-8', h: 'h-12 md:h-16' },
];

export default function JigsawPuzzle({ onBack, onWin, age = 3 }: JigsawPuzzleProps) {
  // +++ أضيف بناءً على طلبك: نظام المراحل +++
  const [level, setLevel] = useState(1);
  const [placed, setPlaced] = useState<string[]>([]);
  const [showTutorial, setShowTutorial] = useState(true);

  // L1: 2 pieces, L2: 3 pieces, L3: 4 pieces, L4: 5 pieces, L5: 6 pieces, L6: 7 pieces, L7: 9 pieces
  let activePiecesCount = 2;
  if (level === 2) activePiecesCount = 3;
  if (level === 3) activePiecesCount = 4;
  if (level === 4) activePiecesCount = 5;
  if (level === 5) activePiecesCount = 6;
  if (level === 6) activePiecesCount = 7;
  if (level >= 7) activePiecesCount = 9;

  const activePieces = PIECES.slice(0, activePiecesCount);

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
        particleCount: 100,
        spread: 80,
        origin: { x: e.clientX / window.innerWidth, y: e.clientY / window.innerHeight }
      });
      speak('عمل رائع!');
      
      // +++ أضيف بناءً على طلبك: الانتقال للمرحلة التالية +++
      if (level < 7) {
        setTimeout(() => {
          setLevel(l => l + 1);
          setPlaced([]);
        }, 2500);
      } else {
        speak('أنت بطل! خلصت كل المراحل!');
      }
    }
  };

  const handleMiss = () => {
    playSound('incorrect');
  };

  return (
    <div className="w-full h-full relative bg-green-50 flex flex-col items-center justify-center p-4 overflow-hidden">
      <TutorialHand show={showTutorial} y={100} action="drag" />
      <button onClick={onBack} className="absolute top-6 left-6 z-[100] bg-white p-4 rounded-full shadow-lg hover:bg-gray-100 active:scale-95 transition-all">
        <ArrowLeft size={32} className="text-green-800" />
      </button>
      {/* +++ أضيف بناءً على طلبك: مؤشر المرحلة وإعادة اللعب +++ */}
      <div className="absolute top-6 right-6 z-50 flex gap-2">
        {level >= 7 && placed.length === activePieces.length && (
          <button onClick={() => { setLevel(1); setPlaced([]); }} className="bg-white px-4 py-2 rounded-full shadow-lg font-bold text-green-800 flex items-center gap-2 hover:bg-gray-100 active:scale-95 transition-all">
            <RotateCcw size={20} /> العب تاني
          </button>
        )}
        <div className="bg-white px-4 py-2 rounded-full shadow-lg font-bold text-green-800">
          مرحلة {level} / 7
        </div>
      </div>
      
      <h2 className="text-3xl md:text-5xl font-bold text-green-800 mb-4 text-center mt-16 md:mt-0">
        يلا نبني البيت! 🏠
      </h2>

      {/* Target Area */}
      <div className="relative w-full max-w-3xl h-[350px] md:h-[450px] mb-8 border-b-8 border-green-600 flex justify-center items-end pb-0">
        
        {/* Sun (Level >= 5) */}
        {level >= 5 && (
          <div className={`absolute top-4 right-8 border-4 border-dashed border-green-300 rounded-full ${PIECES[5].w} ${PIECES[5].h}`}>
            {placed.includes('sun') && (
              <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full rounded-full ${PIECES[5].color}`} />
            )}
          </div>
        )}

        {/* Cloud (Level >= 6) */}
        {level >= 6 && (
          <div className={`absolute top-12 left-12 border-4 border-dashed border-green-300 ${PIECES[6].w} ${PIECES[6].h}`} style={{ borderRadius: '50%' }}>
            {placed.includes('cloud') && (
              <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${PIECES[6].color}`} style={{ borderRadius: '50%' }} />
            )}
          </div>
        )}

        {/* Tree (Level >= 7) */}
        {level >= 7 && (
          <div className="absolute bottom-0 left-4 md:left-12 flex flex-col items-center">
            <div className={`border-4 border-dashed border-green-300 ${PIECES[7].w} ${PIECES[7].h} z-10`} style={{ clipPath: PIECES[7].shape }}>
              {placed.includes('tree') && (
                <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${PIECES[7].color}`} style={{ clipPath: PIECES[7].shape }} />
              )}
            </div>
            <div className={`border-4 border-dashed border-green-300 -mt-2 ${PIECES[8].w} ${PIECES[8].h}`}>
              {placed.includes('trunk') && (
                <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${PIECES[8].color}`} />
              )}
            </div>
          </div>
        )}

        {/* House */}
        <div className="flex flex-col items-center relative z-20">
          {/* Chimney (Level >= 4) */}
          {level >= 4 && (
            <div className={`absolute -top-12 md:-top-16 right-4 border-4 border-dashed border-green-300 ${PIECES[4].w} ${PIECES[4].h} z-0`}>
              {placed.includes('chimney') && (
                <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${PIECES[4].color}`} />
              )}
            </div>
          )}

          {/* Roof */}
          <div className={`border-4 border-dashed border-green-300 flex items-center justify-center mb-1 ${PIECES[0].w} ${PIECES[0].h} z-10`} style={{ clipPath: PIECES[0].shape }}>
            {placed.includes('roof') && (
              <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${PIECES[0].color}`} style={{ clipPath: PIECES[0].shape }} />
            )}
          </div>
          
          {/* Base */}
          <div className={`border-4 border-dashed border-green-300 flex items-center justify-center ${PIECES[1].w} ${PIECES[1].h} relative z-10`}>
            {placed.includes('base') && (
              <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${PIECES[1].color}`} />
            )}
            
            {/* Door (Level >= 2) */}
            {level >= 2 && (
              <div className={`absolute bottom-0 border-4 border-dashed border-green-400 ${PIECES[2].w} ${PIECES[2].h}`}>
                {placed.includes('door') && (
                  <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full ${PIECES[2].color}`} />
                )}
              </div>
            )}
            
            {/* Window (Level >= 3) */}
            {level >= 3 && (
              <div className={`absolute top-4 right-4 border-4 border-dashed border-green-400 rounded-full ${PIECES[3].w} ${PIECES[3].h}`}>
                {placed.includes('window') && (
                  <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className={`w-full h-full rounded-full ${PIECES[3].color}`} />
                )}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Draggable Pieces */}
      <div className="flex gap-2 md:gap-4 items-end flex-wrap justify-center w-full max-w-4xl z-30">
        {activePieces.map((p) => (
          !placed.includes(p.id) && (
            <motion.div
              key={`drag-${p.id}`}
              drag
              dragSnapToOrigin
              onDragEnd={(e, info) => {
                if (info.offset.y < -30) {
                  handlePlace(p.id, e as any);
                } else {
                  handleMiss();
                }
              }}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
              className={`${p.w} ${p.h} ${p.color} shadow-xl cursor-grab active:cursor-grabbing`}
              style={{ clipPath: p.shape !== 'none' ? p.shape : undefined, borderRadius: p.shape.includes('circle') || p.shape.includes('ellipse') ? '50%' : undefined }}
            />
          )
        ))}
      </div>
    </div>
  );
}
