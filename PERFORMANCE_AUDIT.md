# Performance Audit — Kawaii Plugin Suite

_Audit date: 2026-06-24. Scope: all 31 plugins in this repository, focused exclusively on
patterns that lag the server (TPS) or players (client packets/stalls). Report only — no code
was modified._

## Bottom line

Several plugins will lag the server/players under normal multiplayer load, but the damage is
concentrated in ~6 plugins. The rest range from minor cleanups to genuinely well-engineered.

The recurring root causes across the suite are:

1. **Main-thread block scans** — large `getBlockAt()` / `getHighestBlockYAt()` / `setType()` loops
   on the tick thread (the biggest TPS killers).
2. **Synchronous YAML saves on hot paths** — full-file writes on deaths, quits, GUI clicks, and
   transactions.
3. **Full-world `getEntities()` sweeps** — materializing every entity in a world on a timer.
4. **Per-tick cosmetic loops over all online players** — GUI border animations, scoreboards.

---

## Severity index

| Rating | Plugin | Headline issue |
|---|---|---|
| **SEVERE** | KawaiiCompanion | Sync schematic parse on main thread + uncapped per-tick block placement + dual 20 Hz schedulers + full-world entity scans |
| **SEVERE** | KawaiiSeasons | `simulateSnow` 121-column scan + `setType()` per player, all winter |
| **HIGH** | KawaiiSkyblock | ~762k `getBlockAt()` calls per island scan (timer + GUI click) |
| **HIGH** | KawaiiEssentials | 4-tick all-player ItemStack rebuild; sync YAML save on every death |
| **HIGH** | KawaiiEnderChest | Sync YAML load on open + save on close, main thread |
| **HIGH** | KawaiiLogger | `flush()` per log line → fsync storm |
| **HIGH** | KawaiiNights | `world.getEntities()` full scans every cycle |
| **HIGH** | KawaiiScoreboard | Per-character gradient Component rebuild for every player at 2 Hz |
| MODERATE | Herobrine | Reflective packet broadcast over all world players twice per 10 Hz move |
| MODERATE | KawaiiCam | Per-tick raytrace + `getNearbyEntities` (used every 4–8s) |
| MODERATE | KawaiiMobChat | Per-bubble 2-tick teleport task w/ UUID lookups |
| MODERATE | KawaiiSparkles | Always-on 1-tick animator + all-player inventory scan |
| MODERATE | KawaiiHearts | `getNearbyEntities` per player; `view-radius:0` = all-entity scan |
| MODERATE | KawaiiThirst | Biome substring scan per player; 5-tick interval floor |
| MODERATE | KawaiiRecipes | Full `recipeIterator()` rebuild on every player join |
| MODERATE | FiveHearts | Two redundant full-player sweeps; timer duplicates events |
| LOW | KawaiiGroups, KawaiiShop, KawaiiControlPanel, KawaiiQuests, KawaiiDungeons, KawaiiWorlds | Sync YAML saves on mutations |
| CLEAN | KawaiiRTP, KawaiiFurnace, KawaiiNoCheat, KawaiiNoGrief, KawaiiSigns, KawaiiPoem, KawaiiReload, KawaiiDragon, KawaiiBlockHP | No lag-causing patterns |

---

## KawaiiCompanion — SEVERE

Multiple independent tick-stall sources. (Credit: HTTP, skin upload, and memory saves are
correctly async.)

- **C1 — Sync schematic load.** `SchematicLoader.java:78-80` GZIP decompress + NBT parse +
  `int[w*h*l]` allocation on the tick thread. Called from `BuildManager.java:123` and `:738`.
  Fix: load/parse via `runTaskAsynchronously`, hop to main to start; cache parsed `Schematic` on
  the `Session`.
- **C2 — Preview re-reads file on every GUI click.** `BuildManager.java:734-809` (from
  `handleClick` lines 1072/1092/1107/1118/1141). Each rotate/offset press = full disk reparse +
  two volume scans + up to 25k `sendBlockChange`. Fix: cache schematic, debounce re-renders,
  `sendMultiBlockChange`.
- **C3 — `nearestStructureLabel` calls `locateNearestStructure` 14× in a loop.**
  `WorldAwareness.java:184-215`. Fix: async, narrow radius, short-circuit, cache.
- **H1 — Two unconditional 20 Hz schedulers.** `KawaiiCompanion.java:715` (`navMirrorTick`),
  `:718` (`rideTick`), both period `1L`. Fix: fast no-op guard; only mirror actively-following
  companions.
- **H2 — 600-tick full-world entity sweep with per-entity PDC read.**
  `KawaiiCompanion.java:2291-2304`. Fix: track companion UUIDs in a set.
- **H3 — Uncapped per-tick block placement + per-block `BlockPlaceEvent`.**
  `BuildManager.java:421` (`FULL` mode `step = rW*rL`), `:569-571` (event per block, plus
  `Bukkit.getPlayer(UUID)` per block at `:541`). Fix: blocks-per-tick budget regardless of mode;
  resolve owner once; skip event when no protection plugin present.
