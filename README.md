# BlueMap-Factions

> *[BlueMap](https://github.com/BlueMap-Minecraft/BlueMap) addon for showing [Factions](https://github.com/TownyAdvanced/Towny) on your map*

I stole almost all of this from [BlueMap-Towny](https://github.com/Chicken/BlueMap-Towny).
This is a one-off plugin meant for my server running FactionsUUID, so don't expect much support (especially as I do not usually program in Java!).
That said, the entire plugin is around 300 lines long, so it should be *mostly* bug-free. If you *do* need support, I can usually answer within a few days.
Contact me at gritty.pen4969@hucar.io or @hucario on Discord.

## Installation

Put the plugin jar file inside your plugins folder and have both Factions and BlueMap installed.

## Config

```yaml
# BlueMap-Factions configuration
# https://github.com/hucario/BlueMap-Factions#config

# Seconds between checks for marker updates
update-interval: 30



# Max members listed in %members%, %member_display_names%, and %member_uuids%
max-listed-members: 8

# Divider between members in %members%, %member_display_names%, and %member_uuids%
members-split: '`,`'


dynamic-faction-colors: true
special-factions:
  factionNameHere:
    fill: 'red'
    line: 'blue'
    discord: 'https://discord.gg/link'
    icon: 'https://http.cat/200'
    banner: 'https://source.unsplash.com/random'


style:
  # default border settings
  border-color: '#FF0000'
  border-opacity: 0.8
  border-width: 3
  # default fill setting
  fill-color: '#FF0000'
  fill-opacity: 0.35
  # Path to icons on web or a link
  # Town home
  home-icon-enabled: false
  home-icon: assets/house.png

translation:
  and-x-more: '... and %1 more'

# HTML for town popup, placeholders documented in README
# This is a multiline string!
# See the rules for editing this at https://yaml-multiline.info/
popup: |-
  <div class="nationPopup">
    <script>
      (() => {
        const facMembers = [`%members%`];
        const facMemberUUIDs = [`%member_uuids%`];
        const facMemberDisplays = [`%member_display_names%`];
        const leaderUUID = "%leader_uuid%";

        for (let i in facMembers) {
          const memberElem = document.createElement("div");
          memberElem.classList.add("member");
          if (facMemberUUIDs[i] === leaderUUID) {
            memberElem.classList.add("leader");
          }
          const imgElem = document.createElement("img");
          imgElem.src = `https://minotar.net/avatar/${facMemberUUIDs[i]}/100.png`;
          imgElem.classList.add("memberFace");
          memberElem.appendChild(imgElem);

          const displayNameElem = document.createElement("span");
          displayNameElem.classList.add("displayName");
          displayNameElem.innerText = facMemberDisplays[i];
          memberElem.appendChild(displayNameElem);

          if (facMembers[i] !== facMemberDisplays[i]) {
            const userNameElem = document.createElement("span");
            userNameElem.classList.add("userName");
            userNameElem.innerText = facMembers[i];
            memberElem.appendChild(userNameElem);
          }
          document.currentScript.parentElement.querySelector(".members").appendChild(memberElem);
        }
      })();
    </script>
    <span class="title">%name%</span>
    <p class="description">%description%</p>
    <div class="members"></div>
    <div class="balance">$%balance%</div>
  </div>
  
  
```

### Popup placeholders

| Placeholder              | Content                                                                |
|--------------------------|------------------------------------------------------------------------|
| `%name%`                 | Faction name                                                           |
| `%leader%`               | Faction leader username                                                |
| `%leader_uuid%`          | Faction leader uuid
| `%members%`              | Faction members, max count `CONFIG.max-listed-members`, separated by `CONFIG.members-split` |
| `%member_display_names%` | Same as before, but their nicks |
| `%member_uuids%`         | Same as before, but their UUIDs |
| `%mods%`                 | Faction moderators |
| `%membercount%`          | Faction member count |
| `%founded%`              | Faction creation date/time, `dd-MM-yyyy HH:mm:ss` |
| `%description%`          | Faction description |
| `%trusted%`              | Trusted (regular) members (i.e. not Recruits, mods, or admins) |
| `%balance%`              | Faction balance, if enabled |
| `%open%`                 | Is faction open? |
| `%peaceful%`             | Is faction peaceful? true/false |
| `%discord_link%`         | Only settable in config, reads `CONFIG.special-factions.[FACTION_TAG].discord` |
| `%icon_url%`             | Only settable in config, reads `CONFIG.special-factions.[FACTION_TAG].icon` |
| `%banner_url%`           | Only settable in config, reads `CONFIG.special-factions.[FACTION_TAG].banner` |



## Building

```
./gradlew clean build
```

Output in `build/libs/`
