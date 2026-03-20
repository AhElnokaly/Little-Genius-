/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect, useRef } from 'react';
import Home from './components/Home';
import BalloonPop from './games/BalloonPop';
import AnimalFriends from './games/AnimalFriends';
import MagicGlowDraw from './games/MagicGlowDraw';
import ColorCatch from './games/ColorCatch';
import ShapeSorter from './games/ShapeSorter';
import ColorPiano from './games/ColorPiano';
import CatchFish from './games/CatchFish';
import JigsawPuzzle from './games/JigsawPuzzle';
import MemoryMatch from './games/MemoryMatch';
import ArabicLetters from './games/ArabicLetters'; // +++ أضيف بناءً على طلبك +++
import ArabicTashkeel from './games/ArabicTashkeel'; // +++ أضيف بناءً على طلبك +++
import EnglishLetters from './games/EnglishLetters'; // +++ أضيف بناءً على طلبك +++
import NatureExplorer from './games/NatureExplorer'; // +++ أضيف بناءً على طلبك +++
import CountingGame from './games/CountingGame'; // +++ أضيف بناءً على طلبك +++
import LetterMatch from './games/LetterMatch'; // +++ أضيف بناءً على طلبك +++
import HealthyFood from './games/HealthyFood'; // +++ أضيف بناءً على طلبك +++
import InteractiveHouse from './games/InteractiveHouse'; // +++ أضيف بناءً على طلبك +++
import AnimalFamily from './games/AnimalFamily'; // +++ أضيف بناءً على طلبك +++
import NumberBilingual from './games/NumberBilingual'; // +++ أضيف بناءً على طلبك +++
import TimeAndCalendar from './games/TimeAndCalendar'; // +++ أضيف بناءً على طلبك +++
import DrawShapes from './games/DrawShapes'; // +++ أضيف بناءً على طلبك +++
import MoonPhases from './games/MoonPhases'; // +++ أضيف بناءً على طلبك +++
import Coloring from './games/Coloring'; // +++ أضيف بناءً على طلبك +++
import GuessSound from './games/GuessSound'; // +++ أضيف بناءً على طلبك +++
import SimpleMath from './games/SimpleMath'; // +++ أضيف بناءً على طلبك +++
import ParentalGate from './components/ParentalGate';
import Settings, { UserProfile } from './components/Settings';
import StickerBook from './components/StickerBook';
import TimeLockScreen from './components/TimeLockScreen'; // +++ أضيف بناءً على طلبك +++
import { Star } from 'lucide-react';

