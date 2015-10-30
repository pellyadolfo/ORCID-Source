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
<@public classes=['home'] nav="signin">
<#include "sandbox_warning.ftl"/>
    <form class="form-social-sign-in shibboleth" id="SocialloginForm" ng-enter-submit action="<@orcid.rootPath '/signin/auth'/>" method="post">
        
        <div class="row">
        	<div class="col-md-offset-3 col-md-8 col-sm-offset-3 col-sm-9 col-xs-12 bottomBuffer">
	            <div>
		            <h4>${springMacroRequestContext.getMessage("social.link.you_are")} ${providerId} ${springMacroRequestContext.getMessage("social.link.as")} ${emailId}</h4>
			        <p>	
			        	${springMacroRequestContext.getMessage("social.link.to_finish")} ${providerId} ${springMacroRequestContext.getMessage("social.link.account_to_orcid")}
		            </p>
		            <p>
		            	<i>${springMacroRequestContext.getMessage("social.link.you_will_only")} <a href="http://support.orcid.org" target="_blank" >${springMacroRequestContext.getMessage("social.link.visit_knowledgebase_link")}</a></i>
		            </p>
	            </div>
            </div>
        </div>
        
        
        <div class="row">
        	<div class="col-md-offset-3 col-md-8 col-sm-9 col-sm-offset-3 col-xs-12">
        		<p>
        			<b>${springMacroRequestContext.getMessage("social.link.link_this_account")}</b>
        		</p>
        	</div>
            <@spring.bind "loginForm" />             
            <@spring.showErrors "<br/>" "error" />             
            <#include "/common/browser-checks.ftl" />
            
            <div class="col-md-offset-3 col-md-8 col-sm-9 col-sm-offset-3 col-xs-12">
	            <div class="control-group">
	                <label for="userId" class="control-label">${springMacroRequestContext.getMessage("social.link.email_or_orcid")}</label>
	                <div>                
	                    <input type="text" id="userId" name="userId" value="" placeholder="${springMacroRequestContext.getMessage("social.link.email_or_orcid")}">
	                </div>                    
	            </div>
            </div>
            
            <div class="col-md-offset-3 col-md-8 col-sm-9 col-sm-offset-3 col-xs-12">
	            <div class="control-group password social-password-txt">
	                <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>
	                <div>
	                    <input type="password" id="password" name="password" value="" placeholder="${springMacroRequestContext.getMessage("login.password")}">
	                </div>
	            </div>
            </div>
            
            <div class="col-md-offset-3 col-md-8 col-sm-9 col-sm-offset-3 col-xs-12 bottomBuffer">
	            <div class="control-group password social-password-lnk">
		            <a href="<@orcid.rootPath '/reset-password'/>">${springMacroRequestContext.getMessage("login.reset")}</a>
	            </div>
            </div>
            
            <div class=" col-md-offset-3 col-md-8 col-sm-9 col-sm-offset-3 col-xs-12">
	            <div class="control-group">                    
	                            
	                <button id='form-sign-in-button' class="btn btn-primary social-signin-btn" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button>                
	                <span id="ajax-loader" class="no-visible"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x2 green"></i></span>                
	                
	                <#if (RequestParameters['alreadyClaimed'])??>
	                    <div class="alert"><@spring.message "orcid.frontend.security.already_claimed"/></div>
	                </#if>   
	                <#if (RequestParameters['invalidClaimUrl'])??>
	                    <div class="alert"><@spring.message "orcid.frontend.security.invalid_claim_url"/></div>
	                </#if>
	                                
	            </div>
            </div>              
        </div>
        
        <div class="row">
        	<div class="col-md-offset-3 col-md-8 col-sm-9 col-sm-offset-3 col-xs-12 bottomBuffer">
	            <div class="control-group"> 
	            	${springMacroRequestContext.getMessage("social.link.dont_have_orcid")} <a class="reg" href="<@orcid.rootPath '/register'/>">${springMacroRequestContext.getMessage("social.link.register_now")}</a>
	            </div>            
            </div>
        </div>
        
        <div class="row">
        	<div class="col-md-offset-3 col-md-8 col-sm-9 col-sm-offset-3 col-xs-12">
	            <div class="control-group"> 
	            	<b>${springMacroRequestContext.getMessage("social.link.no_thanks")}</b> <a class="reg" href="<@orcid.rootPath '/signin'/>">${springMacroRequestContext.getMessage("social.link.return_to_signin")}</a>
	            </div>            
            </div>
        </div>
    </form>
</@public>