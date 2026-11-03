# Lucky Dangle Browser — Android v1.0.0

A first Android browser foundation with Mozilla GeckoView and an in-browser Lucky Dangle overlay.

## Included
- GeckoView-powered Android browser screen.
- Search/address field, Go, Back, Forward, Reload and Home controls.
- Built-in Lucky Dangle overlay drawn above web pages; it remains visible while navigating within this app.
- Drag the charm/rope, hide/show the overlay, choose four charm symbols and adjust overlay opacity.
- The supplied `luckydongle4.1.html` is bundled unchanged at `app/src/main/assets/luckydangle/index.html` and opens through **Studio**.
- GitHub Actions-ready Gradle project (workflow YAML is supplied separately by the project owner).

## Build
The GitHub Actions workflow should use JDK 17, Android SDK, Gradle 8.13 and run:

```bash
gradle assembleDebug
```

APK output:
`app/build/outputs/apk/debug/app-debug.apk`

## Important scope notes
- This is a browser starter, not a Brave clone. It does not yet include tab management, bookmarks, downloads UI, private browsing, ad blocking, sync, or extension compatibility.
- The Dangle overlay is inside this browser app's own content area. It is not an Android system-wide overlay over Chrome/WhatsApp/other apps.
- The native overlay uses a lightweight Android Canvas physics loop; the original 4.1 HTML studio remains bundled separately.
- Home-screen widget and system-wide floating overlay require additional Android components and permissions and are not claimed as implemented in this first build.
- A successful APK build and real-device testing have not been performed in this environment. GitHub Actions is expected to perform the first build; install and test the resulting APK on an Android phone.

## Engine
Mozilla GeckoView is used as the embedded browser engine. Mozilla's official quick-start requires the Mozilla Maven repository and Java 17 compatibility.
