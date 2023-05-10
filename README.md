# Block Runner

A Minecraft mod. Downloads can be found on [CurseForge](https://www.curseforge.com/members/fuzs_/projects) and [Modrinth](https://modrinth.com/user/Fuzs).

![](https://raw.githubusercontent.com/Fuzss/modresources/main/pages/data/blockrunner/banner.png)

## Configuration
The speed values Block Runner applied to individual blocks are fully configurable in a config file found at `.minecraft/config/blockrunner.json`. The file cannot be edited from in-game, but can be reloaded after having been edited in an external editor by running `/reload`.

The file contains simple key and value pairs consisting of the block id (e.g. `minecraft:dirt_path`) or block tag id (e.g. `#minecraft:stone_bricks`) as a key and the corresponding speed value as a double with `1.0` being the normal block speed.
Setting a block's speed to `1.0` can also be useful for removing a built-in speed, such as making soul sand no longer apply a slowdown effect.

To aid other mod developers who wish to use Block Runners capabilities, Block Runner also includes a few block tags with default speed values associated with them which can easily be used by other modders to add support without declaring an explicit dependency.
- `blockrunner:very_slow_blocks`: 0.45
- `blockrunner:slow_blocks`: 0.65
- `blockrunner:slightly_slow_blocks`: 0.85
- `blockrunner:slightly_quick_blocks`: 1.15
- `blockrunner:quick_blocks`: 1.35
- `blockrunner:very_quick_blocks`: 1.55
