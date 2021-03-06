/*
 * RHQ Management Platform
 * Copyright (C) 2005-2008 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.enterprise.gui.content;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;

import org.rhq.core.clientapi.agent.configuration.ConfigurationUtility;
import org.rhq.core.domain.auth.Subject;
import org.rhq.core.domain.configuration.definition.ConfigurationDefinition;
import org.rhq.core.domain.content.ContentSource;
import org.rhq.core.domain.content.DownloadMode;
import org.rhq.core.gui.util.FacesContextUtility;
import org.rhq.core.util.exception.ThrowableUtil;
import org.rhq.enterprise.gui.util.EnterpriseFacesContextUtility;
import org.rhq.enterprise.server.content.ContentSourceManagerLocal;
import org.rhq.enterprise.server.util.LookupUtil;

public class ContentProviderDetailsUIBean {
    private ContentSource contentSource;

    public ContentSource getContentSource() {
        loadContentSource();
        return this.contentSource;
    }

    public String edit() {
        return "edit";
    }

    public String save() {
        Subject subject = EnterpriseFacesContextUtility.getSubject();
        ContentSourceManagerLocal manager = LookupUtil.getContentSourceManager();

        try {
            manager.updateContentSource(subject, contentSource, true);
            FacesContextUtility.addMessage(FacesMessage.SEVERITY_INFO, "The content source ["
                + contentSource.getName() + "] has been updated.");
        } catch (Exception e) {
            FacesContextUtility.addMessage(FacesMessage.SEVERITY_WARN, "Failed to update content source ["
                + contentSource.getName() + "]", e);
        }

        return "success";
    }

    public String cancel() {
        return "success";
    }

    public String test() {
        ContentSourceManagerLocal manager = LookupUtil.getContentSourceManager();

        try {
            manager.testContentSourceConnection(this.contentSource.getId());
            FacesContextUtility.addMessage(FacesMessage.SEVERITY_INFO,
                    "Test passed - the remote repository for [" + this.contentSource.getName() + "] is available.");
        } catch (Exception e) {
            FacesContextUtility.addMessage(FacesMessage.SEVERITY_WARN,
                    "Test failed - failed to connect to the remote repository for [" + this.contentSource.getName()
                        + "] - check the configuration and make sure the remote repository is up and reachable. Details: "
                        + ThrowableUtil.getAllMessages(e));
        }

        return "success";
    }

    public String sync() {
        // Test the content source connection before proceeding.
        ContentSourceManagerLocal contentSourceManager = LookupUtil.getContentSourceManager();
        try {
            contentSourceManager.testContentSourceConnection(this.contentSource.getId());
        } catch (Exception e) {
            FacesContextUtility.addMessage(FacesMessage.SEVERITY_ERROR,
                    "Failed to connect to the remote repository for [" + this.contentSource.getName()
                        + "] - check the configuration and make sure the remote repository is up and reachable. Details: "
                        + ThrowableUtil.getAllMessages(e));
            return "success";
        }

        Subject subject = EnterpriseFacesContextUtility.getSubject();
        try {
            contentSourceManager.synchronizeAndLoadContentSource(subject, this.contentSource.getId());
            FacesContextUtility.addMessage(FacesMessage.SEVERITY_INFO, "Synchronizing content source ["
                + this.contentSource.getName() + "] now.");
        } catch (Exception e) {
            FacesContextUtility.addMessage(FacesMessage.SEVERITY_WARN,
                "Failed to start the synchronization process for [" + this.contentSource.getName() + "]", e);
        }

        return "success";
    }

    public SelectItem[] getDownloadModes() {
        DownloadMode[] modes = DownloadMode.values();
        SelectItem[] items = new SelectItem[modes.length];
        int i = 0;

        for (DownloadMode mode : modes) {
            items[i++] = new SelectItem(mode.name());
        }

        return items;
    }

    public String getSelectedDownloadMode() {
        return this.contentSource.getDownloadMode().name();
    }

    public void setSelectedDownloadMode(String mode) {
        this.contentSource.setDownloadMode(DownloadMode.valueOf(mode));
    }

    public ConfigurationDefinition getContentSourceTypeConfigurationDefinition() {
        loadContentSource();
        return this.contentSource.getContentSourceType().getContentSourceConfigurationDefinition();
    }

    private void loadContentSource() {
        Integer id = FacesContextUtility.getRequiredRequestParameter("id", Integer.class);
        if (this.contentSource == null || (this.contentSource != null && this.contentSource.getId() != id)) {
            Subject subject = EnterpriseFacesContextUtility.getSubject();
            ContentSourceManagerLocal manager = LookupUtil.getContentSourceManager();
            this.contentSource = manager.getContentSource(subject, id);

            // make sure we load the full CST including the config def
            String cstName = this.contentSource.getContentSourceType().getName();
            this.contentSource.setContentSourceType(manager.getContentSourceType(cstName));

            ConfigurationUtility.normalizeConfiguration(this.contentSource.getConfiguration(), this.contentSource
                .getContentSourceType().getContentSourceConfigurationDefinition());
        }
    }

    public String finishAddMap() {
        FacesContextUtility.addMessage(FacesMessage.SEVERITY_INFO, "Map added.");
        return "success";
    }

    public String finishEditMap() {
        FacesContextUtility.addMessage(FacesMessage.SEVERITY_INFO, "Map updated.");
        return "success";
    }
}