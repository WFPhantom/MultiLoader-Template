plugins {
    `java-library`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
}

base {
    archivesName = "${mod_id}-${project.name}-${minecraft_version}"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(java_version)
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(java_version)
}

repositories {
    mavenCentral()
    // https://docs.gradle.org/current/userguide/declaring_repositories.html#declaring_content_exclusively_found_in_one_repository
    exclusiveContent {
        forRepository {
            maven {
                name = "Sponge"
                url = "https://repo.spongepowered.org/repository/maven-public"
            }
        }
        filter { includeGroupAndSubgroups('org.spongepowered) }
    }
}

sourcesJar {
    from(rootProject.file('LICENSE')) {
        rename { "${it}_${mod_name}" }
    }
}

jar {
    from(rootProject.file('LICENSE')) {
        rename { "${it}_${mod_name}" }
    }

    manifest {
        attributes([
                'Specification-Title'   : mod_name,
                'Specification-Vendor'  : mod_authors,
                'Specification-Version' : project.jar.archiveVersion,
                'Implementation-Title'  : project.name,
                'Implementation-Version': project.jar.archiveVersion,
                'Implementation-Vendor' : mod_authors,
                'Built-On-Minecraft'    : minecraft_version
        ])
    }
}

processResources {
    var expandProps = [
            'version'                      : version,
            'group'                        : project.group, //Else we target the task's group.
            'minecraft_version'            : minecraft_version,
            'minecraft_version_range'      : minecraft_version_range,
            'fabric_loader_version'        : fabric_loader_version,
            'fabric_api_version'           : fabric_api_version,
            'neoforge_version'             : neoforge_version,
            'mod_name'                     : mod_name,
            'mod_authors'                  : mod_authors,
            'mod_id'                       : mod_id,
            'license'                      : license,
            'description'                  : project.description,
            'credits'                      : credits,
            'java_version'                 : java_version,
            'issue_tracker'                : issue_tracker,
            'homepage'                     : homepage,
            'logo'                         : logo,
            'update_json_url'              : update_json_url
    ]

    var jsonExpandProps = expandProps.collectEntries {
        key, value -> [(key): value instanceof String ? value.replace("\n", "\\\\n") : value]
    }

    filesMatching(['META-INF/mods.toml', 'META-INF/neoforge.mods.toml']) {
        expand expandProps
    }

    filesMatching(['pack.mcmeta', 'fabric.mod.json', '*.mixins.json']) {
        expand jsonExpandProps
    }

    inputs.properties(expandProps)
}

publishing {
    publications {
        register('mavenJava', MavenPublication) {
            artifactId base.archivesName.get()
            from components.java
        }
    }
    repositories {
        maven {
            url System.getenv('local_maven_url')
        }
    }
}
