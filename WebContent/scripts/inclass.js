(function() {
	var user_id = '1111';
	var user_fullname = 'John';
	var lng = -122.08;
	var lat = 37.38;

		init();
		function init() {
	    // register event listeners
		/******* step 18 : add login *******/
	    document.querySelector('#login-form-btn').addEventListener('click', onSessionInvalid);
	    document.querySelector('#login-btn').addEventListener('click', login);
		
	    /******* step 20 : add register *******/
	    document.querySelector('#register-form-btn').addEventListener('click', showRegisterForm);
	    document.querySelector('#register-btn').addEventListener('click', register);

		/******* step 16 : add nearby, favorite, recommend *******/
	    document.querySelector('#nearby-btn').addEventListener('click', loadNearbyItems);
	    document.querySelector('#fav-btn').addEventListener('click', loadFavoriteItems);
	    document.querySelector('#recommend-btn').addEventListener('click', loadRecommendedItems);
	    validateSession();
		
		/******* step 5.1 : valid session *******/
	   //  onSessionValid({"user_id":"1111","name":"John Smith","status":"OK"});
	  }
		function validateSession() {
		    onSessionInvalid();
		    // The request parameters
		    var url = './login';
		    var req = JSON.stringify({});

		    // display loading message
		    showLoadingMessage('Validating session...');

		    // make AJAX call
		    ajax('GET', url, req,
		      // session is still valid
		      function(res) {
		        var result = JSON.parse(res);

		        if (result.status === 'OK') {
		          onSessionValid(result);
		        }
		      }, function() {
		    	  console.log("invalidate");
		      });
		  }

		  function onSessionValid(result) {
		    user_id = result.user_id;
		    user_fullname = result.name;

		    var loginForm = document.querySelector('#login-form');
		    var registerForm = document.querySelector('#register-form');
		    var itemNav = document.querySelector('#item-nav');
		    var itemList = document.querySelector('#item-list');
		    var avatar = document.querySelector('#avatar');
		    var welcomeMsg = document.querySelector('#welcome-msg');
		    var logoutBtn = document.querySelector('#logout-link');

		    welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;

		    showElement(itemNav);
		    showElement(itemList);
		    showElement(avatar);
		    showElement(welcomeMsg);
		    showElement(logoutBtn, 'inline-block');
		    hideElement(loginForm);
		    hideElement(registerForm);

		/******* step 7: init Geolocation *******/
		    initGeoLocation();
		  }

		  function onSessionInvalid() {
		    var loginForm = document.querySelector('#login-form');
		    var registerForm = document.querySelector('#register-form');
		    var itemNav = document.querySelector('#item-nav');
		    var itemList = document.querySelector('#item-list');
		    var avatar = document.querySelector('#avatar');
		    var welcomeMsg = document.querySelector('#welcome-msg');
		    var logoutBtn = document.querySelector('#logout-link');

		    hideElement(itemNav);
		    hideElement(itemList);
		    hideElement(avatar);
		    hideElement(logoutBtn);
		    hideElement(welcomeMsg);
		    hideElement(registerForm);

		    clearLoginError();
		    showElement(loginForm);
		  }

		  function hideElement(element) {
		    element.style.display = 'none';
		  }

		  function showElement(element, style) {
		    var displayStyle = style ? style : 'block';
		    element.style.display = displayStyle;
		  }
		 
		 /******* step 8: define init Geolocation function*******/
		  function initGeoLocation() {
			    if (navigator.geolocation) {
			      navigator.geolocation.getCurrentPosition(
			   /******* step 8.1: onPositionUpdated function*******/
			        onPositionUpdated,
			   /******* step 8.2: onLoadPositionFailed function*******/
			        onLoadPositionFailed, {
			          maximumAge: 60000
			        });
			      showLoadingMessage('Retrieving your location...');
			    } else {
			      onLoadPositionFailed();
			    }
			  }
		  /******* step 8.3: define onPositionUpdated function*******/
			  function onPositionUpdated(position) {
			    lat = position.coords.latitude;
			    lng = position.coords.longitude;

		/******* step 9: loadNearbyItems function*******/
			    loadNearbyItems();
			  }
	    /******* step 8.4: define onLoadPositionFailed function*******/
			  function onLoadPositionFailed() {
			    console.warn('navigator.geolocation is not available');
	    /******* step 8.5: getLocationFromIP function*******/
			    getLocationFromIP();
			  }

	/******* step 8.6: define getLocationFromIP function*******/
			  function getLocationFromIP() {
			    // get location from http://ipinfo.io/json
			    var url = 'http://ipinfo.io/json'
			    var data = null;

			    ajax('GET', url, data, function(res) {
			      var result = JSON.parse(res);
			      if ('loc' in result) {
			        var loc = result.loc.split(',');
			        lat = loc[0];
			        lng = loc[1];
			      } else {
			        console.warn('Getting location by IP failed.');
			      }
			      loadNearbyItems();
			    });
			  }

	  // -----------------------------------
	  // Helper Functions
	  // -----------------------------------

	  /**
	   * A helper function that makes a navigation button active
	   *
	   * @param btnId - The id of the navigation button
	   */
	  function activeBtn(btnId) {
	    var btns = document.querySelectorAll('.main-nav-btn');

	    // deactivate all navigation buttons
	    for (var i = 0; i < btns.length; i++) {
	      btns[i].className = btns[i].className.replace(/\bactive\b/, '');
	    }

	    // active the one that has id = btnId
	    var btn = document.querySelector('#' + btnId);
	    btn.className += ' active';
	  }

	  function showLoadingMessage(msg) {
	    var itemList = document.querySelector('#item-list');
	    itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> ' +
	      msg + '</p>';
	  }

	  function showWarningMessage(msg) {
	    var itemList = document.querySelector('#item-list');
	    itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> ' +
	      msg + '</p>';
	  }

	  function showErrorMessage(msg) {
	    var itemList = document.querySelector('#item-list');
	    itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> ' +
	      msg + '</p>';
	  }

	  /**
	   * A helper function that creates a DOM element <tag options...>
	   * @param tag
	   * @param options
	   * @returns {Element}
	   */
	  function $create(tag, options) {
	    var element = document.createElement(tag);
	    for (var key in options) {
	      if (options.hasOwnProperty(key)) {
	        element[key] = options[key];
	      }
	    }
	    return element;
	  }

	  /**
	   * AJAX helper
	   *
	   * @param method - GET|POST|PUT|DELETE
	   * @param url - API end point
	   * @param data - request payload data
	   * @param successCallback - Successful callback function
	   * @param errorCallback - Error callback function
	   */
	  function ajax(method, url, data, successCallback, errorCallback) {
	    var xhr = new XMLHttpRequest();

	    xhr.open(method, url, true);

	    xhr.onload = function() {
	      if (xhr.status === 200) {
	        successCallback(xhr.responseText);
	      } else {
	        errorCallback();
	      }
	    };

	    xhr.onerror = function() {
	      console.error("The request couldn't be completed.");
	      errorCallback();
	    };

	    if (data === null) {
	      xhr.send();
	    } else {
	      xhr.setRequestHeader("Content-Type",
	        "application/json;charset=utf-8");
	      xhr.send(data);
	    }
	  }
		
})()