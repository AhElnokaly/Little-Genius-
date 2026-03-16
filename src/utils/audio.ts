// +++ أضيف بناءً على طلبك: تحسين النطق باللهجة المصرية وإصلاح تأخر الصوت +++
export const playSound = (url: string) => {
  const audio = new Audio(url);
  audio.play().catch(e => console.error("Audio play error:", e));
};

// Shared audio context for synthesized sounds (like piano/balloons) to prevent limits
let sharedAudioContext: AudioContext | null = null;

export const getAudioContext = () => {
  if (!sharedAudioContext) {
    sharedAudioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
  }
  if (sharedAudioContext.state === 'suspended') {
    sharedAudioContext.resume();
  }
  return sharedAudioContext;
};

export const speak = (text: string, lang: string = 'ar-EG'): Promise<void> => {
  return new Promise((resolve) => {
    if (!window.speechSynthesis) {
      resolve();
      return;
    }
    
    // Cancel any ongoing speech to prevent delays
    window.speechSynthesis.cancel();

    const utterance = new SpeechSynthesisUtterance(text);
    
    // Try to find an Egyptian voice, fallback to any Arabic voice
    const voices = window.speechSynthesis.getVoices();
    let voice = voices.find(v => v.lang === 'ar-EG');
    
    if (!voice && lang.startsWith('ar')) {
      voice = voices.find(v => v.lang.startsWith('ar'));
    } else if (!voice && !lang.startsWith('ar')) {
      voice = voices.find(v => v.lang === lang || v.lang.startsWith(lang.split('-')[0]));
    }
    
    if (voice) {
      utterance.voice = voice;
    }
    
    utterance.lang = lang;
    utterance.rate = 0.9; // slightly slower for kids
    
    utterance.onend = () => resolve();
    utterance.onerror = () => resolve();
    
    window.speechSynthesis.speak(utterance);
    
    // Fallback resolve in case onend doesn't fire
    setTimeout(resolve, 3000);
  });
};
