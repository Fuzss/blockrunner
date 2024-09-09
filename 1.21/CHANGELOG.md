# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.0.0-1.21] - 2024-09-09
- Port to Minecraft 1.21
- Forge is no longer support in favor of NeoForge
## Changed
- The config file at `.minecraft/config/blockrunner.json` has been replaced with a block [data map](https://docs.neoforged.net/docs/resources/server/datamaps/) `blockrunner:block_speeds`
- Data maps are available on Fabric as well provided by the bundled [NeoForge Data Pack Extensions](https://github.com/Fuzss/neoforgedatapackextensions) library
