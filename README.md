# 🌍 Travel Buddies - Android App

**Travel Buddies** is a sleek, minimal, dark-themed iOS-inspired Android application designed for travellers to easily select, manage, and call trusted emergency contacts from their device during a trip.

---

## ✨ Key Features

- **📱 iOS Dark Aesthetic**: Clean, modern dark mode interface inspired by iOS design principles, featuring OLED black backgrounds, rounded card containers, and custom pill-shaped search and tab navigation bars.
- **📂 Android Content Provider**: Integrates with the system Content Provider (`ContactsContract.CommonDataKinds.Phone`) to read contact details (names and phone numbers) directly from the device phonebook, with fallback demo contacts for easy testing.
- **🔍 Real-Time Contact Search**: A dedicated search input box with instant query filtering by contact name or phone number.
- **☑️ Emergency Contact Picker**: Mark and unmark trusted contacts as "Travel Buddies" using checkboxes. Selected contacts automatically appear in the Emergency Contacts tab.
- **📞 Direct Phone Calling & Permission Handling**: Each emergency contact features an iOS-styled green call `ImageButton`. Clicking it requests `CALL_PHONE` runtime permission and initiates a phone call, or shows a clear Toast message if permission is denied.
- **🔘 Bottom Navigation View Switching**: Clean iOS-style bottom tab bar allowing users to switch seamlessly between the **Emergency Contacts** view and the **All Contacts** picker view.

---

## 📸 Screenshots

*(Add screenshots of your app here)*

### 1. Emergency Contacts View
![Emergency Contacts Screen](<img width="720" height="1600" alt="WhatsApp Image 2026-11-05 at 11 26 59 AM" src="https://github.com/user-attachments/assets/0ad2e075-6d41-4026-b2e6-9efcd69a19ee" />

)

### 2. All Contacts Picker & Search View
![Contacts Picker Screen](<img width="720" height="1600" alt="WhatsApp Image 2026-11-05 at 11 26 59 AM (2)" src="https://github.com/user-attachments/assets/e89375f8-408d-4376-9553-45a49fc3c6d9" />
<img width="720" height="1600" alt="WhatsApp Image 2026-11-05 at 11 26 58 AM" src="https://github.com/user-attachments/assets/67be345b-af29-4912-ab5a-989b98de16b9" />

)
<img width="720" height="1600" alt="WhatsApp Image 2026-11-05 at 11 26 59 AM (1)" src="https://github.com/user-attachments/assets/8b916c86-0891-4fdf-8332-ddca623ee061" />

---

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Components**: ListView, BaseAdapter, ConstraintLayout, NestedScrollView, Custom Drawable Shapes & Vectors
- **Android APIs**: Content Provider (`ContactsContract`), Runtime Permissions (`READ_CONTACTS`, `CALL_PHONE`), Intent Actions (`ACTION_CALL`)
- **Design System**: Material 3 Dark / Minimal iOS Theme

---

## 🚀 Getting Started

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/travel-buddies-android.git
   ```
2. **Open in Android Studio**:
   - Open Android Studio and select **Open an Existing Project**.
   - Navigate to the cloned `travel-buddies-android` folder.
3. **Run the App**:
   - Sync Gradle and run the app on an Android emulator or physical device (API 24+).

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