- **H4 — Dual unthrottled `getNearbyEntities` combat sweeps.** `KawaiiCompanion.java:5575`
  (`pickPriorityTarget`), `:5694` (`runSpotterScan`), from `engageThreatsNear` every behavior
  tick. Fix: apply the 10-tick target cache `pickFormTarget` already uses; merge sweeps.
- **H5 — `removeStrayCompanionsOf` all-worlds `getEntities()` on every summon/morph/respawn.**
  `KawaiiCompanion.java:5168-5174`. Fix: restrict to owner's world / track entity IDs.
- **H6 — Main-thread A* with ~10k+ block reads; repath-storm risk.**
  `CompanionPathfinder.java:142-419`. Fix: per-companion repath cooldown; lower `maxNodes` for
  distant goals. (Has `isChunkLoaded` guard — good.)
- **H7 — Uncached reflection in `biomeLabel`.** `WorldAwareness.java:98-104`. Fix: `Biome` is
  `Keyed` — call `b.getKey()` directly.
- **Medium:** M1 altitude/flying-threat `getBlockAt` per engaged tick (`:5757,5786`);
  M2 door/dig `getBlockAt` per moving tick (`:5253,5281`); M3 per-viewer NMS reflection in packet
  broadcast (`NmsCompanion.java:965`); M4 pose/swim/ground block lookups per move tick
  (`:2520,4089`); M5 `protectBlocksNearCompanion` O(blocks×companions) in explosion handler
  (`:4335`); M6 `tickBuild` BLOCK-mode O(total²) forward scan (`BuildManager.java:432`); M7 warden
  anger-clear loops all world players every tick (`:7331`).
- **Low:** skin disk read/parse per summon (`:2023`), chat lowercase/substring per message
  (`:4297`), shimmer-border GUI rebuild (`:8207`), `listSchematics` disk read per GUI click
  (`BuildManager.java:635`).

---

## KawaiiSeasons — SEVERE

- **`simulateSnow()` per-player block scan.** `KawaiiSeasons.java:257-279` (from `tick()` at
  `:186`). With `snow-radius: 5` that's 121 columns per player per cycle, each doing
  `getHighestBlockYAt` + 2–3 `getBlockAt` + possible `setType()` (lighting/physics/packets).
  ~2,400 block reads+writes per 3s at 20 players, all winter — sustained TPS drain, not a spike.
  Fix: gate on player movement (cache last column), reduce radius, spread across ticks, skip
  unloaded chunks.
- **`manageWeather()` re-applies weather every tick** (`:208-226`). Fix: only on season change.

---

## KawaiiSkyblock — HIGH

- **Triple-nested block scan `computeLevel` / `computeValueBreakdown`.** `:804-814`, `:843-855`.
  Defaults (radius 48, Y 60→140) = ~762k `getBlockAt().getType()` per island on the main thread;
  can force-load chunks. Runs on a 5-min timer (`recomputeAllLevels` `:880-889`) AND inline on the
  "Island Value" GUI click (`:1230`). Fix: async chunk snapshots; one island/chunk per tick;
  show a "scanning…" placeholder for the GUI.
- **Sync YAML write on every mutation** (`IslandManager.java:117-124`), incl. `touchLastSeen` on
  join/quit and every GUI toggle. Fix: debounce + async.
- **`resolvePlayer` iterates all offline players** (`:904-911`). Fix:
  `getOfflinePlayerIfCached` / async lookup.

---

## KawaiiEssentials — HIGH

- **`animateMenus` every 4 ticks loops ALL players, rebuilds ~20 ItemStacks + `updateInventory()`**
  (`:113`, `:425-444`). Runs 5×/sec even with zero menus open. Fix: track open-menu players in a
  Set; reuse cached panes; skip when empty.
- **Sync YAML saves on main thread**, notably `onDeath` (`:1486-1491`) — deaths cluster at mob
  farms. Also sethome/kit/warp paths. Fix: async/debounce.
- **`safeTeleport` sync `getChunkAt`** (`:297-303`). Fix: `teleportAsync` / `getChunkAtAsync`.

---

## KawaiiEnderChest — HIGH

- **Sync YAML save on every close + load on every open**, main thread (`:134-143`, `:158-190`,
  up to 54 ItemStacks). Fix: cache in memory while online; load on join, async-flush on quit.

---

## KawaiiLogger — HIGH

- **`flush()` per log line.** `LogWriter.java:58`. The write is correctly off-thread, but flushing
  every line = hundreds of fsyncs/sec on busy servers + unbounded submit queue. Fix: flush on a
  timer, not per line.
