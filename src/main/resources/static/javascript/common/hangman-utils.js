
/**
 * state free utility functions common in both modes
 */
var hangmanUtils = (function($) {
	return {
		
		/**
		 * check if argument is accepted as guess ('a'-'z', 'A'-'Z' characters)
		 */
		isGuess: function(value) {
			return $.type(value)==='string' && /^[a-zA-Z]$/.test(value);
		},
		
		/**
		 * Check if letter is still available for guessing
		 */
		isLetterAvailable: function(guesses, letter) {
			if (!$.isArray(guesses)) throw new Error('illlegal argument');
			if (!hangmanUtils.isGuess(letter)) throw new Error('illlegal argument');
			
			var  lwrLetter = letter.toLowerCase(),
				found = false;
			
			$.each(guesses, function() {
				if (this.value.toLowerCase() === lwrLetter) {
					found = true;
				}
			});
			return !found;
		},
		
		/**
		 * Create Lock object - useful for locking view from modifications while executing ajax request 
		 */
		createLock: function() {
			var locked = false;
			return {
				lock: function() {
					locked = true;
				},
				unlock: function() {
					locked = false;
				},
				isLocked: function() {
					return locked;
				}
			};
		},
		
		/**
		 * Send request to server
		 * Function uses passed Lock object to prevent parallel requests
		 * 
		 * @param url - request url
		 * @param data - request data/parameters
		 * @param callbacks
		 * 	- before() [optional]
		 * 	- success(response) - accepts response data
		 * 	- failure(errorCode) [optional] - accepts error code
		 * 	- after() [optional]
		 * @param Lock
		 * @param options [optional]
		 * 	- type - default value is 'GET' 
		 * 	- timeout - request timeout in ms, default value is 2500
		 * @return ajaxRequest
		 */
		requestServer: function(url, data, callbacks, Lock, options) {
			if ($.type(url) !== 'string') throw new Error('illlegal argument'); // validation of public functions
			if (!$.isPlainObject(data)) throw new Error('illlegal argument');
			if (!callbacks || !$.isFunction(callbacks.success)) throw new Error('illlegal argument');
			if (!$.isPlainObject(Lock)) throw new Error('illlegal argument');
			
			var ajaxRequest;

			if (Lock.isLocked()){
				consoleUtils.warn('busy...');
				return null;
			}
			
			// last chance to modify state (couldn't do it in 'beforeSend')
			$.isFunction(callbacks.before) && callbacks.before();
			Lock.lock();
			
			// request settings
			var settings = $.extend({
				type: 'GET',
				timeout: 2500,
			}, options);
			
			// 'GET' musn't be cached
			if (settings.type === 'GET') {
				settings.cache = false;
			}
			
			// request server
			ajaxRequest = $.ajax({
				url: url,
				type: settings.type,
				cache: settings.cache,
				data: data,
				dataType: 'json',
				timeout: settings.timeout
			});
			
			ajaxRequest.done(function(response) {
				Lock.unlock(); // first unlock, then allow state modifications
				callbacks.success(response);
			});
			
			// will be executed on timeout, ajaxRequest.abort(), etc.
			ajaxRequest.fail(function() {
				Lock.unlock(); // first unlock, then allow state modifications
				$.isFunction(callbacks.failure) && callbacks.failure('HTTP-'+ajaxRequest.status); // error code
			});
			
			ajaxRequest.always(function() {
				$.isFunction(callbacks.after) && callbacks.after();
			});
			
			return ajaxRequest;
		},
		
		abortRequest: function(ajaxRequest) {
			consoleUtils.log('abording request');
			ajaxRequest && ajaxRequest.abort();
		}
	};
	
})(jQuery);


/**
 * console utilities = wrap window.log & window.warn (console doesn't always exist eg in old IEs)
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