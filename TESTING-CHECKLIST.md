# AssortedWorld — in-world testing checklist

Manual checks only. Anything automatable lives in `common/src/main/java/.../gametest/`
and runs with `./gradlew :neoforge:runGameTestServer` / `:fabric:runGameTest`.

Run each list on **both** NeoForge and Fabric. Use `/locate structure assortedworld:...` to find things.

## Structures
- [ ] Fountain, pyramid, snowball and water dome all generate
- [ ] Each one contains exactly one rune
- [ ] None of them are cut in half, stepped at a chunk border, or missing chunks
- [ ] A water dome is one ribbing material throughout, and its rune sits on the floor
- [ ] `Detected unsafe terrain read during worldgen` does not appear in the log
- [ ] Structure chests contain loot (fountain, pyramid, ruin, and all four water dome variants)
- [ ] Pyramid and fountain spawners are present

## Features
- [ ] Ruins generate, and more than the first one gets a chest, a spawner and a rune
- [ ] Spires generate
- [ ] Randomite ore and deepslate randomite ore generate, drop XP, and need the right tool

## Runes
- [ ] All 18 runes place and render
- [ ] Standing on a rune applies its potion effect
- [ ] The effect gets stronger with player XP level

## Recipes and config
- [ ] Gunpowder reed grows and its recipe works (`c:gunpowders`)
- [ ] Turning a structure off in the config stops it generating
- [ ] `runeChance` changes how often ruins carry a rune

## Creative
- [ ] The Assorted World tab exists and every block/item in it has a model and a name
