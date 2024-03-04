# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v8.0.4-1.20.1] - 2024-03-04
### Fixed
- Fix block speed values failing to synchronize to clients during lan play

## [v8.0.3-1.20.1] - 2024-01-20
### Fixed
- Fix unable to remove default entries from config file

## [v8.0.2-1.20.1] - 2023-11-07
### Fixed
- Fixed crash on loading world when reading a freshly created `blockrunner.json` config file

## [v8.0.1-1.20.1] - 2023-11-06
### Changed
- Moved default block and block tag configurations to `blockrunner.json` instead of adding those to the tags themselves added by Block Runner
- This way users may change default values without the need for a data pack

## [v8.0.0-1.20.1] - 2023-06-27
- Ported to Minecraft 1.20.1

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
