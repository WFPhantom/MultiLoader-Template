plugins {
    id("net.fabricmc.fabric-loom") apply false
    id("net.neoforged.moddev") apply false
}

group = property("group").toString()
version = property("version").toString()