/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/xml/containerpage/Attic/CmsContainerElementBean.java,v $
 * Date   : $Date: 2009/10/26 10:45:13 $
 * Version: $Revision: 1.1.2.4 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.xml.containerpage;

import org.opencms.main.OpenCms;
import org.opencms.util.CmsUUID;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * One element of a container in a container page.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1.2.4 $ 
 * 
 * @since 7.6 
 */
public class CmsContainerElementBean {

    /** The client id including properties-hash. */
    private transient String m_clientId;

    /** The element's structure id. */
    private CmsUUID m_elementId;

    /** The formatter's structure id. */
    private CmsUUID m_formatterId;

    /** The configured properties. */
    private Map<String, String> m_properties;

    /**
     * Creates a new container page element bean.<p> 
     *  
     * @param elementId the element's structure id
     * @param formatterId the formatter's structure id, could be <code>null</code>
     * @param properties the properties as a map of name/value pairs
     **/
    public CmsContainerElementBean(CmsUUID elementId, CmsUUID formatterId, Map<String, String> properties) {

        m_elementId = elementId;
        m_formatterId = formatterId;
        m_properties = (properties == null ? new HashMap<String, String>() : properties);
        m_properties = Collections.unmodifiableMap(m_properties);
        m_clientId = OpenCms.getADEManager().convertToClientId(m_elementId);
        if (!m_properties.isEmpty()) {
            int hash = m_properties.toString().hashCode();
            m_clientId += "#" + hash;
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof CmsContainerElementBean)) {
            return false;
        }
        return getClientId().equals(((CmsContainerElementBean)obj).getClientId());
    }

    /**
     * Returns the client side id including the property-hash.<p>
     * 
     * @return the id
     */
    public String getClientId() {

        return m_clientId;
    }

    /**
     * Returns the element's structure id.<p>
     *
     * @return the element's structure id
     */
    public CmsUUID getElementId() {

        return m_elementId;
    }

    /**
     * Returns the formatter's structure id.<p>
     *
     * @return the formatter's structure id
     */
    public CmsUUID getFormatterId() {

        return m_formatterId;
    }

    /**
     * Returns the configured properties.<p>
     * 
     * @return the configured properties
     */
    public Map<String, String> getProperties() {

        return m_properties;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return m_clientId.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return getClientId();
    }
}
