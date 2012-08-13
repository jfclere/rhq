/*
 *
 *  * RHQ Management Platform
 *  * Copyright (C) 2005-2012 Red Hat, Inc.
 *  * All rights reserved.
 *  *
 *  * This program is free software; you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation version 2 of the License.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program; if not, write to the Free Software
 *  * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package org.rhq.enterprise.server.plugin.pc.metrics;

import static org.rhq.core.domain.common.composite.SystemSetting.ACTIVE_METRICS_PLUGIN;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.rhq.enterprise.server.auth.SubjectManagerLocal;
import org.rhq.enterprise.server.plugin.pc.ServerPluginEnvironment;
import org.rhq.enterprise.server.plugin.pc.ServerPluginManager;
import org.rhq.enterprise.server.system.SystemManagerLocal;
import org.rhq.enterprise.server.util.LookupUtil;
import org.rhq.enterprise.server.xmlschema.generated.serverplugin.metrics.MetricsPluginDescriptorType;

/**
 * @author John Sanda
 */
public class MetricsServerPluginManager extends ServerPluginManager {

    private final Log log = LogFactory.getLog(this.getClass());

    private Map<String, Class<? extends MetricsServerPluginTestDelegate>> testDelegates =
        new HashMap<String, Class<? extends MetricsServerPluginTestDelegate>>();

    public MetricsServerPluginManager(MetricsServerPluginContainer pc) {
        super(pc);
    }

    public MetricsServerPluginFacet getMetricsServerPluginComponent() {
        Properties sysConfig = getSysConfig();
        String pluginName = sysConfig.getProperty(ACTIVE_METRICS_PLUGIN.getInternalName());

        if (pluginName == null) {
            throw new RuntimeException(ACTIVE_METRICS_PLUGIN + " system configuration property is not set.");
        }

        return (MetricsServerPluginFacet) getServerPluginComponent(pluginName);
    }

    @Override
    public void loadPlugin(ServerPluginEnvironment env, boolean enabled) throws Exception {
        super.loadPlugin(env, enabled);
        MetricsPluginDescriptorType descriptorType = (MetricsPluginDescriptorType) env.getPluginDescriptor();
        String testDelegateClassName = descriptorType.getPluginTestDelegate().getClazz();
        Class<? extends MetricsServerPluginTestDelegate> clazz =
            (Class<? extends MetricsServerPluginTestDelegate>) loadPluginClass(env, testDelegateClassName, true);

        testDelegates.put(env.getPluginKey().getPluginName(), clazz);
    }

    public MetricsServerPluginTestDelegate getTestDelegate(String plugin) {
        Class<? extends MetricsServerPluginTestDelegate> clazz = testDelegates.get(plugin);
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            log.error("Failed to create instance of " + clazz.getName(), e);
            throw new MetricsServerPluginException("Failed to create instance of " + clazz.getName(), e);
        } catch (IllegalAccessException e) {
            log.error("Failed to create instance of " + clazz.getName(), e);
            throw new MetricsServerPluginException("Failed to create instance of " + clazz.getName(), e);
        }
    }

    private Properties getSysConfig() {
        SubjectManagerLocal subjectMgr = LookupUtil.getSubjectManager();
        SystemManagerLocal systemMgr = LookupUtil.getSystemManager();

        return systemMgr.getSystemConfiguration(subjectMgr.getOverlord());
    }

}