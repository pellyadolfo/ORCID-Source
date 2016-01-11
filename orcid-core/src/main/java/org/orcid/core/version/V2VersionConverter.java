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


/**
 * 
 * @author Will Simpson
 * 
 */
public interface V2VersionConverter {

    String getLowerVersion();

    String getUpperVersion();

    V2Convertible downgrade(V2Convertible objectToDowngrade);

    V2Convertible upgrade(V2Convertible objectToUpgrade);

}
