package it.bz.idm.bdp.airquality.utils;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class FtpTools {

	private static final Logger log = LoggerFactory.getLogger(FtpTools.class.getName());

	JSch jsch = new JSch();
	Session session = null;
	ChannelSftp channel = null;

	public FtpTools(final String knownhosts, final String privatekey, final String user, final String server,
			final int port, final String passphrase, boolean hostKeyChecking) throws JSchException {

		log.debug("Private key file: " + privatekey);
		log.debug("Known hosts file: " + knownhosts);
		jsch.setKnownHosts(knownhosts);

		jsch.addIdentity(privatekey);
		session = jsch.getSession(user, server, port);

		/*
		 * Find the correct encoding for the known hosts key, Jsch defaults to ssh-rsa and does not check
		 * automatically from known_hosts file what decoding must be used.
		 */
		HostKeyRepository hkr = jsch.getHostKeyRepository();
		for (HostKey hk : hkr.getHostKey()) {
			if (hk.getHost().equals(server)) {
				String type = hk.getType();
				session.setConfig("server_host_key", type);
			}
		}

		/*
		 * Security! NEVER disable this on a live environment; "no" is just for testing purposes...
		 * With this enabled, we must check the known-hosts file first, which must contain the SFTP server.
		 * In addition: Use hashed host keys, this is the default in Debian/Ubuntu.
		 */
		session.setConfig("StrictHostKeyChecking", hostKeyChecking ? "yes" : "no");
		session.setConfig("HashKnownHosts", "yes");

		session.setUserInfo(new UserInfo() {

			@Override
			public String getPassphrase() {
				return passphrase;
			}

			@Override
			public String getPassword() {
				return null;
			}

			@Override
			public boolean promptPassword(String message) {
				return false;
			}

			@Override
			public boolean promptPassphrase(String message) {
				return true;
			}

			@Override
			public boolean promptYesNo(String message) {
				return false;
			}

			@Override
			public void showMessage(String message) {
			}
		});
	}

	public ChannelSftp connect() throws JSchException {
		session.connect();
		channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
		return channel;
	}

	public void close() {
		if (channel != null)
			channel.exit();
		if (session != null)
			session.disconnect();
	}

}