- **`PlayerMoveEvent` structure/biome scans** (`:717-766`, `:775`). Well guarded (block-delta +
  chunk-cross), but `getStructures` per chunk-cross adds up. Fix: throttle structure checks
  per-player.
- **`EntityDamageEvent` buckets every damage tick** (`:435-441`) — fire/freeze/poison fire many
  times/sec. Fix: short per-cause cooldown before bucketing.

---

## KawaiiNights — HIGH

- **`raidMobsCleared()` iterates `world.getEntities()` every cycle** (`:227-235`, `:242`). Fix:
  track raid mobs in a `Set<UUID>` pruned on death, or `getEntitiesByClass(Monster.class)`.
- **`cullHostiles()` full entity scan while TPS is already low** (`:425-440`) — can compound a lag
  spiral. Fix: `getEntitiesByClass`; throttle.
- (Positive: `isChunkLoaded` guard before spawning and a `lag-guard` TPS check are present.)

---

## KawaiiScoreboard — HIGH

- **`tickAll()` rebuilds every sidebar every cycle** (`:158-187`, `:412-450`). Default
  `update-ticks: 10` = 2 Hz; `gradientTitle` allocates a Component per character every refresh.
  Fix: diff rows before sending; lighten the animated gradient; raise the interval.
- **Sync YAML save on every quit** (`:226`). Fix: rely on the 60s autosave + onDisable; async the
  write.

---

## Moderate

- **Herobrine** — `HerobrineNms.java:271-279` reflective packet broadcast over `world.getPlayers()`
  twice per 10 Hz move; `BossFightManager.java:70-166` uses `distance()` sqrt and has no hard cap
  on `minionCount`; 845-block nested scans in `toggleNearbyDoor`/`alterNearbyBlocks`; structure
  generation places hundreds of blocks synchronously. Fix: cache viewer list; `distanceSquared`;
  clamp minions; spread block placement.
- **KawaiiCam** — `ShotDirector.java:162` per-tick `rayTraceBlocks`; `CamSession.java:172`
  per-tick `getNearbyEntities(12,8,12)` consumed only every 4–8s. Fix: throttle both. Bounded by #
  operators.
- **KawaiiMobChat** — `:787-807` per-bubble 2-tick teleport task + `Bukkit.getEntity(UUID)`
  lookups. Fix: mount TextDisplay as a passenger; single delayed remove.
- **KawaiiSparkles** — `:560` always-on 1-tick animator; `:572-576` all-player inventory scan +
  ItemStack rebuild. Fix: track open menus; reuse panes. (Move-event particles are well throttled.)
- **KawaiiHearts** — `:295-317` `getNearbyEntities` per player + unconditional custom-name re-set
  per scan; `:298-304` `view-radius:0` scans all living entities in all worlds. Fix: cache last
  name; document/cap `view-radius:0`.
- **KawaiiThirst** — `:271-276` `getBlock().getBiome()` + substring scan per player; `:96` 5-tick
  interval floor too low. Fix: cache biome on move; raise floor; use `World.getBiome(x,y,z)`.
- **KawaiiRecipes** — `:92-122` rebuilds full `recipeIterator()` list on every player join. Fix:
  build the key list once and reuse.
- **FiveHearts** — `:28-41` two full `getOnlinePlayers()` sweeps; the 10-tick clamp timer largely
  duplicates the event handlers. Fix: merge timers; raise interval or drop the clamp timer.

---

## Low (sync YAML saves on mutations — debounce + async)

KawaiiGroups (`:166`, per toggle/block), KawaiiShop (`:239`, per transaction), KawaiiControlPanel
(`:292`, per GUI edit), KawaiiQuests (`:446`, per progress/quit), KawaiiDungeons
(`ProgressManager.java:69`, per clear/claim), KawaiiWorlds (`:1552`, sync snapshot load on world
change; `:1377` config reads per block event).

---

## Clean / well-engineered (no action)

- **KawaiiRTP** — textbook async RTP: `getChunkAtAsync` + `teleportAsync`, world-border check
  before chunk load, retries spread across ticks. Use as the reference pattern for the fixes above.
- **KawaiiFurnace, KawaiiNoCheat, KawaiiNoGrief, KawaiiSigns, KawaiiPoem, KawaiiReload,
  KawaiiDragon, KawaiiBlockHP** — light event handlers, no per-tick world work, properly cancelled
  tasks, no unbounded leaks.

---

## Suggested fix order

1. **KawaiiCompanion C1/C2/H3** — async schematic load + cache + per-tick block budget.
2. **KawaiiSkyblock** + **KawaiiSeasons** — get the main-thread block scans off the tick thread.
3. **Sync YAML saves** suite-wide (EnderChest, Essentials, Scoreboard first) — debounce + async.
4. **Full-world `getEntities()`** (Companion, Nights, Hearts) — tracked `Set<UUID>`.
5. **Per-tick all-player loops** (Essentials, Sparkles, Scoreboard) — iterate only affected players.
