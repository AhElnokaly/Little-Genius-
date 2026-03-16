import React, { useState, useRef, useEffect } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft, Eraser, Check } from 'lucide-react';
import confetti from 'canvas-confetti';
import { speak, getAudioContext } from '../utils/audio';

interface DrawShapesProps {
  onBack: () => void;
  onWin?: () => void;
}

const SHAPES = [
  { id: 'circle', name: 'دائرة', path: 'M 150 50 A 100 100 0 1 1 149.9 50', color: '#ef4444' },
  { id: 'triangle', name: 'مثلث', path: 'M 150 50 L 250 250 L 50 250 Z', color: '#3b82f6' },
  { id: 'square', name: 'مربع', path: 'M 50 50 L 250 50 L 250 250 L 50 250 Z', color: '#10b981' },
  { id: 'star', name: 'نجمة', path: 'M 150 20 L 190 100 L 280 110 L 210 170 L 230 260 L 150 210 L 70 260 L 90 170 L 20 110 L 110 100 Z', color: '#eab308' },
];

export default function DrawShapes({ onBack, onWin }: DrawShapesProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [level, setLevel] = useState(0);
  const [progress, setProgress] = useState(0);

  const currentShape = SHAPES[level % SHAPES.length];

  useEffect(() => {
    speak(`ارسم ${currentShape.name}`);
    clearCanvas();
  }, [level]);

  const startDrawing = (e: React.MouseEvent | React.TouchEvent) => {
    setIsDrawing(true);
    draw(e);
  };

  const stopDrawing = () => {
    setIsDrawing(false);
    const ctx = canvasRef.current?.getContext('2d');
    if (ctx) ctx.beginPath();
    checkProgress();
  };

  const draw = (e: React.MouseEvent | React.TouchEvent) => {
    if (!isDrawing) return;
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (!canvas || !ctx) return;

    const rect = canvas.getBoundingClientRect();
    const x = ('touches' in e ? e.touches[0].clientX : e.clientX) - rect.left;
    const y = ('touches' in e ? e.touches[0].clientY : e.clientY) - rect.top;

    ctx.lineWidth = 15;
    ctx.lineCap = 'round';
    ctx.strokeStyle = currentShape.color;

    ctx.lineTo(x, y);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(x, y);
  };

  const clearCanvas = () => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (canvas && ctx) {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      setProgress(0);
    }
  };

  const checkProgress = () => {
    // A simple heuristic: check how many pixels are drawn vs total canvas
    // In a real app, we'd compare drawn pixels with the shape's path area
    // For kids, we just reward them for drawing enough
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (!canvas || !ctx) return;

    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    let coloredPixels = 0;
    for (let i = 3; i < imageData.data.length; i += 4) {
      if (imageData.data[i] > 0) coloredPixels++;
    }

    // Arbitrary threshold for "done"
    const totalPixels = canvas.width * canvas.height;
    const percentage = (coloredPixels / totalPixels) * 100;
    
    setProgress(Math.min(100, percentage * 5)); // Multiply to make it easier

    if (percentage * 5 >= 80) {
      handleWin();
    }
  };

  const handleWin = () => {
    speak('ممتاز!');
    confetti({
      particleCount: 100,
      spread: 70,
      origin: { y: 0.6 }
    });
    try {
      const ctx = getAudioContext();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.type = 'sine';
      osc.frequency.setValueAtTime(400, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(800, ctx.currentTime + 0.5);
      gain.gain.setValueAtTime(0.5, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.5);
    } catch (e) {}

    setTimeout(() => {
      setLevel(l => l + 1);
    }, 2000);
  };

  return (
    <div className="w-full h-full relative bg-amber-50 flex flex-col items-center p-4 py-20">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-amber-800" />
      </button>

      <h2 className="text-3xl md:text-5xl font-bold text-amber-800 mb-8 text-center">
        ارسم {currentShape.name} 🎨
      </h2>

      <div className="relative w-[300px] h-[300px] bg-white rounded-3xl shadow-xl border-4 border-amber-200 overflow-hidden">
        {/* Guide Shape */}
        <svg className="absolute inset-0 w-full h-full opacity-20 pointer-events-none" viewBox="0 0 300 300">
          <path d={currentShape.path} fill="none" stroke={currentShape.color} strokeWidth="20" strokeLinecap="round" strokeLinejoin="round" strokeDasharray="20, 20" />
        </svg>

        <canvas
          ref={canvasRef}
          width={300}
          height={300}
          onMouseDown={startDrawing}
          onMouseUp={stopDrawing}
          onMouseOut={stopDrawing}
          onMouseMove={draw}
          onTouchStart={startDrawing}
          onTouchEnd={stopDrawing}
          onTouchMove={draw}
          className="absolute inset-0 w-full h-full cursor-crosshair touch-none"
        />
      </div>

      <div className="flex gap-4 mt-8">
        <button onClick={clearCanvas} className="bg-white p-4 rounded-full shadow-lg text-red-500 hover:bg-red-50">
          <Eraser size={32} />
        </button>
        <button onClick={handleWin} className="bg-white p-4 rounded-full shadow-lg text-green-500 hover:bg-green-50">
          <Check size={32} />
        </button>
      </div>

      <div className="w-64 h-4 bg-gray-200 rounded-full mt-8 overflow-hidden">
        <motion.div 
          className="h-full bg-green-500"
          initial={{ width: 0 }}
          animate={{ width: `${progress}%` }}
        />
      </div>
    </div>
  );
}
