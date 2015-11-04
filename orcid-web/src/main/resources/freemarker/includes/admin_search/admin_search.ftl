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
<@public classes=['home'] >
<div ng-controller="AdminSearchCtrl" id="AdminSearchCtrl">
	<div class="row">
		<div class="col-md-offset-3 col-md-9 col-xs-12">
								
				<div class="row">
					<div class="col-md-12">
						<h2>Admin search tool</h2>
				    	<div class="input-group input-group">
				      		<input name="term" type="text" class="form-control" ng-model="term" placeholder="What are you looking for?">
				      		<span class="input-group-btn">
				        		<button class="btn btn-primary input-sm" id="search" ng-click="getResults()">Search</button>
				      		</span>
				    	</div>
				  	</div>
				</div>
				
				<!-- RESULTS -->
				<div ng-show="searchResults.numFound > 0" ng-cloak>
					<h3 class="ng-cloak search-result-head">${springMacroRequestContext.getMessage("search_results.h3Searchresults")}</h3>
					<table class="ng-cloak table table-striped">
						<thead>
						<tr>
							<th>Select</th>
							<th>${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thGivenname")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thFamilynames")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thOthernames")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thBiography")}</th>
						</tr>
						</thead>
						<tbody>
							<tr>
							</tr>
							<tr ng-repeat='result in searchResults.orcidSearchResult'>
									<td><input type="checkbox" value="{{$index}}"/></td>
									<td>{{result.orcidProfile.orcidIdentifier.path}}</td>
									<td>{{result.orcidProfile.orcidBio.personalDetails.givenNames.content}}</td>
									<td>{{result.orcidProfile.orcidBio.personalDetails.familyName}}</td>
									<td>{{result.orcidProfile.orcidBio.personalDetails.creditName}}</td>
									<td>{{result.orcidProfile.orcidBio.biography.content}}</td>
								
							</tr>
						</tbody>
					</table>
					<div id="show-more-button-container">
						<button id="show-more-button" type="submit" class="ng-cloak btn" ng-click="getMoreResults()" ng-show="areMoreResults">Show more</button>
						<span id="ajax-loader" class="orcid-hide"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
					</div>
					<div id="no-results-alert" class="orcid-hide alert alert-error"><@spring.message "orcid.frontend.web.no_results"/></div>
				</div>	
			
		</div>
	</div>
</div>
</@public>
			
