/*
 * RHQ Management Platform
 * Copyright (C) 2005-2012 Red Hat, Inc.
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

package org.rhq.enterprise.server.naming.context;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.event.EventContext;
import javax.naming.event.NamingListener;

/**
 * 
 *
 * @author Lukas Krejci
 */
public class AccessCheckingEventContextDecorator extends AccessCheckingContextDecorator implements EventContext {

    private static final long serialVersionUID = 1L;

    public AccessCheckingEventContextDecorator(String... checkedSchemes) {
        super(checkedSchemes);
    }
        
    public AccessCheckingEventContextDecorator(EventContext original, String... checkedSchemes) {
        super(original, checkedSchemes);
    }
    
    @Override
    protected EventContext getOriginal() {
        return (EventContext) super.getOriginal();
    }
    
    public void addNamingListener(Name target, int scope, NamingListener l) throws NamingException {
        check(target);
        getOriginal().addNamingListener(target, scope, l);
    }

    public void addNamingListener(String target, int scope, NamingListener l) throws NamingException {
        check(target);
        getOriginal().addNamingListener(target, scope, l);
    }

    public void removeNamingListener(NamingListener l) throws NamingException {
        check();
        getOriginal().removeNamingListener(l);
    }

    public boolean targetMustExist() throws NamingException {
        check();
        return getOriginal().targetMustExist();
    }

}