export default function App() {
  const [activeGame, setActiveGame] = useState<string | null>(null);
  const [stars, setStars] = useState(0); // +++ أضيف بناءً على طلبك: تصفير النجوم عند فتح التطبيق +++
  const [profile, setProfile] = useState<UserProfile>(() => {
    const saved = localStorage.getItem('childProfile');
    return saved ? JSON.parse(saved) : { name: '', dob: '', lockEnabled: false };
  });
  const [gateAction, setGateAction] = useState<(() => void) | null>(null);
  const [isTimeLocked, setIsTimeLocked] = useState(false); // +++ أضيف بناءً على طلبك +++
  const [timeRemaining, setTimeRemaining] = useState<number | null>(null); // +++ أضيف بناءً على طلبك +++
  const [showExitGate, setShowExitGate] = useState(false); // +++ أضيف بناءً على طلبك +++

  // Refs for synchronous access in popstate handler
  const activeGameRef = useRef(activeGame);
  useEffect(() => { activeGameRef.current = activeGame; }, [activeGame]);

  const gateActionRef = useRef(gateAction);
  useEffect(() => { gateActionRef.current = gateAction; }, [gateAction]);

  const showExitGateRef = useRef(showExitGate);
  useEffect(() => { showExitGateRef.current = showExitGate; }, [showExitGate]);

  const isExitingRef = useRef(false);

  // +++ أضيف بناءً على طلبك: التحكم في زر الرجوع (Back Button) بشكل دقيق +++
  useEffect(() => {
    // Initialize history state to trap the back button
    window.history.replaceState({ app: true }, '');
    window.history.pushState({ app: true }, '');

    const handlePopState = (event: PopStateEvent) => {
      if (isExitingRef.current) return;

      // Prevent the browser from actually going back by pushing the state again
      window.history.pushState({ app: true }, '');

      if (gateActionRef.current) {
        // Close any open parental gate (e.g., when trying to open settings)
        setGateAction(null);
      } else if (showExitGateRef.current) {
        // Close the exit app parental gate
        setShowExitGate(false);
      } else if (activeGameRef.current !== null) {
        // Close the current game/screen and go back to home
        setActiveGame(null);
      } else {
        // We are on the home screen, show the exit app parental gate
        setShowExitGate(true);
      }
    };

    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, []);

  useEffect(() => {
    localStorage.setItem('childProfile', JSON.stringify(profile));
    
    // +++ أضيف بناءً على طلبك: تهيئة التايمر عند تغيير الإعدادات +++
    if (profile.playTimeLimit && profile.playTimeLimit > 0) {
      setTimeRemaining(profile.playTimeLimit * 60); // تحويل الدقائق لثواني
      setIsTimeLocked(false);
    } else {
      setTimeRemaining(null);
      setIsTimeLocked(false);
    }
  }, [profile]);

  // +++ أضيف بناءً على طلبك: منطق التايمر +++
  useEffect(() => {
    if (timeRemaining === null || isTimeLocked) return;

    const timer = setInterval(() => {
      setTimeRemaining(prev => {
        if (prev === null) return null;
        if (prev <= 1) {
          setIsTimeLocked(true);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [timeRemaining, isTimeLocked]);

  const handleWin = () => setStars((s) => s + 1);

  const handleBack = () => {
    // +++ أضيف بناءً على طلبك: العودة باستخدام History API +++
    window.history.back();
  };

  const handleSelectGame = (id: string) => {
    if (id === 'settings') {
      if (profile.lockEnabled) {
        setGateAction(() => () => {
          setActiveGame('settings');
        });
      } else {
        setActiveGame('settings');
      }
    } else {
      setActiveGame(id);
    }
  };

  // +++ أضيف بناءً على طلبك: الخروج من التطبيق بعد حل المسألة +++
  const handleExitSuccess = () => {
    setShowExitGate(false);
    isExitingRef.current = true;
    window.history.back();
    setTimeout(() => {
      window.history.back();
    }, 50);
  };

  const isBirthday = () => {
    if (!profile.dob) return false;
    const today = new Date();
    const dob = new Date(profile.dob);
    return today.getMonth() === dob.getMonth() && today.getDate() === dob.getDate();
  };

  const getAge = () => {
    if (!profile.dob) return 3;
    const diff = Date.now() - new Date(profile.dob).getTime();
    return Math.abs(new Date(diff).getUTCFullYear() - 1970);
  };

  const age = getAge();

  // +++ أضيف بناءً على طلبك: فتح الشاشة +++
  const handleUnlockTime = () => {
    setIsTimeLocked(false);
    // نوقف التايمر مؤقتاً حتى يدخل الأهل للإعدادات ويعدلوه
    setTimeRemaining(null); 
    setActiveGame('settings'); // نوديه للإعدادات عشان يزود الوقت أو يلغيه
  };

  return (
    <div className="w-full h-screen overflow-hidden bg-sky-50 font-sans touch-none select-none relative">
      {isTimeLocked && <TimeLockScreen onUnlock={handleUnlockTime} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {gateAction && <ParentalGate onSuccess={() => { gateAction(); setGateAction(null); }} onClose={() => setGateAction(null)} />}
      {showExitGate && <ParentalGate onSuccess={handleExitSuccess} onClose={() => setShowExitGate(false)} />} {/* +++ أضيف بناءً على طلبك +++ */}
      
      {/* Stars UI - hide in settings/stickers */}
      {activeGame !== 'settings' && activeGame !== 'stickers' && activeGame !== null && (
        <div className="absolute top-6 right-6 z-40 bg-white/90 backdrop-blur-md px-5 py-2 rounded-full shadow-lg border-4 border-yellow-300 flex items-center gap-2 pointer-events-none">
          <Star className="text-yellow-500 fill-yellow-500" size={28} />
          <span className="text-2xl font-extrabold text-yellow-600">{stars}</span>
        </div>
      )}

      {activeGame === null && <Home onSelect={handleSelectGame} profileName={profile.name} isBirthday={isBirthday()} avatar={profile.avatar} />}
      {activeGame === 'settings' && <Settings profile={profile} onSave={setProfile} onBack={handleBack} />}
      {activeGame === 'stickers' && <StickerBook stars={stars} onBack={handleBack} />}
      
      {activeGame === 'balloon' && <BalloonPop onBack={handleBack} onWin={handleWin} />}
      {activeGame === 'animal' && <AnimalFriends onBack={handleBack} onWin={handleWin} />}
      {activeGame === 'draw' && <MagicGlowDraw onBack={handleBack} />}
      {activeGame === 'color' && <ColorCatch onBack={handleBack} onWin={handleWin} />}
      {activeGame === 'sorter' && <ShapeSorter onBack={handleBack} onWin={handleWin} />}
      {activeGame === 'piano' && <ColorPiano onBack={handleBack} onWin={handleWin} />}
      {activeGame === 'fish' && <CatchFish onBack={handleBack} onWin={handleWin} />}
      {activeGame === 'jigsaw' && <JigsawPuzzle onBack={handleBack} onWin={handleWin} age={age} />}
      {activeGame === 'memory' && <MemoryMatch onBack={handleBack} onWin={handleWin} age={age} />}
      {activeGame === 'arabic' && <ArabicLetters onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'english' && <EnglishLetters onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'nature' && <NatureExplorer onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'counting' && <CountingGame onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'lettermatch' && <LetterMatch onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'healthyfood' && <HealthyFood onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'house' && <InteractiveHouse onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'animalfamily' && <AnimalFamily onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'numbers' && <NumberBilingual onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'time' && <TimeAndCalendar onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'drawshapes' && <DrawShapes onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'moon' && <MoonPhases onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'coloring' && <Coloring onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'guesssound' && <GuessSound onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'simplemath' && <SimpleMath onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
      {activeGame === 'tashkeel' && <ArabicTashkeel onBack={handleBack} onWin={handleWin} />} {/* +++ أضيف بناءً على طلبك +++ */}
    </div>
  );
}
