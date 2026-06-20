# Privacy Policy ✿

**Project:** Kawaii Minecraft Plugins
**Author / project owner:** feris — <https://mez.ink/ferisooo>
**Last updated:** 2026

> **Plain-English summary:**
> **feris does not collect anything from you.** The plugins do not call home,
> do not phone feris, do not send analytics, do not track usage, do not
> register your server, and do not transmit anything to feris or any service
> feris controls. Anything they do save stays on your own server.
>
> The optional AI-powered plugins (`KawaiiQuests`, `KawaiiMobChat`,
> `KawaiiCompanion`) and the optional Discord logger (`KawaiiLogger`) talk to
> outside services **only if you turn them on and give them keys / webhooks**.
> Those services have their own privacy policies — feris is not involved.

> **Not legal advice.** This Privacy Policy describes feris's actual practice
> (none) and warns you about what the plugins technically *can* do if you
> enable certain features. It is not legal advice for your server. If you run
> a server in the EU, UK, California, Brazil, or any other place with
> data-protection laws, **you** are the data controller on your server and
> you need to comply with the law yourself.

---

## 1. Who this policy is about

This policy is about **feris**, the author of these plugins. It explains what
information feris does and does not receive when other people install or use
the plugins.

It is **not** the privacy policy of your Minecraft server — if you run a
server with these plugins, **you** are responsible for telling your players
how *your* server handles data. See Section 6.

## 2. What feris collects from you: nothing

**feris collects nothing from you, ever, through these plugins.**

Specifically:

