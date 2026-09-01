# Internals

Create: Bitterballen - Villager Breeding Compatibility modifies two existing vanilla villager collections:

- `Villager.FOOD_POINTS`
- `Villager.WANTED_ITEMS`

The mod does not replace villager AI, breeding goals, inventories, pathfinding, food sharing, or willingness logic.

## Item lookup

The target item is resolved through the Forge item registry using:

`create_bic_bit:bitterballen`

No Java classes from Create or Create: Bitterballen are referenced by the implementation.

## Access Transformer

`META-INF/accesstransformer.cfg` widens the two vanilla fields and removes `final` so immutable replacement snapshots can be assigned safely.

```text
public-f net.minecraft.world.entity.npc.Villager FOOD_POINTS
public-f net.minecraft.world.entity.npc.Villager WANTED_ITEMS
```

No Mixin, reflection, method overwrite, redirect, or injected villager tick is used.

## Startup

During Forge common setup, the mod resolves Bitterballen, captures its pre-mod villager state, and applies the config.

The configured state is applied by copying the current collections, altering only the Bitterballen entry, converting the copies back to immutable snapshots, and assigning the snapshots to the vanilla fields.

## Baseline restoration

Before its first change, the mod records:

- any existing Bitterballen food-point value
- whether Bitterballen was already in `WANTED_ITEMS`

Disabling the compatibility restores that recorded state instead of assuming no other mod touched Bitterballen.

## Performance

The mod adds no recurring world or entity workload.

It registers no custom tick handler, AI goal, scanner, packet, capability, inventory, block, item, entity, effect, recipe, fluid, sound, menu, or command.

After configuration is applied, vanilla villager code reads the same collections it already uses.
