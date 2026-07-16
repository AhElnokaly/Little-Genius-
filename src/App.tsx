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
import ArabicLetters from './games/ArabicLetters';
import ArabicTashkeel from './games/ArabicTashkeel';
import EnglishLetters from './games/EnglishLetters';
import NatureExplorer from './games/NatureExplorer';
import CountingGame from './games/CountingGame';
import LetterMatch from './games/LetterMatch';
import HealthyFood from './games/HealthyFood';
import InteractiveHouse from './games/InteractiveHouse';
import AnimalFamily from './games/AnimalFamily';
import NumberBilingual from './games/NumberBilingual';
import TimeAndCalendar from './games/TimeAndCalendar';
import DrawShapes from './games/DrawShapes';
import MoonPhases from './games/MoonPhases';
import Coloring from './games/Coloring';
import GuessSound from './games/GuessSound';
import SimpleMath from './games/SimpleMath';
import LetterAnimalMatch from './games/LetterAnimalMatch'; // +++ أضيف بناءً على طلبك +++
import WordBuilder from './games/WordBuilder'; // +++ أضيف بناءً على طلبك: لعبة تكوين الكلمات +++
import ParentalGate from './components/ParentalGate';
import Settings, { UserProfile } from './components/Settings';
import StickerBook from './components/StickerBook';
import TimeLockScreen from './components/TimeLockScreen';
import ParentsDashboard from './components/ParentsDashboard'; // +++ أضيف بناءً على طلبك +++
import WinCelebration from './components/WinCelebration'; // +++ أضيف بناءً على طلبك +++
import { Star } from 'lucide-react';

