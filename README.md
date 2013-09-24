
# Simple SSH client for Java

This library is minimal by design, with minimal dependencies. Only public
key authentication is supported. For more advanced SSH/SCP usage, I recommend
[Ansible](https://github.com/ansible/ansible). You can use this Java library
to invoke Ansible remotely, and have Ansible (playbooks) do the real work. 

## Maven dependency

    TODO: Publish to Sonatype's OSS repo and add pom.xml entry here

## Simple usage

    // Think uname=$(ssh root@example.com uname -a)
    UserAtHost userAtHost = new UserAtHost("root", "example.com");
    SshClient sshClient = new JschSshClient("~/.ssh/id_rsa", "passphrase");
    Result result = sshClient.executeCommand("uname -a", userAtHost);
    String uname = result.stdoutAsText();

## Passing in stdin and using a non-standard port

    // Think echo secret | ssh -p 2020 user@example.com "cat - > secret.txt"
    ByteBuffer stdin = ByteBuffer.wrap("secret".getBytes());
    UserAtHost userAtHost = new UserAtHost("user", "example.com", 2020);
    SshClient sshClient = new JschSshClient("path/to/ssh/private_key", "passphrase");
    sshClient.executeCommand("cat - > secret.txt", stdin, userAtHost);

## Usage with empty passphrase, a custom known hosts file and SSH client options

    Options options = new Options("2s", "30m", "64K", "64K", "StrictHostKeyChecking=no");
    UserAtHost userAtHost = new UserAtHost("root", "example.com");
    SshClient sshClient = new JschSshClient("~/.ssh/id_rsa", null, "/dev/null", options);
    sshClient.executeCommand("sleep 5s", userAtHost);

## Spring configuration (using the c-namespace and property placeholders)

    // META-INF/spring/config.xml:

    <bean id="sshClient" class="fi.jpalomaki.ssh.jsch.JschSshClient"
        c:privateKey="${ssh.privateKey}" c:knownHosts="${ssh.knownHosts}"
        c:passphrase="${ssh.passphrase}" c:options-ref="sshOptions"
    />
    
    <bean id="sshOptions" class="fi.jpalomaki.ssh.jsch.JschSshClient$Options"
        c:connectTimeout="2s" c:sessionTimeout="5m" c:maxStdoutBytes="1M"
        c:maxStderrBytes="1M" c:sshConfig="CompressionLevel=1;TCPKeepAlive=no" />
        
    <context:property-placeholder location="classpath:META-INF/spring/props/ssh-client.properties" />
    
    // META-INF/spring/props/ssh-client.properties:
    
    ssh.knownHosts = /path/to/.ssh/known_hosts
    ssh.privateKey = /path/to/.ssh/id_rsa
    ssh.passphrase = secret

Utilizes the [Jsch](http://www.jcraft.com/jsch) SSH 2 library.
