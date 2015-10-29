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
		<div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-offset-9 col-xs-12">
				
				<input name="term" type="text" class="form-control" ng-model="term" placeholder="What are you looking for?">
				<button class="btn btn-primary" id="search" ng-click="getResults()">Search</button>
			
				<!-- RESULTS -->
				<div ng-show="searchResults != null">
					<h3 class="ng-cloak search-result-head" ng-show="areResults()">${springMacroRequestContext.getMessage("search_results.h3Searchresults")}</h3>
					<table class="ng-cloak table table-striped" ng-show="areResults()">
						<thead>
						<tr>
							<th>${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thGivenname")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thFamilynames")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thOthernames")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thBiography")}</th>
						</tr>
						</thead>
						<tbody>
							<tr ng-repeat='result in results' class="new-search-result">
								<td class='search-result-orcid-id'><a href="{{result['orcid-profile']['orcid-identifier'].uri}}">{{result['orcid-profile']['orcid-identifier'].path}}</td>
								<td>{{result['orcid-profile']['orcid-bio']['personal-details']['given-names'].value}}</td>
								<td>{{result['orcid-profile']['orcid-bio']['personal-details']['family-name'].value}}</td>
								<td>{{concatPropertyValues(result['orcid-profile']['orcid-bio']['personal-details']['other-names']['other-name'], 'value')}}</td>
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
			
