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
package org.orcid.core.version;

import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrcidMessageVersionConverterChain {

    OrcidMessage downgradeMessage(OrcidMessage orcidMessage, String requiredVersion);

    OrcidMessage upgradeMessage(OrcidMessage orcidMessage, String requiredVersion);

    public int compareVersion(String v1, String v2);
}
