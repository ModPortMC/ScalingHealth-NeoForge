# Source recovery and validation record

Recorded on 2026-10-06.

## Migration delivery

The original Forge 1.20.1 to NeoForge 26.1.2 migration was performed by the
ModPort + dispatcher SDK automated migration workflow. The original delivered
`ScalingHealth-26.1.2-0.0.0+1.jar` passed 29 target behavior cases and 64 required
assertions: 2 server GameTests and 27 cases in a shared client session, with
zero failures and zero skipped cases. These are results for that original
binary in the locked target environment, not a general compatibility guarantee.

## Recovered source

The final migration workspace was deleted. Its recorded target commit was
`165c7ef92c909a4bc52b8eca941ee5a37a7a4727`; the exact source text and commit
history of that workspace have not been recovered.

An earlier NeoForge source checkout at commit
`2d1a33e7904d685246f2086ba328135a92fa6422` supplied the readable source,
resources, build files and original comments. The user supplied the original
delivery JAR, which was preserved separately before recovery work.

Thirteen changed class entries mapped to these eleven Java source files:

- `client/ClientInit.java`
- `client/KeyManager.java`
- `client/gui/DebugOverlay.java`
- `utils/config/SHDifficulty.java`
- `utils/config/SHPlayers.java`
- `event/CommonEvents.java`
- `event/DamageScaling.java`
- `event/DifficultyEvents.java`
- `objects/Registration.java`
- `objects/item/HeartCrystal.java`
- `utils/MobDifficultyHandler.java`

Paths above are relative to `src/main/java/net/silentchaos512/scalinghealth/`.
Changes were reconstructed from the delivery classes using Vineflower 1.12.0
and inspected against JVM disassembly. Existing readable source was retained
for the remaining files. This publication preserves the upstream repository
history and adds recovered port source; it does not recreate the lost migration
commit history.

## Review and rebuild

Independent review found no blocking differences between the reconstructed
code and the original delivered classes. The recovered project successfully
ran `compileJava jar sourcesJar` with Java 25 and the Gradle wrapper in a
credential-free, offline build sandbox. Mod version `0.0.0` is now explicit
in `gradle.properties`, allowing a source archive to build without Git tags.
The default build number remains `1`.

Decompiling the original and rebuilt JARs with the same tool and dependency
context produced 99 Java files in each. All 99 decompiled files were identical.
The JAR entry sets matched, and all non-class entries matched except the
manifest's generated `Implementation-Timestamp`. This comparison is a source
recovery diagnostic, not a substitute for runtime behavior testing.

The rebuilt JAR has not had a fresh gameplay test run. The original 29-case /
64-assertion result is retained as original-delivery evidence only. No results,
approvals or scheduler state from the deleted migration Run are inherited by
a new execution.

## Publication contents

The repository retains the MIT license and upstream authorship. It includes
product source, resources and the Gradle wrapper. Build output, Gradle caches,
ModPort runtime state, credentials and private recovery material are excluded.
