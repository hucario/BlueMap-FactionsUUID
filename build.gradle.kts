plugins {
    java
    id ("com.github.johnrengelman.shadow") version "8.1.1"
    id ("com.modrinth.minotaur") version "2.+"
    id ("io.papermc.hangar-publish-plugin") version "0.0.5"
}

group = "codes.antti"
version = "2.0.0"

repositories {
    mavenCentral()
    maven {
        setUrl ("https://ci.ender.zone/plugin/repository/everything/")
    }
    maven {
        setUrl ("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        setUrl ("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        setUrl ("https://repo.glaremasters.me/repository/towny/")
    }
    maven {
        setUrl ("https://jitpack.io/")
    }
    maven {
        setUrl ("https://repo.mikeprimm.com/")
    }
}

dependencies {
    compileOnly ("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly ("com.palmergames.bukkit.towny:towny:0.98.3.0")
    compileOnly ("com.github.BlueMap-Minecraft:BlueMapAPI:v2.4.0")
    compileOnly ("com.github.TownyAdvanced:SiegeWar:2.4.0")
    compileOnly ("com.massivecraft:Factions:1.6.9.5-U0.6.30")
    compileOnly ("us.dynmap:DynmapCoreAPI:3.4")
}

val javaTarget = 17
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
}

tasks.processResources {
    from("src/main/resources") {
        include("plugin.yml")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        expand (
                "version" to project.version
        )
    }
}

tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8"
    }
}

tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    archiveClassifier.set("nonshadow")
}

tasks.shadowJar {
    archiveClassifier.set("")

    relocate ("com.technicjelle", "codes.antti.bluemaptowny.shadow.jelle")
}