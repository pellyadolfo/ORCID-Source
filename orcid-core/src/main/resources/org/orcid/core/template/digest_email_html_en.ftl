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
<html>
    <head>
        <meta charset="utf-8">
        <title></title>      
    </head>
    <body style="font-family: Arial, Helvetica; font-size: 14px;">
        <div id="container" style="padding: 5px; color: #494A4C;">
            
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr>
            <p style="font-weight: bold;">Hi John Watson,</p>
            <p>Here's what has happened since the last time you visited ORCID record. <a href="" title="">View all notifications</a></p>
            <div id="inner-container" style="padding: 0 15px;">
                <span style="font-weight: bold;">ORCID</span> would like to let you know
                <ul style="padding: 0 10px;">
                    <li style="list-style-type: none;">We have release a new messaging feature... <a href="" title="read more" style="text-decoration: none; color: #338caf">read more</a>.</li>
                    <li style="list-style-type: none;">The ORCID registry is now available in Orc... <a href="" title="read more" style="text-decoration: none; color: #338caf">read more</a>.</li>
                </ul>                
            </div>
            <div id="inner-container" style="padding: 0 15px">
                
                <div class="item">
                    <p><span style="font-weight: bold;">Test credentials</span> would like to add the following items to your record.</p>
                    <table width="100%" style="margin: 0 15px; max-width: 600px;">                    
                        <tr>
                            <td colspan="0" rowspan="0" style="background: #494A4C; padding: 7px; color: #FFF;  font-size: 14px; font-weight: bold">
                                Works(3)
                            </td>
                            <td colspan="0" rowspan="0" width="100">
                                    <a href="" title="" width="100%" style="background: #338caf; padding: 7px; color: #FFF;  font-size: 14px;  text-decoration: none; display: block; text-align: center; font-weight: bold;"><img src="cloud-upload.png " alt="" width="15" height="15"> Add now</a>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" style="padding: 10px;">
                                <ul style="padding: 0; font-size: 14px;">
                                    <li style="list-style-type: none;"><span style="font-weight: bold">Title of the work</span> (doi: 123446/67654)</li>
                                </ul>
                            </td>
                        </tr>
                    </table>           
                </div>

                <div class="item">
                    <p><span style="font-weight: bold;">Test credentials</span> would like to add the following items to your record.</p>
                    <table width="100%" style="margin: 0 15px; max-width: 600px;">                    
                        <tr>
                            <td colspan="0" rowspan="0" style="background: #494A4C; padding: 7px; color: #FFF;  font-size: 14px; font-weight: bold">
                                Works(3)
                            </td>
                            <td colspan="0" rowspan="0" width="100">
                                    <a href="" title="" width="100%" style="background: #338caf; padding: 7px; color: #FFF;  font-size: 14px;  text-decoration: none; display: block; text-align: center; font-weight: bold;"><img src="cloud-upload.png " alt="" width="15" height="15"> Add now</a>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" style="padding: 10px;">
                                <ul style="padding: 0; font-size: 14px;">
                                    <li style="list-style-type: none;"><span style="font-weight: bold">Title of the work</span> (doi: 123446/67654)</li>
                                </ul>
                            </td>
                        </tr>
                    </table>           
                </div>

                <div class="item">
                    <p><span style="font-weight: bold;">Boston University</span> has added the folling items to your record.</p>
                    <table width="100%" style="margin: 0 15px; max-width: 600px;">
                        <tr>
                            <td colspan="0" rowspan="0" style="background: #494A4C; padding: 7px; color: #FFF;  font-size: 14px; font-weight: bold">
                                Education(1)
                            </td>
                            <td colspan="0" rowspan="0" width="200">
                                    <a href="" title="" width="100%" style="background: #338caf; padding: 7px; color: #FFF;  font-size: 14px;  text-decoration: none; display: block; text-align: center; font-weight: bold;">View on your record</a>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" style="padding: 10px;">
                                <ul style="padding: 0; font-size: 14px;">
                                    <li style="list-style-type: none;"><span style="font-weight: bold">Title of the work</span> (doi: 123446/67654)</li>
                                </ul>
                            </td>
                        </tr>
                    </table>           
                </div>
                <div>
	                <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
		                <@emailMacros.msg "email.common.you_have_received_this_email_opt_out.1" />${baseUri}/account?lang=${locale}.
		            </p>                       
		            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
		               <#include "email_footer_html.ftl"/>
		            </p>
                </div>
            </div>
        </div>        
    </body>
</html>



<!-- Old one just for reference, please remove it after all is done 
<!DOCTYPE html>
<html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
        <div style="padding: 20px; padding-top: 0px;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
                Hi ${emailName},
            </span>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                Here’s what has happened since the last time you visited your ORCID record.
                <a href="${baseUri}/notifications?lang=${locale}">View all notifications</a>.
            </p>            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <ul style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; margin-left: 0;">
                    <#compress>
                    <#if amendedMessageCount gt 0><li>[${amendedMessageCount}] <#if amendedMessageCount == 1>notification<#else>notifications</#if> from ORCID member organizations that added or updated information on your record</li></#if>
                    <#if addActivitiesMessageCount gt 0><li>[${addActivitiesMessageCount}] <#if addActivitiesMessageCount == 1>Request<#else>Requests</#if> to add or update your ORCID record</li></#if>
                    <#if orcidMessageCount gt 0><li>[${orcidMessageCount}] <#if orcidMessageCount == 1>notification<#else>notifications</#if> from ORCID</li></#if>
                    </#compress>
                </ul>
           </p>
           <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
                <@emailMacros.msg "email.common.you_have_received_this_email_opt_out.1" />${baseUri}/account?lang=${locale}.
            </p>                       
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
               <#include "email_footer_html.ftl"/>
            </p>
         </div>
     </body>
 </html>
 -->