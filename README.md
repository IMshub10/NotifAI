# 📱 NotifAI

**NotifAI** is an Android app that classifies incoming SMS messages into meaningful categories such as Transactions, Promotions, OTPs, and Scams using a lightweight on-device DistilBERT-based ML model. It offers smart notifications, real-time inbox updates, and fraud detection—all running entirely offline.

---

## 🚀 Features

- 📩 Default SMS app with full SMS message handling
- 🧠 Offline ML model (DistilBERT) for classification
- 🏷️ Categories: OTP, Transactional, Promotional, Personal, Scam, and more
- 🔔 Smart notifications based on category importance
- 🧪 Rule-based overrides and user-customizable categories
- 🛡️ Scam detection with manual reporting option

---

## 🛠️ Tech Stack

- LiveData & Kotlin Flow
- Room Database
- Paging (manual + Paging library)
- Navigation Component
- RecyclerView + ViewBinding
- ViewModel + Repository Architecture

---

## 📷 Screenshots

> 📌 **TODO:** Add sample UI screenshots or screen recordings here

---

## 🤖 Model & Classification

- Trained on over **50,000 SMS records**, covering **4,500+ unique templates**
- Achieved **~95–97% test accuracy** across 46 distinct SMS types
- Core categories include:
  - `IMPORTANT`
  - `TRANSACTION`
  - `ALERT`
  - `PROMOTIONAL`
  - `OTP`
  - `SCAM`

---

## 🔐 Privacy & Security

- Messages are never uploaded or synced without **explicit user permission**
- Only anonymized data is used for optional model improvements (opt-in)

---

## 🧑‍💻 How to Build

1. Clone this repository
2. Add your Firebase and model configuration if applicable
3. Open the project in **Android Studio** (API 28+ recommended)
4. Run the app and set it as the **default SMS app**

---

## 🤝 Contributing

Pull requests are welcome! If you'd like to contribute to classification logic, UI/UX improvements, or feature enhancements, feel free to fork the repo or open an issue.
