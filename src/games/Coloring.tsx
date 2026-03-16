import { useState, useRef, useEffect } from 'react';
import { motion } from 'motion/react';
import { ArrowLeft, Eraser, Check } from 'lucide-react';
import confetti from 'canvas-confetti';
import { speak, getAudioContext } from '../utils/audio';

interface ColoringProps {
  onBack: () => void;
  onWin?: () => void;
}

const SHAPES = [
  { id: 'apple', name: 'تفاحة', path: 'M150,50 C100,0 0,50 50,150 C100,250 150,250 150,250 C150,250 200,250 250,150 C300,50 200,0 150,50 Z', color: '#ef4444' },
  { id: 'leaf', name: 'ورقة شجر', path: 'M150,20 C250,20 280,150 150,280 C20,150 50,20 150,20 Z', color: '#22c55e' },
  { id: 'heart', name: 'قلب', path: 'M150,80 C150,80 100,0 30,50 C-40,100 50,200 150,280 C250,200 340,100 270,50 C200,0 150,80 150,80 Z', color: '#ec4899' },
  { id: 'star', name: 'نجمة', path: 'M150,20 L190,100 L280,110 L210,170 L230,260 L150,210 L70,260 L90,170 L20,110 L110,100 Z', color: '#eab308' },
];

const COLORS = ['#ef4444', '#f97316', '#eab308', '#22c55e', '#3b82f6', '#a855f7', '#ec4899'];

export default function Coloring({ onBack, onWin }: ColoringProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [level, setLevel] = useState(0);
  const [progress, setProgress] = useState(0);
  const [selectedColor, setSelectedColor] = useState(COLORS[0]);

  const currentShape = SHAPES[level % SHAPES.length];

  useEffect(() => {
    speak(`لون ال${currentShape.name}`);
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

    ctx.lineWidth = 30;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    ctx.strokeStyle = selectedColor;

    // Use globalCompositeOperation to only draw inside the shape
    ctx.globalCompositeOperation = 'source-atop';

    ctx.lineTo(x, y);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(x, y);
  };

  const clearCanvas = () => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (canvas && ctx) {
      // Clear everything
      ctx.globalCompositeOperation = 'source-over';
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      
      // Draw the shape mask
      ctx.fillStyle = '#f3f4f6'; // Light gray background for the shape
      const path = new Path2D(currentShape.path);
      ctx.fill(path);
      
      setProgress(0);
    }
  };

  const checkProgress = () => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (!canvas || !ctx) return;

    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    let coloredPixels = 0;
    let shapePixels = 0;

    for (let i = 0; i < imageData.data.length; i += 4) {
      const r = imageData.data[i];
      const g = imageData.data[i + 1];
      const b = imageData.data[i + 2];
      const a = imageData.data[i + 3];

      if (a > 0) {
        shapePixels++;
        // Check if it's not the light gray background (#f3f4f6 -> rgb(243, 244, 246))
        if (r !== 243 || g !== 244 || b !== 246) {
          coloredPixels++;
        }
      }
    }

    const percentage = shapePixels > 0 ? (coloredPixels / shapePixels) * 100 : 0;
    setProgress(Math.min(100, percentage));

    if (percentage >= 90) {
      handleWin();
    }
  };

  const handleWin = () => {
    speak('عمل رائع!');
    confetti({
      particleCount: 150,
      spread: 80,
      origin: { y: 0.6 }
    });
    try {
      const ctx = getAudioContext();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.type = 'sine';
      osc.frequency.setValueAtTime(500, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(1000, ctx.currentTime + 0.5);
      gain.gain.setValueAtTime(0.5, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + 0.5);
    } catch (e) {}

    setTimeout(() => {
      setLevel(l => l + 1);
    }, 3000);
  };

  return (
    <div className="w-full h-full relative bg-rose-50 flex flex-col items-center p-4 py-20 overflow-y-auto">
      <button onClick={onBack} className="absolute top-6 left-6 z-50 bg-white p-4 rounded-full shadow-lg">
        <ArrowLeft size={32} className="text-rose-800" />
      </button>

      <h2 className="text-3xl md:text-5xl font-bold text-rose-800 mb-8 text-center">
        لون ال{currentShape.name} 🖍️
      </h2>

      <div className="flex flex-col items-center gap-8 w-full max-w-2xl">
        {/* Progress Bar */}
        <div className="w-full max-w-md h-8 bg-white rounded-full shadow-inner overflow-hidden border-2 border-rose-200 relative">
          <motion.div 
            className="h-full bg-gradient-to-r from-green-400 to-green-500"
            initial={{ width: 0 }}
            animate={{ width: `${progress}%` }}
            transition={{ type: 'spring', stiffness: 50 }}
          />
          <div className="absolute inset-0 flex items-center justify-center font-bold text-slate-700 drop-shadow-md">
            {Math.round(progress)}%
          </div>
        </div>

        {/* Canvas Container */}
        <div className="relative w-[300px] h-[300px] bg-white rounded-3xl shadow-xl border-4 border-rose-200 overflow-hidden">
          {/* Outline */}
          <svg className="absolute inset-0 w-full h-full pointer-events-none z-10" viewBox="0 0 300 300">
            <path d={currentShape.path} fill="none" stroke="#1f2937" strokeWidth="8" strokeLinecap="round" strokeLinejoin="round" />
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

        {/* Color Palette */}
        <div className="flex flex-wrap justify-center gap-4 bg-white p-6 rounded-3xl shadow-lg border-2 border-rose-100">
          {COLORS.map(color => (
            <button
              key={color}
              onClick={() => setSelectedColor(color)}
              className={`w-12 h-12 rounded-full shadow-md transition-transform ${selectedColor === color ? 'scale-125 ring-4 ring-offset-2 ring-rose-400' : 'hover:scale-110'}`}
              style={{ backgroundColor: color }}
            />
          ))}
          <div className="w-px h-12 bg-gray-200 mx-2" />
          <button 
            onClick={clearCanvas} 
            className="w-12 h-12 rounded-full shadow-md bg-gray-100 flex items-center justify-center text-red-500 hover:bg-red-50 transition-colors"
            title="مسح"
          >
            <Eraser size={24} />
          </button>
        </div>
      </div>
    </div>
  );
}
