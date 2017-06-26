/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.modules

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
@CompileStatic
class PatchExternalModules extends DefaultTask {

    @Internal
    Configuration modulesToPatch

    @Input
    Set<String> getNamesOfModulesToPatch() {
        modulesToPatch.dependencies*.name as Set
    }

    @Internal
    Configuration allModules

    @Input
    Set<String> getFileNamesOfAllModules() {
        allModules.incoming.artifacts*.file*.name as Set
    }

    @Internal
    Configuration coreModules

    @Input
    Set<String> getFileNamesOfCoreModules() {
        coreModules.incoming.artifacts*.file*.name as Set
    }

    @Classpath
    FileCollection getExternalModules() {
        allModules - coreModules
    }

    @OutputDirectory
    File destination

    PatchExternalModules() {
        description = 'Patches the classpath manifests of external modules such as gradle-script-kotlin to match the Gradle runtime configuration.'
        dependsOn { modulesToPatch }
    }

    @TaskAction
    void patch() {
        ((ProjectInternal) project).sync { CopySpec copySpec ->
            copySpec.from(externalModules)
            copySpec.into(destination)
        }

        def rootProject = project.rootProject as ProjectInternal

        new ClasspathManifestPatcher(rootProject, temporaryDir, allModules, namesOfModulesToPatch)
                .writePatchedFilesTo(destination)

        // TODO: Should this be configurable?
        new ExcludeEntryPatcher(rootProject, temporaryDir, allModules, "kotlin-compiler-embeddable")
                .exclude("META-INF/services/java.nio.charset.spi.CharsetProvider")
                .exclude("net/rubygrapefruit/platform/**")
                .writePatchedFilesTo(destination)
    }
}
