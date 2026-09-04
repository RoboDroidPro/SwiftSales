# Swift Sales

**Swift Sales** is a modern, offline-first Android application designed for independent sellers and small businesses to transition from paper-based sale records to a streamlined digital workflow. 

Built with **Jetpack Compose** and **Material 3**, the app provides a professional, highly responsive interface for managing product catalogs and tracking complex sales transactions.

## 🚀 Features

- **Product Management**: Maintain a complete catalog of products with custom pricing, stock status, and ordering.
- **Dynamic Sale Tracking**: Create sale records with multiple items, automatic price fetching, and support for individual item quantities.
- **Data Integrity**: Built-in safeguards prevent the deletion of products that are referenced in existing sale records, ensuring historical accuracy.
- **Batch Operations**: Streamlined multi-selection for bulk deletion of sale records.
- **Offline-First**: Powered by a local Room database, ensuring all data is available instantly without requiring an internet connection.

## 🛠 Tech Stack

- **UI**: Jetpack Compose with Material 3 components.
- **Architecture**: MVVM (Model-View-ViewModel) with a focus on MVI-style state management.
- **Dependency Injection**: Hilt for robust, testable code.
- **Database**: Room (SQLite) with Foreign Key constraints and automated migrations.
- **Asynchrony**: Kotlin Coroutines and Flow for reactive UI updates.
- **Navigation**: Navigation Compose for seamless screen transitions.

## 📱 Usage Procedure

1.  **Define Your Catalog**: Navigate to the menu icon > **Settings > Add Product** to build your inventory. Products saved here will automatically populate your sales dropdowns.
2.  **Record a Sale**: Tap the **"+" button** in the bottom right. Enter the date and buyer details, then add as many sale items as necessary. Click the checkmark button to save.
3.  **Manage Records**: 
    - **Edit**: Tap any sale to modify items or details and click the save checkmark.
    - **Delete**: Long-press sale entries to select multiple records for deletion, then click the trash icon in the top bar.
4.  **Data Safety**: To delete a product, visit Settings and select the product. If a product is linked to a sale record, the app will prevent deletion to preserve your financial history.

## 🏗️ Project Structure
The project follows clean architecture principles:
- `data/`: Room entities, DAOs, and Repositories.
- `ui/`: Compose screens, reusable components, and theme definitions.
- `viewModel/`: Logic for state handling and database interaction.
- `utils/`: Converters (e.g., currency formatting) and helpers.

---

**Swift Sales** is an open-source tool intended to provide a useful, professional-grade solution for mobile sales tracking. Simple, reliable, and professional.  

