
/**
 * Main library
 * 
 * To start hangman game call startHangmanGame(maxIncorrectGuessesNo, category, callbacks) where:
 * @param maxIncorrectGuessesNo - maximum number of wrong guesses user can make
 * @param category - word category
 * @param callbacks - callback functions executed after library generates a new game. Available callbacks are:
 * 	- before() [optional]
 * 	- success()
 * 	- failure(errorCode) [optional] - accepts error code
 * 	- after() [optional]
 * 
 * Library will either start from the point user finished last time or will generate a new game using parameters described above.
 * startHangmanGame() also injects hangman object into global scope.
 * Use this object to interact with the user. Main operations are:
 * - guess(letter, callbacks) - accepts guess letter & callbacks (@see hangman.guess for more details)
 * - newGame(maxIncorrectGuessesNo, category, callbacks) - same parameters as startHangmanGame (@see hangman.newGame for more details)
 * - abortAllOperations() - stops ongoing communication with server and unlock game
 * - state.* - object containing various methods related to state of the game (@see hangman.state for more details)
 * 	 
 */
function startHangmanGame(maxIncorrectGuessesNo, category, callbacks) {
	if ($.type(maxIncorrectGuessesNo) !== 'number') throw new Error('illlegal argument'); // validation of public functions
	if ($.type(category) !== 'string') throw new Error('illlegal argument');
	if (!callbacks || !$.isFunction(callbacks.success)) throw new Error('illlegal argument');
	
	/*
	 *  make sure $ is jQuery object
	 */
	window.hangman = (function($) {
		"use strict";
		
		// low level function: read data from local storage (I assume that storage is supported)
		function readData(){
			var str = localStorage.getItem('hangman'), data;
			try {
				data = str && JSON.parse(str);
			} catch(gpdExc){ consoleUtils.warn(gpdExc); }
			return data;
		}
		
		// low level function: write data to local storage (I assume that storage is supported)
		function saveData(data){
			var str;
			try {
				str = JSON.stringify(data);
			} catch(spdExc) { consoleUtils.warn(spdExc); }
			localStorage.setItem( 'hangman', str || '' );
		}
	
			/*
			 * State of the game (@see fn.state.isValid())
			 * Get it from persistence storage at startup
			 */
		var data = readData(), 
			
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
			 * 	- if status is 'SUCCESS': update local state, save it and execute original success callback
			 * 	- if status is not 'SUCCESS': execute original failure callback
			 */ 
			commonSuccessCallback = function (originalSuccess, originalFailure) {
				return function(response){
					if (response.status==='SUCCESS') {
						data = response.data; // keep data & persisted data in sync
						saveData(data);
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
		fn.guess = function(value, callbacks) {
			if (!hangmanUtils.isGuess(value)) throw new Error('illlegal argument');
			if (!callbacks || !$.isFunction(callbacks.success)) throw new Error('illlegal argument');
			
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
				window.ctx+'/browser/guess',		// url
				{									// data
					state: JSON.stringify({ state: data.state, checkSum: data.checkSum }),
					guess: value
				},
				{									// callbacks
					bofore: callbacks.before,
					success: commonSuccessCallback(callbacks.success, callbacks.failure),
					failure: callbacks.failure,
					after: callbacks.after
				},
				Lock,								// Lock
				{ type: 'POST' }					// options
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
				window.ctx+'/browser/new-game', // url
				{ 								// data
					maxIncorrectGuessesNo: maxIncorrectGuessesNo,
					category: category
				},
				{								// callbacks
					bofore: callbacks.before,
					success: commonSuccessCallback(callbacks.success, callbacks.failure),
					failure: callbacks.failure,
					after: callbacks.after
				},
				Lock,							// Lock
				{ timeout: 5000 }				// settings
			);
			return true;
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
			/**
			 * check if data.state is a valid state object
			 */ 
			isValid: function () {
				
				// check data structure
				var mainDataOk =  $.isPlainObject(data) &&
					$.type(data.guessedValue) === 'string' &&
					$.type(data.checkSum) === 'string' &&
					$.isPlainObject(data.state) &&
						$.isPlainObject(data.state.secret) && // not exactly a secure solution
						$.type(data.state.maxIncorrectGuessesNo) === 'number' &&
						$.isArray(data.state.guesses) &&
						$.type(data.state.correctGuessesNo) === 'number' &&
						$.type(data.state.incorrectGuessesNo) === 'number' &&
						$.type(data.state.status) === 'string' &&
							$.type(data.state.secret.value) === 'string' &&
							$.type(data.state.secret.category) === 'string';
				
				
				// check guesses
				var guessesOk = true;
				if ( mainDataOk) {
					$.each(data.state.guesses, function() {
						if (!$.isPlainObject(this) || !$.type(this.value) === 'string') {
							guessesOk = false;
						}
					});
				}
				
				return mainDataOk && guessesOk;
			},
			
			getCategory: function() {
				return data.state.secret.category;
			},
			
			getMaxIncorrectGuessesNo: function() {
				return data.state.maxIncorrectGuessesNo;
			},
			
			getCorrectGuessesNo: function() {
				return data.state.correctGuessesNo;
			},
			
			getIncorrectGuessesNo: function() {
				return data.state.incorrectGuessesNo;
			},
			
			getStatus: function() {
				return data.state.status;
			},
			
			getGuessWord: function() {
				return data.guessedValue;
			},
				
			/**
			 * Check if letter is still available for guessing
			 */
			isLetterAvailable: function(letter) {
				return hangmanUtils.isLetterAvailable(data.state.guesses,letter);
			}
		};
		
		// expose local copy to global scope
		return fn;
	})(jQuery);
	
	
	// start game based on it's saved state
	if (hangman.state.isValid()) {
		consoleUtils.log('game ok, restoring');
		callbacks.success();
	} else { // if playing first time or data is somehow corrupted
		consoleUtils.log('game invalid, starting new one');
		hangman.newGame(maxIncorrectGuessesNo, category, callbacks);
	}
}
