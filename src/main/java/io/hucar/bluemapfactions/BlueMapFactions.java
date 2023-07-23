// TODO: ADD WAY TOO MANY getLogger().info("your message");


package io.hucar.bluemapfactions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;

import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;

public final class BlueMapFactions extends JavaPlugin {
    private final Map<UUID, MarkerSet> factionMarkerSets = new ConcurrentHashMap<>();
    private Configuration config;

    @Override
    public void onEnable() {
        boolean isFolia = isFolia();

        BlueMapAPI.onEnable((api) -> {
            reloadConfig();
            saveDefaultConfig();
            this.config = getConfig();
            initMarkerSets();

            if (isFolia) {
                Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(this, task -> this.updateMarkers(), 1L,
                        this.config.getLong("update-interval") * 20L);
            } else {
                Bukkit.getScheduler().runTaskTimer(this, this::updateMarkers, 1L,
                        this.config.getLong("update-interval") * 20L);
            }
        });
        BlueMapAPI.onDisable((api) -> {
            if (isFolia) {
                Bukkit.getServer().getGlobalRegionScheduler().cancelTasks(this);
            } else {
                Bukkit.getScheduler().cancelTasks(this);
            }
        });
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void initMarkerSets() {
        BlueMapAPI.getInstance().ifPresent((api) -> {
            factionMarkerSets.clear();
            for (World world : Bukkit.getWorlds()) {
                api.getWorld(world).ifPresent((bmWorld) -> {
                    MarkerSet set = new MarkerSet("Nations");
                    factionMarkerSets.put(world.getUID(), set);
                    bmWorld.getMaps().forEach((map) -> {
                        map.getMarkerSets().put("factions", set);
                    });
                });
            }
        });
    }

    private Color getFillColor(Faction fac) {
        String opacity = String.format("%02X", (int) (this.config.getDouble("style.fill-opacity") * 255));

        if (this.config.getBoolean("dynamic-faction-colors")) {
            String hex = this.config.getString("special-factions." + fac.getTag() + ".fill");
            if (hex != null && !hex.equals("")) {
                return new Color("#" + hex + opacity);
            }
        }

        return new Color(this.config.getString("style.fill-color") + opacity);
    }

    private Color getLineColor(Faction fac) {
        String opacity = String.format("%02X", (int) (this.config.getDouble("style.border-opacity") * 255));

        if (this.config.getBoolean("dynamic-faction-colors")) {
            String hex = this.config.getString("special-factions." + fac.getTag() + ".line");
            if (hex != null && !hex.equals("")) {
                return new Color("#" + hex + opacity);
            }
        }

        return new Color(this.config.getString("style.border-color") + opacity);
    }

    private String fillPlaceholders(String template, Faction fac) {
        String t = template;

        t = t.replace("%name%", fac.getTag());

        FPlayer leader = fac.getFPlayerAdmin();

        if (leader == null) {
            getLogger().info("no leader for " + fac.getTag());
        } else {
            t = t.replace("%leader%", leader.getName());
            t = t.replace("%leader_uuid%", leader.getId());
        }


        int maxMembers = this.config.getInt("max-listed-members");
        String templateSplit = this.config.getString("members-split");

        String[] members = fac.getFPlayers().stream().map(FPlayer::getName).toArray(String[]::new);
        if (members.length >= maxMembers) {
            String[] old = members;
            members = new String[maxMembers + 1];
            System.arraycopy(old, 0, members, 0, maxMembers);
            members[maxMembers] = this.config.getString("translation.and-x-more").replace("%1", String.valueOf(members.length));
        }
        t = t.replace("%members%", String.join(templateSplit, members));

        String[] membersDisplay = fac.getFPlayers().stream().map((r) -> {
            Player p = Bukkit.getPlayer(r.getName());
            if (p == null)
                return r.getName();
            return String.valueOf(p.displayName()).replace("`", "\\`").replace("${", "\\${");
        }).toArray(String[]::new);
        if (membersDisplay.length >= maxMembers) {
            String[] old = membersDisplay;
            membersDisplay = new String[maxMembers + 1];
            System.arraycopy(old, 0, membersDisplay, 0, maxMembers);
            membersDisplay[maxMembers] = this.config.getString("translation.and-x-more").replace("%1", String.valueOf(members.length));
        }
        t = t.replace("%member_display_names%", String.join(templateSplit, membersDisplay));

        String[] membersUUID = fac.getFPlayers().stream().map((r) -> r.getId()).toArray(String[]::new);
        if (membersUUID.length >= maxMembers) {
            String[] old = membersUUID;
            membersUUID = new String[maxMembers];
            System.arraycopy(old, 0, membersUUID, 0, maxMembers);
        }
        t = t.replace("%member_uuids%", String.join(templateSplit, membersUUID));

        t = t.replace("%mods%", String.join(", ",
                fac.getFPlayersWhereRole(Role.MODERATOR).stream().map(FPlayer::getName).toArray(String[]::new)));

        t = t.replace("%membercount%", "" + fac.getFPlayers().size());

        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        t = t.replace("%founded%",
                fac.getFoundedDate() != 0 ? myFormatObj.format(LocalDateTime
                        .ofInstant(Instant.ofEpochMilli(fac.getFoundedDate()), TimeZone.getDefault().toZoneId()))
                        : "N/A");

        t = t.replace("%description%", fac.getDescription());

        t = t.replace("%trusted%", fac.getFPlayersWhereRole(Role.NORMAL).isEmpty() ? "None"
                : fac.getFPlayersWhereRole(Role.NORMAL).stream().map(FPlayer::getName).collect(Collectors.joining(", ")));

        if (Econ.shouldBeUsed()) {
            t = t.replace("%balance%", String.valueOf(Econ.getBalance(fac)));
        }

        t = t.replace("%open%", fac.getOpen() ? "true" : "false");

        t = t.replace("%peaceful%", fac.isPeaceful() ? "true" : "false");

        t = t.replace("%icon_url%", this.config.getString("special-factions." + fac.getTag() + ".icon"));

        t = t.replace("%banner_url%", this.config.getString("special-factions." + fac.getTag() + ".banner"));

        return t;
    }

    private void updateMarkers() {
        BlueMapAPI.getInstance().ifPresent((api) -> {
            Factions FactionsInstance = Factions.getInstance();
            if (FactionsInstance == null) {
                return;
            }
            ArrayList<Faction> factions = FactionsInstance.getAllFactions();

            for (World world : Bukkit.getWorlds()) {
                if (api.getWorld(world.getName()).isEmpty())
                    continue;
                MarkerSet set = factionMarkerSets.get(world.getUID());
                if (set == null)
                    continue;
                Map<String, Marker> markers = set.getMarkers();
                markers.clear();

                for (Faction fac : factions) {
                    Stream<FLocation> allClaims = fac.getAllClaims().stream();

                    List<List<Vector2d>> borders = new ArrayList<>();
                    List<List<Vector2d>> areas = new ArrayList<>();

                    Set<Vector2i> chunksInThisWorld = allClaims
                            .filter((fl) -> fl.getWorld().equals(world))
                            .map((fl) -> new Vector2i(fl.getX(), fl.getZ())).collect(Collectors.toSet());
                    MapUtils.areaToBlockPolygon(chunksInThisWorld, 16, areas, borders);

                    double layerY = this.config.getDouble("style.y-level");
                    String facName = fac.getTag();
                    String townDetails = fillPlaceholders(this.config.getString("popup"), fac);
                    int seq = 0;
                    for (List<Vector2d> area : areas) {
                        ExtrudeMarker chunkMarker = new ExtrudeMarker.Builder()
                                .label(facName)
                                .detail(townDetails)
                                .lineColor(getLineColor(fac))
                                .lineWidth(this.config.getInt("style.border-width"))
                                .fillColor(getFillColor(fac))
                                .depthTestEnabled(false)
                                .shape(new Shape(area), 0, 255)
                                .centerPosition()
                                .build();
                        markers.put("factions." + facName + ".area." + seq, chunkMarker);
                        seq += 1;
                    }

                    Optional<Location> home = Optional.ofNullable(fac.getHome());
                    if (home.isPresent() && home.get().getWorld().equals(world)) {
                        if (this.config.getBoolean("style.home-icon-enabled")) {
                            POIMarker iconMarker = new POIMarker.Builder()
                                    .label(facName)
                                    .detail(townDetails)
                                    .icon(this.config.getString("style.home-icon"), 8, 8)
                                    .position(home.get().getX(), layerY, home.get().getZ())
                                    .build();
                            markers.put("factions." + facName + ".homeIcon", iconMarker);
                        }
                    }
                }
            }
        });
    }
}
