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
package org.orcid.core.profileEvent;

import org.orcid.persistence.jpa.entities.ProfileEventType;

public class ProfileEventResult {

    private ProfileEventType outcome;

    private String orcidId;

    public ProfileEventResult(String orcidId, ProfileEventType outcome) {
        this.setOrcidId(orcidId);
        this.setOutcome(outcome);
    }

    public ProfileEventType getOutcome() {
        return outcome;
    }

    public void setOutcome(ProfileEventType outcome) {
        this.outcome = outcome;
    }

    public String getOrcidId() {
        return orcidId;
    }

    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
    }


}
