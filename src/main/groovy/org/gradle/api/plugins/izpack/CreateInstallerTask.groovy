/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.plugins.izpack

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

/**
 * IzPack compilation task.
 *
 * @author Benjamin Muschko
 */
@Slf4j
class CreateInstallerTask extends DefaultTask {
    @InputFiles FileCollection classpath
    @InputDirectory @Optional File baseDir
    String installerType
    @InputFile File installFile
    @OutputFile File outputFile
    String compression
    Integer compressionLevel
    Map appProperties

    @TaskAction
    void start() {
        validateConfiguration()
        compile()
    }

    void validateConfiguration() {
        if(getInstallerType() && !InstallerType.getInstallerTypeForName(getInstallerType())) {
            throw new InvalidUserDataException("Unsupported installer type: '${getInstallerType()}'. Please pick a valid one: ${InstallerType.getNames()}")
        }
        else {
            log.info "Installer type = ${getInstallerType()}"
        }

        if(getCompression() && !Compression.getCompressionForName(getCompression())) {
            throw new InvalidUserDataException("Unsupported compression: '${getCompression()}'. Please pick a valid one: ${Compression.getNames()}")
        }
        else {
            log.info "Compression = ${getCompression()}"
        }

        if(getCompressionLevel() && (getCompressionLevel() < -1 || getCompressionLevel() > 9)) {
            throw new InvalidUserDataException("Unsupported compression level: ${getCompressionLevel()}. Please pick a value between -1 and 9!")
        }
        else {
            log.info "Compression level = ${getCompressionLevel()}"
        }
    }

    void compile() {
        log.info "Starting to create IzPack installer from base directory '${getBaseDir().canonicalPath}' and install file '${getInstallFile().canonicalPath}'."

        //ant.taskdef(name: 'izpack', classpath: getClasspath().asPath, classname: 'com.izforge.izpack.ant.IzPackTask')

        getAppProperties().entrySet().each {
            ant.property(name: it.key, value: it.value)
        }

        ant.izpack(input: getInstallFile().canonicalPath,
				   basedir: getBaseDir().canonicalPath,
                   output: getOutputFile().canonicalPath,
                   installerType: getInstallerType(),
                   compression: getCompression(),
                   compressionlevel: getCompressionLevel(),
				   inheritAll: 'true') 
       
        log.info("Finished creating IzPack installer.")
    }
}
