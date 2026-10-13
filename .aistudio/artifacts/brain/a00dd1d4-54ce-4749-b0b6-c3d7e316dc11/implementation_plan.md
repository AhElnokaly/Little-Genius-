# Unified Phonics & Arabic Letters Learning Hub (Jolly Phonics & مخارج الحروف)

A comprehensive, interactive single-page learning environment for early learners (ages 3–6) teaching English reading via the proven **Jolly Phonics** method (synthetic phonics with actions, sounds, and 7-group sequencing) alongside authentic **Arabic Letter Sounds & Pronunciation** (مخارج الحروف مع الحركات القصيرة والأمثلة المصنفة: خضار، فاكهة، حيوانات، وأشياء).

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following core UX decisions were confirmed by the user in Phase 1:
> - **English Ordering:** Jolly Phonics 7-group pedagogical order (`s, a, t, i, p, n`, `c/k, e, h, r, m, d`, `g, o, u, l, f, b`, etc.) rather than dry alphabetical A-Z.
> - **Detail Presentation:** Interactive Expandable Bottom Sheet displaying category tabs (Animals 🦁, Fruits 🍎, Vegetables 🥦, Objects 🚗) with individual pronunciation and phonetic sound playback.
> - **Unified Page Layout:** A top segmented language switch allowing seamless single-tap toggling between English Jolly Phonics and Arabic Letter Sounds while keeping all letters accessible on a single scrollable grid without pagination barriers.

---

## 1. Overview & Core Concept

### What It Does
- **English Phonics Mode (Jolly Phonics):**
  - Displays all 26+ letters organized into the standard **7 Jolly Phonics sound groups**.
  - Highlights the **Phonic Sound** (e.g. `/s/` like a hissing snake) rather than just the letter name ("Ess").
  - Includes the signature **Jolly Phonics Action / Story cue** (e.g., weave hand like a snake).
- **Arabic Phonics Mode (أصوات الحروف العربية ومخارجها):**
  - Displays all 28 Arabic letters in a vibrant grid.
  - Teaches the root phonetic articulation (صوت الحرف) plus the 3 short vowels (الحركات القصيرة: الفتحة، الضمة، الكسرة) with audio for each.
- **Rich Multi-Category Examples (أمثلة متعددة ومصنفة):**
  - Tapping any letter opens an **Expandable Modal Sheet** showcasing multiple real-world examples:
    - 🦁 **حيوان (Animal)**
    - 🍎 **فاكهة (Fruit)**
    - 🥦 **خضار (Vegetable)**
    - 🚗 **شيء / وسيلة (Object / Vehicle)**
  - Each example features a large emoji/icon, Arabic and English labels with transliteration, and a sound button speaking the word naturally.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Entry & Overview:**
   - Child enters the **Letters & Phonics Hub** (`PhonicsHubScreen.kt`).
   - Top Header provides back navigation, star count, and a toggle between **🇬🇧 Jolly Phonics** and **🇸🇦 الحروف العربية**.
   - Below the toggle, an **All-in-One Board** presents all letters organized by colorful group headers (e.g. Group 1: `s, a, t, i, p, n`).
2. **Letter Exploration (Single Tap):**
   - Tapping any letter card plays its immediate phonetic sound (e.g., crisp `/s/` or `/بَ/`) with a bouncy 3D spring press effect.
   - An interactive detail bottom sheet slides up smoothly.
3. **Multi-Example Exploration:**
   - Inside the sheet, the child sees:
     - Prominent upper & lower case letter with action hint (e.g., "Weave hand like a snake: sssss").
     - Vowel / Sound buttons (for Arabic: أَ، أُ، إِ / for English: short sound & action sound).
     - Category pills: **الكل (All)** | **حيوانات (Animals)** | **فواكه (Fruits)** | **خضروات (Vegetables)** | **أشياء (Objects)**.
     - Tapping any item speaks its name using Android TextToSpeech in natural child-friendly voice.

### Visual Identity & Theme
- **Background Atmosphere:** Warm buttery background (`Color(0xFFFFFBEB)`) with playful subtle dot matrix and vibrant pastel card accents.
- **Group Color Coding (Jolly Phonics):**
  - Group 1 (`s, a, t, i, p, n`): Sunshine Amber (`Color(0xFFFEF08A)`)
  - Group 2 (`c, k, e, h, r, m, d`): Sky Azure (`Color(0xFFBAE6FD)`)
  - Group 3 (`g, o, u, l, f, b`): Mint Emerald (`Color(0xFFA7F3D0)`)
  - Group 4 (`j, z, w, v`): Soft Lilac (`Color(0xFFDDD6FE)`)
  - Group 5 (`y, x, q`): Coral Peach (`Color(0xFFFED7AA)`)
- **Arabic Section Palette:** Soft sage and turquoise pastels (`Color(0xFFD1FAE5)`) with emerald callouts.
- **Accessibility & Touch Targets:** All letter cards maintain minimum 64.dp touch bounds for toddler finger accuracy.

---

## 3. Technical Architecture & Component Hierarchy

