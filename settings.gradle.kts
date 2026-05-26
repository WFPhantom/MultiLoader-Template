pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven {
                    name = "Fabric"
                    url = uri("https://maven.fabricmc.net/")
                }
            }
            filter {
                includeGroupAndSubgroups("net.fabricmc")
            }
        }
    }
    plugins{
        id("net.fabricmc.fabric-loom") version providers.gradleProperty("loom_version")
        id("net.neoforged.moddev") version providers.gradleProperty("mdg_version")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val modId = providers.gradleProperty("mod_id").get()

includeBuild("build-logic")

rootDir.listFiles()?.filter {
    it.isDirectory()
            && it.name != "build-logic"
            && (File(it, "build.gradle").exists() || File(it, "build.gradle.kts").exists())
}?.forEach {
    val relativePath = rootDir.toPath().relativize(it.toPath()).toString()
    val projectName = ":$modId-$relativePath"

    include(projectName)
    project(projectName).projectDir = it
}