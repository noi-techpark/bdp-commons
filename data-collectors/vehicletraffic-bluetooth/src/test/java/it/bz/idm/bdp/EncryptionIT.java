package it.bz.idm.bdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.bz.idm.bdp.util.EncryptUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext*.xml" })
@WebAppConfiguration
public class EncryptionIT {

	@Autowired
	private EncryptUtil util;

	@Test
	public void testStringEncryption() {
		assertTrue(util.isValid());
		String mac="whatever";
		String encryptedString = util.encrypt(mac);
		assertEquals("73c0611fced8cc3a8c890e649b461ce0a76dd6207b161458887332af2ee97133", encryptedString);
	}
}