```
┌─────────────────────────────────────────────────────────────┐
│                      MainActivity.kt                        │
│                Routes to: "phonics_hub"                     │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  PhonicsHubScreen.kt                        │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │  TopBar: Title, Stars, Audio Feedback                   │ │
│ ├─────────────────────────────────────────────────────────┤ │
│ │  Language Mode Switch: [Jolly Phonics 🇬🇧] [العربية 🇸🇦]  │ │
│ ├─────────────────────────────────────────────────────────┤ │
│ │  Scrollable Single-Page Board (All Letters in One View) │ │
│ │  - Group 1 to 7 Headers (English) / Alphabetical (Ar)   │ │
│ │  - 3D Responsive LetterCard Grid (Letter + Sound + Icon)│ │
│ └────────────────────────────┬────────────────────────────┘ │
│                              │ Selected Letter              │
│                              ▼                              │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │          LetterDetailBottomSheet.kt                     │ │
│ │  - Phonic Audio Player & Action Description             │ │
│ │  - Arabic Tashkeel Pronouncer (َ ُ ِ ْ)                 │ │
│ │  - Multi-Category Example Cards:                        │ │
│ │    [🦁 Animal] [🍎 Fruit] [🥦 Vegetable] [🚗 Object]   │ │
│ │  - Tap-to-Speak TTS Integration (Ar & En)               │ │
│ └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│              SoundManager.kt (Audio Engine)                 │
│  - playRawSound(resId) for crisp PCM sound effects          │
│  - speak(text, "en") / speak(text, "ar") for speech         │
│  - Phonic synthetic sounds for core phonemes                │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. Data Models & Comprehensive Content Library

### English Jolly Phonics Data Structure:
```kotlin
data class PhonicsExample(
    val category: ExampleCategory, // ANIMAL, FRUIT, VEGETABLE, OBJECT
    val word: String,
    val arabicTranslation: String,
    val emoji: String
)

data class JollyPhonicsLetter(
    val letter: String,
    val groupNumber: Int,
    val phonicSoundText: String, // e.g. "/s/ as in snake"
    val actionHint: String,       // e.g. "Weave hand like a snake: sssss"
    val examples: List<PhonicsExample>
)
```

**Full 7-Group Jolly Phonics Dataset:**
- **Group 1:**
  - `S`: 🐍 Snake (Animal), 🍓 Strawberry (Fruit), 🥬 Spinach (Vegetable), ☀️ Sun (Object)
  - `A`: 🐊 Alligator (Animal), 🍎 Apple (Fruit), 🥑 Artichoke (Vegetable), ✈️ Airplane (Object)
  - `T`: 🐯 Tiger (Animal), 🍊 Tangerine (Fruit), 🍅 Tomato (Vegetable), 🚂 Train (Object)
  - `I`: 🦎 Iguana (Animal), 🍦 Ice cream (Fruit/Treat), 🫚 Iceberg lettuce (Vegetable), 🏝️ Island (Object)
  - `P`: 🐼 Panda (Animal), 🍑 Peach (Fruit), 🥔 Potato (Vegetable), ✏️ Pencil (Object)
  - `N`: 🪺 Nightingale (Animal), 🥜 Nectarine (Fruit), 🌰 Nut (Vegetable/Food), 🪡 Needle (Object)
- **Groups 2–7:**
  - Comprehensive coverage for `c, k, e, h, r, m, d`, `g, o, u, l, f, b`, `j, z, w, v`, `y, x, q` with 3 to 4 categorized items per letter.

### Arabic Letter Pronunciation Data Structure:
```kotlin
data class ArabicLetterDetail(
    val letter: String,
    val name: String,
    val makhrajDescription: String, // وصف المخرج الصوتي: من الحلق / طرف اللسان / الشفتان
    val fatHa: String, // أَ
    val damma: String, // أُ
    val kasra: String, // إِ
    val sukun: String, // أْ
    val examples: List<PhonicsExample>
)
```

**Full 28 Arabic Letters Dataset:**
- Each letter with:
  - 🦁 **حيوان**: (أ: أسد، ب: بطة، ت: تمساح، ث: ثعلب، ج: جمل، ح: حصان، خ: خروف...)
  - 🍎 **فاكهة**: (أ: أناناس، ب: برتقال، ت: تفاح، ث: ثمرة التين، ج: جوافة، ح: حبحب/بطيخ، خ: خوخ...)
  - 🥦 **خضار**: (أ: أرز/أرضي شوكي، ب: باذنجان، ت: توم/ثوم، ث: ثوم، ج: جزر، ح: حمص، خ: خيار...)
  - 🚗 **شيء / جماد**: (أ: أريكة/طائرة، ب: باب/بيت، ت: تاج/تلفاز، ث: ثوب، ج: جرس...)

---

## 5. Implementation Sequence & Plan

1. **Step 1: Data Definitions (`PhonicsData.kt`):**
   - Create comprehensive categorized models for all 26 English Jolly Phonics letters (with group groupings, action descriptions, phonemes, and 4 categorized examples each).
   - Create complete Arabic phonetic data for all 28 letters (with Tashkeel short vowels, articulation cues, and 4 categorized examples each).
2. **Step 2: Interactive Hub Screen (`PhonicsHubScreen.kt`):**
   - Build unified top bar, language segment switcher (Jolly Phonics vs. الحروف العربية).
   - Single-page sticky group headers and 3D bounce letter grid.
3. **Step 3: Multi-Category Bottom Sheet (`LetterDetailBottomSheet.kt`):**
   - Modal bottom sheet with vowel player, action story, category filter chips (الكل، حيوانات، فواكه، خضار، أشياء).
   - Audio trigger for both letter phoneme and vocabulary speech using `SoundManager`.
4. **Step 4: Integration in Home Screen & Navigation:**
   - Link the new feature in `HomeScreen.kt` under the Language & Learning section and route in `MainActivity.kt`.
5. **Step 5: Compilation & Verification:**
   - Verify compilation using `compile_applet` and test resource integrity.
