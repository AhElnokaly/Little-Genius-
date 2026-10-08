# دليل تصدير وتحديث تطبيق أندرويد عبر GitHub Actions 🚀

يوفر هذا الدليل شرحاً تفصيلياً لكيفية استخدام وتخصيص الـ Workflow لتصدير ملفات **APK** قابلة للتحديث التراكمي (**Updateable APK**) على هواتف المستخدمين دون الحاجة لحذف التطبيق أو فقدان بيانات ونجوم الأطفال.

---

## 🔑 شروط أندرويد لقبول تثبيت أي تحديث (In-Place Update)

لكي يقبل نظام أندرويد تثبيت ملف APK فوق إصدار سابق دون ظهور رسالة الخطأ الشهيرة `App not installed as package appears to be invalid`، يجب توافر 3 شروط أساسية تم ضبطها بالكامل:

1. **تطابق اسم الحزمة (Package ID / Application ID):**
   - تم تثبيته في `app/build.gradle.kts`:
     ```kotlin
     applicationId = "com.aistudio.littlegenius.kgvxp"
     ```
   - يظل ثابتاً دائماً ولا يتغير.

2. **تصاعد رقم البناء الداخلي (`versionCode`):**
   - أندرويد يرفض التحديث إذا كان الـ `versionCode` الجديد أقل من أو مساوياً للقديم في بعض الأجهزة.
   - يقوم الـ Workflow بحساب `versionCode` تصاعدياً بشكل تلقائي ومضمون بالاعتماد على `${{ github.run_number }}` (كل بناء جديد يحصل على رقم أكبر تلقائياً: 1 ثم 2 ثم 3 وهكذا).
   - يمكنك أيضاً كتابة رقم مخصص يدوياً عند تشغيل الـ Workflow.

3. **تطابق مفتاح التوقيع الرقمي (Keystore Signature):**
   - هوية التطبيق المشفرة. يجب توقيع التحديث بنفس المفتاح الذي وُقع به الإصدار الأول.
   - يدعم الـ Workflow مفاتيح التوقيع عبر **GitHub Secrets** بمخططات التوقيع الحديثة (`v1 + v2 + v3`) المتوافقة مع أندرويد 7 حتى أندرويد 15+.
   - في حال عدم إدخال الـ Secrets، يقوم الـ Workflow بتوليد مفتاح موحد وتصديره كـ Artifact ليظل معك مدى الحياة.

---

## 🛠️ طرق تشغيل الـ Workflow

ملف الـ Workflow موجود في المسار:
`.github/workflows/build-apk.yml`

### 1. التشغيل اليدوي (Manual Dispatch) - الأسهل:
1. اذهب لمستودعك على GitHub واضغط على تبويب **Actions**.
2. اختر من القائمة الجانبية: **`Build & Export Updateable Android APK`**.
3. اضغط على زر **Run workflow** الأزرق.
4. يمكنك تحديد:
   - **Version Name:** مثل `1.0.1` أو `1.1.0`.
   - **Version Code:** اتركه فارغاً ليزيد تلقائياً.
   - **Release Notes:** كتابة ما تم تغييره وتحديثه.
   - **Create GitHub Release:** وضع علامة صح لنشره في صفحة الـ Releases.
5. اضغط **Run workflow**.

### 2. التصدير التلقائي مع كل Release Tag:
عند الرغبة في إطلاق إصدار رسمي، ما عليك سوى عمل Tag في Git:
```bash
git tag v1.0.1
git push origin v1.0.1
```
سيقوم الـ Workflow بالعمل تلقائياً وبناء الـ APK ونشره في صفحة **Releases** مع إرفاق ملف الـ APK الموقّع مباشرة للتحميل.

---

## 🔐 إعداد مفتاح التوقيع الدائم (GitHub Secrets) - خطوة بخطوة

لضمان أن كل التحديثات المستقبلية تستخدم نفس المفتاح المشفر بنسبة 100%:

### الخطوة 1: توليد المفتاح
يمكنك استخدام السكربت الجاهز المرفق في المشروع:
```bash
./scripts/generate-keystore.sh
```
أو عبر سطر الأوامر مباشرة:
```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias littlegenius \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass "YourPassword123" \
  -keypass "YourPassword123" \
  -dname "CN=LittleGenius, OU=App, O=AIStudio, C=EG"
```

ثم تحويله إلى صيغة Base64:
- على Linux / macOS:
  ```bash
  base64 -w 0 release.keystore > keystore_base64.txt
  ```
- على Windows PowerShell:
  ```powershell
  [Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) | Out-File -Encoding utf8 keystore_base64.txt
  ```

### الخطوة 2: إضافة الـ Secrets إلى GitHub
1. افتح مستودعك على GitHub واضغط على **Settings** (الإعدادات).
2. من القائمة اليسرى اختر **Secrets and variables** ثم اضغط **Actions**.
3. اضغط على **New repository secret** وأضف الآتي:

| اسم الـ Secret | القيمة (Value) |
|---|---|
| `KEYSTORE_BASE64` | محتوى ملف `keystore_base64.txt` كاملاً |
| `KEYSTORE_PASSWORD` | كلمة مرور الـ Keystore (مثلاً `YourPassword123`) |
| `KEY_ALIAS` | اسم الألياس (مثلاً `littlegenius`) |
| `KEY_PASSWORD` | كلمة مرور المفتاح (نفسها أو المحددة) |

---

## 📲 كيفية تحميل وتثبيت الـ APK على الهاتف

1. بعد انتهاء الـ Workflow بنجاح، اضغط على التشغيل (Run).
2. في أسفل الصفحة ستجد قسم **Artifacts**:
   - ستجد ملفاً باسم: `LittleGenius-v1.0.X-buildY-APK`.
   - اضغط عليه للتحميل كملف ZIP وفك الضغط لتحصل على ملف الـ APK المباشر.
3. أو من صفحة **Releases** إذا اخترت نشر إصدار (تجد ملف الـ `.apk` جاهزاً مباشرة للتحميل بضغطة واحدة).
4. أرسل الملف إلى هاتفك (عبر واتساب أو تليجرام أو Google Drive أو كابل USB).
5. افتح الملف واضغط **تحديث (Update)**:
   - سيتم تثبيت الإصدار الجديد فوراً فوق الإصدار القديم.
   - ستظل جميع نجوم الطفل، الأوسمة، إعدادات الآباء، ومراحل الألعاب محفوظة كما هي تماماً!
