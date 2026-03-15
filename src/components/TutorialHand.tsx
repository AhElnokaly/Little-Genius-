import { motion, AnimatePresence } from 'motion/react';

interface TutorialHandProps {
  show: boolean;
  x?: number;
  y?: number;
  action?: 'tap' | 'drag';
}

export default function TutorialHand({ show, x = 0, y = 0, action = 'tap' }: TutorialHandProps) {
  return (
    <AnimatePresence>
      {show && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 0.9 }}
          exit={{ opacity: 0 }}
          className="absolute z-[60] pointer-events-none text-7xl drop-shadow-2xl"
          style={{ left: `calc(50% + ${x}px)`, top: `calc(50% + ${y}px)` }}
        >
          <motion.div
            animate={
              action === 'tap' 
                ? { scale: [1, 0.8, 1], y: [0, -15, 0] } 
                : { x: [0, 100, 0], y: [0, -50, 0] }
            }
            transition={{ repeat: Infinity, duration: 1.5, ease: "easeInOut" }}
          >
            👆
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
