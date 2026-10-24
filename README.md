![](https://cdn.modrinth.com/data/cached_images/b99eb4538afa569e8e40580e1f0d658ba537ced2_0.webp)
![](https://cdn.modrinth.com/data/cached_images/c791abd4829248bd13ec46eba8a4813858d6e429.png)
* Utility classes for quick use.
* Convenient thread timer.
* Loaders for YAML and JSON with support for asynchronous saving (Configuration).
* Database module for creating databases (MySQL, MariaDB, PostgreSQL).
* UI module for creating menus (forked from [SGui](https://github.com/Patbox/sgui)).

![](https://cdn.modrinth.com/data/cached_images/f65db17eb44c94abfae49e184184143f5f74a8d8.png)

[<img height="150" src="https://cdn.modrinth.com/data/cached_images/1e3337a8d8872eefaab20ab637a1c3d56b7601d8.png" width="150"/>](https://modrinth.com/mod/legendcontrol)[<img height="150" src="https://cdn.modrinth.com/data/cached_images/a5a0dc6ea6b36a004eddb10257c168b0059f783e.png" width="150"/>](https://modrinth.com/mod/legendcontrol-defender)[<img height="150" src="https://cdn.modrinth.com/data/cached_images/fb73358dfccf7f74983790b95f0f19d21f5e1c76.png" width="150"/>](https://modrinth.com/mod/extraquests)[<img height="150" src="https://cdn.modrinth.com/data/cached_images/28551091cd8c2cfbb0798a17fc4f82024d752ec5.png" width="150"/>](https://modrinth.com/mod/extrartp)[<img height="150" src="https://cdn.modrinth.com/data/cached_images/40138064681359508f55c8c8578c98914dc14930_0.webp" width="150"/>](https://modrinth.com/mod/chunklimiter)[<img height="150" src="https://cdn.modrinth.com/data/cached_images/d9388119d59e8c9fdd247afcd9a4a7f549aa034f_0.webp" width="150"/>](https://modrinth.com/mod/extrawarp)[<img height="150" src="https://cdn.modrinth.com/data/cached_images/a7c62596d60653799f085399a16534e7e72fe2cb.png" width="150"/>](https://modrinth.com/mod/movelearner)

## Development modules

| Module | Responsibility |
| --- | --- |
| `core` | Java 17 code without Minecraft or loader APIs: configuration, JSON/YAML, databases and time utilities. |
| `common20` | Shared Minecraft 1.20.1 sources. |
| `forge20` | Forge 1.20.1 entry point, events and loader integration. |
| `common21` | Shared Minecraft 1.21.1 sources, UI API and common Mixin configuration. |
| `fabric21` | Fabric 1.21.1 entry point, events, permissions and loader-specific Mixins. |
| `neoforge21` | NeoForge 1.21.1 entry point, events, permissions and loader-specific Mixins. |

Public Java packages remain `com.vecoo.extralib`. Each loader compiles its version's common sources against its own Minecraft classes; core classes are included in the final mod JAR. Players install only the matching loader JAR, with no separate core/common mod required.

Use `gradlew build` to build all implemented modules, or `gradlew :fabric21:build`, `gradlew :neoforge21:build` and `gradlew :forge20:build` for one loader. Finished mod JARs are copied to `release/` and retained in each loader's `build/libs/` directory. JDK 17 and JDK 21 must be available to Gradle.

Planned modules `fabric20`, `common261`, `neoforge261` and `fabric261` are included when their `build.gradle` exists. A future `common261` is for Minecraft 26.1.x sources; it does not imply compatibility with 26.3.

Put loader-independent changes in `core` or the matching `commonXX` module. Keep loader event subscriptions, permissions and patched-method Mixins in the loader modules. Shared dependency declarations and Shadow relocation rules are in `gradle/library-dependencies.gradle` and `gradle/shadow.gradle`.
