package se.transmode.gradle.plugins.docker.client

import org.junit.Before
import org.junit.Test

import static java.util.Collections.EMPTY_LIST
import static java.util.Collections.EMPTY_MAP
import static org.mockito.Mockito.*


class NativeDockerClientTest {

    NativeDockerClient client
    CommandExecutor mockExecutor

    @Before
    void before(){
        mockExecutor = mock(CommandExecutor.class)
     }

    @Test(expected = IllegalArgumentException)
    void testConstructorWithEmptyBinary(){
        new NativeDockerClient(EMPTY_LIST)
    }

    @Test(expected = IllegalArgumentException)
    void testConstructorWithNullBinary(){
        new NativeDockerClient(null)
    }

    @Test
    void testBuild(){
        client = new NativeDockerClient(["docker"])
        client.commandExecutor = mockExecutor
        client.buildImage(new File(""), 'tag', false)
        verify(mockExecutor).executeAndWait(anyList())

    }

    @Test
    void testPush(){
        client = new NativeDockerClient(["docker", "-H", "1.2.3"])
        client.commandExecutor = mockExecutor
        client.pushImage( 'tag')
        verify(mockExecutor).executeAndWait(["docker", "-H", "1.2.3", "push", "tag"])
    }

    @Test
    void testRun(){
        client = new NativeDockerClient(["docker"])
        client.commandExecutor = mockExecutor
        client.run('tag', 'cname',false, false, EMPTY_MAP, EMPTY_MAP, EMPTY_MAP, EMPTY_LIST, EMPTY_LIST)
        verify(mockExecutor).executeAndWait(anyList())
    }

}
