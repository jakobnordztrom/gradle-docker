/*
 * Copyright 2014 Transmode AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.transmode.gradle.plugins.docker.client
import com.google.common.base.Preconditions
import org.gradle.api.GradleException

class NativeDockerClient implements DockerClient {

    private final List<String> binary
    private CommandExecutor commandExecutor

    NativeDockerClient(List<String> binary) {
        Preconditions.checkArgument((binary && !binary.isEmpty()),  "Docker binary can not be empty or null.")
        this.binary = Collections.unmodifiableList(binary)
        this.commandExecutor = new CommandExecutorImpl()
    }

    void setCommandExecutor(CommandExecutor executor){
        this.commandExecutor = executor
    }

    @Override
    String buildImage(File buildDir, String tag, boolean pull) {
        Preconditions.checkArgument(tag as Boolean,  "Image tag can not be empty or null.")
        List cmdLine = []
        cmdLine.addAll(binary)
        cmdLine.addAll(["build", "--pull=${pull}", "-t", tag, buildDir.toString()])
        return commandExecutor.executeAndWait(cmdLine)
    }

    @Override
    String pushImage(String tag) {
        Preconditions.checkArgument(tag as Boolean,  "Image tag can not be empty or null.")
        List cmdLine = []
        cmdLine.addAll(binary)
        cmdLine.addAll(["push", tag])
        return commandExecutor.executeAndWait(cmdLine)
    }


    @Override
    String run(String tag, String containerName, boolean detached, boolean autoRemove, 
            Map<String, String> env,
            Map<String, String> ports, Map<String, String> volumes, List<String> volumesFrom,
            List<String> links) {
        Preconditions.checkArgument(tag as Boolean,  "Image tag cannot be empty or null.")
        Preconditions.checkArgument(containerName as Boolean,  "Image name cannot be empty or null.")
        Preconditions.checkArgument(env != null,  "Environment map cannot be null.")
        Preconditions.checkArgument(ports != null,  "Exported port map cannot be null.")
        Preconditions.checkArgument(volumes != null,  "Volume map cannot be null.")
        Preconditions.checkArgument(volumesFrom != null,  "Volumes from list cannot be null.")
        Preconditions.checkArgument(links != null,  "Link list cannot be null.")
        Preconditions.checkArgument(!detached || !autoRemove,
            "Cannot set both detached and autoRemove options to true.");

        def detachedArg = detached ? '-d' : ''
        def removeArg = autoRemove ? '--rm' : ''
        List<String> cmdLine = []
        cmdLine.addAll(binary)
        cmdLine.addAll(["run", detachedArg, removeArg, "--name" , containerName])
        cmdLine = appendArguments(cmdLine, env, "--env", '=')
        cmdLine = appendArguments(cmdLine, ports, "--publish")
        cmdLine = appendArguments(cmdLine, volumes, "--volume")
        cmdLine = appendArguments(cmdLine, volumesFrom, "--volumes-from")
        cmdLine = appendArguments(cmdLine, links, "--link")
        cmdLine.add(tag)
        return commandExecutor.executeAndWait(cmdLine)
    }

    private static List<String> appendArguments(List<String> cmdLine, Map<String, String> map, String option,
            String separator = ':') {
        // Add each entry in the map as the indicated argument
        map.each { key, value ->
            cmdLine.add(option)
            cmdLine.add("${key}${separator}${value}")
        }
        return cmdLine
    }

    private static List<String> appendArguments(List<String> cmdLine, List<String> list, String option) {
        // Add each entry in the map as the indicated argument
        list.each {
            cmdLine.add(option)
            cmdLine.add(it)
        }
        return cmdLine
    }

}
