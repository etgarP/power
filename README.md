Workout Planner App
An Android app designed with simplicity and functionality in mind, built using Kotlin and Room as the database solution. This app allows users to manage and track their workout plans seamlessly, with a clean and intuitive interface that adheres to Google’s Material Design guidelines.

Key Features
1. Home Screen
Before Choosing a Plan:
Users can start a quick workout, select from available plans, or choose one from a curated list based on their preferences.
After Choosing a Plan:
Displays progress within the current workout plan.
Easily navigate to the next workout session.
Splits workouts by week and highlights the current week’s tasks.
Automatically updates history once a workout plan is completed.
2. History Screen
Two tabs to track workout history and plan history.
Dive into individual workout details from the history section.
Plan history allows users to view completed plans, making it easy to track long-term progress.
3. Configure Screen
Organize and customize workouts, exercises, and plans across three tabs:
Workout Tab: Edit and create new workout routines, drag and drop to reorder exercises, and track metrics like sets, reps, weight, duration, and cardio distance.
Exercise Tab: Filter and manage exercises by category (Core, Arms, Back, etc.), and create new exercises that meet your fitness goals.
Plan Tab: Build and modify workout plans with flexibility for any fitness level.
4. Dynamic Color Theming
The app integrates Android's dynamic color theming based on the OS, ensuring that the UI adapts to the user's system preferences for a cohesive visual experience.
5. Fluid Navigation and Transitions
A bottom navigation bar allows seamless transitions between the Home, History, and Configure screens.
A NavHost is implemented for smooth screen transitions, following Android's best practices for user-friendly navigation.
Thoughtful animations are used throughout the app to make interactions feel fluid and engaging.
6. Custom Workout Features
Drag-and-Drop Reordering: Enables users to rearrange exercises within workouts, a feature manually implemented in Kotlin to enhance user flexibility.
Custom Exercise Filters: The app provides a robust filtering system for exercises, categorized by type (Cardio, Weights, etc.) and target areas (Core, Arms, Legs, etc.), for easy customization.
Timers and Notifications: Set-specific timers for breaks and duration-based exercises, with notifications to keep users on track during workouts.
7. Workout Progress Tracking
After completing a workout or workout plan, the app automatically marks them as complete and adds them to the history, allowing users to reflect on their achievements.
Active workout sessions include break timers and duration tracking, which trigger notifications when it's time to resume or wrap up.
Technical Highlights
Kotlin: The app is fully built in Kotlin, leveraging modern Android development practices.
Room Database: All user data, workouts, and plans are stored locally using Room, ensuring a reliable and smooth user experience without the need for constant network access.
Material Design: Every UI component follows Google’s Material Design guidelines for consistency, accessibility, and a beautiful look and feel.
Dynamic UI Customization: With Android’s dynamic theming, the app adjusts its color scheme based on the device’s system colors, enhancing user experience and personalization.
Project Motivation
I aimed to create an app that not only helps users stay on track with their fitness goals but also offers an elegant and engaging user experience. A focus on usability, design, and a modern tech stack ensures that this app feels responsive and intuitive, providing users with a workout management tool that’s both powerful and easy to use.

Screenshots
Add relevant screenshots of the app in use, showcasing the Home, History, and Configure screens, workout tracking, and dynamic theming.
