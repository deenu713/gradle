/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    java
}

version.set("1.0.2")
group = "org.gradle.sample"

repositories {
    mavenCentral()
}

// tag::configure-suite-dependencies[]
testing {
    suites {
        val test by getting(JvmTestSuite::class) { // <1>
            dependencies {
                implementation("org.assertj:assertj-core:3.21.0") // <2>
            }
        }
    }
}

// Note that this is equivalent to:
dependencies {
    testImplementation("org.assertj:assertj-core:3.21.0")
}
// tag::configure-suite-dependencies[]
