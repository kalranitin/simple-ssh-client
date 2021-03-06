package fi.jpalomaki.ssh;

import java.nio.ByteBuffer;

/**
 * Simple SSH client interface. Thread-safe.
 * 
 * @author jpalomaki
 */
public interface SshClient {
    
    /**
     * <p>Executes the given command as the given user on the given host.</p>
     * 
     * <p>Roughly equivalent to: <code>ssh -p &lt;port&gt; &lt;user&gt;@&lt;host&gt; &lt;command&gt;</code>.</p>
     * 
     * @param command Command to execute, not <code>null</code> or empty
     * @param userAtHost User at host (and port), not <code>null</code>
     * @return Result of running the command, never <code>null</code>
     * @throws SshClientException In case of errors
     */
    Result executeCommand(String command, UserAtHost userAtHost) throws SshClientException;
    
    /**
     * <p>Executes the given command as the given user on the given host,
     * passing data to command standard input from the given byte buffer.</p>
     * 
     * <p>Roughly equivalent to: <code>echo &lt;stdin&gt; | ssh -p &lt;port&gt; &lt;user&gt;@&lt;host&gt; &lt;command&gt;</code>.</p> 
     * 
     * @param command Command to execute, not <code>null</code> or empty
     * @param stdin Bytes to pass to command standard input, not <code>null</code>
     * @param userAtHost User at host (and port), not <code>null</code>
     * @return Result of running the command, never <code>null</code>
     * @throws SshClientException In case of errors
     */
    Result executeCommand(String command, ByteBuffer stdin, UserAtHost userAtHost) throws SshClientException;
}
