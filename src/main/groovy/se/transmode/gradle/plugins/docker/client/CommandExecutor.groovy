package se.transmode.gradle.plugins.docker.client

interface CommandExecutor {

    String executeAndWait(List<String> cmdLine);

}
