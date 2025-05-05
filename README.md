# 🚆 RailDesk - JavaFX Railway Reservation System

**RailDesk** is a sleek and functional desktop-based Railway Reservation System built using JavaFX and MySQL. It allows users to sign up, search trains, book tickets with multiple travelers, and view only their own booking history — all from an elegant and responsive desktop interface.

---

## 🧩 Features

✅ User Registration & Login  
✅ Search Trains by Route & Class  
✅ Add Multiple Travelers Dynamically  
✅ Real-Time Fare Calculation  
✅ "My Bookings" Section (User-specific)  
✅ Clean JavaFX UI with CSS Styling  
✅ Logout & Session Control  

---

## 🛠 Tech Stack

| Layer              | Technology                                 |
|-------------------|--------------------------------------------|
| 💻 **Language**     | Java (JDK 17+)                             |
| 🎨 **UI Framework** | JavaFX (FXML, CSS)                         |
| 🛢 **Database**     | MySQL + JDBC                               |
| 🔧 **IDE**          | IntelliJ IDEA                              |
| 📦 **Build Tool**   | Maven                                      |

---

## 🗄 Database Structure

- **users** – Stores login/signup credentials  
- **trains** – Train info (route, fare, class, time)  
- **bookings** – Booking data linked to users  
- **travellers** – Individual traveler details with `booking_id`  

Only the currently logged-in user's bookings are displayed in *MyBookings* for privacy and relevance.

---

## 🖼 UI Screenshots 

Experience the modern, intuitive design of RailDesk in action:

### 🔐 Login Page
![Login Page](src/screenshots/log%20in.png)

---

### 🧑‍💼 Account Creation
![Create Account](src/screenshots/create%20account.png)

---

### 🏠 Dashboard View
![Dashboard](src/screenshots/dashboard%20view.png)

---

### 🚆 Train Search by Route
![From Location](src/screenshots/Train%20from%20search.png)
![To Location](src/screenshots/Train%20to%20search.png)

---

### 🔍 Train Search by Date & Class
![Date Search](src/screenshots/Train%20Date%20search.png)
![Class Search](src/screenshots/Train%20class%20search.png)

---

### 📊 Dashboard Panel
![Dashboard Panel](src/screenshots/git%20dashboard.png)

---

### 📋 Train Search Result
![Train Result](src/screenshots/Git%20Train%20result.png)

---

### 🎫 Booking Interface
![Booking Interface](src/screenshots/git%20booking.png)

---
### 👥 Add Travelers
![Add Traveler](src/screenshots/Git%20traveller%20add.png)

---

### 💳 Booking Interface & Confirmation
![Booking Confirmation](src/screenshots/Git%20Booking%20confirm.png)
![Booking Details](src/screenshots/git%20booking%20details.png)

---

## ✅ Conclusion

**RailDesk – JavaFX Railway Reservation System** is a complete desktop-based solution that streamlines the ticket booking experience with a clean interface, secure user login, and real-time traveler management. Designed with modularity and scalability in mind, it bridges the gap between a user-friendly UI and robust backend integration using MySQL.

This project not only demonstrates core JavaFX skills but also reflects real-world application of MVC architecture, database design, and user-centric thinking. It is ideal for academic use, personal learning, or as a base for future expansion into web or mobile platforms.


> 🚀 Built with ❤️ using JavaFX, CSS, and MySQL



---
