import React, { useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Star, Trophy, Award, Sparkles } from 'lucide-react';
import confetti from 'canvas-confetti';
import { getAudioContext, speak } from '../utils/audio';

interface WinCelebrationProps {
  isOpen: boolean;
  onClose: () => void;
  earnedStars: number;
}

export default function WinCelebration({ isOpen, onClose, earnedStars }: WinCelebrationProps) {
  useEffect(() => {
    if (isOpen) {
      // 1. Play real Web Audio API chime (reliable ascending happy chime)
      playCelebrationChime();

      // 2. Egyptian verbal congrats
      const congratsPhrases = [
        "أحسنت يا بطل! أنت عبقري وممتاز جداً!",
        "يا سلام عليك يا بطل! فوز رائع وجميل!",
        "ممتاز جداً! حصلت على النجوم يا شاطر!",
        "رائع جداً! أنت ذكي للغاية يا بطل!"
      ];
      const randomPhrase = congratsPhrases[Math.floor(Math.random() * congratsPhrases.length)];
      speak(randomPhrase, 'ar-EG');

      // 3. Fire colorful beautiful confetti cannons
      triggerConfetti();
    }
  }, [isOpen]);

  const playCelebrationChime = () => {
    try {
      const ctx = getAudioContext();
      const now = ctx.currentTime;
      
      // Joyful ascending pentatonic arpeggio notes (C5, E5, G5, A5, C6)
      const notes = [523.25, 659.25, 783.99, 880.00, 1046.50]; 
      notes.forEach((freq, idx) => {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        
        osc.type = 'triangle'; // warmer than pure sine for cute game chime
        osc.frequency.setValueAtTime(freq, now + idx * 0.12);
        
        gain.gain.setValueAtTime(0, now + idx * 0.12);
        gain.gain.linearRampToValueAtTime(0.3, now + idx * 0.12 + 0.04);
        gain.gain.exponentialRampToValueAtTime(0.001, now + idx * 0.12 + 0.35);
        
        osc.connect(gain);
        gain.connect(ctx.destination);
        
        osc.start(now + idx * 0.12);
        osc.stop(now + idx * 0.12 + 0.4);
      });
    } catch (e) {
      console.error("Failed to play win chime:", e);
    }
  };

  const triggerConfetti = () => {
    const duration = 2.5 * 1000;
    const animationEnd = Date.now() + duration;
    const defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 9999 };

    function randomInRange(min: number, max: number) {
      return Math.random() * (max - min) + min;
    }

    const interval = setInterval(function() {
      const timeLeft = animationEnd - Date.now();

      if (timeLeft <= 0) {
        return clearInterval(interval);
      }

      const particleCount = 50 * (timeLeft / duration);
      // Confetti burst from random sides
      confetti({ ...defaults, particleCount, origin: { x: randomInRange(0.1, 0.3), y: Math.random() - 0.2 } });
      confetti({ ...defaults, particleCount, origin: { x: randomInRange(0.7, 0.9), y: Math.random() - 0.2 } });
    }, 250);
  };

  // Fun shake keyframes for Framer Motion to make it super bouncy & lively
  const cardVariants: any = {
    hidden: { scale: 0, rotate: -20, opacity: 0 },
    visible: { 
      scale: 1, 
      rotate: 0, 
      opacity: 1,
      transition: {
        type: "spring",
        stiffness: 260,
        damping: 15,
        mass: 0.8
      }
    },
    exit: { scale: 0, rotate: 15, opacity: 0, transition: { duration: 0.2 } }
  };

  const starContainerVariants: any = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.15,
        delayChildren: 0.2
      }
    }
  };

  const singleStarVariants: any = {
    hidden: { scale: 0, y: 50 },
    visible: {
      scale: [0, 1.4, 1],
      y: 0,
      rotate: [0, -15, 15, 0],
      transition: {
        type: "spring",
        stiffness: 300,
        damping: 10
      }
    }
  };

  const kidsWobble: any = {
    animate: {
      y: [0, -8, 0],
      rotate: [-1, 1, -1],
      transition: {
        duration: 2,
        repeat: Infinity,
        ease: "easeInOut"
      }
    }
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-[99999] flex items-center justify-center p-4">
          {/* Ambient colorful party backdrop overlay */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="absolute inset-0 bg-sky-950/40 backdrop-blur-md"
          />

          {/* Celebration Card */}
          <motion.div
            variants={cardVariants}
            initial="hidden"
            animate="visible"
            exit="exit"
            className="relative w-full max-w-md bg-gradient-to-b from-amber-50 to-white border-8 border-yellow-400 rounded-3xl p-8 shadow-2xl text-center select-none overflow-hidden"
          >
            {/* Playful background decorative shapes */}
            <div className="absolute top-0 left-0 w-16 h-16 bg-pink-200 rounded-full mix-blend-multiply filter blur-xl opacity-60 animate-blob" />
            <div className="absolute bottom-4 right-4 w-20 h-20 bg-yellow-200 rounded-full mix-blend-multiply filter blur-xl opacity-60 animate-blob" />

            {/* +++ أضيف بناءً على طلبك: مؤثرات حركية خفيفة للتكبير والاهتزاز +++ */}
            <motion.div variants={kidsWobble} animate="animate" className="relative z-10">
              {/* Achievement Badge */}
              <div className="flex justify-center mb-4">
                <div className="relative">
                  <motion.div
                    animate={{ rotate: 360 }}
                    transition={{ duration: 15, repeat: Infinity, ease: "linear" }}
                    className="absolute -inset-4 text-yellow-300 opacity-60"
                  >
                    <Sparkles size={48} className="absolute -top-3 -left-3" />
                    <Sparkles size={36} className="absolute -bottom-2 -right-2" />
                  </motion.div>
                  
                  <div className="bg-yellow-400 p-4 rounded-full border-4 border-white shadow-md">
                    <Trophy className="text-white w-12 h-12" />
                  </div>
                </div>
              </div>

              {/* Congratulations message */}
              <h2 className="text-3xl font-extrabold text-amber-600 mb-1 font-sans">
                أحسنت يا بطل! 🥳
              </h2>
              <p className="text-lg font-bold text-sky-600 mb-6 font-sans">
                لقد أنجزت المهمة بنجاح تام!
              </p>

              {/* Earned Stars Animation */}
              <motion.div 
                variants={starContainerVariants}
                initial="hidden"
                animate="visible"
                className="flex justify-center gap-3 mb-8"
              >
                {Array.from({ length: Math.min(earnedStars, 3) }).map((_, i) => (
                  <motion.div
                    key={i}
                    variants={singleStarVariants}
                    className="relative"
                  >
                    <Star className="text-yellow-400 fill-yellow-400 drop-shadow-md w-14 h-14" />
                    <motion.div
                      animate={{ scale: [1, 1.2, 1] }}
                      transition={{ duration: 1.5, repeat: Infinity, delay: i * 0.2 }}
                      className="absolute inset-0 flex items-center justify-center text-white font-black text-xl font-sans"
                    >
                      +1
                    </motion.div>
                  </motion.div>
                ))}
              </motion.div>

              {/* Encouragement sentence */}
              <div className="bg-amber-100/80 border-2 border-dashed border-amber-300 rounded-2xl py-3 px-6 mb-6 inline-block">
                <span className="text-base font-black text-amber-700 font-sans flex items-center gap-2 justify-center">
                  <Award className="text-amber-600" size={20} />
                  زادت نجومك بمقدار {earnedStars} {earnedStars === 1 ? 'نجمة' : 'نجوم'}! ⭐
                </span>
              </div>

              {/* Action Button with playful zoom/hover effect */}
              <div className="flex justify-center">
                <motion.button
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={onClose}
                  className="bg-gradient-to-r from-emerald-500 to-teal-400 hover:from-emerald-600 hover:to-teal-500 text-white font-extrabold text-xl py-3 px-10 rounded-full shadow-lg border-b-4 border-emerald-700 flex items-center gap-2 cursor-pointer font-sans"
                >
                  استمر باللعب 🚀
                </motion.button>
              </div>
            </motion.div>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
}
