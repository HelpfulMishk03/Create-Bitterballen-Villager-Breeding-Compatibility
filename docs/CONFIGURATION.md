# Configuration

The mod creates:

`config/bitterballen_villager_compat-common.toml`

Default values:

```toml
[villager_food]
    enabled = true
    foodPoints = 2
    allowPickup = true
```

## enabled

Master compatibility toggle.

When disabled, the mod restores the Bitterballen villager-food state that existed before this mod applied its changes.

## foodPoints

Controls how many vanilla villager food points one Bitterballen contributes.

- `0` = no breeding-food value
- `1` = carrot/potato/beetroot-like
- `2` = default
- `4` = bread-like

Valid range: `0` through `64`.

## allowPickup

Controls whether Bitterballen is included in the villager wanted-item set.

When false, this mod does not make villagers seek dropped Bitterballen. If Bitterballen reaches a villager inventory by another mechanism, `foodPoints` still applies when greater than zero.
