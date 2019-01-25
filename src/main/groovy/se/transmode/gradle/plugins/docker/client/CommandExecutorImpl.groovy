package se.transmode.gradle.plugins.docker.client

import com.google.common.base.Preconditions
import org.gradle.api.GradleException

class CommandExecutorImpl implements CommandExecutor {

    String executeAndWait(List<String> cmdLine) {
        Preconditions.checkArgument((cmdLine && !cmdLine.isEmpty()),  "Docker binary can not be empty or null.")
        def process = cmdLine.execute()
        process.waitForProcessOutput(System.out, System.err)
        if (process.exitValue()) {
            throw new GradleException("Docker execution failed\nCommand line [${cmdLine}]")
        }
        return "Done"
    }

}
