/*
 * Copyright 2016-2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.galleon.cli.config.mvn;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.RemoteRepository;
import org.jboss.galleon.cli.Util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author jdenise@redhat.com
 */
public class MvnSettingsTestCase {

    @Test
    public void test() throws Exception {
        RepositorySystem system = Util.newRepositorySystem();
        MavenConfig config = new MavenConfig();
        InputStream stream = MvnSettingsTestCase.class.getClassLoader().
                getResourceAsStream("settings_cli_test.xml");
        File tmp = File.createTempFile("cli_mvn_test", null);
        tmp.deleteOnExit();
        Files.copy(stream, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        config.setSettings(tmp.toPath());
        MavenMvnSettings settings = new MavenMvnSettings(config, system, null);
        assertEquals(settings.getRepositories().size(), 3);
        boolean seen1 = false;
        boolean seen2 = false;
        boolean seen3 = false;
        for (RemoteRepository remote : settings.getRepositories()) {
            if (remote.getId().equals("repo1")) {
                seen1 = true;
                assertTrue(remote.getUrl().equals("http://repo1"));
            }
            if (remote.getId().equals("repo2")) {
                seen2 = true;
                assertTrue(remote.getUrl().equals("http://repo2"));
            }
            if (remote.getId().equals("repo3")) {
                assertTrue(remote.getUrl().equals("http://repo3"));
                seen3 = true;
            }
            assertNotNull(remote.getAuthentication());
        }
        assertTrue(seen1 && seen2 && seen3);
    }

    @Test
    public void testMirror() throws Exception {
        RepositorySystem system = Util.newRepositorySystem();
        MavenConfig config = new MavenConfig();
        InputStream stream = MvnSettingsTestCase.class.getClassLoader().
                getResourceAsStream("settings_cli_test_mirror.xml");
        File tmp = File.createTempFile("cli_mvn_test", null);
        tmp.deleteOnExit();
        Files.copy(stream, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        config.setSettings(tmp.toPath());
        MavenMvnSettings settings = new MavenMvnSettings(config, system, null);
        assertEquals(settings.getRepositories().size(), 2);
        boolean seen3 = false;
        boolean seenMirror = false;
        for (RemoteRepository remote : settings.getRepositories()) {
            if (remote.getId().equals("repo3")) {
                seen3 = true;
            }
            if (remote.getId().equals("mirror1")) {
                assertTrue(remote.getUrl().equals("http://mirror1"));
                seenMirror = true;
                assertEquals(remote.getMirroredRepositories().size(), 2);
                boolean seen1 = false;
                boolean seen2 = false;
                for (RemoteRepository mirrored : remote.getMirroredRepositories()) {
                    if (mirrored.getId().equals("repo1")) {
                        seen1 = true;
                        assertTrue(mirrored.getUrl(), mirrored.getUrl().equals("http://repo1"));
                    }
                    if (mirrored.getId().equals("repo2")) {
                        seen2 = true;
                        assertTrue(mirrored.getUrl(), mirrored.getUrl().equals("http://repo2"));
                    }
                }
                assertTrue(seen1 && seen2);
            }
        }
        assertTrue(seenMirror && seen3);
    }

    @Test
    public void testMirrorAll() throws Exception {
        RepositorySystem system = Util.newRepositorySystem();
        MavenConfig config = new MavenConfig();
        InputStream stream = MvnSettingsTestCase.class.getClassLoader().
                getResourceAsStream("settings_cli_test_mirror_all.xml");
        File tmp = File.createTempFile("cli_mvn_test", null);
        tmp.deleteOnExit();
        Files.copy(stream, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        config.setSettings(tmp.toPath());
        MavenMvnSettings settings = new MavenMvnSettings(config, system, null);
        assertEquals(settings.getRepositories().size(), 1);

        boolean seenMirror = false;
        for (RemoteRepository remote : settings.getRepositories()) {
            if (remote.getId().equals("mirror1")) {
                assertTrue(remote.getUrl().equals("http://mirror1"));
                seenMirror = true;
                assertEquals(remote.getMirroredRepositories().size(), 3);
                boolean seen1 = false;
                boolean seen2 = false;
                boolean seen3 = false;
                for (RemoteRepository mirrored : remote.getMirroredRepositories()) {
                    if (mirrored.getId().equals("repo1")) {
                        seen1 = true;
                        assertTrue(mirrored.getUrl(), mirrored.getUrl().equals("http://repo1"));
                    }
                    if (mirrored.getId().equals("repo2")) {
                        seen2 = true;
                        assertTrue(mirrored.getUrl(), mirrored.getUrl().equals("http://repo2"));
                    }
                    if (mirrored.getId().equals("repo3")) {
                        seen3 = true;
                        assertTrue(mirrored.getUrl(), mirrored.getUrl().equals("http://repo3"));
                    }
                }
                assertTrue(seen1 && seen2 && seen3);
            }
        }
        assertTrue(seenMirror);
    }

    @Test
    public void testProxy() throws Exception {
        RepositorySystem system = Util.newRepositorySystem();
        MavenConfig config = new MavenConfig();
        InputStream stream = MvnSettingsTestCase.class.getClassLoader().
                getResourceAsStream("settings_cli_test_proxy.xml");
        File tmp = File.createTempFile("cli_mvn_test", null);
        tmp.deleteOnExit();
        Files.copy(stream, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        config.setSettings(tmp.toPath());
        MavenMvnSettings settings = new MavenMvnSettings(config, system, null);
        ProxySelector proxy = settings.getSession().getProxySelector();
        assertEquals(settings.getRepositories().size(), 3);
        for (RemoteRepository remote : settings.getRepositories()) {
            if (remote.getId().equals("repo1")) {
                assertNull(proxy.getProxy(remote));
            }
            if (remote.getId().equals("repo2")) {
                assertNull(proxy.getProxy(remote));
            }
            if (remote.getId().equals("repo3")) {
                Proxy p = proxy.getProxy(remote);
                assertEquals("proxy1", p.getHost());
            }
        }
    }
}