- ❌ **No telemetry, analytics, metrics, or "phone home" calls** of any kind.
- ❌ **No usage statistics** ("how many servers installed KawaiiHearts?" —
  feris doesn't know and has no way to find out).
- ❌ **No server registration, server lists, or pingbacks.**
- ❌ **No crash reports** are sent to feris.
- ❌ **No player accounts, IPs, UUIDs, chat messages, coordinates, or
  inventory data** ever leave your server because of code feris wrote.
- ❌ **No cookies, no tracking pixels, no third-party trackers.**
- ❌ **No account or registration** is needed to use the plugins.
- ❌ **No update server** that feris controls is contacted by the plugins.

If any future version of these plugins ever adds telemetry or analytics, this
file will be updated to say so clearly, and that feature will be **opt-in**.

## 3. What the plugins *do* save — and it stays on your server

The plugins are normal Bukkit/Paper plugins, so they save game data
**locally** inside your server's `plugins/<PluginName>/` folder. For example:

| Plugin | What it saves locally on your server |
| ------ | ------------------------------------ |
| `KawaiiClaims` | Claim chunks, trust lists, claim flags |
| `KawaiiEnderChest` | Per-player ender chest contents |
| `KawaiiEssentials` | Homes, last-location for `/back`, starter-kit flag |
| `KawaiiDungeons` | Per-player dungeon tokens, reputation, weekly cooldowns |
| `KawaiiGroups` | Group membership, roles, group chat settings |
| `KawaiiSkyblock` | Island ownership, locations, members |
| `KawaiiCompanion` | Companion settings, chat history with the AI companion |
| `KawaiiQuests` | Active quest state per player |
| `KawaiiScoreboard` | Per-world playtime counters |

This data:

- Lives **only on the disk of the server where the plugin is installed**.
- Is read and written **only by that server**.
- Is **not sent anywhere** by feris's code.
- Is yours (and your server admin's) to manage, back up, export, or delete.

If you want to delete a player's data, deleting the entries for that player in
the plugin's data files (or, in extreme cases, the whole
`plugins/<PluginName>/` folder) is sufficient. The plugin will recreate empty
data on next launch.

## 4. Optional features that talk to outside services

A few plugins have features that **only run if you turn them on and supply
your own credentials**. When enabled, they send data to a third-party service.
feris does **not** receive that data and is **not** the operator of those
services.

### 4.1 DeepSeek AI (KawaiiQuests, KawaiiMobChat, KawaiiCompanion)

If you paste a DeepSeek API key into `config.yml` for any of these plugins,
the plugin sends prompts to **DeepSeek's API** (<https://platform.deepseek.com/>)
so DeepSeek can generate text replies.

- What is sent to DeepSeek depends on the plugin and may include: the player's
  chat message, the mob type or quest context, recent in-game context (your
  health, biome, nearby creatures, etc. for `KawaiiCompanion`), and the
  plugin's system prompt.
- Player **Minecraft account names** can appear in the prompts (for example,
  in the companion's situational context).
- DeepSeek's handling of that data is governed by **DeepSeek's own privacy
  policy and terms** — read them before enabling these features.
- **feris does not see any of this data.** You and DeepSeek do.

If you do not paste a key, none of this happens — the plugin runs without the
AI features.

### 4.2 Discord webhook logging (KawaiiLogger)

If you paste a Discord webhook URL into `KawaiiLogger`'s `config.yml`, the
plugin sends events (joins, deaths, advancements, chat messages, etc.) to
that Discord channel.

- The webhook URL is controlled **entirely by you**.
- Data is sent **to Discord** (Discord's privacy policy applies).
- **feris does not see this data** and has no access to your Discord channel.
- Server owners using this feature should **tell their players that chat is
  being logged to Discord** — many jurisdictions require this disclosure.

If you do not paste a webhook URL, nothing is sent.

### 4.3 MineSkin (KawaiiCompanion, optional)

`KawaiiCompanion` can optionally call **MineSkin** (<https://mineskin.org/>) to
generate companion skin textures. This is an optional feature. If enabled, the
plugin sends skin image data to MineSkin. MineSkin's own privacy policy
applies. **feris does not see this data.**

### 4.4 Maven dependency downloads

When you **build** the plugins for the first time, Maven downloads the Paper
API and other build dependencies from **Maven Central** and the
**PaperMC repository**. This is a normal part of any Maven-based build and is
the same as installing any other software. feris does not see, log, or
control these downloads.

### 4.5 Mojang services

Minecraft itself talks to **Mojang / Microsoft** services for authentication
and skin lookups. The plugins do not add any extra Mojang traffic beyond what
Paper/Spigot already do.

## 5. Where the plugins do *not* send data

For clarity, the plugins **do not**:

- Send any data to `mez.ink`, `ferisooo`, or any domain feris owns.
- Send any data to Anthropic, the makers of Claude (Claude was used at
  development time, not at runtime).
- Auto-update themselves from any server (you update by re-downloading and
  re-installing).
- Open inbound network ports.
- Read files outside the server's own working directory.

## 6. If you run a server — you are the "data controller"

Under most data-protection laws (GDPR, UK GDPR, CCPA, LGPD, PIPEDA, APPI,
etc.), if you run a Minecraft server that other people connect to, **you** —
not feris — are the **data controller** for your players' data. That means:

- **You** decide which plugins to install and which features to enable.
- **You** decide whether chat is logged to Discord.
- **You** decide whether AI features are enabled and which prompts include
  what player context.
- **You** must tell your players what data you collect, how long you keep it,
  and how they can request access or deletion.
- **You** are responsible for honoring data-subject requests (access,
  rectification, erasure, portability, objection) for data on your server.
- **You** must not collect more data than you need, and should secure your
  server appropriately.

Your players have rights against **you and your server**, not against feris.

A simple, fair-use rule of thumb for small community servers:

1. Have a short notice in your server description, Discord, or MOTD that says
   what you log and where.
2. Don't paste server logs publicly.
3. If a player asks you to delete their data, do it: stop the server, delete
   their entries from the relevant `plugins/<PluginName>/` data files, restart.
4. If you're a business or your server is large, talk to a lawyer.

## 7. Children's privacy

feris does not knowingly collect any information from anyone of any age,
because **feris does not collect any information at all** (Section 2). If a
child uses a server running these plugins, the server operator is responsible
for complying with children's-privacy laws (COPPA in the US, GDPR consent
rules for under-16s in the EU, the UK's Age-Appropriate Design Code, etc.).

## 8. Security

The plugins are open source — you can read every line of code. They are
provided **as is**, with no security warranty (see [TERMS.md](TERMS.md)
Sections 6–7). To keep your players safe:

- Keep your server software, plugins, and operating system patched.
- Back up your worlds and plugin data regularly.
- Never share API keys, webhook URLs, or server admin passwords publicly.
- Read each plugin's own `README.md` before enabling sensitive features.

If you find a security issue in the plugins, please report it via
**[mez.ink/ferisooo](https://mez.ink/ferisooo)** rather than posting it
publicly, so it can be fixed before being widely exploited.

## 9. International transfers

Because feris collects nothing, no data is transferred internationally by
feris. If you enable third-party features (DeepSeek, Discord, MineSkin), those
services may move your data across borders under **their** policies, not
feris's.

## 10. Changes to this policy

This policy may be updated when the plugins change in ways that affect
privacy. The "Last updated" date at the top reflects the latest version. The
authoritative copy lives in this repository on GitHub.

If a future version of the plugins ever introduces any feature that would
make feris (rather than a third party) receive any data, this file will be
updated **first** and that feature will be **opt-in**, **off by default**, and
documented.

## 11. Contact

For privacy questions, reach feris through
**[mez.ink/ferisooo](https://mez.ink/ferisooo)**.

For questions about how **your** server handles data, ask **your** server's
admin — feris cannot answer questions about specific servers.

---

*Your data is yours. Stay kawaii. ✿*
