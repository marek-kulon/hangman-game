<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath == '/' ? '' : pageContext.request.contextPath}" scope="request"/>


<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="description" content="Hangman Game">
	<meta name="author" content="Marek Kulon">
	
	<title>Hangman - Browser Mode</title>
	
	<%-- 	--------------------------- CSS --------------------------------- --%>
	<link href="${ctx}/styles/bootstrap.min.css" rel="stylesheet">
	<link href="${ctx}/styles/bootstrap-theme.min.css" rel="stylesheet">
	<link href="${ctx}/styles/hangman.css" rel="stylesheet">
	<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>

	<%-- 	--------------------------- MENU --------------------------------- --%>
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="${ctx}/">Hangman - Home</a>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li><a href="javascript:void(0);" data-hangman-new-game="animals">New Game - Animals</a></li>
					<li><a href="javascript:void(0);" data-hangman-new-game="fruits">New Game - Fruits</a></li>
					<li><a href="javascript:void(0);" data-hangman-new-game="vegetables">New Game - Vegetables</a></li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>
	
	<%-- 	--------------------------- COMMON VIEW --------------------------------- --%>
	<%@include file="/pages/common/hangman-view.jsp" %>
	

	<%-- 	--------------------------- JS --------------------------------- --%>	
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
	<script src="${ctx}/scripts/bootstrap.min.js"></script>
	<script src="${ctx}/scripts/common/hangman-utils.js"></script>
	<script src="${ctx}/scripts/hangman-browser.js"></script>
	<script type="text/javascript">
		$(function() {
			
			/*
			 * update view based on hangman.state
			 */
			function syncViewWithModel() {
				var i, guessWord, status;
				
				// higlight category
				$('[data-hangman-new-game]').each(function() {
					$(this).parent().removeClass('active');
				})
				$('[data-hangman-new-game="'+hangman.state.getCategory().toLowerCase()+'"]').parent().addClass('active');
				
				
				// update image
				$('[data-hangman-image]').attr('src', '${ctx}/images/stages/'+hangman.state.getIncorrectGuessesNo()+'.png');
				// show & hide letters
				$('[data-hangman-letter]').each(function(){
					var $this = $(this),
						letter = $this.data('hangmanLetter');
					
					if(hangman.state.isLetterAvailable(letter)) {
						$this.show();
					} else {
						$this.hide();
					}
				});
				// display guess word
				$('[data-hangman-word]').html('');
				guessWord = hangman.state.getGuessWord();
				for (i=0; i < guessWord.length; i++) {
					$('[data-hangman-word]').append('<span class="">'+guessWord[i]+'</span>');
				}
				// display game status
				status = hangman.state.getStatus();
				if (status==='IN_PROGRESS') {
					$('[data-hangman-status]').html('');
				} else {
					$('[data-hangman-status]').html(status==='WON' ? 'You Won': 'You Lost');
				}
			}
			
			/*
			 * display notification about error
			 */
			function displayErrorAlert(errorCode) {
				var id = 'alert'+new Date().getTime(); // should be unique enough
				// show alert
				$('body').append(
					'<div id="'+id+'" class="alert alert-danger">'+
						'<strong>'+errorCode+' error</strong>'+
					'</div>'
				);
				// remove the very same alert
				setTimeout(function() {
					$('#'+id).hide(500, function(){ $(this).remove()});
				}, 1500);
			}
			
			
				// start category = first category from menu
			var startCategory = $('[data-hangman-new-game]:first').data('hangmanNewGame'),
				callbacks = { // callbacks for hangman operations 
						success: syncViewWithModel,
						failure: displayErrorAlert
				};
				
			// set contex everywhere	
			window.ctx = '${ctx}';	
			
			// start game
			startHangmanGame(10, startCategory, callbacks);
			
			// bind guessing
			$('[data-hangman-letter]').click(function() {
				var $letter = $(this), // jQuery letter object
					letter = $letter.data('hangmanLetter'); // letter character
				
				hangman.guess(letter, callbacks);
			});
			
			// bind new game buttons
			$('[data-hangman-new-game]').click(function() {
				var category = $(this).data('hangmanNewGame');
				
				hangman.abortAllOperations(); // stop current operations & make sure that state/view doesn't change after reset
				hangman.newGame(10, category, callbacks);
			});
			
		});
	</script>
</body>
</html>
