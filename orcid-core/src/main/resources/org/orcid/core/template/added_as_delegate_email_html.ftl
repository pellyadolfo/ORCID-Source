<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#import "email_macros.ftl" as emailMacros />
<!DOCTYPE html>
<html>
	<head>
		<title>${subject}</title>
		<!--  Do not remove -->
		<script type="text/javascript" src="${baseUri}/static/javascript/iframeResizer.contentWindow.min.js"></script>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="http://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
			    <@emailMacros.msg "email.common.dear" /> ${emailNameForDelegate}<@emailMacros.msg "email.common.dear.comma" />
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.added_as_delegate.you_have.1" />${grantingOrcidName}<@emailMacros.msg "email.added_as_delegate.you_have.2" /><a href="${baseUri}/${grantingOrcidValue}?lang=${locale}">${baseUri}/${grantingOrcidValue}</a><@emailMacros.msg "email.added_as_delegate.you_have.3" />${grantingOrcidName}<@emailMacros.msg "email.added_as_delegate.you_have.4" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.added_as_delegate.for_a_tutorial" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		    	<@emailMacros.msg "email.added_as_delegate.if_you_have.1" />${grantingOrcidName}<@emailMacros.msg "email.added_as_delegate.if_you_have.2" />${grantingOrcidEmail}<@emailMacros.msg "email.added_as_delegate.if_you_have.3" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; white-space: pre;">
<@emailMacros.msg "email.common.kind_regards" />
<a href="${baseUri}/home?lang=${locale}">${baseUri}/<a/>				
		    </p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
				<@emailMacros.msg "email.common.you_have_received_this_email" />
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>