# AssortedWorld — in-world testing checklist

Manual checks only. Anything automatable lives in `common/src/gametest/java/.../gametest/`
and runs with `./gradlew :neoforge:runGameTestServer` / `:fabric:runGameTest`.

Run each list on **both** NeoForge and Fabric. Use `/locate structure assortedworld:...` to find things.

## Structures
- [ ] Fountain, pyramid, snowball and water dome all generate
- [ ] Each one contains exactly one rune
- [ ] None of them are cut in half, stepped at a chunk border, or missing chunks
- [ ] A water dome is one ribbing material throughout, and its rune sits on the floor
- [ ] `Detected unsafe terrain read during worldgen` does not appear in the log
- [ ] Structure chests are actually placed (fountain, pyramid, ruin, all four water dome variants)
- [ ] Pyramid and fountain spawners are present

## Features
- [ ] Ruins generate, and more than the first one gets a chest, a spawner and a rune
- [ ] Spires generate
- [ ] Randomite ore and deepslate randomite ore generate
- [ ] `runeChance` changes how often ruins carry a rune

## Creative
- [ ] Everything in the Assorted World tab renders
