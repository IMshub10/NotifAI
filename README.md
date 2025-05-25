# 📱 NotifAI

**NotifAI** is an Android app that classifies incoming SMS messages into meaningful categories such as Transactions, Promotions, OTPs, and Scams using a lightweight on-device DistilBERT-based ML model. It offers smart notifications, real-time inbox updates, and fraud detection—all running entirely offline.

---

## 🚀 Features

- 📩 Default SMS app with full SMS message handling
- 🧠 Offline ML model (MobileBERT) for classification
- 🏷️ Categories: OTP, Transactional, Promotional, Personal, Scam, and more
- 🔔 Smart notifications based on category importance
- 🧪 Rule-based overrides and user-customizable categories
- 🛡️ Scam detection with manual reporting option
- 🔐 Privacy-first toggle: Choose whether messages are stored in Android’s public SMS database or kept privately within the app

---

## 🛠️ Tech Stack

- LiveData & Kotlin Flow
- Room Database
- Paging (Maual + Paging library)
- Navigation Component
- RecyclerView + ViewBinding
- ViewModel + Repository Architecture

---

## 📷 Screenshots

 > ![image](https://github.com/user-attachments/assets/d905cba5-0d8f-4ff8-861a-1875fc2107e9)

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

🧠 Message Importance Scoring

Each SMS message is automatically assigned an importance score (1–5) based on its classification. This score is used to prioritize notifications and help users filter messages effectively.
| Score | Category         | Examples                                                              |
|-------|------------------|-----------------------------------------------------------------------|
| **5** | 🔴 Critical       | OTPs, Banking Transactions, Emergency Alerts                         |
| **4** | 🟠 Important      | Bill/Due Reminders, Reward Usage, Invoices                           |
| **3** | 🟡 General Updates| Delivery Updates, Service Notifications, Telecom, Tax Notifications  |
| **2** | 🟢 Less Important | Offers, Promotions, Social Awareness                                 |
| **1** | ⚪ Least Important| Spam, Scam, Advertising                                              |

These scores drive:
	•	Notification importance levels
	•	Auto-filtering in conversations
	•	Category-wise message breakdowns

Users can override scores via rules if needed.

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
