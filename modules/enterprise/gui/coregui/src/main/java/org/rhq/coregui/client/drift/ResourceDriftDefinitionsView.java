/*
 * RHQ Management Platform
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.rhq.coregui.client.drift;

import org.rhq.core.domain.common.EntityContext;
import org.rhq.core.domain.resource.composite.ResourceComposite;

/**
 * @author Jay Shaughnessy
 */
public class ResourceDriftDefinitionsView extends DriftDefinitionsView {
    public static ResourceDriftDefinitionsView get(ResourceComposite composite) {
        String tableTitle = MSG.view_drift_table_resourceDef();
        EntityContext context = EntityContext.forResource(composite.getResource().getId());
        boolean hasWriteAccess = composite.getResourcePermission().isDrift();
        return new ResourceDriftDefinitionsView(tableTitle, context, hasWriteAccess);
    }

    private ResourceDriftDefinitionsView(String tableTitle, EntityContext context, boolean hasWriteAccess) {
        super(tableTitle, context, hasWriteAccess);
    }
}
