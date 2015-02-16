
/**
 * console utilities = wrap window.log & window.warn as console doesn't always exist eg in old IEs
 */
var consoleUtils = (function() {
	"use strict";
	
	return {
		log: function(message) {
			window.console && window.console.log && window.console.log(message);
		},
		warn: function(message) {
			window.console && window.console.warn && window.console.warn(message);
		}	
	}
})();


/**
 * 
 * Library injects hangman object into global scope.
 * Use this object to interact with the user. Main operations are:
 * - guess(value, callbacks)
 * - newGame(maxIncorrectGuessesNo, category, callbacks) 
 * - load(token, callbacks)
 * - abortAllOperations()
 * - state.* - object containing various methods related to state of the game (@see hangman.state for more details)
 * 	 
 */
hangman = (function($) {
	"use strict";
	
	/**
	 * Send request to server
	 * Function uses passed ajaxRequest object to prevent parallel requests
	 * 
	 * @param url - request url
	 * @param callbacks
	 * 	- before() [optional]
	 * 	- success(response) - accepts response data
	 * 	- failure(errorMessage) [optional] - accepts error message
	 * 	- after() [optional]
	 * @param ajaxRequest [optional] - requestServer function won't fire new request if ajaxRequest is still pending
	 *	- instance of jqXHR
	 * @param options [optional]
	 * 	- type - default value is 'GET' 
	 * 	- timeout - request timeout in ms, default value is 2500
	 * @return jqXHR if request is executed, received ajaxRequest object otherwise
	 */
	function requestServer(url, callbacks, ajaxRequest, options) {
		if ($.type(url) !== 'string') throw new Error('illegal argument'); // validation of public functions
		if (!callbacks || !$.isFunction(callbacks.success)) throw new Error('illegal argument');
		
		var jqXHR;

		if (ajaxRequest && ajaxRequest.state() === 'pending'){
			consoleUtils.warn('busy...');
			return ajaxRequest;
		}
		
		// last chance to modify state (couldn't do it in 'beforeSend')
		$.isFunction(callbacks.before) && callbacks.before();
		
		// request settings
		var settings = $.extend(
			{						// default settings
				type: 'GET',
				timeout: 2500,
			}, 
			options,				// user options
			{						// overwrites
				url: url,
				dataType: 'json'
			}
		);
		
		// 'GET' mustn't be cached
		if (settings.type === 'GET') {
			settings.cache = false;
		}
		
		// request server
		jqXHR = $.ajax(settings);
		
		jqXHR.done(function(response) {
			callbacks.success(response);
		});
		
		// will be executed on timeout, jqXHR.abort(), etc.
		if ($.isFunction(callbacks.failure)) {
			jqXHR.fail(function() {
				var errorMsg = (jqXHR.responseJSON && jqXHR.responseJSON.message) ||  	// json message if exists
					('HTTP-STATUS-'+jqXHR.status); 										// otherwise http response code
				
				callbacks.failure(errorMsg); // pass error message
			});
		}
		
		if ($.isFunction(callbacks.after)) {
			jqXHR.always(callbacks.after);
		} 
		
		return jqXHR;
	}
	
	
	/**
	 * Check if argument is accepted as a guess ('a'-'z', 'A'-'Z' characters)
	 */
	function isValidGuess(value) {
		return $.type(value)==='string' && /^[a-zA-Z]$/.test(value);
	}
	
	
	
		/*
		 * State of the game
		 */
	var data,
	
		/*
		 * Global ajax request
		 */
		ajaxRequest,
		
		/*
		 * Local copy of game library
		 */
		fn = {};
		
		
	/**
	 * Execute a guess. Operation is ignored if game is not loaded/game is finished/another request is pending
	 * 
	 * @param value - guess received from user
	 * @param callbacks
	 * 	- before() [optional]
	 * 	- success()
	 * 	- failure(errorMessage) [optional] - accepts error message 
	 * 	- after() [optional]
	 */
	fn.guess = function(value, callbacks) {
		if (!isValidGuess(value)) throw new Error('illegal argument');
		if (!callbacks || !$.isFunction(callbacks.success)) throw new Error('illegal argument');
		
		consoleUtils.log('guess, value: '+value);
		
		if (!fn.state.isLoaded()) {
			consoleUtils.log('game not loaded');
			$.isFunction(callbacks.failure) && callbacks.failure('Game not loaded');
			return;
		}
		
		// exit if finished
		if (fn.state.getStatus() !== 'IN_PROGRESS') {
			consoleUtils.log('game finished');
			$.isFunction(callbacks.failure) && callbacks.failure('Game is finished');
			return;
		}
		
		ajaxRequest = requestServer(
			window.ctx+'/game/guess/'+fn.state.getToken()+'/'+value, 		// url
			{																// callbacks: new object, decorated success callback
				before: callbacks.before,
				success: function(response){
					$.extend(data, response);  // update game data - response contains game state and guessed value
					callbacks.success();
				},
				failure: callbacks.failure,
				after: callbacks.after
			},
			ajaxRequest,													// current ajaxRequest
			{																// settings
				type: 'PATCH'
			}
		);
	};
	
	
	/**
	 * Generate new game. Operation is ignored if another request is pending
	 * 
	 * @param maxIncorrectGuessesNo - maximum number of user incorrect guesses
	 * @param category - category of the game (eg animals, movies)
	 * @param callbacks
	 * 	- before() [optional]
	 * 	- success()
	 * 	- failure(errorMessage) [optional] - accepts error message 
	 * 	- after() [optional]
	 */
	fn.newGame = function(maxIncorrectGuessesNo, category, callbacks) {
		if ($.type(maxIncorrectGuessesNo) !== 'number') throw new Error('illegal argument');
		if ($.type(category) !== 'string') throw new Error('illegal argument');
		if (!callbacks || !$.isFunction(callbacks.success)) throw new Error('illegal argument');
		
		consoleUtils.log('newGame, maxIncorrectGuessesNo: '+maxIncorrectGuessesNo+', category: '+category);
		
		ajaxRequest = requestServer(
			window.ctx+'/game/new-game/'+category+'/'+maxIncorrectGuessesNo, 		// url
			{																		// callbacks: new object, decorated success callback
				before: callbacks.before,
				success: function(response){
					data = {
						token: response.links[0].href, // token is now the first link
						state: response.state,
						guessedValue: response.guessedValue
					};
					callbacks.success();
				},
				failure: callbacks.failure,
				after: callbacks.after
			},
			ajaxRequest,															// current ajaxRequest
			{ 																		// settings
				type: 'POST',
				timeout: 5000
			}														
		);
	};
	
	/**
	 * Load data based on token. Operation is ignored if another request is pending
	 * 
	 * @param token
	 * @param callbacks
	 * 	- before() [optional]
	 * 	- success()
	 * 	- failure(errorMessage) [optional] - accepts error message 
	 * 	- after() [optional]
	 */
	fn.load = function(token, callbacks) {
		if ($.type(token) !== 'string') throw new Error('illegal argument');
		if (!callbacks || !$.isFunction(callbacks.success)) throw new Error('illegal argument');
		
		consoleUtils.log('load, token: '+token);
		
		ajaxRequest = requestServer(
			window.ctx+'/game/load/'+token,							// url
			{														// callbacks: new object, decorated success callback
				before: callbacks.before,
				success: function(response){
					// data is now a new object: given token and response (game state and guessed value)
					data = $.extend({token: token}, response);  
					callbacks.success();
				},
				failure: callbacks.failure,
				after: callbacks.after
			},
			ajaxRequest												// current ajaxRequest
		);
	};
	
	
	/**
	 * Abort all pending operations ( = abort ajax request)
	 */
	fn.abortAllOperations = function() {
		ajaxRequest && ajaxRequest.abort();
	};
	
	
	/**
	 * Functions related to state of the game
	 */
	fn.state = {
		
		isLoaded: function() {
			return !!data; // data is defined
		},
			
		getToken: function() {
			if (!fn.state.isLoaded()) {
				return '';
			}
			
			return data.token;
		},
			
		getCategory: function() {
			if (!fn.state.isLoaded()) {
				return '';
			}
			
			return data.state.category;
		},
		
		getMaxIncorrectGuessesNo: function() {
			if (!fn.state.isLoaded()) {
				return 0;
			}
			
			return data.state.maxIncorrectGuessesNo;
		},
		
		getCorrectGuessesNo: function() {
			if (!fn.state.isLoaded()) {
				return 0;
			}
			
			return data.state.correctGuessesNo;
		},
		
		getIncorrectGuessesNo: function() {
			if (!fn.state.isLoaded()) {
				return 0;
			}
			
			return data.state.incorrectGuessesNo;
		},
		
		getStatus: function() {
			if (!fn.state.isLoaded()) {
				return '';
			}
			
			return data.state.status;
		},
		
		getGuessWord: function() {
			if (!fn.state.isLoaded()) {
				return '';
			}
			
			return data.guessedValue;
		},
			
		/**
		 * Check if letter is still available for guessing
		 */
		isLetterAvailable: function(letter) {
			if (!isValidGuess(letter)) throw new Error('illegal argument');
			
			var found = false;
			
			if (!fn.state.isLoaded()) {
				return true;
			}
			
			$.each(data.state.guesses, function() {
				if (this.value.toLowerCase() === letter.toLowerCase()) {
					found = true;
				}
			});
			return !found;
		}
	};
	
	// expose local copy to global scope
	return fn;
})(jQuery);
