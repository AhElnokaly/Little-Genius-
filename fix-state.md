## fix-state: Little Genius Enhancements
| # | المهمة | Status | QA Gate | Notes |
|---|--------|--------|---------|-------|
| 1 | Create PuzzleGameScreen with 10 levels | done | PASS | 10 levels (2x2 and 3x3) with tile swap and celebration |
| 2 | Create MazeGameScreen with 10 levels | done | PASS | 10 levels with pathfinding, D-Pad controls and goals |
| 3 | Add LayoutDirection (RTL for Arabic, LTR for English) | done | PASS | CompositionLocalProvider wrapping on all screens |
| 4 | Add multi-level progression across existing games | done | PASS | BalloonPop, Counting, SimpleMath, ShapeSorter, WordBuilder, EnglishLetters, ArabicLetters |
| 5 | Update HomeScreen and MainActivity with new games & navigation | done | PASS | New games added to categories and routed |
| 6 | Verification with compile_applet | done | PASS | Build succeeded completely with Android Gradle toolchain |
| 7 | Fix audio system (SoundManager & AudioTrack) & complete routing | done | PASS | Pure PCM audio engine and full screen routing |
| 8 | Fix CatchFishScreen swimming physics & correct head direction | done | PASS | LTR aquarium coordinates, swimming coroutine, scaleX direction correction, ambient bubbles & splash |
| 9 | Enhance BalloonPopScreen floating physics & pop sparks | done | PASS | LTR physics container, sinusoidal sway, pop particles and sound echo |
| 10 | Fix CI/CD AAPT PNG resource compilation & add Git LFS sanitization | done | PASS | LFS checkout support and automatic image resource validation in GitHub Actions workflow |
| 11 | Upgrade ShapeSorterScreen with silhouette slot, snap bounce & shake feedback | done | PASS | Silhouette slot, snap celebration scale, spring bounce, shake animation on wrong choice |
| 12 | Upgrade ColorPianoScreen with 3D tactile keys & floating music notes | done | PASS | 3D physical key displacement, floating note particles, bouncing guide pointer, auto-release |
| 13 | Upgrade MazeGameScreen with breadcrumb footsteps & smart BFS hint | done | PASS | Footstep trails (🐾), BFS smart hint lightbulb (💡), physical LTR D-pad for accurate orientation |
| 14 | Integrate real animal audio recordings & upgrade GuessSoundScreen | done | PASS | 21 authentic natural animal sound recordings (44.1kHz PCM mono), 4 progressive game levels, pulsing sound waves visualizer, card bounce animations |
| 15 | Fix Turkey gobble acoustics & complete audio overhaul for 21 animals | done | PASS | Realistic dual-burst turkey gobble with guttural throat flutter, fixed inaudible monkey/mouse audio, added turkey to GuessSoundScreen, auto-release selection |
| 16 | Implement Unified Jolly Phonics & Arabic Makharij Hub with multi-category examples | done | PASS | Single-page all-letters grids, 7-group Jolly Phonics progression with action hints, Arabic articulation with short vowels (Tashkeel), expandable bottom sheet with categorized examples (Animals, Fruits, Vegetables, Objects) and instant TTS |
| 17 | Implement Phonics Blending Train Game (قطار دمج الأصوات) | done | PASS | Interactive train with moving wheels & sway, 3-letter carriages, letter-by-letter phonetic vocalization and automated word blending with celebration |
| 18 | Implement Phonics Sound Hunt Listening Quiz (صائد الأصوات) | done | PASS | Audio listening quiz with pulsing speaker, Arabic & English phonics questions, 3 options with immediate feedback and celebratory progression |
| 19 | Implement Guided Letter Tracing Game (سبورة تتبع الحروف المضيئة) | done | PASS | Night chalkboard canvas with neon glowing trails, pulsing star starting point, dot-guided stroke paths, and fireworks celebration on completion |
| 20 | Upgrade Parents Dashboard with Phonics Progress & fix launcher icon mipmaps | done | PASS | Dedicated Jolly Phonics mastery card, updated game names in stats, and regenerated all clean mipmap PNG icons fixing AAPT release builds |
