package com.example.littlegenius.data

enum class PhonicsCategory(val titleAr: String, val titleEn: String, val emoji: String) {
    ALL("الكل", "All", "✨"),
    ANIMAL("حيوانات", "Animals", "🦁"),
    FRUIT("فواكه", "Fruits", "🍎"),
    VEGETABLE("خضار", "Vegetables", "🥦"),
    OBJECT("أشياء", "Objects", "🚗")
}

data class CategorizedExample(
    val category: PhonicsCategory,
    val wordEn: String,
    val wordAr: String,
    val emoji: String
)

data class JollyPhonicsLetter(
    val letter: String,
    val lowerLetter: String,
    val groupNumber: Int,
    val groupNameAr: String,
    val groupNameEn: String,
    val phonicSoundText: String,
    val actionHintAr: String,
    val actionHintEn: String,
    val examples: List<CategorizedExample>
)

data class ArabicLetterPhonics(
    val letter: String,
    val name: String,
    val makhrajAr: String,
    val fatHa: String,
    val damma: String,
    val kasra: String,
    val sukun: String,
    val examples: List<CategorizedExample>
)

object PhonicsRepository {

    val JOLLY_PHONICS_LETTERS = listOf(
        // === GROUP 1: s, a, t, i, p, n ===
        JollyPhonicsLetter(
            "S", "s", 1, "المجموعة 1", "Group 1", "/s/ sssss",
            "حرك يدك كالثعبان وانطق: سسسس", "Weave your hand like a snake and say sssss",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Snake", "ثعبان", "🐍"),
                CategorizedExample(PhonicsCategory.FRUIT, "Strawberry", "فراولة", "🍓"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Spinach", "سبانخ", "🥬"),
                CategorizedExample(PhonicsCategory.OBJECT, "Sun", "شمس", "☀️")
            )
        ),
        JollyPhonicsLetter(
            "A", "a", 1, "المجموعة 1", "Group 1", "/æ/ a-a-a",
            "المس ذراعك كأن نملة تسير عليه وقل: أ-أ-أ", "Wiggle fingers on arm like ants: a-a-ants",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Alligator", "تمساح", "🐊"),
                CategorizedExample(PhonicsCategory.FRUIT, "Apple", "تفاحة", "🍎"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Artichoke", "خرشوف", "🥑"),
                CategorizedExample(PhonicsCategory.OBJECT, "Airplane", "طائرة", "✈️")
            )
        ),
        JollyPhonicsLetter(
            "T", "t", 1, "المجموعة 1", "Group 1", "/t/ t-t-t",
            "حرك رأسك يميناً ويساراً كأنك تشاهد كرة التنس وقل: ت-ت-ت", "Turn your head side to side watching tennis: t-t-t",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Tiger", "نمر", "🐯"),
                CategorizedExample(PhonicsCategory.FRUIT, "Tangerine", "يوسفي", "🍊"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Tomato", "طماطم", "🍅"),
                CategorizedExample(PhonicsCategory.OBJECT, "Train", "قطار", "🚂")
            )
        ),
        JollyPhonicsLetter(
            "I", "i", 1, "المجموعة 1", "Group 1", "/ɪ/ i-i-i",
            "المس أنفك كفأر مبلل بالحبر وقل: إِ-إِ-إِ", "Pretend to be a mouse wiping ink on your nose: i-i-ink",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Iguana", "إغوانا", "🦎"),
                CategorizedExample(PhonicsCategory.FRUIT, "Ice cream", "مثلجات", "🍦"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Iceberg lettuce", "خس كابوتشا", "🥬"),
                CategorizedExample(PhonicsCategory.OBJECT, "Island", "جزيرة", "🏝️")
            )
        ),
        JollyPhonicsLetter(
            "P", "p", 1, "المجموعة 1", "Group 1", "/p/ p-p-p",
            "انفخ إصبعك كأنك تطفئ شمعة عيد الميلاد وقل: ب-ب-ب", "Pretend to puff out birthday candles: p-p-p",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Panda", "باندا", "🐼"),
                CategorizedExample(PhonicsCategory.FRUIT, "Peach", "خوخ", "🍑"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Potato", "بطاطس", "🥔"),
                CategorizedExample(PhonicsCategory.OBJECT, "Pencil", "قلم رصاص", "✏️")
            )
        ),
        JollyPhonicsLetter(
            "N", "n", 1, "المجموعة 1", "Group 1", "/n/ nnnnn",
            "افرد ذراعيك كطائرة تحلق في السماء وقل: ننننن", "Hold out arms like an aeroplane: nnnnn",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Nightingale", "عندليب", "🐦"),
                CategorizedExample(PhonicsCategory.FRUIT, "Nectarine", "نكتارين", "🍑"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Nut", "مكسرات", "🥜"),
                CategorizedExample(PhonicsCategory.OBJECT, "Nest", "عش طائر", "🪺")
            )
        ),

        // === GROUP 2: c, k, e, h, r, m, d ===
        JollyPhonicsLetter(
            "C", "c", 2, "المجموعة 2", "Group 2", "/k/ k-k-k",
            "فرقع بأصابعك كآلات الإيقاع الإسبانية وقل: ك-ك-ك", "Click fingers like castanets: k-k-k",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Cat", "قطة", "🐱"),
                CategorizedExample(PhonicsCategory.FRUIT, "Cherry", "كرز", "🍒"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Carrot", "جزر", "🥕"),
                CategorizedExample(PhonicsCategory.OBJECT, "Car", "سيارة", "🚗")
            )
        ),
        JollyPhonicsLetter(
            "K", "k", 2, "المجموعة 2", "Group 2", "/k/ k-k-k",
            "اقفز كطائرة ورقية تطير في الهواء وقل: ك-ك-ك", "Fly your hands like kites: k-k-k",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Koala", "كوالا", "🐨"),
                CategorizedExample(PhonicsCategory.FRUIT, "Kiwi", "كيوي", "🥝"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Kale", "كرنب أجعد", "🥬"),
                CategorizedExample(PhonicsCategory.OBJECT, "Kite", "طائرة ورقية", "🪁")
            )
        ),
        JollyPhonicsLetter(
            "E", "e", 2, "المجموعة 2", "Group 2", "/e/ e-e-e",
            "تظاهر بكسر بيضة بيديك على المقلاة وقل: إ-إ-إ", "Pretend to crack an egg on the pan: e-e-eggs",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Elephant", "فيل", "🐘"),
                CategorizedExample(PhonicsCategory.FRUIT, "Elderberry", "بيلسان", "🫐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Eggplant", "باذنجان", "🍆"),
                CategorizedExample(PhonicsCategory.OBJECT, "Envelope", "ظرف بريد", "✉️")
            )
        ),
        JollyPhonicsLetter(
            "H", "h", 2, "المجموعة 2", "Group 2", "/h/ h-h-h",
            "ضع يدك أمام فمك وتنفس كأنك تجري متعباً: هـ-هـ-هـ", "Hold hand to mouth panting as if exhausted: h-h-h",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Horse", "حصان", "🐴"),
                CategorizedExample(PhonicsCategory.FRUIT, "Honeydew melon", "شمام عسلي", "🍈"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Herb", "أعشاب طازجة", "🌿"),
                CategorizedExample(PhonicsCategory.OBJECT, "House", "منزل", "🏠")
            )
        ),
        JollyPhonicsLetter(
            "R", "r", 2, "المجموعة 2", "Group 2", "/r/ rrrrr",
            "هز رأسك ككلب يمسك بلعبة بحماس: ررررر", "Shake head like a puppy pulling a rag: rrrrr",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Rabbit", "أرنب", "🐰"),
                CategorizedExample(PhonicsCategory.FRUIT, "Raspberry", "توت العليق", "🫐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Radish", "فجل", "🥗"),
                CategorizedExample(PhonicsCategory.OBJECT, "Rocket", "صاروخ فضائي", "🚀")
            )
        ),
        JollyPhonicsLetter(
            "M", "m", 2, "المجموعة 2", "Group 2", "/m/ mmmmm",
            "امسح على بطنك كأنك أكلت طعاماً لذيذاً وقل: ممممم", "Rub your tummy thinking of yummy food: mmmmm",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Monkey", "قرد", "🐵"),
                CategorizedExample(PhonicsCategory.FRUIT, "Mango", "مانجو", "🥭"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Mushroom", "فطر / عيش الغراب", "🍄"),
                CategorizedExample(PhonicsCategory.OBJECT, "Moon", "قمر", "🌙")
            )
        ),
        JollyPhonicsLetter(
            "D", "d", 2, "المجموعة 2", "Group 2", "/d/ d-d-d",
            "اضرب بيديك في الهواء كأنك تعزف على الطبل: د-د-د", "Beat your hands as if playing drums: d-d-d",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Dog", "كلب", "🐶"),
                CategorizedExample(PhonicsCategory.FRUIT, "Dates", "تمر", "🌴"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Dill", "شبت", "🌿"),
                CategorizedExample(PhonicsCategory.OBJECT, "Drum", "طبلة", "🥁")
            )
        ),

        // === GROUP 3: g, o, u, l, f, b ===
        JollyPhonicsLetter(
            "G", "g", 3, "المجموعة 3", "Group 3", "/g/ g-g-g",
            "دوّر يدك كأن ماء ينساب في البالوعة وقل: ج-ج-ج", "Spiral hand down like water gurgling: g-g-g",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Giraffe", "زرافة", "🦒"),
                CategorizedExample(PhonicsCategory.FRUIT, "Grapes", "عنب", "🍇"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Garlic", "ثوم", "🧄"),
                CategorizedExample(PhonicsCategory.OBJECT, "Guitar", "غيتار", "🎸")
            )
        ),
        JollyPhonicsLetter(
            "O", "o", 3, "المجموعة 3", "Group 3", "/ɒ/ o-o-o",
            "تظاهر بالضغط على مفتاح النور لتشغيله وإطفائه: أُو-أُو-أُو", "Flick a light switch on and off: o-o-on, o-o-off",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Owl", "بومة", "🦉"),
                CategorizedExample(PhonicsCategory.FRUIT, "Orange", "برتقال", "🍊"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Onion", "بصل", "🧅"),
                CategorizedExample(PhonicsCategory.OBJECT, "Oven", "فرن", "🍲")
            )
        ),
        JollyPhonicsLetter(
            "U", "u", 3, "المجموعة 3", "Group 3", "/ʌ/ u-u-u",
            "تظاهر بفتح مظلة المطر فوق رأسك وقل: أ-أ-أمبريلا", "Pretend to open an umbrella: u-u-umbrella",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Urchin", "قنفذ البحر", "🐡"),
                CategorizedExample(PhonicsCategory.FRUIT, "Ugli fruit", "فاكهة أوغلي الحمضية", "🍊"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Ube", "يام أرجواني", "🍠"),
                CategorizedExample(PhonicsCategory.OBJECT, "Umbrella", "مظلة", "☂️")
            )
        ),
        JollyPhonicsLetter(
            "L", "l", 3, "المجموعة 3", "Group 3", "/l/ l-l-l",
            "تظاهر بلعق مصاصة حلوى شهية وقل: ل-ل-ل", "Pretend to lick a lollipop: l-l-lollipop",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Lion", "أسد", "🦁"),
                CategorizedExample(PhonicsCategory.FRUIT, "Lemon", "ليمون", "🍋"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Lettuce", "خس", "🥬"),
                CategorizedExample(PhonicsCategory.OBJECT, "Lamp", "مصباح", "💡")
            )
        ),
        JollyPhonicsLetter(
            "F", "f", 3, "المجموعة 3", "Group 3", "/f/ fffff",
            "ضم يديك معاً كأنك تفرغ هواء سمكة منتفخة: ففففف", "Let hands float like a deflating fish: fffff",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Fish", "سمكة", "🐟"),
                CategorizedExample(PhonicsCategory.FRUIT, "Fig", "تين", "🫐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Fennel", "شمر", "🥗"),
                CategorizedExample(PhonicsCategory.OBJECT, "Fan", "مروحة", "🪭")
            )
        ),
        JollyPhonicsLetter(
            "B", "b", 3, "المجموعة 3", "Group 3", "/b/ b-b-b",
            "تظاهر بضرب الكرة بمضرب البيسبول وقل: ب-ب-ب", "Swing a bat hitting a ball: b-b-ball",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Bear", "دب", "🐻"),
                CategorizedExample(PhonicsCategory.FRUIT, "Banana", "موز", "🍌"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Broccoli", "بروكلي", "🥦"),
                CategorizedExample(PhonicsCategory.OBJECT, "Ball", "كرة", "⚽")
            )
        ),

        // === GROUP 4: j, z, w, v ===
        JollyPhonicsLetter(
            "J", "j", 4, "المجموعة 4", "Group 4", "/dʒ/ j-j-j",
            "اهتز كطبق من حلوى الجيلي اللذيذة وقل: ج-ج-جيلي", "Wobble your body like jelly on a plate: j-j-jelly",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Jellyfish", "قنديل البحر", "🪼"),
                CategorizedExample(PhonicsCategory.FRUIT, "Jackfruit", "كاكايا / جاك فروت", "🍈"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Jalapeno", "فلفل هلابينو", "🌶️"),
                CategorizedExample(PhonicsCategory.OBJECT, "Juice", "عصير", "🧃")
            )
        ),
        JollyPhonicsLetter(
            "Z", "z", 4, "المجموعة 4", "Group 4", "/z/ zzzzz",
            "افرد يديك كأجنحة نحلة تطير وتزن: ززززز", "Flap your arms buzzing like a bee: zzzzz",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Zebra", "حمار وحشي", "🦓"),
                CategorizedExample(PhonicsCategory.FRUIT, "Zucchini fruit", "كوسا حلوة", "🥒"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Zucchini", "كوسة", "🥒"),
                CategorizedExample(PhonicsCategory.OBJECT, "Zipper", "سحاب الملابس", "🤐")
            )
        ),
        JollyPhonicsLetter(
            "W", "w", 4, "المجموعة 4", "Group 4", "/w/ w-w-w",
            "انفخ بقوة على راحة يدك كنسيم الرياح: و-و-و", "Blow on open hands like wind: w-w-wind",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Wolf", "ذئب", "🐺"),
                CategorizedExample(PhonicsCategory.FRUIT, "Watermelon", "بطيخ", "🍉"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Watercress", "جرجير", "🥬"),
                CategorizedExample(PhonicsCategory.OBJECT, "Watch", "ساعة يد", "⌚")
            )
        ),
        JollyPhonicsLetter(
            "V", "v", 4, "المجموعة 4", "Group 4", "/v/ vvvvv",
            "امسك مقود سيارة فان وتظاهر بالقيادة: ففففف", "Drive a steering wheel like a van: vvvvv",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Vulture", "نسر نفاع", "🦅"),
                CategorizedExample(PhonicsCategory.FRUIT, "Vanilla bean", "قرن فانيليا", "🍦"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Vegetable soup", "شوربة خضار", "🍲"),
                CategorizedExample(PhonicsCategory.OBJECT, "Van", "شاحنة صغيرة", "🚐")
            )
        ),

        // === GROUP 5: y, x, q ===
        JollyPhonicsLetter(
            "Y", "y", 5, "المجموعة 5", "Group 5", "/j/ y-y-y",
            "تظاهر بتناول زبادي لذيذ بالملعقة وقل: ي-ي-ي", "Pretend to eat yummy yogurt: y-y-yogurt",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Yak", "ثور التبت / ياق", "🐂"),
                CategorizedExample(PhonicsCategory.FRUIT, "Yellow pear", "كمثرى صفراء", "🍐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Yam", "بطاطا حلوة", "🍠"),
                CategorizedExample(PhonicsCategory.OBJECT, "Yo-yo", "يويو لعبة", "🪀")
            )
        ),
        JollyPhonicsLetter(
            "X", "x", 5, "المجموعة 5", "Group 5", "/ks/ ks-ks",
            "تظاهر بالتقاط صورة أشعة سينية بالكاميرا: كس-كس", "Take an x-ray picture with a flash: ks-ks",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "X-ray tetra fish", "سمكة شفافة", "🐟"),
                CategorizedExample(PhonicsCategory.FRUIT, "Xigua watermelon", "بطيخ أصفر صيني", "🍉"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Xanthium root", "نبات عشبي مفيد", "🌿"),
                CategorizedExample(PhonicsCategory.OBJECT, "Xylophone", "إكسيليفون موسيقي", "🎼")
            )
        ),
        JollyPhonicsLetter(
            "Q", "q", 5, "المجموعة 5", "Group 5", "/kw/ qu-qu",
            "ضم يديك كمنقار بطة تصيح: كواك-كواك", "Make a duck's beak with hands: qu-qu-quack",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Quail", "طائر السمان", "🐦"),
                CategorizedExample(PhonicsCategory.FRUIT, "Quince", "سفرجل", "🍐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Quinoa greens", "أوراق الكينوا", "🥗"),
                CategorizedExample(PhonicsCategory.OBJECT, "Queen crown", "تاج الملكة", "👑")
            )
        )
    )

    val ARABIC_LETTERS_DATA = listOf(
        ArabicLetterPhonics(
            "أ", "أَلِف", "مخرجه من أقصى الحلق (أقرب للصدر)",
            "أَ (أسد)", "أُ (أذن)", "إِ (إبريق)", "أْ (فأس)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Lion", "أَسَد", "🦁"),
                CategorizedExample(PhonicsCategory.FRUIT, "Pineapple", "أَنَانَاس", "🍍"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Artichoke", "أَرْضِي شَوْكِي", "🥑"),
                CategorizedExample(PhonicsCategory.OBJECT, "Couch", "أَرِيكَة", "🛋️")
            )
        ),
        ArabicLetterPhonics(
            "ب", "بَاء", "مخرجه من بين الشفتين بانطباقهما",
            "بَ (بطة)", "بُ (برتقال)", "بِ (بطيخ)", "بْ (حبل)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Duck", "بَطَّة", "🦆"),
                CategorizedExample(PhonicsCategory.FRUIT, "Orange", "بُرْتُقَال", "🍊"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Eggplant", "بَاذِنْجَان", "🍆"),
                CategorizedExample(PhonicsCategory.OBJECT, "Door", "بَاب", "🚪")
            )
        ),
        ArabicLetterPhonics(
            "ت", "تَاء", "مخرجه من طرف اللسان مع أصول الثنايا العليا",
            "تَ (تفاح)", "تُ (توت)", "تِ (تمساح)", "تْ (مكتب)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Crocodile", "تِمْسَاح", "🐊"),
                CategorizedExample(PhonicsCategory.FRUIT, "Apple", "تُفَّاح", "🍎"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Garlic herb", "تُوم / ثُوم", "🧄"),
                CategorizedExample(PhonicsCategory.OBJECT, "Crown", "تَاج", "👑")
            )
        ),
        ArabicLetterPhonics(
            "ث", "ثَاء", "مخرجه من طرف اللسان مع أطراف الثنايا العليا (لثوي)",
            "ثَ (ثعلب)", "ثُ (ثعبان)", "ثِ (ثمار)", "ثْ (عثمان)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Fox", "ثَعْلَب", "🦊"),
                CategorizedExample(PhonicsCategory.FRUIT, "Fig fruit", "ثَمَرَة التِّين", "🫐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Garlic", "ثُوم", "🧄"),
                CategorizedExample(PhonicsCategory.OBJECT, "Dress", "ثَوْب", "👗")
            )
        ),
        ArabicLetterPhonics(
            "ج", "جِيم", "مخرجه من وسط اللسان مع ما يقابله من الحنك الأعلى",
            "جَ (جمل)", "جُ (جبن)", "جِ (جدار)", "جْ (نجم)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Camel", "جَمَل", "🐪"),
                CategorizedExample(PhonicsCategory.FRUIT, "Guava", "جَوَافَة", "🍈"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Carrot", "جَزَر", "🥕"),
                CategorizedExample(PhonicsCategory.OBJECT, "Bell", "جَرَس", "🔔")
            )
        ),
        ArabicLetterPhonics(
            "ح", "حَاء", "مخرجه من وسط الحلق بنقاء ورقة",
            "حَ (حمامة)", "حُ (حوت)", "حِ (حصان)", "حْ (بحر)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Horse", "حِصَان", "🐴"),
                CategorizedExample(PhonicsCategory.FRUIT, "Watermelon", "حَبْحَب (بِطِّيخ)", "🍉"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Chickpeas", "حِمَّص", "🧆"),
                CategorizedExample(PhonicsCategory.OBJECT, "Rope", "حَبْل", "🪢")
            )
        ),
        ArabicLetterPhonics(
            "خ", "خَاء", "مخرجه من أدنى الحلق مع تفخيم الصوت",
            "خَ (خروف)", "خُ (خبز)", "خِ (خيار)", "خْ (نخل)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Sheep", "خَرُوف", "🐑"),
                CategorizedExample(PhonicsCategory.FRUIT, "Peach", "خَوْخ", "🍑"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Cucumber", "خِيَار", "🥒"),
                CategorizedExample(PhonicsCategory.OBJECT, "Ring", "خَاتَم", "💍")
            )
        ),
        ArabicLetterPhonics(
            "د", "دَال", "مخرجه من طرف اللسان مع أصول الثنايا العليا",
            "دَ (دراجة)", "دُ (دب)", "دِ (ديك)", "دْ (بدر)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Bear", "دُبّ", "🐻"),
                CategorizedExample(PhonicsCategory.FRUIT, "Dates", "دَوْم / تَمْر", "🌴"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Pumpkin", "دُبَّاء (قَرْع)", "🎃"),
                CategorizedExample(PhonicsCategory.OBJECT, "Bicycle", "دَرَّاجَة", "🚲")
            )
        ),
        ArabicLetterPhonics(
            "ذ", "ذَال", "مخرجه من طرف اللسان مع أطراف الثنايا العليا بلطف",
            "ذَ (ذهب)", "ذُ (ذرة)", "ذِ (ذئب)", "ذْ (جذر)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Wolf", "ذِئْب", "🐺"),
                CategorizedExample(PhonicsCategory.FRUIT, "Sweet Corn", "ذُرَة حُلْوَة", "🌽"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Corn", "ذُرَة صَفْرَاء", "🌽"),
                CategorizedExample(PhonicsCategory.OBJECT, "Gold", "ذَهَب", "🪙")
            )
        ),
        ArabicLetterPhonics(
            "ر", "رَاء", "مخرجه من طرف اللسان مائلاً إلى ظهره مع تكرير خفيف",
            "رَ (رمان)", "رُ (رمح)", "رِ (ريشة)", "رْ (برج)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Raccoon", "رَاكُون", "🦝"),
                CategorizedExample(PhonicsCategory.FRUIT, "Pomegranate", "رُمَّان", "🫐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Arugula", "رُوكَا (جِرْجِير)", "🥗"),
                CategorizedExample(PhonicsCategory.OBJECT, "Feather", "رِيشَة", "🪶")
            )
        ),
        ArabicLetterPhonics(
            "ز", "زَاي", "مخرجه من طرف اللسان مع ما فوق الثنايا السفلى مع صفير",
            "زَ (زرافة)", "زُ (زهور)", "زِ (زر)", "زْ (مزمار)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Giraffe", "زَرَافَة", "🦒"),
                CategorizedExample(PhonicsCategory.FRUIT, "Olive", "زَيْتُون", "🫒"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Ginger", "زَنْجَبِيل", "🫚"),
                CategorizedExample(PhonicsCategory.OBJECT, "Flower", "زَهْرَة", "🌸")
            )
        ),
        ArabicLetterPhonics(
            "س", "سِين", "مخرجه من طرف اللسان مع الثنايا السفلى بصوت صفيري نقي",
            "سَ (سمكة)", "سُ (سلحفاة)", "سِ (ساعة)", "سْ (مسبح)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Fish", "سَمَكَة", "🐟"),
                CategorizedExample(PhonicsCategory.FRUIT, "Sugar Apple", "سَفَرْجَل", "🍐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Spinach", "سَبَانِخ", "🥬"),
                CategorizedExample(PhonicsCategory.OBJECT, "Watch", "سَاعَة", "⏰")
            )
        ),
        ArabicLetterPhonics(
            "ش", "شِين", "مخرجه من وسط اللسان مع تفشي وانتشار الهواء",
            "شَ (شمس)", "شُ (شباك)", "شِ (شبل)", "شْ (مشط)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Lion cub", "شِبْل", "🦁"),
                CategorizedExample(PhonicsCategory.FRUIT, "Cantaloupe", "شَمَّام", "🍈"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Dill", "شَبَت", "🌿"),
                CategorizedExample(PhonicsCategory.OBJECT, "Sun", "شَمْس", "☀️")
            )
        ),
        ArabicLetterPhonics(
            "ص", "صَاد", "مخرجه من طرف اللسان مفخماً مطبقاً مع صفير قوي",
            "صَ (صقر)", "صُ (صندوق)", "صِ (صاروخ)", "صْ (عصفور)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Falcon", "صَقْر", "🦅"),
                CategorizedExample(PhonicsCategory.FRUIT, "Cactus Fruit", "صَبَّار (تِين شَوْكِي)", "🌵"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Soybean", "صُويَا", "🫘"),
                CategorizedExample(PhonicsCategory.OBJECT, "Box", "صُنْدُوق", "📦")
            )
        ),
        ArabicLetterPhonics(
            "ض", "ضَاد", "مخرجه من إحدى حافتي اللسان مع الأضراس العليا (حرف الضاد الفريد)",
            "ضَ (ضفدع)", "ضُ (ضوء)", "ضِ (ضرس)", "ضْ (مضرب)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Frog", "ضِفْدَع", "🐸"),
                CategorizedExample(PhonicsCategory.FRUIT, "Tangerine segment", "ضَرِيبَة (فَصّ بُرْتُقَال)", "🍊"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Greens", "ضُمَّة جِرْجِير", "🥬"),
                CategorizedExample(PhonicsCategory.OBJECT, "Flashlight", "ضَوْء", "🔦")
            )
        ),
        ArabicLetterPhonics(
            "ط", "طَاء", "مخرجه من طرف اللسان مع أصول الثنايا العليا بقوة واستعلاء",
            "طَ (طائرة)", "طُ (طيور)", "طِ (طفل)", "طْ (مطر)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Peacock", "طَاوُوس", "🦚"),
                CategorizedExample(PhonicsCategory.FRUIT, "Fresh fruit", "طَازَج (تِين)", "🫐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Tomato", "طَمَاطِم", "🍅"),
                CategorizedExample(PhonicsCategory.OBJECT, "Airplane", "طَائِرَة", "✈️")
            )
        ),
        ArabicLetterPhonics(
            "ظ", "ظَاء", "مخرجه من طرف اللسان مع أطراف الثنايا العليا مفخماً",
            "ظَ (ظبي)", "ظُ (ظفر)", "ظِ (ظل)", "ظْ (عظم)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Deer", "ظَبْي", "🦌"),
                CategorizedExample(PhonicsCategory.FRUIT, "Tamarind pod", "ظِلّ شَجَرَة فَاكِهَة", "🌳"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Fresh greens", "ظَاهِر النَّبَات", "🌿"),
                CategorizedExample(PhonicsCategory.OBJECT, "Envelope", "ظَرْف", "✉️")
            )
        ),
        ArabicLetterPhonics(
            "ع", "عَيْن", "مخرجه من وسط الحلق صوت عميق ونقي",
            "عَ (عصفور)", "عُ (عنب)", "عِ (عش)", "عْ (كعبة)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Bird", "عُصْفُور", "🐦"),
                CategorizedExample(PhonicsCategory.FRUIT, "Grapes", "عِنَب", "🍇"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Lentils", "عَدَس", "🍲"),
                CategorizedExample(PhonicsCategory.OBJECT, "Flag", "عَلَم", "🚩")
            )
        ),
        ArabicLetterPhonics(
            "غ", "غَيْن", "مخرجه من أدنى الحلق مفخماً رخواً",
            "غَ (غزال)", "غُ (غراب)", "غِ (غابة)", "غْ (مغناطيس)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Gazelle", "غَزَال", "🦌"),
                CategorizedExample(PhonicsCategory.FRUIT, "Forest berries", "غَابَات التُّوت", "🫐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Beetroot", "غِذَاء صِحِّي (شَمَنْدَر)", "🥗"),
                CategorizedExample(PhonicsCategory.OBJECT, "Cloud", "غَيْمَة", "☁️")
            )
        ),
        ArabicLetterPhonics(
            "ف", "فَاء", "مخرجه من باطن الشفة السفلى مع أطراف الثنايا العليا",
            "فَ (فراشة)", "فُ (فستق)", "فِ (فيل)", "فْ (مفتاح)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Elephant", "فِيل", "🐘"),
                CategorizedExample(PhonicsCategory.FRUIT, "Strawberry", "فَرَاوْلَة", "🍓"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Pepper", "فُلْفُل", "🫑"),
                CategorizedExample(PhonicsCategory.OBJECT, "Key", "مِفْتَاح", "🔑")
            )
        ),
        ArabicLetterPhonics(
            "ق", "قَاف", "مخرجه من أقصى اللسان مع الحنك الأعلى اللحمي بقوة",
            "قَ (قطة)", "قُ (قفل)", "قِ (قرد)", "قْ (قلم)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Cat", "قِطَّة", "🐱"),
                CategorizedExample(PhonicsCategory.FRUIT, "Cane sugar", "قَصَب السُّكَّر", "🎋"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Pumpkin", "قَرْع عَسَلِي", "🎃"),
                CategorizedExample(PhonicsCategory.OBJECT, "Pen", "قَلَم", "🖊️")
            )
        ),
        ArabicLetterPhonics(
            "ك", "كَاف", "مخرجه من أقصى اللسان تحت القاف قليلاً مهموساً",
            "كَ (كلب)", "كُ (كرة)", "كِ (كتاب)", "كْ (سكرة)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Dog", "كَلْب", "🐶"),
                CategorizedExample(PhonicsCategory.FRUIT, "Cherry", "كَرَز", "🍒"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Cabbage", "كَرْنَب", "🥬"),
                CategorizedExample(PhonicsCategory.OBJECT, "Book", "كِتَاب", "📖")
            )
        ),
        ArabicLetterPhonics(
            "ل", "لاَم", "مخرجه من أدنى حافة اللسان إلى منتهاها مع لثة الأسنان العليا",
            "لَ (ليمون)", "لُ (لعبة)", "لِ (لسان)", "لْ (قلب)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Llama", "لاَمَا", "🦙"),
                CategorizedExample(PhonicsCategory.FRUIT, "Lemon", "لَيْمُون", "🍋"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Turnip", "لِفْت", "🥗"),
                CategorizedExample(PhonicsCategory.OBJECT, "Toy", "لُعْبَة", "🧸")
            )
        ),
        ArabicLetterPhonics(
            "م", "مِيم", "مخرجه من الشفتين مع غنة تخرج من الخيشوم",
            "مَ (موز)", "مُ (مهرج)", "مِ (مقص)", "مْ (شمس)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Monkey", "قِرْد (مَيْمُون)", "🐵"),
                CategorizedExample(PhonicsCategory.FRUIT, "Banana", "مَوْز", "🍌"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Okra", "مُلُوخِيَّة / بَامِيَا", "🍲"),
                CategorizedExample(PhonicsCategory.OBJECT, "Scissors", "مِقَصّ", "✂️")
            )
        ),
        ArabicLetterPhonics(
            "ن", "نُون", "مخرجه من طرف اللسان مع لثة الأسنان العليا مع غنة",
            "نَ (نحلة)", "نُ (نجوم)", "نِ (نسر)", "نْ (عين)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Bee", "نَحْلَة", "🐝"),
                CategorizedExample(PhonicsCategory.FRUIT, "Palm dates", "نَخِيل البَلَح", "🌴"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Mint", "نَعْنَاع", "🌿"),
                CategorizedExample(PhonicsCategory.OBJECT, "Star", "نَجْمَة", "⭐")
            )
        ),
        ArabicLetterPhonics(
            "هـ", "هَاء", "مخرجه من أقصى الحلق رقيقاً سلساً",
            "هَـ (هاتف)", "هُـ (هدية)", "هِـ (هلال)", "هْـ (فهد)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Cheetah", "فَهْد", "🐆"),
                CategorizedExample(PhonicsCategory.FRUIT, "Indian melon", "هِنْدِي (تَمْر هِنْدِي)", "🫐"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Asparagus", "هِلْيُون", "🥦"),
                CategorizedExample(PhonicsCategory.OBJECT, "Gift", "هَدِيَّة", "🎁")
            )
        ),
        ArabicLetterPhonics(
            "و", "وَاو", "مخرجه بانضمام الشفتين مع استدارة",
            "وَ (وردة)", "وُ (ورود)", "وِ (وسادة)", "وْ (ضوء)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Wild Boar", "وَحْش الغَابَة", "🐗"),
                CategorizedExample(PhonicsCategory.FRUIT, "Grape leaf", "وَرَق عِنَب", "🍇"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Watercress greens", "وَرَقِيَّات طَازَجَة", "🥬"),
                CategorizedExample(PhonicsCategory.OBJECT, "Rose", "وَرْدَة", "🌹")
            )
        ),
        ArabicLetterPhonics(
            "ي", "يَاء", "مخرجه من وسط اللسان مع الحنك الأعلى",
            "يَ (يد)", "يُ (يمامة)", "يِ (ينابيع)", "يْ (بيت)",
            listOf(
                CategorizedExample(PhonicsCategory.ANIMAL, "Dove", "يَمَامَة", "🕊️"),
                CategorizedExample(PhonicsCategory.FRUIT, "Tangerine", "يُوسُفِي", "🍊"),
                CategorizedExample(PhonicsCategory.VEGETABLE, "Yam", "يَام (بَطَاطَا)", "🍠"),
                CategorizedExample(PhonicsCategory.OBJECT, "Hand", "يَد", "✋")
            )
        )
    )
}
