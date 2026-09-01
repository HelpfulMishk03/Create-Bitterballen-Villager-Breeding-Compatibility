# Release Testing

## Environment

Use Java 17.

## Build

`\.\gradlew.bat clean build`

Confirm the release JAR appears in `build/libs`.

## Client

Run:

`\.\gradlew.bat runClient`

Confirm Forge loads the mod, Create, and Create: Bitterballen without missing-dependency errors.

## Behavior

1. Spawn villagers with available inventory space.
2. Drop Bitterballen near them.
3. Confirm pickup with `allowPickup=true`.
4. Confirm breeding-food behavior with `foodPoints=2`.
5. Set `foodPoints=0` and confirm Bitterballen no longer contributes breeding food.
6. Set `allowPickup=false` and confirm this mod no longer marks dropped Bitterballen as wanted.
7. Set `enabled=false` and confirm pre-mod state restoration.
8. Restart the client/server after config changes as the safest release-validation path.

## Dedicated server

Run `\.\gradlew.bat runServer`, accept the EULA, then confirm clean startup with no client-only class errors.
