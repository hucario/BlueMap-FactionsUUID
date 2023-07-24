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
max-listed-members: 3

# Divider between eachMemberHTML in %membersHTML%
members-split: ''


dynamic-faction-colors: true
special-factions:
  TestFac:
    fill: '#0891b2'
    line: '#006c86'
    discord: 'https://discord.gg/TP5GYwf'
    icon: 'https://www.globia.net/favicon.png'
    banner: 'https://images.unsplash.com/photo-1689333131663-6bda0b18e257?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxfDB8MXxyYW5kb218MHx8fHx8fHx8MTY5MDE2OTczMw&ixlib=rb-4.0.3&q=80&w=1080'


style:
  # default border settings
  border-color: '#FF0000'
  border-opacity: 0.8
  border-width: 3
  # default fill setting
  fill-color: '#FF0000'
  fill-opacity: 0.35
  # How high should the marker be?
  min-y-height: -64
  max-y-height: 150
  # Is the marker obstructed by things in its way?
  depth-test: true
  home-icon-enabled: false
  # Path to icon on web or a link
  home-icon: assets/house.png

translation:
  and-x-more: '<div class="xMoreMembers">+%1 more</div>'

bannerHTML: '<img class="popupBanner" src="%banner_url%">'
iconHTML: '<img class="popupIcon" src="%icon_url%">'
# This is a multiline string!
# See the rules for editing this at https://yaml-multiline.info/
discordHTML: |-
  <a class="discordLink" href="%discord_link%">
    <img class="discordImg" src="assets/img/discordIcon.svg">
    <span>Faction Discord</span>
  </a>
  
  
# This is hooked into PlaceholderAPI.
# This is a multiline string!
# See the rules for editing this at https://yaml-multiline.info/
eachMemberHTML: |-
  <div class="member %is_leader%">
    <img src="https://minotar.net/avatar/%player_uuid%/100.png" class="memberFace">
    <span class="displayName">%essentials_nickname_stripped%</span>
    <span class="userName">%player_name%</span>
  </div>
  
  


# HTML for faction popup, placeholders documented in README
# This is a multiline string!
# See the rules for editing this at https://yaml-multiline.info/
popup: |-
  <div class="nationPopup">
    %bannerHTML%
    <div class="iconGroup">
      %iconHTML%
      <div class="rightGroup">
        <span class="title">%name%</span>
        <p class="description">“%description%”</p>
      </div>
    </div>
    <label class="membersLabel">Members</label>
    <div class="members">
      %membersHTML%
    </div>
    <div class="buttonsRow">
      %discordHTML%
      <div class="balance">
        <span class="actualBalance">$%balance%</span>
      </div>
    </div>
  </div>
  
  
```

### Popup placeholders

| Placeholder              | Content                                                                          |
|--------------------------|----------------------------------------------------------------------------------|
| `%name%`                 | Faction name                                                                     |
| `%leader%`               | Faction leader username                                                          |
| `%leader_uuid%`          | Faction leader uuid                                                              |
| `%membercount%`          | Faction member count                                                             |
| `%founded%`              | Faction creation date/time, `dd-MM-yyyy HH:mm:ss`                                |
| `%description%`          | Faction description                                                              |
| `%trusted%`              | Trusted (regular) members (i.e. not Recruits, mods, or admins)                   |
| `%balance%`              | Faction balance, if enabled                                                      |
| `%open%`                 | Is faction open?                                                                 |
| `%peaceful%`             | Is faction peaceful? true/false                                                  |
| `%bannerHTML%`           | Set in config, reads `CONFIG.bannerHTML`, ONLY IF %banner_url% NOT FALSE         |
| `%iconHTML%`             | Set in config, reads `CONFIG.iconHTML`, ONLY IF %icon_url% NOT FALSE             |
| `%discordHTML%`          | Set in config, reads `CONFIG.discordHTML`, ONLY IF %discord_url% NOT FALSE       |
| `%discord_link%`         | Only settable in config, reads `CONFIG.special-factions.[FACTION_TAG].discord`   |
| `%icon_url%`             | Only settable in config, reads `CONFIG.special-factions.[FACTION_TAG].icon`      |
| `%banner_url%`           | Only settable in config, reads `CONFIG.special-factions.[FACTION_TAG].banner`    |



## Building

```
./gradlew clean build
```

Output in `build/libs/`
