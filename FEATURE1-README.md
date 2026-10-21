# Feature 1: Circuits, gates and switches

## Design

- `GateSquare` owns the physical gate state (`OPEN`, `CLOSED`, `PENDING_CLOSE`) and the movement-access rule.
- `SwitchSquare` is a persistent traversable square that identifies its circuit; it does not trigger itself when occupied.
- `GateCircuit` owns the circuit command and broadcasts it to all gates belonging to that circuit.
- `MapParser` recognizes `1`, `2`, `a`, and `b`, checks both circuits are complete whenever a map uses any new symbol, and preserves compatibility with maps that use only the original symbols.
- `Level.move` toggles a circuit only after a successful player move onto a switch. Ghosts cannot activate switches, and entering a switch occupied by a ghost does not toggle it.
- `BoardFactory` and `PacManSprites` provide the new square types and reuse `key.png`, `floor.png`, and `wall.png`.

## Map symbols

`#` wall; space floor; `.` pellet; `P` player start; `G` ghost; `1`/`2` switches; `a`/`b` gates for circuits 1/2.

A map that uses any of the new symbols must contain at least one gate and one switch for each circuit. A map using only legacy symbols remains valid.

## Demo map

`src/main/resources/board-gates.txt` is a demonstration map. To run it, temporarily set `Launcher.DEFAULT_MAP` (or the `levelMap` initial value) to `/board-gates.txt`, or create a launcher with `withMapFile("/board-gates.txt")` before calling `launch()`.

## Tests

Added unit tests cover circuit independence, multi-gate toggling, entry blocking, pending closure with multiple occupants, and cancellation of pending closure. Run `./gradlew test` in an environment with the configured Gradle 5.3 distribution and project dependencies available.

## Verification limitation

The test suite could not be executed in this environment because the Gradle wrapper distribution was not cached and outbound network access was unavailable. Please run the tests locally before treating this as a verified build.
