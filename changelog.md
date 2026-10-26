* Fix Fabric deferred GUI opening, server lifecycle checks, cached player lookups and shutdown file flushing.
* Restart reset scheduling for subsequent integrated servers without reviving tasks from previous servers.
* Port server GUI API to Forge 1.20.1 with NBT item builders and production Mixin refmaps.
* Align selected-tag item counting/removal and partial-inventory item delivery across loaders.
* Add Forge player heads, Fabric offline permission limits, packet batching and explicit legacy item migration.
* Add dedicated client/server development runs for Forge, NeoForge and Fabric.
* Preserve JSON/YAML write order, report save failures and bound blocking save/flush waits.
* Back up malformed configuration files without moving valid files after read or write I/O failures.
* Add contextual, rate-limited GUI error logging and forward GUI failures to custom exception handlers.
* Fix GUI redirect replacement/clearing flags and immediately expire zero-iteration timers.
* Resolve storage paths against the actual world directory, including custom world names.
* Package Fabric Permissions API as a shared nested mod and add explicit default values to Fabric permission checks.
* Add server integration tests for permissions with and without LuckPerms on Fabric and NeoForge.
* Add cross-loader functional tests for item delivery/counting/removal, item serialization, GUI callbacks and network interactions, timers and world paths.
* Add regression tests for configuration persistence and GUI error logging.
* Add Fabric 1.20.1 (fabric20) with shared legacy GUI/NBT support, Fabric permissions, networking, client/server runs and functional tests.
* Add Minecraft 26.1 modules (common261, fabric261 and neoforge261) on Java 25, including GUI, items, permissions, networking and client/server runs.
* Adapt 26.1 item components, player profiles, GUI input, rendering and legacy item migration, with functional and permission integration tests.
* Declare Minecraft 26.1 through 26.1.2 compatibility for the Fabric and NeoForge 26.1 modules.
