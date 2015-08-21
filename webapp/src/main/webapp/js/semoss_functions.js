					
					var SEMOSS = [];
					
					SEMOSS.DbInfo = function(){};
					
					SEMOSS.DbInfo.prototype.name = null;
					SEMOSS.DbInfo.prototype.serverUrl = null;
					SEMOSS.DbInfo.prototype.dataUrl = null;
					SEMOSS.DbInfo.prototype.insightsUrl = null;
					
					SEMOSS.DbInfo.prototype.setAttributes = function(object){
						this.name = object['name'];
						this.name = object['serverUrl'];
						this.name = object['dataUrl'];
						this.name = object['insightsUrl'];
					}

					SEMOSS.createDatabase = function (knowledgebase, callBackFunction, asynchronous){
						var json = SEMOSS.encodeParamObject(knowledgebase);
						var url = 'databases/create' + json;
						if (asynchronous == undefined){
							asynchronous = true;
						}
						$.ajax({
							type : "POST",
							url : url,
							async: asynchronous,
							success : function(response) {
								var token = SEMOSS.processResponse(response);
								callBackFunction(token);
							}
						});
					}
					
					SEMOSS.updateDatabase = function (knowledgebase, callBackFunction, asynchronous){
						var json = SEMOSS.encodeParamObject(knowledgebase);
						var url = 'databases/create' + json;
						if (asynchronous == undefined){
							asynchronous = true;
						}
						$.ajax({
							type : "POST",
							url : url,
							success : function(response) {
								var token = SEMOSS.processResponse(response);
								callBackFunction(token);
							}
						});
					}
					
					SEMOSS.deleteDatabase = function (id, callBackFunction, asynchronous){
						var url = 'databases/delete/' + id;
						if (asynchronous == undefined){
							asynchronous = true;
						}
						$.ajax({
							type : "POST",
							url : url,
							success : function(response) {
								var token = SEMOSS.processResponse(response);
								callBackFunction(token);
							}
						});
					}
					
					SEMOSS.getDatabase = function (id, callBackFunction, asynchronous){
						var url = 'databases/' + id;
						if (asynchronous == undefined){
							asynchronous = true;
						}
						$.ajax({
							type : "POST",
							url : url,
							success : function(response) {
								var token = SEMOSS.processResponse(response);
								callBackFunction(token);
							}
						});
					}
					
					SEMOSS.listDatabases = function (callBackFunction, asynchronous){
						var url = 'databases/';
						if (asynchronous == undefined){
							asynchronous = true;
						}
						$.ajax({
							type : "POST",
							url : url,
							success : function(response) {
								var token = SEMOSS.processResponse(response);
								callBackFunction(token);
							}
						});
					}


					SEMOSS.processResponse = function(response) {
						var unescapedString = unescape(response);
						var token = JSON.parse(unescapedString);
						SEMOSS.checkErrors(token);
						return token;
					}

					
					SEMOSS.encodeParamObject = function (object){
						var json = encodeURIComponent(JSON.stringify(object));
						var find = '%2F';
						var re = new RegExp(find, 'g');
						json = json.replace(re, '%252F');
						var secondFind = '%5F';
						var re2 = new RegExp(secondFind, 'g');
						json = json.replace(re2, '%255C');
						return json;
					}

					SEMOSS.processResponse = function(response) {
						var unescapedString = unescape(response);
						var token = JSON.parse(unescapedString);
						SEMOSS.checkErrors(token);
						return token;
					}


/**
					 * Checks for errors and warnings in a communication token
					 * that came back from the server after a REST call.
					 * @return False if the operation was successful, even with warnings
					 * or true if it did in fact fail.
					 */
					SEMOSS.processAnyFailures = function (token, successMessage){
						var status = token.result;
						if (token.result == DTConstants.SUCCESS){
							$('#success_message_panel').show();
							$('#failure_message_panel').hide();
							$('#warning_message_panel').hide();
							$('#success_message_area').html(successMessage);
							setTimeout(function(){ $('#success_message_panel').hide({'duration':1000}) }, 2000);
							console.log(successMessage);
							return false;
						}
						else if (token.result  == DTConstants.SUCCESS_WITH_WARNINGS){
							$('#warning_message_panel').show();
							$('#failure_message_panel').hide();
							$('#success_message_panel').hide();
							$('#warning_message_area').html(token.message);
							setTimeout(function(){ $('#warning_message_panel').hide({'duration':1000}) }, 2000);
							console.log(successMessage);
							return false;					
						}
						else if (token.result  == DTConstants.FAILURE){
							$('#failure_message_panel').show();
							$('#warning_message_panel').hide();
							$('#success_message_panel').hide();
							$('#failure_message_area').html(token.message);
							setTimeout(function(){ $('#failure_message_panel').hide({'duration':1000}) }, 2000);
							console.log(successMessage);
							return true;
						}
					}
					
					SEMOSS.menuVisible = true;
					
					SEMOSS.toggleNavMenu = function (){
						if (SEMOSS.menuVisible){
							$('#dt_navigation_menu').hide(200);
							$('#expand_border').removeClass('glyphicon-menu-left');
							$('#expand_border').addClass('glyphicon-menu-right');
							SEMOSS.menuVisible = false;
						}
						else {
							$('#dt_navigation_menu').show(200);
							$('#expand_border').removeClass('glyphicon-menu-right');
							$('#expand_border').addClass('glyphicon-menu-left');
							SEMOSS.menuVisible = true;
						}
					}
					
					SEMOSS.showPanel = function(id) {
						$('.main_div').each(function() {
							$(this).hide(200);
						});
						$('#' + id).show(200);
						
						$('.admin_nav_item').each(function() {
							$(this).removeClass('active');
						});
						$('#' + id + "_nav").addClass('active');
					}
					
					SEMOSS.showNavMenu = function(navmenuID, buttonID){
						$('.semoss_nav_menu_btn').each(function() {
							$(this).removeClass('btn-info');
						});
						$('.semoss_nav_menu').each(function() {
							$(this).hide();
						});
						$('#' + buttonID).addClass('btn-info');
						$('#' + navmenuID).show();
					}
					
					$(document).ready(function (){
						$('#success_message_panel').hide();
						$('#warning_message_panel').hide();
						$('#failure_message_panel').hide();
						$('.main_div').each(function() {
							$(this).hide();
						});

						
						// Set the set of nav items 
						$('.semoss_nav_menu_btn').each(function() {
							$(this).removeClass('btn-info');
						});
						
						
						// Set the active nave item (Organizations)
						$('.admin_nav_item').each(function() {
							$(this).removeClass('active');
						});
						$('#dtorganizations_nav').addClass('active');
						$('#dt_organizations').hide();
						
					});