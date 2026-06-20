# Kawaii Minecraft Plugins ✿

A collection of **32 cute-themed Paper/Spigot plugins** for Minecraft **1.21**.

> ### ✨ Imagined and directed by **[feris](https://mez.ink/ferisooo)** ✨
> ### 🤖 Code generated with the assistance of **Claude** (Anthropic AI)
>
> Every idea, theme, design choice, and creative direction in this project is
> **feris**'s. The Java code was generated with the help of **Claude** under
> feris's direction. Find more of feris's work at
> **[mez.ink/ferisooo](https://mez.ink/ferisooo)**.

> Project background: see [ABOUT.md](ABOUT.md) ・
> Legal terms: see [TERMS.md](TERMS.md) ・
> Privacy: see [PRIVACY.md](PRIVACY.md) ・
> License: see [LICENSE](LICENSE)

---

## ❀ What is this? (read me first if you've never coded)

These are **plugins** — little add-ons — for a Minecraft **server**. You install
them on a server you own (your own computer, or a rented one) and they add new
features for everyone who joins it.

You do **not** need to know how to code to use them. You just need to do two
things:

1. **Build** each plugin (turn the source folders into `.jar` files).
2. **Drop** those `.jar` files into your server's `plugins/` folder.

The rest of this README walks you through both steps with the exact commands to
type.

---

## ❀ Beginner setup — step by step

### Step 1 — Install the free tools you need

You need three programs. They're all free, official, and safe.

| Tool | What it's for | Where to get it |
| ---- | ------------- | --------------- |
| **Java 21 (JDK)** | Runs Minecraft servers and builds the plugins | <https://adoptium.net/> — pick "Temurin 21 (LTS)" for your OS |
| **Apache Maven** | Turns the source code into `.jar` files | A copy is already included in this repo (`apache-maven-3.9.16/`). You can also install your own from <https://maven.apache.org/download.cgi> |
| **Git** *(optional)* | Downloads updates of this repo | <https://git-scm.com/downloads> |

> **Windows users:** during the Java installer, tick the box that says
> *"Set JAVA_HOME variable"*. It makes everything later just work.
>
> **Mac users:** the easiest install is `brew install --cask temurin@21`
> (after installing Homebrew from <https://brew.sh>).

To check it worked, open a terminal (Command Prompt on Windows, Terminal on
Mac/Linux) and type:

```bash
java -version
```

You should see something starting with `21`. If not, restart your computer and
try again.

### Step 2 — Download this repository

**The easy way (no Git needed):**

1. Go to <https://github.com/ferisooo/Kawaii-Plugins-Minecraft>.
2. Click the green **Code** button → **Download ZIP**.
3. Unzip the file somewhere easy to find, like your Desktop.

**Or with Git** (lets you pull updates later):

```bash
git clone https://github.com/ferisooo/Kawaii-Plugins-Minecraft.git
cd Kawaii-Plugins-Minecraft
```

### Step 3 — Get a Minecraft server

If you don't already have one:

1. Download **Paper** (the server software the plugins are built for) from
   <https://papermc.io/downloads/paper>. Pick version **1.21.x**.
2. Put the downloaded `paper-1.21.x.jar` in an **empty folder**.
3. Open a terminal inside that folder and run:

   ```bash
   java -jar paper-1.21.x.jar
   ```

   The first run will quit with a message about `eula.txt`. Open that file,
   change `false` to `true`, save, and run the command again. The server starts
   up and creates a `plugins/` folder next to the jar.

### Step 4 — Build the plugins (Windows is easiest)

**Windows — point-and-click:**

Just double-click **`kawaii-start.bat`** in the repo folder. A GUI opens that
lets you build any or all plugins. That's it.

**Mac / Linux — one command:**

Open a terminal in the repo folder and paste:

```bash
for d in Kawaii* Herobrine FiveHearts-source; do
  (cd "$d" && ../apache-maven-3.9.16/bin/mvn -q clean package) && \
  echo "✓ built $d"
done
```

(If you installed Maven yourself, you can just write `mvn` instead of
`../apache-maven-3.9.16/bin/mvn`.)

When it's done, each plugin folder has a `target/` subfolder with a `.jar`
inside, e.g. `KawaiiHearts/target/KawaiiHearts.jar`.

> **First build is slow** — Maven downloads the Paper API on first run. After
> that it's fast.

### Step 5 — Install the plugins into your server

Copy the `.jar` files into your server's `plugins/` folder. You only need the
plugins you actually want — skip the rest.

```bash
# example: copy just KawaiiHearts
cp KawaiiHearts/target/KawaiiHearts.jar /path/to/your/server/plugins/
```

Start (or restart) your server. The first time each plugin runs it creates a
`plugins/<PluginName>/config.yml` you can edit to change settings. After
editing, type `/reload` in the server console (or just restart).

### Step 6 — Connect with Minecraft

In Minecraft → **Multiplayer** → **Direct Connect** → type `localhost` if the
server is on the same computer, or the server's IP address otherwise. Join and
enjoy! ✿

---

## ❀ Optional: AI-powered plugins

`KawaiiQuests`, `KawaiiMobChat`, and `KawaiiCompanion` can optionally call the
**DeepSeek** AI service. To use them you need your own free API key:

1. Get a key at <https://platform.deepseek.com/>.
2. Open the plugin's `plugins/<PluginName>/config.yml` on your server.
3. Paste the key into the `api-key:` line. Save and `/reload`.

These plugins still **work without a key** — they just skip the AI features.

> **Important:** treat your API key like a password. Never share it, never paste
> it into a public Discord, never commit it to GitHub. The config files in this
> repo only contain placeholders.

---

## ❀ The plugins

| Plugin | What it does |
| ------ | ------------ |
| **FiveHearts-source** | Forcefully locks all players to 5 hearts. |
| **Herobrine** | Full Herobrine entity — stalking AI, threat tracking, structures, multi-phase boss fight. |
| **KawaiiBlockHP** | Shows a depleting HP bar while a block is being mined. |
| **KawaiiCam** | An autonomous cinematic camera that flies smart shots and records replays. |
| **KawaiiClaims** | Multi-chunk land claim & protection — golden-shovel claiming, trust, flags. |
| **KawaiiCompanion** | AI companion that follows you and chats via DeepSeek. |
| **KawaiiControlPanel** | In-game chest-GUI to configure every Kawaii plugin live. |
| **KawaiiDragon** | The Ender Dragon grows stronger each time it's defeated. |
| **KawaiiDungeons** | Instanced dungeons with loot, tokens and per-dungeon progression. |
| **KawaiiEnderChest** | Bigger ender chest — 54 slots, per-player, saved to disk. |
| **KawaiiEssentials** | Homes, tpa, hub, back, starter kit, and a trash bin. |
| **KawaiiFurnace** | Speeds up furnaces, blast furnaces and smokers. |
| **KawaiiGroups** | Online-only player groups with roles, invites, group chat, shared hearts. |
| **KawaiiHearts** | Floats a cute pink health bar above every mob's head. ✿ |
| **KawaiiLogger** | Discord webhook logger for ~40 Minecraft event types. ✨ |
| **KawaiiMobChat** | Mobs respond to player chat via DeepSeek — insult them and they fight back! |
| **KawaiiNights** | Extra hostile mobs spawn anywhere; hostiles don't burn in daylight. |
| **KawaiiNoCheat** | Blocks cheat commands for non-bypass players with a cute popup. |
| **KawaiiNoGrief** | Explosions damage & push entities, but blocks survive. |
| **KawaiiPoem** | Custom End-Poem-style scrolling credits from config. |
| **KawaiiQuests** | AI-generated quests with an animated loot crate reward (Java + Bedrock). |
| **KawaiiRTP** | Random teleport that lands on dry, safe ground. |
| **KawaiiRecipes** | Unlocks every recipe in the recipe book. |
| **KawaiiReload** | Reload plugins or restart the server without leaving the game. |
| **KawaiiScoreboard** | Sidebar with online count, world, coords, playtime, edition. |
| **KawaiiSeasons** | In-game seasons that change the player, crops and environment. |
| **KawaiiShop** | Skyblock buy/sell shop GUI with a coin economy. |
| **KawaiiSigns** | Classic `[command]` signs — right-click to run as the clicker. |
| **KawaiiSkyblock** | Skyblock void world with per-player islands. |
| **KawaiiSparkles** | Server-side visual flair — chest sparkles, footstep particles. |
| **KawaiiThirst** | A thirst boss bar that drains and is restored by drinking (Bedrock via Geyser). |
| **KawaiiWorlds** | Multi-world manager — create, teleport, load, unload, delete worlds. |

Most plugins ship their own `README.md` inside the folder with more detail.

---

## ❀ Credits

This project exists because of:

- **feris** — **[mez.ink/ferisooo](https://mez.ink/ferisooo)** — the imagination,
  the design, the theme, the kawaii vision, every idea, every "wouldn't it be
  cute if…". Nothing here would exist without feris's direction.
- **Claude** (Anthropic) — the AI assistant that generated the Java code under
  feris's direction.

If you use, fork, share, or build on this project, please credit **both** as a
matter of basic respect. See [TERMS.md](TERMS.md) for the full ask.

---

## ❀ Legal, in plain English

- **License:** MIT — see [LICENSE](LICENSE). Free to use, modify, and
  redistribute.
- **Terms of use:** see [TERMS.md](TERMS.md). Forks and modifications are
  welcome **as long as you credit feris's imagination and Claude's work**.
- **Privacy:** see [PRIVACY.md](PRIVACY.md). **feris does not collect, see, or
  receive any data about anyone who uses these plugins.**
- **Not affiliated with Mojang, Microsoft, Paper, Spigot, DeepSeek, or
  Anthropic.** This is a community fan project.

Stay kawaii~ ✿
