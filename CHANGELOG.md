# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v4.2.1-1.19.2] - 2023-05-10
### Added
- Added a few default block tags with associated speed values to allow other mods to easily add support for Block Runner without having to declare an explicit dependency
- The new block tags and their default speed values are: 
  - `blockrunner:very_slow_blocks`: 0.45
  - `blockrunner:slow_blocks`: 0.65
  - `blockrunner:slightly_slow_blocks`: 0.85
  - `blockrunner:slightly_quick_blocks`: 1.15
  - `blockrunner:quick_blocks`: 1.35
  - `blockrunner:very_quick_blocks`: 1.55
### Fixed
- Block Runner can now support all kinds of blocks independently of their height, such as very thing blocks like carpets
- Fixed Block Runner breaking vanilla's fov effects intensity option

## [v4.2.0-1.19.2] - 2022-08-21
- Compiled for Minecraft 1.19.2
- Updated to Puzzles Lib v4.2.0
### Added
- Added Simplified Chinese thanks to [CatastropheChou]

## [v4.1.0-1.19.1] - 2022-07-30
- Compiled for Minecraft 1.19.1
- Updated to Puzzles Lib v4.1.0

## [v4.0.1-1.19] - 2022-07-26
### Fixed
- Fixed speed boost being applied very unreliably when walking on slab blocks

## [v4.0.0-1.19] - 2022-07-06
- Ported to Minecraft 1.19
- Split into multi-loader project

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
[CatastropheChou]: https://github.com/CatastropheChou
