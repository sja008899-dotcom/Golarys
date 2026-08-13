# راهنمای ساخت و نصب APK روی گوشی اندروید

## ۱. پیش‌نیازها

- **Android Studio** را دانلود کنید: https://developer.android.com/studio
- یا استفاده کنید از **Gradle Command Line**

## ۲. ساخت APK

### گزینه الف: استفاده از Android Studio
1. پروژه را در Android Studio باز کنید
2. از منوی بالا: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
3. فایل APK در مسیر تولید می‌شود

### گزینه ب: استفاده از Gradle Command Line

#### برای Debug APK (آسان‌تر، برای تست):
```bash
./gradlew assembleDebug
```
فایل APK در این مسیر ایجاد می‌شود:
```
app/build/outputs/apk/debug/app-debug.apk
```

#### برای Release APK (برای انتشار):
```bash
./gradlew assembleRelease
```

## ۳. نصب روی گوشی

### روش الف: استفاده از اندروید استودیو
1. گوشی را از طریق USB به کامپیوتر وصل کنید
2. USB Debugging را روی گوشی فعال کنید
3. در Android Studio: `Run` → `Run app` یا `Shift + F10`

### روش ب: استفاده از ADB (Android Debug Bridge)

```bash
# ADB موجود نیست؟ ابتدا Android SDK Platform Tools را نصب کنید

# گوشی را وصل کنید و APK را نصب کنید:
adb install app/build/outputs/apk/debug/app-debug.apk
```

### روش ج: نصب دستی
1. فایل APK را به گوشی منتقل کنید
2. با فایل منیجر فایل را باز کنید
3. روی تایید کلیک کنید تا نصب شود

## ۴. تنظیمات متغیرهای محیطی (اختیاری)

اگر پروژه از Firebase یا API Keys استفاده می‌کند:
1. فایل `.env` را به صورت زیر تکمیل کنید:
   ```
   GOOGLE_API_KEY=your_api_key_here
   ```

## ۵. حل مشکلات رایج

### خطا: `Execution failed for task ':app:mergeDebugResources'`
- Gradle Cache را پاک کنید: `./gradlew clean`

### خطا: `minSdk and targetSdk`
- فایل `app/build.gradle.kts` را بررسی کنید (تنظیمات موجود است)

### گوشی توسط ADB شناخته نشده
```bash
adb kill-server
adb start-server
```

---

**ورژن فعلی:**
- Min SDK: 24 (Android 7.0)
- Target SDK: 36 (Android 15)
- Application ID: com.aistudio.golarys.flower
