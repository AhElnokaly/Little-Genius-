/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState, useEffect } from 'react';
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
import EnglishLetters from './games/EnglishLetters'; // +++ أضيف بناءً على طلبك +++
import NatureExplorer from './games/NatureExplorer'; // +++ أضيف بناءً على طلبك +++
import CountingGame from './games/CountingGame'; // +++ أضيف بناءً على طلبك +++
import LetterMatch from './games/LetterMatch'; // +++ أضيف بناءً على طلبك +++
import HealthyFood from './games/HealthyFood'; // +++ أضيف بناءً على طلبك +++
import InteractiveHouse from './games/InteractiveHouse'; // +++ أضيف بناءً على طلبك +++
import AnimalFamily from './games/AnimalFamily'; // +++ أضيف بناءً على طلبك +++
import NumberBilingual from './games/NumberBilingual'; // +++ أضيف بناءً على طلبك +++
import ParentalGate from './components/ParentalGate';
import Settings, { UserProfile } from './components/Settings';
import StickerBook from './components/StickerBook';
import { Star } from 'lucide-react';

export default function App() {
  const [activeGame, setActiveGame] = useState<string | null>(null);
  const [stars, setStars] = useState(0); // +++ أضيف بناءً على طلبك: تصفير النجوم عند فتح التطبيق +++
  const [profile, setProfile] = useState<UserProfile>(() => {
    const saved = localStorage.getItem('childProfile');
    return saved ? JSON.parse(saved) : { name: '', dob: '', lockEnabled: false };
  });
  const [gateAction, setGateAction] = useState<(() => void) | null>(null);

  useEffect(() => {
    localStorage.setItem('childProfile', JSON.stringify(profile));
  }, [profile]);

  const handleWin = () => setStars((s) => s + 1);

  const handleBack = () => {
    setActiveGame(null);
  };

  const handleSelectGame = (id: string) => {
    if (id === 'settings') {
      if (profile.lockEnabled) {
        setGateAction(() => () => setActiveGame('settings'));
      } else {
        setActiveGame('settings');
      }
    } else {
      setActiveGame(id);
    }
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

  return (
    <div className="w-full h-screen overflow-hidden bg-sky-50 font-sans touch-none select-none relative">
      {gateAction && <ParentalGate onSuccess={() => { gateAction(); setGateAction(null); }} onClose={() => setGateAction(null)} />}
      
      {/* Stars UI - hide in settings/stickers */}
      {activeGame !== 'settings' && activeGame !== 'stickers' && activeGame !== null && (
        <div className="absolute top-6 right-6 z-40 bg-white/90 backdrop-blur-md px-5 py-2 rounded-full shadow-lg border-4 border-yellow-300 flex items-center gap-2 pointer-events-none">
          <Star className="text-yellow-500 fill-yellow-500" size={28} />
          <span className="text-2xl font-extrabold text-yellow-600">{stars}</span>
        </div>
      )}

      {activeGame === null && <Home onSelect={handleSelectGame} profileName={profile.name} isBirthday={isBirthday()} />}
      {activeGame === 'settings' && <Settings profile={profile} onSave={setProfile} onBack={() => setActiveGame(null)} />}
      {activeGame === 'stickers' && <StickerBook stars={stars} onBack={() => setActiveGame(null)} />}
      
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
    </div>
  );
}
