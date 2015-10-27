/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
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

package org.opencms.ui.components.fileselect;

import org.opencms.db.CmsResourceState;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.CmsVfsResourceNotFoundException;
import org.opencms.file.types.I_CmsResourceType;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.ui.components.CmsErrorDialog;
import org.opencms.ui.components.CmsResourceTableProperty;
import org.opencms.util.CmsUUID;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.explorer.CmsExplorerTypeSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;

/**
 * Data container which is used as a data source for VFS file selectors.<p>
 *
 */
public class CmsResourceTreeContainer extends HierarchicalContainer {

    /** Serial version id. */
    private static final long serialVersionUID = 1L;

    /** The logger instance for this class. */
    private static final Log LOG = CmsLog.getLog(CmsResourceTreeContainer.class);

    /** Property which is used to store the CmsResource. */
    public static final String PROPERTY_RESOURCE = "resource";

    /**
     * Create a new instance.<p>
     */
    public CmsResourceTreeContainer() {
        addContainerProperty(CmsResourceTableProperty.PROPERTY_RESOURCE_NAME, String.class, null);
        addContainerProperty(CmsResourceTableProperty.PROPERTY_STATE, CmsResourceState.class, null);
        addContainerProperty(CmsResourceTableProperty.PROPERTY_TYPE_ICON, Resource.class, null);
        addContainerProperty(PROPERTY_RESOURCE, CmsResource.class, null);
    }

    /**
     * Gets the resource for the given item.<p>
     *
     * @param item the item
     *
     * @return the resource
     */
    public static CmsResource getResource(Item item) {

        return (CmsResource)(item.getItemProperty(PROPERTY_RESOURCE).getValue());
    }

    /**
     * Adds an item to the folder tree.<p>
     *
     * @param resource the folder resource
     * @param parentId the parent folder id
     */
    public void addTreeItem(CmsResource resource, CmsUUID parentId) {

        Item resourceItem = getItem(resource.getStructureId());
        if (resourceItem == null) {
            resourceItem = addItem(resource.getStructureId());
        }
        resourceItem.getItemProperty(PROPERTY_RESOURCE).setValue(resource);
        // use the root path as name in case of the root item
        resourceItem.getItemProperty(CmsResourceTableProperty.PROPERTY_RESOURCE_NAME).setValue(
            parentId == null ? resource.getRootPath() : resource.getName());
        resourceItem.getItemProperty(CmsResourceTableProperty.PROPERTY_STATE).setValue(resource.getState());
        I_CmsResourceType type = OpenCms.getResourceManager().getResourceType(resource);
        CmsExplorerTypeSettings settings = OpenCms.getWorkplaceManager().getExplorerTypeSetting(type.getTypeName());
        resourceItem.getItemProperty(CmsResourceTableProperty.PROPERTY_TYPE_ICON).setValue(
            new ExternalResource(CmsWorkplace.getResourceUri(CmsWorkplace.RES_PATH_FILETYPES + settings.getBigIcon())));
        if (resource.isFile()) {
            setChildrenAllowed(resource.getStructureId(), false);
        }
        if (parentId != null) {
            setParent(resource.getStructureId(), parentId);
        }
    }

    /**
     * Initializes the root level of the tree.<p>
     *
     * @param cms the CMS context
     * @param root the root folder
     * @param filter the resource filter
     */
    public void initRoot(CmsObject cms, CmsResource root, CmsResourceFilter filter) {

        addTreeItem(root, null);
        readTreeLevel(cms, root.getStructureId(), filter);
    }

    /**
     * Reads the given tree level.<p>
     * @param cms the CMS context
     * @param parentId the parent id
     * @param filter the resource filter to use
     */
    public void readTreeLevel(CmsObject cms, CmsUUID parentId, CmsResourceFilter filter) {

        try {
            CmsResource parent = cms.readResource(parentId, filter);
            List<CmsResource> children = cms.readResources(parent, filter, false);

            // sets the parent to leaf mode, in case no child folders are present
            setChildrenAllowed(parentId, !children.isEmpty());

            for (CmsResource resource : children) {
                addTreeItem(resource, parentId);
            }
        } catch (CmsException e) {
            CmsErrorDialog.showErrorDialog(e);
            LOG.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Clears the given tree level.<p>
     *
     * @param parentId the parent id
     */
    public void removeChildren(CmsUUID parentId) {

        // create a new list to avoid concurrent modifications
        Collection<?> children = getChildren(parentId);
        // may be null when monkey clicking
        if (children != null) {
            List<Object> childIds = new ArrayList<Object>(children);
            for (Object childId : childIds) {
                removeItemRecursively(childId);
            }
        }
    }

    /**
     * Updates the item for the given structure id.<p>
     *
     * @param cms the CMS context
     * @param id the structure id
     * @param filter the resource filter used for reading the resource
     *
     * @throws CmsException if something goes wrong
     */
    public void update(CmsObject cms, CmsUUID id, CmsResourceFilter filter) throws CmsException {

        try {
            CmsResource resource = cms.readResource(id, filter);

            CmsResource parent = cms.readParentFolder(id);
            CmsUUID parentId = parent.getStructureId();
            Item resourceItem = getItem(id);
            if (resourceItem != null) {
                // use the root path as name in case of the root item
                resourceItem.getItemProperty(CmsResourceTableProperty.PROPERTY_RESOURCE_NAME).setValue(
                    parentId == null ? resource.getRootPath() : resource.getName());
                resourceItem.getItemProperty(CmsResourceTableProperty.PROPERTY_STATE).setValue(resource.getState());
                if (parentId != null) {
                    setParent(resource.getStructureId(), parentId);
                }
            } else {
                addTreeItem(resource, parentId);
            }
        } catch (CmsVfsResourceNotFoundException e) {
            removeItemRecursively(id);
            LOG.debug(e.getLocalizedMessage(), e);
        }
    }

}