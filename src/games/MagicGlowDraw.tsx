import React, { useRef, useEffect, useState } from 'react';
import { ArrowLeft, Trash2 } from 'lucide-react';

interface MagicGlowDrawProps {
  onBack: () => void;
}

export default function MagicGlowDraw({ onBack }: MagicGlowDrawProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const hueRef = useRef(0);
  const lastPosRef = useRef<{ x: number; y: number } | null>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const resize = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
      // Fill with dark background
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.fillStyle = '#0f172a'; // slate-900
        ctx.fillRect(0, 0, canvas.width, canvas.height);
      }
    };

    window.addEventListener('resize', resize);
    resize();

    return () => window.removeEventListener('resize', resize);
  }, []);

  const getPos = (e: React.PointerEvent) => {
    return { x: e.clientX, y: e.clientY };
  };

  const startDrawing = (e: React.PointerEvent) => {
    setIsDrawing(true);
    lastPosRef.current = getPos(e);
    (e.target as HTMLElement).setPointerCapture(e.pointerId);
  };

  const draw = (e: React.PointerEvent) => {
    if (!isDrawing || !lastPosRef.current) return;

    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (!canvas || !ctx) return;

    const currentPos = getPos(e);

    ctx.beginPath();
    ctx.moveTo(lastPosRef.current.x, lastPosRef.current.y);
    ctx.lineTo(currentPos.x, currentPos.y);
    
    ctx.strokeStyle = `hsl(${hueRef.current}, 100%, 60%)`;
    ctx.lineWidth = 15;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    
    // Glow effect
    ctx.shadowBlur = 20;
    ctx.shadowColor = `hsl(${hueRef.current}, 100%, 60%)`;
    
    ctx.stroke();

    lastPosRef.current = currentPos;
    hueRef.current = (hueRef.current + 2) % 360;
  };

  const stopDrawing = (e: React.PointerEvent) => {
    setIsDrawing(false);
    lastPosRef.current = null;
    (e.target as HTMLElement).releasePointerCapture(e.pointerId);
  };

  const clearCanvas = () => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (!canvas || !ctx) return;
    ctx.fillStyle = '#0f172a';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
  };

  return (
    <div className="w-full h-full relative bg-slate-900 overflow-hidden">
      <div className="absolute top-6 left-6 z-50 flex gap-4">
        <button 
          onClick={onBack}
          className="bg-white/10 backdrop-blur-md p-4 rounded-full shadow-lg border border-white/20"
        >
          <ArrowLeft size={32} className="text-white" />
        </button>
        <button 
          onClick={clearCanvas}
          className="bg-white/10 backdrop-blur-md p-4 rounded-full shadow-lg border border-white/20"
        >
          <Trash2 size={32} className="text-white" />
        </button>
      </div>

      <canvas
        ref={canvasRef}
        onPointerDown={startDrawing}
        onPointerMove={draw}
        onPointerUp={stopDrawing}
        onPointerCancel={stopDrawing}
        className="w-full h-full touch-none"
      />
    </div>
  );
}
