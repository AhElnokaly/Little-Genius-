#!/usr/bin/env bash
# Script to generate a production Android Keystore and output its Base64 string for GitHub Secrets

set -e

KEYSTORE_PATH="release.keystore"
ALIAS="littlegenius"
VALIDITY_DAYS=10000

echo "======================================================="
echo "  مولد مفاتيح توقيع أندرويد (Android Keystore Generator) "
echo "======================================================="
echo ""

read -s -p "أدخل كلمة مرور الـ Keystore (Store Password): " KS_PASS
echo ""
read -s -p "أعد إدخال كلمة المرور للتأكيد: " KS_PASS_CONFIRM
echo ""

if [ "$KS_PASS" != "$KS_PASS_CONFIRM" ]; then
    echo "❌ كلمتا المرور غير متطابقتين!"
    exit 1
fi

if [ -z "$KS_PASS" ]; then
    echo "❌ لا يمكن ترك كلمة المرور فارغة!"
    exit 1
fi

echo "جاري إنشاء ملف الـ Keystore: $KEYSTORE_PATH ..."

keytool -genkeypair -v \
  -keystore "$KEYSTORE_PATH" \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity "$VALIDITY_DAYS" \
  -storepass "$KS_PASS" \
  -keypass "$KS_PASS" \
  -dname "CN=LittleGenius, OU=KidsApp, O=AIStudio, C=EG"

echo "✅ تم إنشاء المفتاح بنجاح!"
echo ""
echo "======================================================="
echo "  بيانات الإعداد في GitHub Repository Secrets:"
echo "======================================================="
echo "1. اذهب إلى: GitHub Repo -> Settings -> Secrets and variables -> Actions"
echo "2. أضف الـ Secrets التالية:"
echo ""
echo "   - اسم الـ Secret: KEYSTORE_PASSWORD"
echo "     القيمة: (نفس كلمة المرور التي أدخلتها)"
echo ""
echo "   - اسم الـ Secret: KEY_ALIAS"
echo "     القيمة: $ALIAS"
echo ""
echo "   - اسم الـ Secret: KEY_PASSWORD"
echo "     القيمة: (نفس كلمة المرور التي أدخلتها)"
echo ""
echo "   - اسم الـ Secret: KEYSTORE_BASE64"
echo "     القيمة: انسخ السطر التالي بالكامل:"
echo "-------------------------------------------------------"
base64 -w 0 "$KEYSTORE_PATH"
echo ""
echo "-------------------------------------------------------"
echo ""
echo "⚠️ تنبيه هام: احتفظ بملف $KEYSTORE_PATH في مكان آمن ولا تحذفه، فهو الهوية الرقمية لتطبيقك!"