export default function App() {
  const [activeGame, setActiveGame] = useState<string | null>(null);
  const [stars, setStars] = useState(0);
  const [profile, setProfile] = useState<UserProfile>(() => {
    try {
      const saved = localStorage.getItem('childProfile');
      return saved ? JSON.parse(saved) : { name: '', dob: '', lockEnabled: false, difficulty: 'medium', stats: {} };
    } catch (e) {
      console.error('Error parsing profile from localStorage:', e);
      return { name: '', dob: '', lockEnabled: false, difficulty: 'medium', stats: {} };
    }
  });
  const [gateAction, setGateAction] = useState<(() => void) | null>(null);
  const [isTimeLocked, setIsTimeLocked] = useState(false);
  const [timeRemaining, setTimeRemaining] = useState<number | null>(null);
  const [showExitGate, setShowExitGate] = useState(false);
  const [showCelebration, setShowCelebration] = useState(false); // +++ أضيف بناءً على طلبك +++
  const [celebrationStars, setCelebrationStars] = useState(1); // +++ أضيف بناءً على طلبك +++

  // Refs for synchronous access in popstate handler
  const activeGameRef = useRef(activeGame);
  useEffect(() => { activeGameRef.current = activeGame; }, [activeGame]);

  const gateActionRef = useRef(gateAction);
  useEffect(() => { gateActionRef.current = gateAction; }, [gateAction]);

  const showExitGateRef = useRef(showExitGate);
  useEffect(() => { showExitGateRef.current = showExitGate; }, [showExitGate]);

  const isExitingRef = useRef(false);

  useEffect(() => {
    window.history.replaceState({ app: true }, '');
    window.history.pushState({ app: true }, '');

    const handlePopState = (event: PopStateEvent) => {
      if (isExitingRef.current) return;

      window.history.pushState({ app: true }, '');

      if (gateActionRef.current) {
        setGateAction(null);
      } else if (showExitGateRef.current) {
        setShowExitGate(false);
      } else if (activeGameRef.current !== null) {
        setActiveGame(null);
      } else {
        setShowExitGate(true);
      }
    };

    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, []);

  useEffect(() => {
    localStorage.setItem('childProfile', JSON.stringify(profile));
    
    if (profile.playTimeLimit && profile.playTimeLimit > 0) {
      setTimeRemaining(profile.playTimeLimit * 60);
      setIsTimeLocked(false);
    } else {
      setTimeRemaining(null);
      setIsTimeLocked(false);
    }
  }, [profile]);

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

  // +++ أضيف بناءً على طلبك: نظام النجوم والإحصائيات +++
  const handleWin = (earnedStars: number = 1) => {
    setStars((s) => s + earnedStars);
    setCelebrationStars(earnedStars); // +++ أضيف بناءً على طلبك +++
    setShowCelebration(true); // +++ أضيف بناءً على طلبك +++
    if (activeGame && activeGame !== 'settings' && activeGame !== 'dashboard' && activeGame !== 'stickers') {
      setProfile(prev => {
        const currentStats = prev.stats || {};
        const gameStats = currentStats[activeGame] || { played: 0, stars: 0 };
        return {
          ...prev,
          stats: {
            ...currentStats,
            [activeGame]: {
              played: gameStats.played + 1,
              stars: gameStats.stars + earnedStars
            }
          }
        };
      });
    }
  };

  const handleBack = () => {
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

  const handleUnlockTime = () => {
    setIsTimeLocked(false);
    setTimeRemaining(null); 
    setActiveGame('settings');
  };

  return (
    <div className="w-full h-screen overflow-hidden bg-sky-50 font-sans touch-none select-none relative">
      {/* 1. Overlays (Time Lock & Parental Gates) - uppermost layer (z-50 / z-100) */}
      {isTimeLocked && <TimeLockScreen onUnlock={handleUnlockTime} />}
      {gateAction && <ParentalGate onSuccess={() => { gateAction(); setGateAction(null); }} onClose={() => setGateAction(null)} />}
      {showExitGate && <ParentalGate onSuccess={handleExitSuccess} onClose={() => setShowExitGate(false)} />}
      <WinCelebration isOpen={showCelebration} onClose={() => setShowCelebration(false)} earnedStars={celebrationStars} />
      
      {/* 2. Global Star Count Indicator (rendered above active games at z-40) */}
      {activeGame !== 'settings' && activeGame !== 'dashboard' && activeGame !== 'stickers' && activeGame !== null && (
        <div className="absolute top-6 right-6 z-40 bg-white/90 backdrop-blur-md px-5 py-2 rounded-full shadow-lg border-4 border-yellow-300 flex items-center gap-2 pointer-events-none">
          <Star className="text-yellow-500 fill-yellow-500" size={28} />
          <span className="text-2xl font-extrabold text-yellow-600">{stars}</span>
        </div>
      )}

      {/* 3. Base Home Screen Layer (z-10) */}
      {activeGame === null && (
        <div className="absolute inset-0 z-10 w-full h-full overflow-hidden bg-sky-50">
          <Home onSelect={handleSelectGame} profileName={profile.name} isBirthday={isBirthday()} avatar={profile.avatar} />
        </div>
      )}

      {/* 4. Settings/Parent Pages Layer (z-20) */}
      {activeGame === 'settings' && (
        <div className="absolute inset-0 z-20 w-full h-full overflow-hidden bg-sky-50">
          <Settings profile={profile} onSave={setProfile} onBack={handleBack} onOpenDashboard={() => setActiveGame('dashboard')} />
        </div>
      )}
      {activeGame === 'dashboard' && (
        <div className="absolute inset-0 z-20 w-full h-full overflow-hidden bg-sky-50">
          <ParentsDashboard profile={profile} onBack={handleBack} />
        </div>
      )}
      {activeGame === 'stickers' && (
        <div className="absolute inset-0 z-20 w-full h-full overflow-hidden bg-sky-50">
          <StickerBook stars={stars} onBack={handleBack} />
        </div>
      )}
      
      {/* 5. Games Layer (z-30) - Guaranteed to render above everything except system modal gates */}
      {activeGame && activeGame !== 'settings' && activeGame !== 'dashboard' && activeGame !== 'stickers' && (
        <div className="absolute inset-0 z-30 w-full h-full overflow-hidden bg-sky-100">
          {activeGame === 'balloon' && <BalloonPop onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'animal' && <AnimalFriends onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'draw' && <MagicGlowDraw onBack={handleBack} />}
          {activeGame === 'color' && <ColorCatch onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'sorter' && <ShapeSorter onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'piano' && <ColorPiano onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'fish' && <CatchFish onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'jigsaw' && <JigsawPuzzle onBack={handleBack} onWin={() => handleWin(2)} age={age} />}
          {activeGame === 'memory' && <MemoryMatch onBack={handleBack} onWin={() => handleWin(2)} age={age} />}
          {activeGame === 'arabic' && <ArabicLetters onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'english' && <EnglishLetters onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'nature' && <NatureExplorer onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'counting' && <CountingGame onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'lettermatch' && <LetterMatch onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'healthyfood' && <HealthyFood onBack={handleBack} onWin={(stars) => handleWin(stars || 1)} difficulty={profile.difficulty} />}
          {activeGame === 'house' && <InteractiveHouse onBack={handleBack} onWin={() => handleWin(2)} />}
          {activeGame === 'animalfamily' && <AnimalFamily onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'numbers' && <NumberBilingual onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'time' && <TimeAndCalendar onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'drawshapes' && <DrawShapes onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'moon' && <MoonPhases onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'coloring' && <Coloring onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'guesssound' && <GuessSound onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'simplemath' && <SimpleMath onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'tashkeel' && <ArabicTashkeel onBack={handleBack} onWin={() => handleWin(1)} />}
          {activeGame === 'letteranimal' && <LetterAnimalMatch onBack={handleBack} onWin={handleWin} difficulty={profile.difficulty} />}
          {activeGame === 'wordbuilder' && <WordBuilder onBack={handleBack} onWin={handleWin} />}
        </div>
      )}
    </div>
  );
}
