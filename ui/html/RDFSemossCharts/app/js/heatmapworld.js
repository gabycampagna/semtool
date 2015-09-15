var heatmap;
mapSel = "#heatmap svg g";

function start(data) {
	var dataObject = jQuery.parseJSON( data );
	
	worldData = true;

	if (dataObject.heatDataName != null) 
		heatDataName = dataObject.heatDataName;
	
	for (i = 0; i < dataObject.dataSeries.length; i++) {
		var thisLoc = getThreeLetterCode( dataObject.dataSeries[i].locationId );
		if (thisLoc == "") {
			continue;
		}
		
		dataObject.dataSeries[i].locationId = thisLoc;
		
		rateById.set(dataObject.dataSeries[i].locationId, new HeatLocation(dataObject.dataSeries[i]));
		valueArray.push(dataObject.dataSeries[i].heatValue);
	}
	
	valueArray.sort(function(a, b) { return a - b; });
	setColorScale(valueArray);
	
	initVisualization();

	initSlider('slider', valueArray, updateVisualization);
//	$("#slider").slider("disable");
	initColorChooserAndAddToEndOfHtmlElementWithId("putColorChooserHere", updateVisualization, true);

	updateVisualization();
};

function initVisualization() {
	heatmap = new Datamap({
		element: document.getElementById('heatmap'),
		fills: {
			defaultFill: 'Gray'
		},
		geographyConfig: {
			popupTemplate: function(geo, data) {
				var thisTip = getTipForId(geo.id);
				if (thisTip == null)
					return "<div class='hoverinfo'><strong>" + geo.properties.name + "</strong></div>";
				return "<div class='hoverinfo'><strong>" + thisTip + '</strong></div>';
			}
		}
	});
	
	pan(0, 65, mapSel);
};

function updateHeatmap(domainArray) {
	var updatedColors = {};
	var locations = rateById.values();
	for (var i=0; i<locations.length; i++) {
		updatedColors[locations[i].locationId] = getColorForIdsInRange(locations[i].locationId, domainArray[0], domainArray[1]);;
	};
	
	heatmap.updateChoropleth(updatedColors);

	buildLegend("#legend", 100, 50, 60, 20);
};

