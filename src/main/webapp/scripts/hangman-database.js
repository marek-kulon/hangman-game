
/**
 * 
 * Library injects hangman object into global scope.
 * Use this object to interact with the user. Main operations are:
 * - guess(token, letter, callbacks)
 * - newGame(maxIncorrectGuessesNo, category, callbacks) 
 * - load(token, callbacks)
 * - abortAllOperations()
 * - state.* - object containing various methods related to state of the game (@see hangman.state for more details)
 * 	 
 */
hangman = (function($) {
	"use strict";
	
		/*
		 * State of the game
		 */
	var data,
	
		/*
		 * internal lock
		 */
		Lock = hangmanUtils.createLock(),
		
		/*
		 * Global ajax request
		 * Thanks to 'Lock' object ajax requests is synchronous
		 */
		ajaxRequest,
		
		/*
		 * Build common success callback passed to ajax
		 * New function checks result status and:
		 * 	- if status is 'SUCCESS': update local state and execute original success callback
		 * 	- if status is not 'SUCCESS': execute original failure callback
		 */ 
		commonSuccessCallback = function (originalSuccess, originalFailure) {
			return function(response){
				if (response.status==='SUCCESS') {
					data = response.data; 
					originalSuccess(); // execute original success callback
				} else {
					$.isFunction(originalFailure) && originalFailure(response.message);
				}
			};
		},
		
		/*
		 * Local copy of game library
		 */
		fn = {};
		
		
	/**
	 * Execute a guess. Operation locks game until result is ready
	 * 
	 * @param token - game token
	 * @param value - guess received from user
	 * @param callbacks
	 * 	- before() [optional]
	 * 	- success()
	 * 	- failure(errorCode) [optional] - accepts error code 
	 * 	- after() [optional]
	 * @returns
	 * 	- true: request accepted (will be executed)
	 * 	- false: request ignored (game is busy/locked)
	 */
	fn.guess = function(token, value, callbacks) {
		if ($.type(token) !== 'string') throw new Error('illlegal argument');
		if (!hangmanUtils.isGuess(value)) throw new Error('illlegal argument');
		if (!callbacks || !$.isFunction(callbacks.success)) throw new Error('illlegal argument');
		
		if (!fn.state.isLoaded()) {
			consoleUtils.log('game not loaded');
			$.isFunction(callbacks.failure) && callbacks.failure('GAME-NOT-LOADED');
			return false;
		}
		
		// exit if finished
		if (fn.state.getStatus() !== 'IN_PROGRESS') {
			consoleUtils.log('game finished');
			return false;
		}
		
		consoleUtils.log('guess, value: '+value);
		
		if (Lock.isLocked()) {
			consoleUtils.warn('busy...');
			return false;
		}
		
		ajaxRequest = hangmanUtils.requestServer(
			window.ctx+'/database/guess',		// url
			{									// data
				token: token,
				guess: value
			},
			{									// callbacks
				before: callbacks.before,
				success: commonSuccessCallback(callbacks.success, callbacks.failure),
				failure: callbacks.failure,
				after: callbacks.after
			},
			Lock								// Lock
		);
		return true;
	};
	
	
	/**
	 * Generate new game. Operation locks game until result is ready
	 * 
	 * @param maxIncorrectGuessesNo - maximum number of user incorrect guesses
	 * @param category - category of the game (eg animals, movies)
	 * @param callbacks
	 * 	- before() [optional]
	 * 	- success()
	 * 	- failure(errorCode) [optional] - accepts error code 
	 * 	- after() [optional]
	 * @returns
	 * 	- true: request accepted (will be executed)
	 * 	- false: request ignored (game is busy/locked)
	 */
	fn.newGame = function(maxIncorrectGuessesNo, category, callbacks) {
		if ($.type(maxIncorrectGuessesNo) !== 'number') throw new Error('illlegal argument');
		if ($.type(category) !== 'string') throw new Error('illlegal argument');
		if (!callbacks || !$.isFunction(callbacks.success)) throw new Error('illlegal argument');
		
		consoleUtils.log('newGame, maxIncorrectGuessesNo: '+maxIncorrectGuessesNo+', category: '+category);
		
		if (Lock.isLocked()) {
			consoleUtils.warn('busy...');
			return false;
		}
		
		ajaxRequest = hangmanUtils.requestServer(
			window.ctx+'/database/new-game', 	// url
			{ 									// data
				maxIncorrectGuessesNo: maxIncorrectGuessesNo,
				category: category
			},
			{									// callbacks
				before: callbacks.before,
				success: commonSuccessCallback(callbacks.success, callbacks.failure),
				failure: callbacks.failure,
				after: callbacks.after
			},
			Lock,								// Lock
			{ timeout: 5000 }					// settings
		);
		return true;
	};
	
	/**
	 * Load data based on token
	 * 
	 * @param token
	 * @param callbacks
	 * 	- before() [optional]
	 * 	- success()
	 * 	- failure(errorCode) [optional] - accepts error code 
	 * 	- after() [optional]
	 */
	fn.load = function(token, callbacks) {
		if ($.type(token) !== 'string') throw new Error('illlegal argument');
		if (!callbacks || !$.isFunction(callbacks.success)) throw new Error('illlegal argument');
		
		ajaxRequest = hangmanUtils.requestServer(
			window.ctx+'/database/load',		// url
			{ token: token },					// data
			{									// callbacks
				before: callbacks.before,
				success: commonSuccessCallback(callbacks.success, callbacks.failure),
				failure: callbacks.failure,
				after: callbacks.after
			},
			Lock
		);
	};
	
	
	/**
	 * Abort all pending operations ( = abort ajax request)
	 */
	fn.abortAllOperations = function() {
		hangmanUtils.abortRequest(ajaxRequest);
		Lock.unlock(); // just to be sure
	};
	
	
	/**
	 * Functions related to state of the game
	 */
	fn.state = {
		
		isLoaded: function() {
			return !!data;
		},
			
		getToken: function() {  // useful only when starting new game
			return fn.state.isLoaded() ? data.token : '';
		},
			
		getCategory: function() {
			return fn.state.isLoaded() ? data.state.category : '';
		},
		
		getMaxIncorrectGuessesNo: function() {
			return fn.state.isLoaded() ? data.state.maxIncorrectGuessesNo : 0;
		},
		
		getCorrectGuessesNo: function() {
			return fn.state.isLoaded() ? data.state.correctGuessesNo : 0;
		},
		
		getIncorrectGuessesNo: function() {
			return fn.state.isLoaded() ? data.state.incorrectGuessesNo : 0;
		},
		
		getStatus: function() {
			return fn.state.isLoaded() ? data.state.status : '';
		},
		
		getGuessWord: function() {
			return fn.state.isLoaded() ? data.guessedValue : '';
		},
			
		/**
		 * Check if letter is still available for guessing
		 */
		isLetterAvailable: function(letter) {
			return fn.state.isLoaded() ?
					hangmanUtils.isLetterAvailable(data.state.guesses,letter) :
					true; // show it if game is not loaded 
		}
	};
	
	// expose local copy to global scope
	return fn;
})(jQuery);
