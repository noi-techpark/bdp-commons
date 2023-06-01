// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.airquality;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.jcraft.jsch.ChannelSftp;

import it.bz.idm.bdp.airquality.utils.FileTools;
import it.bz.idm.bdp.airquality.utils.FtpTools;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class CommunicationIT extends AbstractJUnit4SpringContextTests {

	@Autowired
	private Environment env;

	@Autowired
	private DataCom com;

	@Test
	public void testRemoveFiles() throws Exception {
		FtpTools ftp = null;
		try {
			String knownHostsFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.knownhosts"));
			String privateKeyFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.privatekey"));
			String testFolder = env.getRequiredProperty("ftp.folder.remote")
							  + File.separator
							  + env.getRequiredProperty("ftp.folder.remote.rmtests");

			ftp = new FtpTools(knownHostsFile, privateKeyFile, env.getRequiredProperty("ftp.user"),
							   env.getRequiredProperty("ftp.server"), env.getRequiredProperty("ftp.port", Integer.class),
							   env.getRequiredProperty("ftp.pass"),
							   env.getRequiredProperty("ftp.stricthostkeychecking", Boolean.class));

			ChannelSftp c = ftp.connect();
			try {
				c.mkdir(testFolder);
			} catch (Exception e) {
				/*
				 * Ignore mkdir issues, maybe the folder exists already;
				 * ftp-rename fails otherwise nevertheless, hence we can ignore an
				 * expensive check here.
				 */
			}

			/* File1: Set the modification time to something 4 days ago */
			String path = testFolder + File.separator + "testfile1.dat";
			String input = "ST3,...\n";
			InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
			c.put(is, path);
			c.setMtime(path, (int) (System.currentTimeMillis() / 1000L - 4 * 24 * 60 * 60));

			/* File2: Set the modification time to something 1 day ago */
			path = testFolder + File.separator + "testfile2.dat";
			input = "ST5,...\n";
			is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
			c.put(is, path);
			c.setMtime(path, (int) (System.currentTimeMillis() / 1000L - 1 * 24 * 60 * 60));

			/* Delete files that are older than 3 days, if the space on the ftp server is less than ~1TB (=always) */
			com.deleteOldFiles(testFolder, 1000000, 3);

			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list = c.ls(testFolder + "/*");
			boolean found1 = false;
			boolean found2 = false;
			for (ChannelSftp.LsEntry entry : list) {
				if (entry.getFilename().equals("testfile1.dat"))
					found1 = true;
				if (entry.getFilename().equals("testfile2.dat"))
					found2 = true;
			}

			/* File 1 should not be found anymore, whereas file 2 should still be present. */
			assertEquals(found1, false);
			assertEquals(found2, true);
		} finally {
			if (ftp != null)
				ftp.close();
		}
	}

}