function runOutsideApp() {
	start('{"dataSeries":[{"heatValue":250.6255628,"locationId":"PW","paramMap":{"Country_Name":"Palau"}},{"heatValue":286538.04776590003,"locationId":"EG","paramMap":{"Country_Name":"Egypt, Arab Rep."}},{"heatValue":8746.9927335,"locationId":"BJ","paramMap":{"Country_Name":"Benin"}},{"heatValue":341951.60773040005,"locationId":"DK","paramMap":{"Country_Name":"Denmark"}},{"heatValue":647.7201025,"locationId":"KM","paramMap":{"Country_Name":"Comoros"}},{"heatValue":100543.173,"locationId":"EC","paramMap":{"Country_Name":"Ecuador"}},{"heatValue":9241.6278406,"locationId":"TJ","paramMap":{"Country_Name":"Tajikistan"}},{"heatValue":202902.76029270003,"locationId":"PE","paramMap":{"Country_Name":"Peru"}},{"heatValue":258061.5228865,"locationId":"CL","paramMap":{"Country_Name":"Chile"}},{"heatValue":20841.951232,"locationId":"AF","paramMap":{"Country_Name":"Afghanistan"}},{"heatValue":5061.180371,"locationId":"MR","paramMap":{"Country_Name":"Mauritania"}},{"heatValue":16529.9631874,"locationId":"GE","paramMap":{"Country_Name":"Georgia"}},{"heatValue":38648.1541004,"locationId":"GH","paramMap":{"Country_Name":"Ghana"}},{"heatValue":81796.6189857,"locationId":"OM","paramMap":{"Country_Name":"Oman"}},{"heatValue":6624.068037,"locationId":"GN","paramMap":{"Country_Name":"Guinea"}},{"heatValue":434.3863077,"locationId":"TO","paramMap":{"Country_Name":"Tonga"}},{"heatValue":533382.7856761,"locationId":"BE","paramMap":{"Country_Name":"Belgium"}},{"heatValue":199043.6522155,"locationId":"RO","paramMap":{"Country_Name":"Romania"}},{"heatValue":43866.4231669,"locationId":"RS","paramMap":{"Country_Name":"Serbia"}},{"heatValue":33868.9893617,"locationId":"BH","paramMap":{"Country_Name":"Bahrain"}},{"heatValue":1860597.9227634,"locationId":"RU","paramMap":{"Country_Name":"Russian Federation"}},{"heatValue":205522.8712514,"locationId":"CZ","paramMap":{"Country_Name":"Czech Republic"}},{"heatValue":8713.031260200001,"locationId":"HT","paramMap":{"Country_Name":"Haiti"}},{"heatValue":31920.8156483,"locationId":"LV","paramMap":{"Country_Name":"Latvia"}},{"heatValue":7944.1849298,"locationId":"MD","paramMap":{"Country_Name":"Moldova"}},{"heatValue":19636.1864693,"locationId":"NP","paramMap":{"Country_Name":"Nepal"}},{"heatValue":888538.2010253001,"locationId":"ID","paramMap":{"Country_Name":"Indonesia"}},{"heatValue":11805.641286799999,"locationId":"NI","paramMap":{"Country_Name":"Nicaragua"}},{"heatValue":1282719.9548618,"locationId":"MX","paramMap":{"Country_Name":"Mexico"}},{"heatValue":1782.9279027,"locationId":"CF","paramMap":{"Country_Name":"Central African Republic"}},{"heatValue":570591.2661598,"locationId":"SE","paramMap":{"Country_Name":"Sweden"}},{"heatValue":270673.5841615,"locationId":"FI","paramMap":{"Country_Name":"Finland"}},{"heatValue":882.2222222,"locationId":"GD","paramMap":{"Country_Name":"Grenada"}},{"heatValue":833.3333332999999,"locationId":"KN","paramMap":{"Country_Name":"St. Kitts and Nevis"}},{"heatValue":49183.8848175,"locationId":"TZ","paramMap":{"Country_Name":"Tanzania"}},{"heatValue":334.9023621,"locationId":"ST","paramMap":{"Country_Name":"Sao Tome and Principe"}},{"heatValue":220505.68286539998,"locationId":"IQ","paramMap":{"Country_Name":"Iraq"}},{"heatValue":807.0694882,"locationId":"GM","paramMap":{"Country_Name":"Gambia, The"}},{"heatValue":12074.4730018,"locationId":"ML","paramMap":{"Country_Name":"Mali"}},{"heatValue":166.7623236,"locationId":"KI","paramMap":{"Country_Name":"Kiribati"}},{"heatValue":48172.2425173,"locationId":"LT","paramMap":{"Country_Name":"Lithuania"}},{"heatValue":728.6967037000001,"locationId":"VC","paramMap":{"Country_Name":"St. Vincent and the Grenadines"}},{"heatValue":137103.927313,"locationId":"HU","paramMap":{"Country_Name":"Hungary"}},{"heatValue":26312.3993014,"locationId":"UG","paramMap":{"Country_Name":"Uganda"}},{"heatValue":1365.4265555999998,"locationId":"LC","paramMap":{"Country_Name":"St. Lucia"}},{"heatValue":2088.0216241,"locationId":"LS","paramMap":{"Country_Name":"Lesotho"}},{"heatValue":57471.277325099996,"locationId":"UY","paramMap":{"Country_Name":"Uruguay"}},{"heatValue":3032.2394781,"locationId":"MV","paramMap":{"Country_Name":"Maldives"}},{"heatValue":800.5866712000001,"locationId":"WS","paramMap":{"Country_Name":"Samoa"}},{"heatValue":2829192.0391718,"locationId":"FR","paramMap":{"Country_Name":"France"}},{"heatValue":746248.5333333,"locationId":"SA","paramMap":{"Country_Name":"Saudi Arabia"}},{"heatValue":1821.4128728,"locationId":"BT","paramMap":{"Country_Name":"Bhutan"}},{"heatValue":25904.8743123,"locationId":"EE","paramMap":{"Country_Name":"Estonia"}},{"heatValue":47931.929824599996,"locationId":"TM","paramMap":{"Country_Name":"Turkmenistan"}},{"heatValue":537.7777778,"locationId":"DM","paramMap":{"Country_Name":"Dominica"}},{"heatValue":290896.4095437,"locationId":"HK","paramMap":{"Country_Name":"Hong Kong SAR, China"}},{"heatValue":12542.969275200001,"locationId":"BF","paramMap":{"Country_Name":"Burkina Faso"}},{"heatValue":32548.591285900002,"locationId":"CM","paramMap":{"Country_Name":"Cameroon"}},{"heatValue":41119.144923,"locationId":"LY","paramMap":{"Country_Name":"Libya"}},{"heatValue":76139.2503645,"locationId":"BY","paramMap":{"Country_Name":"Belarus"}},{"heatValue":16385.584919,"locationId":"MZ","paramMap":{"Country_Name":"Mozambique"}},{"heatValue":1158.1830538,"locationId":"SB","paramMap":{"Country_Name":"Solomon Islands"}},{"heatValue":49552.5806831,"locationId":"CR","paramMap":{"Country_Name":"Costa Rica"}},{"heatValue":1404306.5360579,"locationId":"ES","paramMap":{"Country_Name":"Spain"}},{"heatValue":17228.443336400003,"locationId":"GA","paramMap":{"Country_Name":"Gabon"}},{"heatValue":35826.9257746,"locationId":"JO","paramMap":{"Country_Name":"Jordan"}},{"heatValue":46212.6,"locationId":"PA","paramMap":{"Country_Name":"Panama"}},{"heatValue":212247.91326829998,"locationId":"KZ","paramMap":{"Country_Name":"Kazakhstan"}},{"heatValue":307871.907186,"locationId":"SG","paramMap":{"Country_Name":"Singapore"}},{"heatValue":2346118.1751943,"locationId":"BR","paramMap":{"Country_Name":"Brazil"}},{"heatValue":99790.1456528,"locationId":"SK","paramMap":{"Country_Name":"Slovak Republic"}},{"heatValue":377739.6228658,"locationId":"CO","paramMap":{"Country_Name":"Colombia"}},{"heatValue":16709.4324027,"locationId":"KH","paramMap":{"Country_Name":"Cambodia"}},{"heatValue":4258.0336153,"locationId":"MW","paramMap":{"Country_Name":"Malawi"}},{"heatValue":1269.117037,"locationId":"AG","paramMap":{"Country_Name":"Antigua and Barbuda"}},{"heatValue":10881.605059399999,"locationId":"AM","paramMap":{"Country_Name":"Armenia"}},{"heatValue":1.7419E7,"locationId":"US","paramMap":{"Country_Name":"United States"}},{"heatValue":186204.65292229998,"locationId":"VN","paramMap":{"Country_Name":"Vietnam"}},{"heatValue":4518.443907399999,"locationId":"TG","paramMap":{"Country_Name":"Togo"}},{"heatValue":55501.5325281,"locationId":"MO","paramMap":{"Country_Name":"Macao SAR, China"}},{"heatValue":13922.2245608,"locationId":"TD","paramMap":{"Country_Name":"Chad"}},{"heatValue":13663.3142797,"locationId":"ZW","paramMap":{"Country_Name":"Zimbabwe"}},{"heatValue":15813.3710632,"locationId":"BW","paramMap":{"Country_Name":"Botswana"}},{"heatValue":4029.9897288,"locationId":"FJ","paramMap":{"Country_Name":"Fiji"}},{"heatValue":415338.50453629997,"locationId":"IR","paramMap":{"Country_Name":"Iran, Islamic Rep."}},{"heatValue":3093.6472935,"locationId":"BI","paramMap":{"Country_Name":"Burundi"}},{"heatValue":19385.3099858,"locationId":"HN","paramMap":{"Country_Name":"Honduras"}},{"heatValue":34253.611098299996,"locationId":"CI","paramMap":{"Country_Name":"Cote d\u0027Ivoire"}},{"heatValue":1552.0,"locationId":"TP","paramMap":{"Country_Name":"Timor-Leste"}},{"heatValue":799534.9633539,"locationId":"TR","paramMap":{"Country_Name":"Turkey"}},{"heatValue":214063.1731876,"locationId":"DZ","paramMap":{"Country_Name":"Algeria"}},{"heatValue":13069.991258299999,"locationId":"SS","paramMap":{"Country_Name":"South Sudan"}},{"heatValue":3857.8211382,"locationId":"ER","paramMap":{"Country_Name":"Eritrea"}},{"heatValue":131400.6350261,"locationId":"AO","paramMap":{"Country_Name":"Angola"}},{"heatValue":1410382.9439728002,"locationId":"KR","paramMap":{"Country_Name":"South Korea"}},{"heatValue":107004.984357,"locationId":"MA","paramMap":{"Country_Name":"Morocco"}},{"heatValue":14135.462555799999,"locationId":"CG","paramMap":{"Country_Name":"Congo, Rep."}},{"heatValue":284582.0231206,"locationId":"PH","paramMap":{"Country_Name":"Philippines"}},{"heatValue":2144338.1850646003,"locationId":"IT","paramMap":{"Country_Name":"Italy"}},{"heatValue":75198.0109652,"locationId":"AZ","paramMap":{"Country_Name":"Azerbaijan"}},{"heatValue":245920.7127565,"locationId":"IE","paramMap":{"Country_Name":"Ireland"}},{"heatValue":237592.274371,"locationId":"GR","paramMap":{"Country_Name":"Greece"}},{"heatValue":869508.1254803,"locationId":"NL","paramMap":{"Country_Name":"Netherlands"}},{"heatValue":34175.832127400005,"locationId":"BO","paramMap":{"Country_Name":"Bolivia"}},{"heatValue":62643.9530218,"locationId":"UZ","paramMap":{"Country_Name":"Uzbekistan"}},{"heatValue":373804.1349118,"locationId":"TH","paramMap":{"Country_Name":"Thailand"}},{"heatValue":54797.6796575,"locationId":"ET","paramMap":{"Country_Name":"Ethiopia"}},{"heatValue":13429.5032849,"locationId":"NA","paramMap":{"Country_Name":"Namibia"}},{"heatValue":7404.4127103,"locationId":"KG","paramMap":{"Country_Name":"Kyrgyz Republic"}},{"heatValue":1871.1873335,"locationId":"CV","paramMap":{"Country_Name":"Cabo Verde"}},{"heatValue":229583.7114904,"locationId":"PT","paramMap":{"Country_Name":"Portugal"}},{"heatValue":131805.1267383,"locationId":"UA","paramMap":{"Country_Name":"Ukraine"}},{"heatValue":1786655.0645095,"locationId":"CA","paramMap":{"Country_Name":"Canada"}},{"heatValue":2026.9395953,"locationId":"LR","paramMap":{"Country_Name":"Liberia"}},{"heatValue":27066.2300091,"locationId":"ZM","paramMap":{"Country_Name":"Zambia"}},{"heatValue":1581.5197055,"locationId":"DJ","paramMap":{"Country_Name":"Djibouti"}},{"heatValue":4583.1988855,"locationId":"ME","paramMap":{"Country_Name":"Montenegro"}},{"heatValue":18344.278252599997,"locationId":"BA","paramMap":{"Country_Name":"Bosnia and Herzegovina"}},{"heatValue":73815.37618460001,"locationId":"SD","paramMap":{"Country_Name":"Sudan"}},{"heatValue":349817.0962065,"locationId":"ZA","paramMap":{"Country_Name":"South Africa"}},{"heatValue":304226.3362703,"locationId":"IL","paramMap":{"Country_Name":"Israel"}},{"heatValue":58728.2323272,"locationId":"GT","paramMap":{"Country_Name":"Guatemala"}},{"heatValue":4348.0,"locationId":"BB","paramMap":{"Country_Name":"Barbados"}},{"heatValue":509964.08493129996,"locationId":"VE","paramMap":{"Country_Name":"Venezuela, RB"}},{"heatValue":23226.1589862,"locationId":"CY","paramMap":{"Country_Name":"Cyprus"}},{"heatValue":1.0360105247908302E7,"locationId":"CN","paramMap":{"Country_Name":"China"}},{"heatValue":326933.0438006,"locationId":"MY","paramMap":{"Country_Name":"Malaysia"}},{"heatValue":12015.9443365,"locationId":"MN","paramMap":{"Country_Name":"Mongolia"}},{"heatValue":74941.183242,"locationId":"LK","paramMap":{"Country_Name":"Sri Lanka"}},{"heatValue":2941885.5374615,"locationId":"UK","paramMap":{"Country_Name":"United Kingdom"}},{"heatValue":17256.754269200002,"locationId":"BN","paramMap":{"Country_Name":"Brunei Darussalam"}},{"heatValue":60936.509778,"locationId":"KE","paramMap":{"Country_Name":"Kenya"}},{"heatValue":30984.7478633,"locationId":"PY","paramMap":{"Country_Name":"Paraguay"}},{"heatValue":63968.9615631,"locationId":"DO","paramMap":{"Country_Name":"Dominican Republic"}},{"heatValue":401646.5831734,"locationId":"AE","paramMap":{"Country_Name":"United Arab Emirates"}},{"heatValue":45730.9452736,"locationId":"LB","paramMap":{"Country_Name":"Lebanon"}},{"heatValue":14308.0942247,"locationId":"GQ","paramMap":{"Country_Name":"Equatorial Guinea"}},{"heatValue":57222.574023199995,"locationId":"HR","paramMap":{"Country_Name":"Croatia"}},{"heatValue":540197.4574435,"locationId":"AR","paramMap":{"Country_Name":"Argentina"}},{"heatValue":7890.1903366999995,"locationId":"RW","paramMap":{"Country_Name":"Rwanda"}},{"heatValue":32962.2611557,"locationId":"CD","paramMap":{"Country_Name":"Congo, Dem. Rep."}},{"heatValue":15578.9168654,"locationId":"SN","paramMap":{"Country_Name":"Senegal"}},{"heatValue":10593.1475269,"locationId":"MG","paramMap":{"Country_Name":"Madagascar"}},{"heatValue":8168.6958699,"locationId":"NE","paramMap":{"Country_Name":"Niger"}},{"heatValue":4892.363979199999,"locationId":"SL","paramMap":{"Country_Name":"Sierra Leone"}},{"heatValue":173818.9322157,"locationId":"BD","paramMap":{"Country_Name":"Bangladesh"}},{"heatValue":246876.3241886,"locationId":"PK","paramMap":{"Country_Name":"Pakistan"}},{"heatValue":436343.6224352,"locationId":"AT","paramMap":{"Country_Name":"Austria"}},{"heatValue":1453770.210672,"locationId":"AU","paramMap":{"Country_Name":"Australia"}},{"heatValue":1405.7641579,"locationId":"SC","paramMap":{"Country_Name":"Seychelles"}},{"heatValue":12616.4210884,"locationId":"MU","paramMap":{"Country_Name":"Mauritius"}},{"heatValue":3228.3728879,"locationId":"GY","paramMap":{"Country_Name":"Guyana"}},{"heatValue":25220.0,"locationId":"SV","paramMap":{"Country_Name":"El Salvador"}},{"heatValue":3852556.169656,"locationId":"DE","paramMap":{"Country_Name":"Germany"}},{"heatValue":13370.191506,"locationId":"AL","paramMap":{"Country_Name":"Albania"}},{"heatValue":11771.7257976,"locationId":"LA","paramMap":{"Country_Name":"Lao PDR"}},{"heatValue":8510.5,"locationId":"BS","paramMap":{"Country_Name":"Bahamas"}},{"heatValue":211816.75824179998,"locationId":"QA","paramMap":{"Country_Name":"Qatar"}},{"heatValue":568508.2623778001,"locationId":"NG","paramMap":{"Country_Name":"Nigeria"}},{"heatValue":500103.0944195,"locationId":"NO","paramMap":{"Country_Name":"Norway"}},{"heatValue":3400.4229361999996,"locationId":"SZ","paramMap":{"Country_Name":"Swaziland"}},{"heatValue":2066902.3973333,"locationId":"IN","paramMap":{"Country_Name":"India"}},{"heatValue":11323.7616235,"locationId":"MK","paramMap":{"Country_Name":"Macedonia, FYR"}},{"heatValue":49416.0556092,"locationId":"SI","paramMap":{"Country_Name":"Slovenia"}},{"heatValue":548003.360279,"locationId":"PL","paramMap":{"Country_Name":"Poland"}},{"heatValue":17071.0044992,"locationId":"IS","paramMap":{"Country_Name":"Iceland"}},{"heatValue":55734.6764347,"locationId":"BG","paramMap":{"Country_Name":"Bulgaria"}},{"heatValue":64330.0386647,"locationId":"MM","paramMap":{"Country_Name":"Myanmar"}},{"heatValue":1022.3719915,"locationId":"GW","paramMap":{"Country_Name":"Guinea-Bissau"}},{"heatValue":4601461.206885099,"locationId":"JP","paramMap":{"Country_Name":"Japan"}}],"heatDataName":"GDPinMil2014"}');
};