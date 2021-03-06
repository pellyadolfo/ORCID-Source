/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.jaxb.model.record_rc2;

import javax.xml.bind.annotation.XmlRootElement;

import org.orcid.jaxb.model.common_rc2.OrcidIdentifier;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlRootElement(name = "application-orcid")
public class ApplicationOrcid extends OrcidIdentifier {
    private static final long serialVersionUID = -7671665742488740027L;

}
