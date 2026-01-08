# LibreDiet

An open-source, privacy-focused meal tracking application for Android.

## Features

### User Interface
- **Intuitive Design**: Clean, modern Material Design 3 interface
- **Quick Meal Entry**: Streamlined input with minimal taps required
- **Dark Mode Support**: Follows system theme with dynamic colors on Android 12+

### Data Entry
- **Barcode Scanning**: Scan product barcodes using your camera for instant nutritional information
- **Voice Input**: Use voice commands to quickly log meals
- **Meal Templates**: Save frequently eaten meals for one-tap logging
- **Food Search**: Search from a comprehensive nutritional database

### Nutritional Database Integration
- **Open Food Facts Integration**: Access to millions of food products worldwide
- **Real-time Updates**: Nutritional information fetched in real-time
- **Custom Foods**: Add your own foods with custom nutritional values
- **Offline Support**: Previously viewed foods are cached locally

### Meal Tracking
- **Timestamps**: Automatic logging of meal times
- **Portion Sizes**: Flexible serving size and unit options
- **Meal Categories**: Breakfast, Lunch, Dinner, and Snack categorization
- **Daily Summary**: Visual overview of daily nutritional intake
- **Progress Tracking**: Track progress toward daily goals

### Export Functionality
- **CSV Export**: Compatible with spreadsheet applications
- **PDF Reports**: Print-ready meal reports
- **Date Range Selection**: Export data for custom time periods
- **Customizable Content**: Choose what data to include in exports

### Performance Optimization
- **Fast Loading**: Optimized for quick startup times
- **Low Memory Usage**: Efficient resource management
- **Battery Efficient**: Minimal background processing
- **Works on Various Devices**: Supports Android 8.0 (API 26) and above

### Data Privacy and Security
- **Local Storage**: All data stored locally on your device
- **No Account Required**: No sign-up or login needed
- **Optional Encryption**: Enable encryption for stored data
- **No Tracking**: No analytics or user tracking
- **Open Source**: Fully auditable codebase

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **Local Database**: Room
- **Networking**: Retrofit + OkHttp
- **Barcode Scanning**: ML Kit + CameraX
- **PDF Generation**: iText7

## Building

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Build Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/JeremyLakeyJr/librediet.git
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Build and run on an emulator or device

## Project Structure

```
app/
├── src/main/
│   ├── java/com/librediet/app/
│   │   ├── data/
│   │   │   ├── local/       # Room database and DAOs
│   │   │   ├── remote/      # API services
│   │   │   ├── repository/  # Data repositories
│   │   │   └── model/       # Data models
│   │   ├── di/              # Dependency injection modules
│   │   ├── ui/
│   │   │   ├── components/  # Reusable UI components
│   │   │   ├── navigation/  # Navigation setup
│   │   │   ├── screens/     # Screen composables and ViewModels
│   │   │   └── theme/       # App theming
│   │   └── util/            # Utility classes
│   └── res/                 # Android resources
└── build.gradle.kts         # App-level build configuration
```

## Contributing

Contributions are welcome! Please feel free to submit pull requests.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Privacy Policy

LibreDiet is designed with privacy in mind:
- All meal data is stored locally on your device
- No data is sent to external servers except for food lookups (Open Food Facts API)
- No user accounts or personal information required
- No analytics or tracking

## Acknowledgments

- [Open Food Facts](https://world.openfoodfacts.org/) for the nutritional database API
- Material Design team for the design guidelines
- All open-source libraries used in this project
