/*
* File   : $Source: /alkacon/cvs/opencms/src/com/opencms/workplace/Attic/CmsButtonSubmit.java,v $
* Date   : $Date: 2003/07/31 13:19:36 $
* Version: $Revision: 1.12 $
*
* This library is part of OpenCms -
* the Open Source Content Mananagement System
*
* Copyright (C) 2001  The OpenCms Group
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* For further information about OpenCms, please see the
* OpenCms Website: http://www.opencms.org 
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package com.opencms.workplace;

import com.opencms.core.CmsException;
import com.opencms.file.CmsObject;
import com.opencms.template.A_CmsXmlContent;

import java.util.Hashtable;

import org.w3c.dom.Element;

/**
 * Class for building workplace submit buttons. <BR>
 * Called by CmsXmlTemplateFile for handling the special XML tag <code>&lt;SUBMITBUTTON&gt;</code>.
 * 
 * @author Michael Emmerich
 * @version $Revision: 1.12 $ $Date: 2003/07/31 13:19:36 $
 * @see com.opencms.workplace.CmsXmlWpTemplateFile
 */

public class CmsButtonSubmit extends A_CmsWpElement {
    
    /**
     * Handling of the special workplace <CODE>&lt;SUBMITBUTTON&gt;</CODE> tags.
     * <P>
     * Reads the code of a submit button from the buttons definition file
     * and returns the processed code with the actual elements.
     * <P>
     * Select boxes can be referenced in any workplace template by <br>
     * <CODE>&lt;SUBMITBUTTON name="..." action="..." alt="..."/&gt;</CODE>
     * 
     * @param cms CmsObject Object for accessing resources.
     * @param n XML element containing the <code>&lt;SUBMITBUTTON&gt;</code> tag.
     * @param doc Reference to the A_CmsXmlContent object of the initiating XLM document.  
     * @param callingObject reference to the calling object.
     * @param parameters Hashtable containing all user parameters <em>(not used here)</em>.
     * @param lang CmsXmlLanguageFile conataining the currently valid language file.
     * @return Processed button.
     * @throws CmsException
     */
    
    public Object handleSpecialWorkplaceTag(CmsObject cms, Element n, A_CmsXmlContent doc, 
            Object callingObject, Hashtable parameters, CmsXmlLanguageFile lang) throws CmsException {
        
        // Read button parameters
        String buttonName = n.getAttribute(C_BUTTON_NAME);
        String buttonAction = n.getAttribute(C_BUTTON_ACTION);
        String buttonValue = n.getAttribute(C_BUTTON_VALUE);
        String buttonStyle = n.getAttribute(C_BUTTON_STYLE);
        String buttonWidth = n.getAttribute(C_BUTTON_WIDTH);
        
        // Get button definition and language values
        CmsXmlWpButtonsDefFile buttondef = getButtonDefinitions(cms);
        buttonValue = lang.getLanguageValue(C_LANG_BUTTON + "." + buttonValue);
        
        // get the processed button.
        String result = buttondef.getButtonSubmit(buttonName, buttonAction, buttonValue, 
                buttonStyle, buttonWidth);
        return result;
    }
}
