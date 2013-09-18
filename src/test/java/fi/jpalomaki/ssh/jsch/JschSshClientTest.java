package fi.jpalomaki.ssh.jsch;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;

import static org.junit.Assert.*;
import fi.jpalomaki.ssh.Result;
import fi.jpalomaki.ssh.SshClient;
import fi.jpalomaki.ssh.SshClientException;
import fi.jpalomaki.ssh.UserAtHost;
import fi.jpalomaki.ssh.jsch.JschSshClient.Options;

/**
 * Tests for {@link JschSshClient}. Tests assume user "test" is available on the local host,
 * and has added the public keys under src/test/resources in his/her ~/.ssh/authorized_keys.
 */
public final class JschSshClientTest {
    
    private final UserAtHost userAtHost = new UserAtHost("test", "localhost");
    
    @Test(expected = SshClientException.class)
    public void testNonExistentPrivateKeyFile() {
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_testos", "ankka");
        sshClient.executeCommand("whoami", userAtHost);
    }
    
    @Test(expected = SshClientException.class)
    public void testIncorrectPrivateKeyPassphrase() {
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test", "ankka2");
        sshClient.executeCommand("whoami", userAtHost);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullCommand() {
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test", "ankka");
        sshClient.executeCommand(null, userAtHost);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCommand() {
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test", "ankka");
        sshClient.executeCommand(" ", userAtHost);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullUserAtHost() {
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test", "ankka");
        sshClient.executeCommand("whoami", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullStdin() {
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test", "ankka");
        sshClient.executeCommand("cat -", null, userAtHost);
    }
    
    @Test
    public void testAutoCreatedKnownHostsFile() throws IOException {
        Options options = new Options(0, 0, 64, 64, Collections.singletonMap("StrictHostKeyChecking", "no"));
        File knownHosts = File.createTempFile("testAutoCreatedKnownHostsFile", "known_hosts", new File("/tmp"));
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test_nopass", null, knownHosts.getAbsolutePath(), options);
        Result result = sshClient.executeCommand("whoami", userAtHost);
        assertEquals(0, result.exitCode);
    }
    
    @Test
    public void testLsAtSlashTmp() {
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test", "ankka");
        Result result = sshClient.executeCommand("cd /tmp; ls -la", userAtHost);
        assertEquals(0, result.exitCode);
        assertFalse(result.stdoutAsText().isEmpty());
        assertTrue(result.stderrAsText().isEmpty());
        System.out.println(result.stdoutAsText());
    }
    
    @Test
    public void testLsAtSlashTmpNoPassphrase() {
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test_nopass", null);
        Result result = sshClient.executeCommand("cd /tmp; ls -la", userAtHost);
        assertEquals(0, result.exitCode);
        assertFalse(result.stdoutAsText().isEmpty());
        assertTrue(result.stderrAsText().isEmpty());
        System.out.println(result.stdoutAsText());
    }
    
    @Test
    public void testNonZeroExitCodeWithStderr() {
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test_nopass", null);
        Result result = sshClient.executeCommand("cat /tmp2/no_such_file_for_sure", userAtHost);
        assertTrue(result.exitCode != 0);
        assertTrue(result.stdoutAsText().isEmpty());
        assertFalse(result.stderrAsText().isEmpty());
        System.out.println(result.stderrAsText());
    }
    
    @Test
    public void testKnownHostsAtDevNullNoStrictHostKeyChecking() {
        Options options = new Options(0, 0, 64, 64, Collections.singletonMap("StrictHostKeyChecking", "no"));
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test_nopass", null, "/dev/null", options);
        Result result = sshClient.executeCommand("whoami", userAtHost);
        assertEquals(0, result.exitCode);
        assertEquals("test", result.stdoutAsText().trim());
    }
    
    @Test
    public void testCatStdin() {
        ByteBuffer stdin = ByteBuffer.wrap("secretÄ".getBytes());
        SshClient sshClient = new JschSshClient("src/test/resources/id_rsa_test", "ankka");
        Result result = sshClient.executeCommand("cat -", stdin, userAtHost);
        assertEquals(0, result.exitCode);
        assertEquals("secretÄ", result.stdoutAsText().trim());
    }
}