plugins {
    java
    signing
}

group = "gradle"
version.set("1.0")

// tag::configure-signatory[]
signing {
    useGpgCmd()
    sign(configurations.archives.get())
}
// end::configure-signatory[]
