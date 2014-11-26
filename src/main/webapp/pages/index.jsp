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
	
	<title>Hangman</title>
	
	<%-- 	--------------------------- CSS --------------------------------- --%>
	<link href="${ctx}/styles/bootstrap.min.css" rel="stylesheet">
	<link href="${ctx}/styles/bootstrap-theme.min.css" rel="stylesheet">
	<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>


	<div class="container">
		<div class="row">
	        <div class="col-md-12 top-margin"></div>
	    </div>
	    
	    <%-- 	--------------------------- GOTO BROWSER VERSION--------------------------------- --%>
		<div class="row">
			<div class="jumbotron">
		        <h1>Browser Mode</h1>
		        <p>Data is saved in client-side localStorage</p>
		        <p>
		        	<a class="btn btn-lg btn-primary" href="${ctx}/browser" role="button">Play »</a>
		        </p>
		      </div>
	    </div>
	    
	    <%-- 	--------------------------- GOTO DATABASE VERSION --------------------------------- --%>
		<div class="row">
			<div class="jumbotron">
		        <h1>Database Mode</h1>
		        <p>Data is saved in server-side database</p>
		        <p>
		        	<a class="btn btn-lg btn-primary" href="${ctx}/database" role="button">Play »</a>
		        </p>
		      </div>
	    </div>
	</div>
</body>
</html>
