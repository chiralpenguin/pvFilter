plugins {
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
    id("maven-publish")
}

group = "com.purityvanilla"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.purityvanilla", "pvLib", "1.0")
    compileOnly("com.github.retrooper", "packetevents-spigot", "2.11.1")

    implementation("com.ibm.icu", "icu4j", "68.1")
}

tasks.shadowJar {
    archiveClassifier.set("") // This removes the default "-all" classifier
    archiveFileName.set("pvFilter.jar")
}

val testServerPluginsPath: String by project
tasks {
    val copyToServer by registering(Copy::class, fun Copy.() {
            dependsOn("shadowJar")
            from(layout.buildDirectory.file("libs"))
            include("pvFilter.jar") // Change to "plugin-version.jar" if no shadowing
            into(file(testServerPluginsPath)) // Use the externalized path here
        })
}

val localMavenRepoPath: String by project
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.shadowJar.get()) {
                classifier = "" // Ensure this is different or empty if not using a classifier
            }
        }
    }
    repositories {
        maven {
            name = "local"
            url = uri(localMavenRepoPath) // Local Maven repo path
        }
    }
}
