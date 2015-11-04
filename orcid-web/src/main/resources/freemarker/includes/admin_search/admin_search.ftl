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
				        		<button class="btn btn-primary input-sm" id="clear" ng-click="cleanResults()">Clear</button>
				      		</span>
				    	</div>
				  	</div>
				</div>
				
				<!-- RESULTS -->
				<div ng-show="searchResults.numFound == 0" ng-clock>
					<br />
					Nothing to show
				</div>
				<div ng-show="searchResults.numFound > 0" ng-cloak>
					<h3 class="ng-cloak search-result-head">${springMacroRequestContext.getMessage("search_results.h3Searchresults")}</h3>
					<table class="ng-cloak table table-striped">
						<thead>

						<tr>
							<th>${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thGivenname")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thFamilynames")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thCreditname")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thOthernames")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thBiography")}</th>
							<th>Lock <!-- <label>Lock</label><input type="checkbox" ng-model="selectedAllLock" ng-click="checkAllLock()"/> --></th>
							<th>Review <!--<label for="">Review</label><input type="checkbox" ng-model="selectedAllReview" ng-click="checkAllReview()"/>--></th>
						</tr>
						</thead>
						<tbody ng-repeat='result in searchResults.orcidSearchResult' id='{{result.orcidProfile.orcidIdentifier.path}}'>
							<tr ng-hide="hideRow[$index] == true">
									<td>{{result.orcidProfile.orcidIdentifier.path}}</td>
									<td>{{result.orcidProfile.orcidBio.personalDetails.givenNames.content}}</td>
									<td>{{result.orcidProfile.orcidBio.personalDetails.familyName.content}}</td>
									<td>{{result.orcidProfile.orcidBio.personalDetails.creditName}}</td>
									<td>
										<span ng-repeat='otherName in result.orcidProfile.orcidBio.personalDetails.otherNames.otherName'>
											{{otherName.content}}
										</span>
									</td>									
									<td>{{result.orcidProfile.orcidBio.biography.content}}</td>
									<td><input type="checkbox" value="{{$index}}" ng-click="lockResult(result.orcidProfile.orcidIdentifier.path, $index)" ng-model="result.SelectedLock"/></td>
									<td><input type="checkbox" value="{{$index}}" ng-click="reviewResult(result.orcidProfile.orcidIdentifier.path, $index)" ng-model="result.SelectedReview"/></td>
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
			
