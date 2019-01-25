package se.transmode.gradle.plugins.docker.client


import org.junit.Test


class CommandExecutorTest {

    @Test(expected = IllegalArgumentException)
    void testExecuteWithNullArg(){
        new CommandExecutorImpl().executeAndWait(null)
    }

    @Test(expected = IllegalArgumentException)
    void testExecuteWithEmptyArg(){
        new CommandExecutorImpl().executeAndWait([])
    }

}
