# Scaling Health — NeoForge 26.1.2

An unofficial NeoForge port of [SilentChaos512's Scaling Health](https://github.com/SilentChaos512/ScalingHealth).

本项目由 **ModPort + dispatcher SDK 的全自动迁移工作流**将原版 Forge 1.20.1 Mod 迁移至 NeoForge 26.1.2。交付 JAR 已完成真实游戏环境中的目标行为测试：**29 个用例、64 个必需断言全部通过，0 失败、0 跳过**。

The port was produced by **ModPort + [dispatcher SDK](https://github.com/FlightDan/dispatcher-sdk)** through an automated migration workflow. The delivered JAR passed **29 target behavior cases covering 64 required assertions**, with no failures or skipped cases: 2 GameTests and 27 cases in a shared client session.

## Supported environment

| Component | Version |
| --- | --- |
| Minecraft | 26.1.2 |
| NeoForge | 26.1.2.106 used for the verified delivery |
| Java | 25 |
| Silent Lib | 11.2.0 or compatible newer version for this Minecraft version |
| Gradle wrapper | 9.2.1 |
| ModDevGradle | 2.0.146 |

Install the mod and its Silent Lib dependency into the matching NeoForge instance. Minecraft 26.3 has not been established as a supported runtime for this release.

## Features

Scaling Health allows players to gain extra health and scales mob health and damage with configurable difficulty. Its configuration controls health, difficulty progression, healing, regeneration, loot, world generation, client feedback, and HUD rendering. See the [original project](https://github.com/SilentChaos512/ScalingHealth) for the mod's background and feature documentation.

## Build from source

Use a Java 25 JDK. The Gradle wrapper downloads the matching build dependencies on the first build.

```sh
./gradlew compileJava jar sourcesJar
```

On Windows:

```bat
gradlew.bat compileJava jar sourcesJar
```

The compiled mod and Java source JAR are written to `build/libs/`. The version is declared in `gradle.properties`, so a source ZIP can build without local Git tags.

## Source recovery and verification

After the original migration workspace was removed, this published source was recovered using the saved delivery JAR and an earlier source checkout. Existing readable source and comments were retained, and changes in eleven Java files were reconstructed from the delivered classes. The original deleted source text and migration commit history have not been recovered.

The recovered changes received an independent source and bytecode review. The recovered project also compiled successfully and produced both the mod JAR and source JAR in a credential-free, offline build environment. All 99 Java files decompiled from the rebuilt JAR matched those from the original delivery; resources matched, with only the generated manifest timestamp differing.

The 29-case / 64-assertion gameplay result above belongs to the original delivered JAR. It is not a claim that gameplay tests were run again against the newly rebuilt JAR, or that every possible gameplay scenario has been covered. Details are recorded in [SOURCE_RECOVERY.md](SOURCE_RECOVERY.md).

## Attribution and license

Original mod by **SilentChaos512**, with **Cyborgmas** credited in the mod metadata. Port maintained under **ModPortMC**.

The original [MIT license](LICENSE) and copyright notice are retained. EvalEx remains a build dependency shaded into the mod, and Silent Lib remains an external runtime dependency. This repository is an unofficial port and is not the upstream project's official release channel.
